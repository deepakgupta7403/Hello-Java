package Basics.Generics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type Erasure
 * ------------
 * Generics are a COMPILE-TIME feature. The compiler uses type parameters to
 * check your code and insert the necessary casts. After that, all the
 * generic information is THROWN AWAY ("erased"): a List&lt;String&gt; and a
 * List&lt;Integer&gt; have the SAME runtime class.
 *
 *      ----------- source code -----------
 *      List&lt;String&gt; names = new ArrayList&lt;&gt;();
 *      names.add("Alice");
 *      String s = names.get(0);
 *
 *      ----- after compilation (roughly) -----
 *      List names = new ArrayList();
 *      names.add("Alice");
 *      String s = (String) names.get(0);
 *
 *
 * Why Erase?
 * ----------
 * Java introduced generics in 1.5 with BACKWARDS COMPATIBILITY as the
 * highest priority. Old class files and old code without generics had to
 * keep working with new generic code. Erasure was the chosen path: existing
 * `.class` files don't change, the JVM doesn't need to learn new tricks.
 *
 * The cost: many things that "feel obvious" are actually IMPOSSIBLE - see
 * GenericRestrictions.java for the full list.
 *
 *
 * How Erasure Looks At Runtime
 * ----------------------------
 *
 *   1. Type parameters become their UPPER BOUND (Object by default):
 *
 *         class Box&lt;T&gt; { T value; }
 *
 *      becomes:
 *
 *         class Box { Object value; }
 *
 *   2. CASTS are inserted by the compiler wherever the generic value is
 *      read out:
 *
 *         String s = box.get();             // (String) box.get()
 *
 *   3. BRIDGE METHODS are generated to keep polymorphism consistent. See
 *      the demo below.
 *
 *
 * Bounded Type Parameters Erase To Their Bound
 * --------------------------------------------
 *      class Repo&lt;T extends Number&gt; { T value; }
 *      // erases to:
 *      class Repo                    { Number value; }
 *
 *
 * Consequences of Erasure
 * -----------------------
 *   - You CANNOT do `new T()`           (no T-specific constructor at runtime).
 *   - You CANNOT create `new T[5]`      (no T-specific element type).
 *   - You CANNOT `if (x instanceof T)`  (T does not exist at runtime).
 *   - You CANNOT have static fields of type T (statics are shared across
 *     parameterisations, but each parameterisation might want a different T).
 *   - Overloading with same erasure is FORBIDDEN:
 *
 *         void doX(List&lt;String&gt;) { }
 *         void doX(List&lt;Integer&gt;){ }    // ERROR - same erased signature
 *
 *
 * Bridge Methods - The Compiler's Polymorphism Glue
 * -------------------------------------------------
 * When a generic method overrides a method whose signature changed under
 * erasure, the compiler emits a "bridge method" so virtual dispatch still
 * works. main() prints them via reflection.
 *
 *
 * Reflection Can Still See Some Generic Info
 * ------------------------------------------
 * Generic signatures DO appear in fields, method declarations, and class
 * declarations - they are kept in the `.class` file's Signature attribute.
 * Use ParameterizedType to inspect, e.g.,
 *
 *      Field f = MyClass.class.getDeclaredField("data");
 *      ParameterizedType pt = (ParameterizedType) f.getGenericType();
 *      pt.getActualTypeArguments();      // -> [class java.lang.String]
 */

public class TypeErasure {

    // ============================================================
    // A small generic class so we can poke at it via reflection
    // ============================================================
    static class Box<T> {
        T value;
        public Box(T value) { this.value = value; }
        public T get() { return value; }
    }

    // ============================================================
    // Bridge-method demo: a generic supertype and a typed override
    // ============================================================
    static abstract class Provider<T> {
        abstract T provide();
    }

    static class StringProvider extends Provider<String> {
        @Override
        String provide() { return "hi"; }
    }

