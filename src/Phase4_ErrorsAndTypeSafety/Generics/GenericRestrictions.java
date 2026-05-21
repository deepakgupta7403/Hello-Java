package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generic Restrictions
 * --------------------
 * Type erasure (see TypeErasure.java) makes Java generics simple to bolt on
 * to the existing JVM, but it forbids a number of things that look like
 * they should work. Knowing the rules saves hours of frustration.
 * <p>
 *
 * The Forbidden List (with workarounds)
 * -------------------------------------
 * <p>
 *
 *   1. Cannot use a PRIMITIVE as a type argument
 *         List&lt;int&gt; xs;                     // ERROR
 *         List&lt;Integer&gt; xs;                  // OK (boxing)
 * <p>
 *
 *   2. Cannot instantiate a TYPE PARAMETER directly
 *         T t = new T();                      // ERROR
 *         WORKAROUND: pass a Class&lt;T&gt; "type token" or a Supplier&lt;T&gt;.
 * <p>
 *
 *   3. Cannot create a GENERIC ARRAY directly
 *         T[]      a = new T[10];             // ERROR
 *         List&lt;String&gt;[] b = new List&lt;String&gt;[10]; // ERROR
 *         WORKAROUND: Object[] cast, or use ArrayList&lt;T&gt;.
 * <p>
 *
 *   4. Cannot use `instanceof` with a parameterised type
 *         if (x instanceof List&lt;String&gt;) ... // ERROR
 *         if (x instanceof List&lt;?&gt;)      ... // OK (wildcard)
 * <p>
 *
 *   5. Cannot CATCH a parameterised exception
 *         catch (T e) ...                     // ERROR
 *         class MyException&lt;T&gt; extends Exception { } // also illegal
 * <p>
 *
 *   6. Cannot have STATIC fields whose type uses T
 *         class Box&lt;T&gt; { static T DEFAULT; }  // ERROR
 *         static methods CAN have their OWN type parameter though.
 * <p>
 *
 *   7. Cannot OVERLOAD methods that have the SAME erased signature
 *         void m(List&lt;String&gt;)                // both erase to m(List)
 *         void m(List&lt;Integer&gt;)               // ERROR
 * <p>
 *
 *   8. Cannot CAST to a parameterised type (warning rather than error)
 *         List&lt;String&gt; ls = (List&lt;String&gt;) obj;   // unchecked-cast WARNING
 *         // The cast is REAL-CHECKED at the erased type (List), but the
 *         // <String> bit is unverifiable.
 * <p>
 *
 * Each restriction is demonstrated below - the FORBIDDEN line is commented
 * with `// ERROR:` so you can uncomment and see for yourself.
 */

public class GenericRestrictions {

    public static void main(String[] args) {

        section("1) Cannot use a primitive as a type argument");
        // List<int> ints = new ArrayList<>();   // ERROR: type argument cannot be primitive
        List<Integer> ints = new ArrayList<>();
        ints.add(42);
        System.out.println("Use the wrapper class: " + ints);

        section("2) Cannot do `new T()` directly - use a Class<T> token");
        Factory<Person> fac = new Factory<>();
        try {
            fac.cannotNew();
        } catch (UnsupportedOperationException e) {
            System.out.println("cannotNew -> " + e.getMessage());
        }
        Person p1 = fac.createViaClass(Person.class);
        Person p2 = fac.createViaSupplier(Person::new);
        System.out.println("created via Class<T>  : " + p1);
        System.out.println("created via Supplier  : " + p2);

        section("3) Cannot create a generic array directly");
        // T[] a    = new T[10];                       // ERROR
        // List<String>[] b = new List<String>[10];    // ERROR

        // Workaround A: typed ArrayList<T> instead of T[].
        List<Person> people = new ArrayList<>();
        people.add(new Person());
        // Workaround B: hostile Object[] cast (suppresses an unchecked warning).
        @SuppressWarnings("unchecked")
        Person[] arr = (Person[]) new Object[3];        // brittle - prefer A
        arr[0] = new Person();
        System.out.println("array workarounds: list=" + people + ", arr[0]=" + arr[0]);

        section("4) instanceof - wildcard YES, parameterised NO");
        Object o = new ArrayList<String>();
        // if (o instanceof List<String>) ...    // ERROR
        if (o instanceof List<?>) {              // OK - wildcard
            System.out.println("o is a List of SOMETHING (we cannot say what)");
        }
        // Since Java 16 pattern-matching for instanceof is also generic-aware
        // but only with wildcards:
        if (o instanceof List<?> lst) {
            System.out.println("pattern-matched: size = " + lst.size());
        }

        section("5) Cannot throw or catch a parameterised exception");
        // class GenericException<T> extends Exception { }   // ERROR (illegal subclass)
        // catch (T e)                                       // ERROR
        // The catch clause type is checked at runtime - and at runtime T doesn't exist.

        section("6) No static field of type T");
        // Static members are SHARED across all parameterisations of the
        // class. T means something different for Box<String> vs Box<Integer> -
        // so it cannot live as a static.
        new Box<>("alpha");
        new Box<>(42);
        System.out.println("Box.count (shared static) = " + Box.count);

        section("7) Cannot overload by erasure");
        // The two methods below have the same erased signature. Uncomment to
        // see the compile error.
        // void m(List<String> xs)  { }
        // void m(List<Integer> xs) { }

        section("8) Unchecked cast warning");
        Object obj = new ArrayList<Integer>();
        @SuppressWarnings("unchecked")
        List<String> wrong = (List<String>) obj;       // compiler can't actually verify
        wrong.add("oops?");                             // poisons the original list
        // The CRASH happens later, far away from the bad cast:
        Object reread = ((ArrayList<?>) obj).get(0);
        System.out.println("reread first = " + reread + "  (raw type, no cast)");
        try {
            // Pretend a caller expects Integers...
            Integer i = ((List<Integer>) (List<?>) wrong).get(0);
            System.out.println(i);
        } catch (ClassCastException cce) {
            System.out.println("ClassCastException at read-back: " + cce.getMessage());
        }

        // OUTPUT (representative)
    }

    // -------- helpers --------

    static class Person {
        @Override public String toString() { return "Person()"; }
    }

    static class Factory<T> {
        public T cannotNew() {
            // T t = new T();   // ERROR
            throw new UnsupportedOperationException("cannot 'new T()' due to erasure");
        }
        public T createViaClass(Class<T> type) {
            try { return type.getDeclaredConstructor().newInstance(); }
            catch (Exception e) { throw new RuntimeException(e); }
        }
        public T createViaSupplier(Supplier<T> sup) {
            return sup.get();
        }
    }

    static class Box<T> {
        // static T DEFAULT;       // ERROR - static cannot use class T
        static int count;          // legal - not parameterised
        T value;
        Box(T value) { this.value = value; count++; }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
