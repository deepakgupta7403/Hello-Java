package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wildcards (?)
 * -------------
 * A WILDCARD `?` is an "unknown type" placeholder used in TYPE ARGUMENTS
 * (when REFERRING to a generic type), never in type PARAMETER lists. It is
 * how Java relaxes the "generics are not covariant" rule when you need it.
 * <p>
 *
 * Three Flavours
 * --------------
 * <p>
 *
 *   UNBOUNDED      List&lt;?&gt;
 *      - "a list of SOMETHING - I don't know or care what"
 *      - You can read elements only as Object.
 *      - You cannot add anything except null.
 *      - Useful for size, isEmpty, clear, toString, iteration as Object.
 * <p>
 *
 *   UPPER-BOUNDED  List&lt;? extends Number&gt;
 *      - "a list of Number or some subtype" (PRODUCER)
 *      - You can READ elements as Number.
 *      - You CANNOT add anything (except null) - we don't know which subtype.
 * <p>
 *
 *   LOWER-BOUNDED  List&lt;? super Integer&gt;
 *      - "a list that can hold Integer (Integer or any supertype)" (CONSUMER)
 *      - You can ADD Integer (and its subtypes).
 *      - Reads come back typed as Object.
 * <p>
 *
 * Why Wildcards Exist
 * -------------------
 * Generics are INVARIANT: List&lt;String&gt; is NOT a List&lt;Object&gt; even though
 * String is-a Object. This rule prevents you from putting a Date into a
 * List&lt;String&gt; via an Object-typed reference - a feature, not a bug.
 * <p>
 *
 * But sometimes you only want to READ from a list of unknown type, or only
 * WRITE Integers into a list of Numbers. Wildcards bridge the gap safely.
 * <p>
 *
 * The PECS Mnemonic
 * -----------------
 *      Producer Extends, Consumer Super.
 * <p>
 *
 * - If a parameter PRODUCES T values for you, use `? extends T`.
 * - If a parameter CONSUMES T values from you, use `? super T`.
 * <p>
 *
 * See PecsPrinciple.java for worked examples.
 * <p>
 *
 * Wildcards vs Type Parameters - When to Use Which
 * ------------------------------------------------
 *   - Need to REFER to the type inside the method (return it, store it,
 *     pass it to another generic method)?
 *        -> use a NAMED type parameter   &lt;T&gt;
 *   - Just consuming the collection or producing values from it?
 *        -> a WILDCARD is more flexible.
 * <p>
 *
 *      static &lt;T&gt; T firstOf(List&lt;T&gt; xs)              // need to RETURN a T
 *      static void printAll(List&lt;?&gt; xs)                // just iterating
 *      static double sumAll(List&lt;? extends Number&gt; xs)
 *      static void addAllInts(List&lt;? super Integer&gt; into, int n)
 */

public class Wildcards {

    // ============================================================
    // 1) UNBOUNDED  List<?>
    //    Useful when you only need methods that don't depend on the element type.
    // ============================================================
    static void printAll(List<?> xs) {
        for (Object o : xs) System.out.print(o + " ");   // read as Object
        System.out.println();
        // xs.add("anything");        // COMPILE ERROR - cannot add to List<?>
        // xs.add(null);              // legal, but degenerate
    }

    static int sizeOf(List<?> xs) {
        return xs.size();                                 // size() doesn't care about <?>
    }

    // ============================================================
    // 2) UPPER-BOUNDED  ? extends Number
    //    "I will only READ" - a PRODUCER of Number values.
    // ============================================================
    static double sumAll(List<? extends Number> xs) {
        double total = 0;
        for (Number n : xs) total += n.doubleValue();      // read as Number
        return total;
        // xs.add(42);     // COMPILE ERROR - we don't know what concrete subtype this is
    }

    // ============================================================
    // 3) LOWER-BOUNDED  ? super Integer
    //    "I will WRITE Integers in" - a CONSUMER of Integer values.
    // ============================================================
    static void fillWithIntegers(List<? super Integer> dest, int n) {
        for (int i = 0; i < n; i++) dest.add(i);           // OK - we can add Integer
        // Integer x = dest.get(0);     // ERROR - reads come back as Object only
        Object o = dest.get(0);
        System.out.println("first elem (read as Object) = " + o);
    }

    // ============================================================
    // 4) Wildcards vs type parameters
    // ============================================================
    static <T> T firstOf(List<T> xs) {                    // need T as a return type
        return xs.get(0);
    }

    public static void main(String[] args) {

        section("1) Unbounded - List<?>");
        printAll(List.of(1, 2, 3));
        printAll(List.of("a", "b", "c"));
        printAll(List.of(true, false));
        System.out.println("size of List<?> = " + sizeOf(List.of(1, 2, 3)));

        section("2) Upper-bounded - ? extends Number (producer)");
        List<Integer> ints   = Arrays.asList(1, 2, 3);
        List<Double>  reals  = Arrays.asList(1.5, 2.5);
        System.out.println("sumAll(ints)  = " + sumAll(ints));
        System.out.println("sumAll(reals) = " + sumAll(reals));
        // The SAME `sumAll` works on Integer, Double, BigDecimal, ... - that
        // is the win over a plain List<Number> parameter.

        section("3) Lower-bounded - ? super Integer (consumer)");
        List<Number> numbersDest = new ArrayList<>();
        List<Object> objectsDest = new ArrayList<>();
        // Both are valid targets - Number and Object are supertypes of Integer.
        fillWithIntegers(numbersDest, 3);
        fillWithIntegers(objectsDest, 3);
        System.out.println("numbersDest = " + numbersDest);
        System.out.println("objectsDest = " + objectsDest);

        section("4) Type parameter when you need the type as a return value");
        Integer first = firstOf(ints);            // returns Integer, not Object
        String  s     = firstOf(List.of("alpha", "beta"));
        System.out.println("first(int) = " + first + ", first(str) = " + s);

        section("5) Why generics are invariant - the safety story");
        List<String> ls = new ArrayList<>(List.of("alpha"));
        // List<Object> lo = ls;          // COMPILE ERROR - good!
        // lo.add(42);                    // would pollute the String list with an Integer
        // String oops = ls.get(1);        // and crash later

        // The wildcard variants let you express "read only" or "write only"
        // safely:
        List<? extends Object> readOnlyView = ls;        // read as Object, no writes
        System.out.println("readOnlyView = " + readOnlyView);

        section("6) Wildcards in METHOD RETURN types are usually a smell");
        // Avoid: List<? extends Foo> doStuff() - callers have a hard time
        // composing it. Prefer a named T return type so callers know what
        // they're getting.

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
