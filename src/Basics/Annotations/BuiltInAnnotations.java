package Basics.Annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-In Annotations
 * --------------------
 * The standard ones every Java developer should recognise.
 *
 *
 * Compiler / language family
 * --------------------------
 *   @Override              - "I'm overriding a superclass method." The
 *                            compiler errors if the signature doesn't
 *                            actually override anything.
 *
 *   @Deprecated            - "This is going away. Don't add new uses."
 *                            Java 9+ adds elements: since, forRemoval.
 *
 *   @SuppressWarnings("unchecked", "rawtypes", ...)
 *                          - Tell the compiler to ignore named warnings
 *                            in the smallest scope you can manage.
 *
 *   @SafeVarargs           - "This varargs method is safe even though it
 *                            uses a generic type." See Generics section.
 *
 *   @FunctionalInterface   - On an interface: error if it has more than
 *                            one abstract method. Catches typos in
 *                            single-method contracts.
 *
 *
 * Meta-annotation family (used on YOUR annotations)
 * -------------------------------------------------
 *   @Retention             - SOURCE / CLASS / RUNTIME
 *   @Target                - what code element it can attach to
 *   @Inherited             - sub-classes inherit it from a class
 *   @Repeatable            - declares an annotation as repeatable
 *   @Documented            - include in javadoc
 *
 *
 * Reflection / serialization family
 * ---------------------------------
 *   @Native                - hint for native bytecode tools (rarely used).
 *   @Serial (Java 14+)     - "this is a serialization-related member."
 *   @SuppressWarnings("serial") - skip the missing-serialVersionUID warning.
 */

@SuppressWarnings("unused")   // class-level scope, suppresses unused-warnings inside
public class BuiltInAnnotations {

    // ----- @Override -----
    @Override public String toString() { return "demo"; }

    // ----- @Deprecated (modern form) -----
    @Deprecated(since = "1.5", forRemoval = true)
    public static String oldGreet() { return "hello, old API"; }

    // ----- @SuppressWarnings — keep it tight -----
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List badList() {
        List l = new ArrayList();
        l.add("string");
        l.add(42);
        return l;
    }

    // ----- @SafeVarargs — see Generics section -----
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        return List.of(items);
    }

    // ----- @FunctionalInterface -----
    @FunctionalInterface
    public interface Transformer<T, R> {
        R apply(T in);
        // adding a second abstract method here would be a COMPILE ERROR.
    }

    public static void main(String[] args) {

        section("1) @Override — compile-time safety net");
        System.out.println(new BuiltInAnnotations());

        section("2) @Deprecated — flagged in javadoc, IDEs, and tools");
        System.out.println(oldGreet());

        section("3) @SuppressWarnings — minimum scope wins");
        System.out.println(badList());

        section("4) @SafeVarargs — silence unchecked-warning on generic varargs");
        System.out.println(listOf("a", "b", "c"));

        section("5) @FunctionalInterface");
        Transformer<String, Integer> length = String::length;
        System.out.println("len('hi') = " + length.apply("hi"));

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