    public static void main(String[] args) throws Exception {

        section("1) Two different generic parameterisations - SAME runtime class");
        Box<String>  bs = new Box<>("hello");
        Box<Integer> bi = new Box<>(42);
        System.out.println("bs.getClass() = " + bs.getClass().getName());
        System.out.println("bi.getClass() = " + bi.getClass().getName());
        System.out.println("same class?    " + (bs.getClass() == bi.getClass()));

        section("2) Inserted casts are visible if you reflect on bytecode");
        // We can't see the compiler-inserted casts directly here, but we can
        // see what `get()` returns when called via reflection - it gives you
        // an OBJECT, not a typed String.
        Method get = Box.class.getMethod("get");
        Object out = get.invoke(bs);                  // declared return type: Object (erased)
        System.out.println("get() runtime return type = " + get.getReturnType().getName());
        System.out.println("value via reflection       = " + out);

        section("3) Generic signatures ARE retained in .class - reflect on fields");
        java.lang.reflect.Field f = MapHolder.class.getDeclaredField("data");
        // getType() returns the ERASED type:
        System.out.println("erased type   = " + f.getType().getName());
        // getGenericType() returns the FULL parameterised type:
        System.out.println("generic type  = " + f.getGenericType());

        section("4) Overloading on the same erased signature is FORBIDDEN");
        // void doX(List<String>)  and  void doX(List<Integer>)  both erase
        // to doX(List) - so they cannot coexist. (See the commented method
        // pair at the bottom of this file for the would-be error.)

        section("5) Bridge methods - the compiler keeps polymorphism honest");
        // StringProvider.provide() returns String. The supertype erases
        // Provider<T>.provide() to Object. The compiler emits a bridge:
        //   Object provide() { return provide(); }   // bridge
        // So both Provider.provide() and StringProvider.provide() dispatch
        // correctly. We can SEE the bridge via reflection:
        for (Method m : StringProvider.class.getDeclaredMethods()) {
            System.out.println("  method=" + m.getName()
                    + "  return=" + m.getReturnType().getName()
                    + "  bridge=" + m.isBridge()
                    + "  synthetic=" + m.isSynthetic());
        }

        section("6) Erasure of bounded type parameters - bound becomes the erased type");
        // class NumericBox<T extends Number> erases to NumericBox<Number>.
        // We can see the runtime field type:
        java.lang.reflect.Field nb = NumericBox.class.getDeclaredField("v");
        System.out.println("erased  type of v = " + nb.getType().getName());          // Number
        System.out.println("generic type of v = " + nb.getGenericType());             // T

        section("7) The 'new T()' problem - no constructor at runtime");
        try {
            new Factory<Holder>().create();             // throws unless we pass the Class object
        } catch (UnsupportedOperationException e) {
            System.out.println("create() needed a Class<T> token - " + e.getMessage());
        }
        // The workaround: pass Class<T> in (a "type token"):
        Holder h = new Factory<Holder>().create(Holder.class);
        System.out.println("created via reflection: " + h);

        section("8) Practical takeaway");
        System.out.println(
                "Generics are about the COMPILER. Once code is compiled, the\n" +
                "JVM does not know whether your List is full of String or Integer.\n" +
                "Most of the time you can ignore that. The exceptions are\n" +
                "covered in GenericRestrictions.java."
        );

        // OUTPUT (representative - depends on JDK version)
    }

    /** Demonstrates that fields keep their generic signature in metadata. */
    static class MapHolder {
        @SuppressWarnings("unused")
        Map<String, List<Integer>> data = new HashMap<>();
    }

    /** Demonstrates that a bound becomes the erased type for fields. */
    static class NumericBox<T extends Number> {
        @SuppressWarnings("unused")
        T v;
    }

    /** A throwaway holder. */
    static class Holder {
        @Override public String toString() { return "Holder()"; }
    }

    /** Demonstrates the "new T()" workaround using a Class&lt;T&gt; token. */
    static class Factory<T> {
        public T create() {
            throw new UnsupportedOperationException("cannot 'new T()' - pass a Class<T> token");
        }
        public T create(Class<T> type) {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // The would-be erasure conflict:
    //   void doX(List<String>)  { }
    //   void doX(List<Integer>) { }   // would be a duplicate after erasure

    @SuppressWarnings("unused")
    private static ArrayList<?> keepImport;

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
