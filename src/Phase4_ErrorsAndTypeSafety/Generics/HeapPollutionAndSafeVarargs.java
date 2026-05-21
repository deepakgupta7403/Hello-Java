package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Heap Pollution and @SafeVarargs
 * -------------------------------
 * HEAP POLLUTION happens when a variable of a parameterised type refers to
 * an object that does NOT match that type at runtime. Type erasure means
 * the JVM cannot stop you - so the compiler issues an "unchecked" warning
 * whenever you do something that could cause heap pollution.
 * <p>
 *
 * Where Heap Pollution Comes From
 * -------------------------------
 *   1. Unchecked casts:    (List&lt;String&gt;) someObject
 *   2. Raw types:          List l = listOfStrings;
 *                          l.add(42);     // poisons the String list
 *   3. Generic VARARGS:    when a method takes T... it secretly creates an
 *                          Object[] - that array is heap pollution if T is
 *                          itself a generic type.
 * <p>
 *
 * The Varargs Bridge
 * ------------------
 * Varargs are syntactic sugar for an ARRAY. When the element type is
 * generic, the compiler must allocate an Object[] (it cannot create a
 * `new List&lt;String&gt;[N]` - generic-array creation is illegal). It then
 * issues a warning:
 * <p>
 *
 *      "unchecked: Possible heap pollution from parameterized vararg type"
 * <p>
 *
 * @SafeVarargs
 * -----------
 * Annotate a method (constructor) that:
 *   - takes a generic varargs parameter, AND
 *   - PROMISES not to misuse it (no writes to the array, no leaks of the
 *     array to non-generic code).
 * <p>
 *
 * Restrictions:
 *   - Allowed on `static`, `final`, or `private` methods, and on
 *     constructors. Since Java 9, also allowed on `private` instance methods.
 *   - You CANNOT use it on a non-final non-private instance method - a
 *     subclass could violate the promise.
 * <p>
 *
 * What Goes Wrong (The Concrete Bug)
 * ----------------------------------
 *      static &lt;T&gt; T[] toArray(T... a) { return a; }
 * <p>
 *
 *      // caller:
 *      Object[] objs = toArray(List.of(1), List.of("a"));
 *      objs[0] = "anything";     // legal at runtime - it's an Object[]
 *      List&lt;Integer&gt; bad = (List&lt;Integer&gt;) objs[0];   // succeeds at type-erasure level
 *      Integer i = bad.get(0);    // ClassCastException far from the bug
 * <p>
 *
 * The method exposed its INTERNAL Object[] to outside code - a leak. The
 * @SafeVarargs promise is "I won't do that."
 */

public class HeapPollutionAndSafeVarargs {

    public static void main(String[] args) {

        section("1) The classic vararg warning");
        // The method below uses a generic vararg. Java would warn at the
        // declaration site without @SafeVarargs.
        List<Integer> combined = safeUnion(List.of(1, 2), List.of(3, 4), List.of(5));
        System.out.println("safeUnion = " + combined);

        section("2) DON'T do this - leaking the vararg array");
        // unsafeLeak returns its OWN parameter array, which is shared - and
        // re-typed wrong. Demonstrates real heap pollution.
        try {
            Object[] obj = unsafeLeak(List.of(1, 2), List.of("a"));
            obj[0] = "now I'm a String";        // legal at runtime
            // Eventually some unsuspecting caller pulls a Number out:
            @SuppressWarnings("unchecked")
            List<Integer> oops = (List<Integer>) obj[0];
            Integer i = oops.get(0);
            System.out.println(i);
        } catch (ClassCastException cce) {
            System.out.println("CCE caught: " + cce.getMessage());
        }

        section("3) Raw-type heap pollution");
        List<String> strings = new ArrayList<>();
        strings.add("hello");
        // Through a RAW reference we can stuff anything in:
        @SuppressWarnings({"unchecked", "rawtypes"})
        List raw = strings;
        raw.add(42);                    // heap-pollutes strings
        System.out.println("strings raw-polluted = " + strings);

        try {
            for (String s : strings) System.out.println("  " + s);
        } catch (ClassCastException cce) {
            System.out.println("CCE during iteration: " + cce.getMessage());
        }

        section("4) @SafeVarargs example - the right way");
        // Same shape as #1 but with the annotation made explicit. The
        // implementation only READS from the vararg array and never leaks it.
        List<String> joined = listConcat(List.of("a", "b"), List.of("c", "d"));
        System.out.println("listConcat = " + joined);

        section("5) Java 9+ - @SafeVarargs on PRIVATE instance methods");
        new SafeUser().run();

        section("6) When you cannot use @SafeVarargs - avoid generic varargs entirely");
        // If you cannot guarantee the array won't escape or be mutated,
        // either:
        //   - re-declare the method to take a Collection&lt;T&gt; instead,
        //   - or accept the unchecked warning at the CALL site.

        // OUTPUT (representative)
    }

    /** READ-ONLY vararg method - safe. Annotated to suppress the warning. */
    @SafeVarargs
    public static <T> List<T> safeUnion(List<? extends T>... lists) {
        List<T> out = new ArrayList<>();
        for (List<? extends T> l : lists) out.addAll(l);   // read only
        return out;
    }

    /** Returns its OWN vararg array - DANGEROUS. Don't mark @SafeVarargs. */
    @SuppressWarnings("unchecked")
    static <T> T[] unsafeLeak(T... a) {
        return a;                  // the call site can mutate `a` afterwards
    }

    /** Another @SafeVarargs example with composition. */
    @SafeVarargs
    public static <T> List<T> listConcat(List<T>... lists) {
        List<T> out = new ArrayList<>();
        Collections.addAll(out, (T[]) new Object[0]);  // unused, just to keep imports lively
        for (List<T> l : lists) out.addAll(l);
        return out;
    }

    static class SafeUser {
        void run() {
            List<Integer> result = privateSafe(List.of(1), List.of(2, 3));
            System.out.println("private safe varargs = " + result);
        }

        // Private instance method + @SafeVarargs is allowed since Java 9.
        @SafeVarargs
        private final <T> List<T> privateSafe(List<? extends T>... lists) {
            List<T> out = new ArrayList<>();
            for (var l : lists) out.addAll(l);
            return out;
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused")
    private static Arrays keepImport;
}
