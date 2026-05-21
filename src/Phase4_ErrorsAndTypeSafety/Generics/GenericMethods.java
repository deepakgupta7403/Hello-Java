package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.*;
import java.util.function.Function;

/**
 * Generic Methods
 * ---------------
 * A method is GENERIC when it declares its OWN type parameters in front of
 * the return type, regardless of whether the enclosing class is generic.
 * <p>
 *
 *      public &lt;T&gt; List&lt;T&gt; singletonList(T t) { ... }
 *      public &lt;K, V&gt; Map.Entry&lt;K, V&gt; entry(K k, V v) { ... }
 *      public static &lt;T extends Comparable&lt;T&gt;&gt; T max(List&lt;T&gt; xs) { ... }
 * <p>
 *
 * The type parameters BEFORE the return type belong to THE METHOD - not
 * the class. They are bound EACH CALL and may differ between calls.
 * <p>
 *
 * Type Inference at the Call Site
 * -------------------------------
 * In almost every case the compiler can INFER the type parameters from the
 * call's arguments and the assignment context:
 * <p>
 *
 *      List&lt;Integer&gt; xs = singletonList(42);   // T inferred as Integer
 *      var pair         = entry("a", 1);        // K=String, V=Integer
 * <p>
 *
 * You can also write the type parameter explicitly, but it is rarely needed:
 * <p>
 *
 *      Collections.&lt;String&gt;emptyList();
 *      Pair.&lt;String, Integer&gt;of("a", 1);
 * <p>
 *
 * Where Type Parameters Go
 * ------------------------
 *      public  &lt;T&gt; void doSomething(T t)                    // before return type
 *      private &lt;K, V&gt; Map&lt;K, V&gt; emptyMap()
 *      static  &lt;T&gt; T noOp(T t)
 * <p>
 *
 * On a CONSTRUCTOR the parameter list goes BEFORE the class name in the
 * declaration (rarely used in practice):
 * <p>
 *
 *      public class Box {
 *          public &lt;T&gt; Box(T t) { ... }
 *      }
 * <p>
 *
 * Static Generic Methods
 * ----------------------
 * Static methods may have their OWN type parameters. A static method in a
 * generic class CANNOT use the CLASS's type parameter:
 * <p>
 *
 *      class Box&lt;T&gt; {
 *          static T DEFAULT;                   // ERROR - static can't use T
 *          static &lt;T&gt; T noOp(T t) { ... }    // OK - method's own T
 *      }
 * <p>
 *
 * Bounded Generic Methods
 * -----------------------
 *      public static &lt;T extends Comparable&lt;T&gt;&gt; T max(List&lt;T&gt; xs)
 * <p>
 *
 * See BoundedTypeParameters.java for the deep dive.
 */

public class GenericMethods {

    // ============================================================
    // 1) Simplest generic method - returns its argument
    // ============================================================
    public static <T> T identity(T t) { return t; }

    // ============================================================
    // 2) Generic method building a List from a varargs
    // ============================================================
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        List<T> out = new ArrayList<>(items.length);
        Collections.addAll(out, items);
        return out;
    }

    // ============================================================
    // 3) Two type parameters - pair factory
    // ============================================================
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return Map.entry(key, value);          // JDK builtin
    }

    // ============================================================
    // 4) Generic method with a bound (Comparable)
    // ============================================================
    public static <T extends Comparable<T>> T max(List<T> xs) {
        if (xs.isEmpty()) throw new IllegalArgumentException("empty list");
        T best = xs.get(0);
        for (T x : xs) if (x.compareTo(best) > 0) best = x;
        return best;
    }

    // ============================================================
    // 5) Generic method that uses TWO type parameters with a relationship
    //    (a Function from T to R)
    // ============================================================
    public static <T, R> List<R> mapAll(List<T> source, Function<? super T, ? extends R> mapper) {
        List<R> out = new ArrayList<>(source.size());
        for (T t : source) out.add(mapper.apply(t));
        return out;
    }

    // ============================================================
    // 6) Generic method on the JDK Collections-style swap
    // ============================================================
    public static <T> void swap(List<T> xs, int i, int j) {
        T tmp = xs.get(i);
        xs.set(i, xs.get(j));
        xs.set(j, tmp);
    }

    // ============================================================
    // 7) "Type witness" - manually specifying the type parameter
    // ============================================================
    static <T> Map<String, T> emptyTypedMap() { return new HashMap<>(); }

    public static void main(String[] args) {

        section("1) Type inference at the call site");
        String s = identity("hello");          // T inferred as String
        Integer n = identity(42);              // T inferred as Integer
        System.out.println(s + " / " + n);

        section("2) listOf varargs");
        List<Integer> a = listOf(1, 2, 3);
        List<String>  b = listOf("alpha", "beta");
        System.out.println(a + " / " + b);

        section("3) Pair factory with two parameters");
        Map.Entry<String, Integer> e = entry("score", 95);
        System.out.println(e);

        section("4) Bounded generic method - max");
        List<Integer> nums   = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        List<String>  words  = Arrays.asList("banana", "apple", "cherry");
        System.out.println("max(nums)  = " + max(nums));
        System.out.println("max(words) = " + max(words));

        section("5) Map - transform a list lazily? eagerly. T -> R");
        List<Integer> lengths = mapAll(words, String::length);
        System.out.println("lengths = " + lengths);

        section("6) Swap - in-place mutation on any List");
        List<String> letters = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        swap(letters, 0, 3);
        System.out.println("after swap = " + letters);

        section("7) Type witness (rarely needed - included for completeness)");
        // Without the witness the compiler might infer Map<String, Object>:
        Map<String, Integer> ints = GenericMethods.<Integer>emptyTypedMap();
        ints.put("a", 1);
        System.out.println(ints);

        section("8) Generic method on a NON-generic class - perfectly fine");
        // GenericMethods is not generic, but the methods above are.
        // That is the WHOLE POINT of generic methods.

        section("9) Method-level type parameters can SHADOW class-level ones");
        new HoldsString().describe();

        // OUTPUT (representative)
    }

    /** Demonstrates that a generic method can declare its OWN T independent
     *  of any enclosing class's T. */
    static class HoldsString {
        // Class is non-generic; the method below is.
        <T> void describe() {
            System.out.println("a generic method on a non-generic class");
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
