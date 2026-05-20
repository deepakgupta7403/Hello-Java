package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generic Classes
 * ---------------
 * A class is GENERIC when it declares one or more TYPE PARAMETERS in
 * angle brackets after its name:
 *
 *      public class Box&lt;T&gt; { ... }
 *      public class Pair&lt;K, V&gt; { ... }
 *      public class Triple&lt;A, B, C&gt; { ... }
 *
 * Inside the class body the type parameter behaves like a real type:
 * fields, method parameters, return types - all can reference T.
 *
 *
 * Instantiation - The Diamond Operator (Java 7+)
 * ----------------------------------------------
 *      Box&lt;String&gt; b = new Box&lt;&gt;("hi");        // compiler infers <String>
 *      var          c = new Box&lt;&gt;("hi");        // works (Java 10+)
 *      Box          d = new Box&lt;&gt;("hi");        // raw type left side: warning
 *
 * Pre-Java-7 you had to repeat the type on both sides:
 *      Box&lt;String&gt; b = new Box&lt;String&gt;("hi");
 *
 *
 * Multiple Type Parameters
 * ------------------------
 *      public class Pair&lt;K, V&gt; {
 *          private final K key;
 *          private final V value;
 *          ...
 *      }
 *
 * No limit, but in practice 1-3 type parameters is the readable maximum.
 *
 *
 * Type Parameters Are NOT Static
 * ------------------------------
 * Each INSTANCE of a generic class binds the type parameter independently:
 *
 *      Box&lt;String&gt;  s = new Box&lt;&gt;("hello");
 *      Box&lt;Integer&gt; i = new Box&lt;&gt;(42);
 *
 * The type parameter T cannot be referenced from a `static` member - statics
 * are shared across all parameterisations. (See GenericRestrictions.java.)
 *
 *
 * Generic Class Hierarchies
 * -------------------------
 * One generic class may extend another. You either fix the parameter or
 * pass it through:
 *
 *      class Stack&lt;E&gt; extends ArrayList&lt;E&gt; { ... }          // pass through
 *      class IntStack    extends Stack&lt;Integer&gt;     { ... }   // fix to Integer
 *
 * IMPORTANT: Generic types are NOT covariant.
 *
 *      List&lt;String&gt; ls = ...;
 *      List&lt;Object&gt; lo = ls;                                  // COMPILE ERROR
 *
 * Even though String is-a Object, List&lt;String&gt; is NOT a List&lt;Object&gt;. To
 * relax this, use wildcards (Wildcards.java).
 */

public class GenericClasses {

    // ============================================================
    // 1) A single-parameter class
    // ============================================================
    static class Box<T> {
        private T value;
        public Box() {}
        public Box(T value) { this.value = value; }
        public T get() { return value; }
        public void set(T value) { this.value = value; }
        @Override public String toString() { return "Box[" + value + "]"; }
    }

    // ============================================================
    // 2) Multiple type parameters - the canonical Pair
    // ============================================================
    static class Pair<K, V> {
        private final K key;
        private final V value;
        public Pair(K key, V value) { this.key = key; this.value = value; }
        public K key()   { return key; }
        public V value() { return value; }
        @Override public boolean equals(Object o) {
            return o instanceof Pair<?, ?> p
                && Objects.equals(p.key, key) && Objects.equals(p.value, value);
        }
        @Override public int hashCode() { return Objects.hash(key, value); }
        @Override public String toString() { return "(" + key + ", " + value + ")"; }
    }

    // ============================================================
    // 3) Three type parameters - usually a sign to make a record
    // ============================================================
    record Triple<A, B, C>(A a, B b, C c) {}

    // ============================================================
    // 4) Subclassing a generic class - pass-through or fix
    // ============================================================
    static class Stack<E> extends ArrayList<E> {
        public void push(E e) { add(e); }
        public E pop()        { return remove(size() - 1); }
        public E peek()       { return get(size() - 1); }
    }

    /** Inherits Stack but fixes the parameter to Integer. */
    static class IntStack extends Stack<Integer> {
        public int popInt() { return pop(); }      // returns primitive
    }

    public static void main(String[] args) {

        section("1) Single-parameter Box");
        Box<String> b = new Box<>("hi");
        b.set("hello");
        System.out.println("b = " + b);

        Box<Integer> bi = new Box<>(42);
        System.out.println("bi = " + bi);

        section("2) Multi-parameter Pair");
        Pair<String, Integer> ageOf = new Pair<>("Alice", 30);
        System.out.println("ageOf = " + ageOf);
        System.out.println("key   = " + ageOf.key() + ",  value = " + ageOf.value());

        // Use a Pair as a Map value, etc.
        List<Pair<String, Integer>> people = new ArrayList<>();
        people.add(new Pair<>("Alice", 30));
        people.add(new Pair<>("Bob", 25));
        people.forEach(p -> System.out.println("  " + p));

        section("3) Generic record - three parameters, equals/hashCode for free");
        Triple<String, Integer, Boolean> row = new Triple<>("alpha", 7, true);
        System.out.println("row = " + row);
        System.out.println("row.a = " + row.a() + ", b = " + row.b() + ", c = " + row.c());

        section("4) Subclass that PASSES THROUGH the type parameter");
        Stack<String> s = new Stack<>();
        s.push("alpha"); s.push("beta"); s.push("gamma");
        System.out.println("peek = " + s.peek());
        System.out.println("pop  = " + s.pop() + "  stack=" + s);

        section("5) Subclass that FIXES the type parameter");
        IntStack is = new IntStack();
        is.push(10); is.push(20); is.push(30);
        int top = is.popInt();
        System.out.println("popInt = " + top + "  stack=" + is);

        section("6) Generic types are NOT covariant (compile-error demo)");
        List<String> ls = new ArrayList<>();
        ls.add("hello");
        // List<Object> lo = ls;             // COMPILE ERROR
        // To bridge them use a wildcard - see Wildcards.java:
        List<? extends Object> readOnly = ls;
        System.out.println("readOnly = " + readOnly);

        section("7) Diamond <> + var = no type repetition");
        Box<String> verbose  = new Box<String>("verbose");        // pre-7 style
        Box<String> diamond  = new Box<>("diamond");              // Java 7+
        var         inferred = new Box<>("var + diamond");         // Java 10+
        System.out.println(verbose + " / " + diamond + " / " + inferred);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
