package Basics.Generics;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Bounded Type Parameters
 * -----------------------
 * A BOUND restricts what types can be used for a type parameter. The syntax
 * is `T extends X` (read as "T which is some subtype of X"). The bound
 * applies whether X is a CLASS or an INTERFACE - the keyword is always
 * `extends`, never `implements`.
 *
 *      &lt;T extends Number&gt;             T must be Number, Integer, Double, ...
 *      &lt;T extends Comparable&lt;T&gt;&gt;     T must be comparable to itself
 *      &lt;T extends Number &amp; Comparable&lt;T&gt;&gt;   multiple bounds via `&amp;`
 *
 *
 * Why Bound?
 * ----------
 * Without a bound, T is just Object - you can only call Object's methods on
 * it. With a bound, the compiler lets you treat T as the bound type:
 *
 *      static &lt;T extends Number&gt; double sum(List&lt;T&gt; xs) {
 *          double s = 0;
 *          for (T x : xs) s += x.doubleValue();        // legal because T is-a Number
 *          return s;
 *      }
 *
 *
 * Multiple Bounds
 * ---------------
 *      &lt;T extends Number &amp; Comparable&lt;T&gt;&gt;
 *
 *   - Use `&` (ampersand), not commas, between bounds.
 *   - At most ONE bound may be a CLASS, and it must come FIRST. The rest
 *     must be interfaces.
 *   - The compiler treats T as having all the bounds' members.
 *
 *
 * Recursive Bounds
 * ----------------
 *      &lt;T extends Comparable&lt;T&gt;&gt;
 *
 * T's bound references T itself. This is how you require "comparable to its
 * own type" - the classic recipe for type-safe sorting / min / max. See
 * RecursiveTypeBounds.java for the full discussion.
 *
 *
 * Bounded Class-Level Type Parameter
 * ----------------------------------
 *      class NumericBox&lt;T extends Number&gt; {
 *          T value;
 *          double asDouble() { return value.doubleValue(); }
 *      }
 *
 * Now NumericBox&lt;String&gt; is a COMPILE ERROR. The class cannot be
 * instantiated with anything other than a Number subclass.
 *
 *
 * Wildcards (?) vs Bounded Type Parameters
 * ----------------------------------------
 *      &lt;T extends Number&gt;      named parameter T, usable in the body
 *      &lt;? extends Number&gt;      wildcard, no name, less flexible
 *
 * Use a NAMED type parameter when you need to REFER to that type inside the
 * method (return it, hold a reference, pass it to another generic method).
 * Use a WILDCARD when you only consume values and never need the type name.
 * See Wildcards.java and PecsPrinciple.java.
 */

public class BoundedTypeParameters {

    // ============================================================
    // 1) Single bound - T must be a Number
    // ============================================================
    public static <T extends Number> double sum(List<T> xs) {
        double total = 0;
        for (T x : xs) total += x.doubleValue();         // legal - Number method
        return total;
    }

    // ============================================================
    // 2) Recursive bound - T extends Comparable<T>
    // ============================================================
    public static <T extends Comparable<T>> T max(List<T> xs) {
        if (xs.isEmpty()) throw new IllegalArgumentException("empty");
        T best = xs.get(0);
        for (T x : xs) if (x.compareTo(best) > 0) best = x;
        return best;
    }

    // ============================================================
    // 3) Multiple bounds with the & operator
    //    "must be a Number AND Comparable to itself"
    // ============================================================
    public static <T extends Number & Comparable<T>> T pickHigh(T a, T b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }

    // ============================================================
    // 4) Bounded CLASS-level type parameter
    // ============================================================
    static class NumericBox<T extends Number> {
        private final T value;
        public NumericBox(T value) { this.value = value; }
        public T get() { return value; }
        public double asDouble() { return value.doubleValue(); }
        @Override public String toString() {
            return "NumericBox<" + value.getClass().getSimpleName() + ">[" + value + "]";
        }
    }

    // ============================================================
    // 5) Bound enabling method calls from the bound's interface
    // ============================================================
    public static <T extends Closeable> void closeQuietly(T resource) {
        try {
            resource.close();         // legal - Closeable.close exists
        } catch (IOException ignored) {
            // swallow
        }
    }

    // ============================================================
    // 6) Multiple-bound class - first must be a class, others interfaces
    // ============================================================
    static class TaggedNumber<T extends Number & Serializable & Comparable<T>> {
        private final T number;
        TaggedNumber(T number) { this.number = number; }
        public T get() { return number; }
        public int compareTo(TaggedNumber<T> other) { return number.compareTo(other.number); }
    }

    public static void main(String[] args) {

        section("1) Single bound - T extends Number");
        List<Integer> ints   = Arrays.asList(1, 2, 3, 4, 5);
        List<Double>  reals  = Arrays.asList(0.5, 1.5, 2.5);
        // List<String> bad  = Arrays.asList("a"); sum(bad);   // COMPILE ERROR
        System.out.println("sum(ints)  = " + sum(ints));
        System.out.println("sum(reals) = " + sum(reals));

        section("2) Recursive bound - max on anything Comparable");
        List<String>  words  = Arrays.asList("banana", "apple", "cherry");
        System.out.println("max(ints)  = " + max(ints));
        System.out.println("max(words) = " + max(words));

        section("3) Multiple bounds - Number AND Comparable");
        System.out.println("pickHigh(3, 7)       = " + pickHigh(3, 7));
        System.out.println("pickHigh(2.5, 1.7)   = " + pickHigh(2.5, 1.7));

        section("4) Bounded CLASS-level parameter");
        NumericBox<Integer> bi = new NumericBox<>(42);
        NumericBox<Double>  bd = new NumericBox<>(3.14);
        // NumericBox<String> bs = ...;  // COMPILE ERROR - String not a Number
        System.out.println(bi + " -> double = " + bi.asDouble());
        System.out.println(bd + " -> double = " + bd.asDouble());

        section("5) Calling interface methods through the bound");
        java.io.StringReader reader = new java.io.StringReader("hello");
        closeQuietly(reader);
        System.out.println("reader closed via bounded generic method");

        section("6) Multi-bound class - Number & Serializable & Comparable");
        TaggedNumber<Integer> ta = new TaggedNumber<>(7);
        TaggedNumber<Integer> tb = new TaggedNumber<>(9);
        System.out.println("compareTo = " + ta.compareTo(tb));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
