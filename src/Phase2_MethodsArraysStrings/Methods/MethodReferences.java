package Phase2_MethodsArraysStrings.Methods;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Method References (Java 8+)
 * ---------------------------
 * A method reference is a compact, expressive ALTERNATIVE TO A LAMBDA when the
 * lambda just calls an existing method. Syntax:
 * <p>
 *
 *      Target::methodName
 * <p>
 *
 * Four Forms of Method References
 * -------------------------------
 * <p>
 *
 *  1. STATIC method                    Class::staticMethod
 * <p>
 *
 *         Function<String, Integer> p = Integer::parseInt;
 *         // equivalent lambda:  s -> Integer.parseInt(s)
 * <p>
 *
 *  2. INSTANCE method of a PARTICULAR object    instance::method
 * <p>
 *
 *         StringBuilder sb = new StringBuilder();
 *         Consumer<String> append = sb::append;
 *         // equivalent lambda:  s -> sb.append(s)
 * <p>
 *
 *  3. INSTANCE method of an ARBITRARY object of a type    Class::method
 * <p>
 *
 *         Function<String, Integer> len = String::length;
 *         // equivalent lambda:  s -> s.length()
 * <p>
 *
 *  4. CONSTRUCTOR reference            Class::new
 * <p>
 *
 *         Supplier<ArrayList<String>> mk = ArrayList::new;
 *         Function<String, StringBuilder> wrap = StringBuilder::new;
 * <p>
 *
 * Why Use Them?
 * -------------
 *  - Less noise than the equivalent lambda.
 *  - Reads almost like English: "stream of names, sort by String::length".
 *  - Encourages thinking in terms of WHAT to apply rather than HOW.
 * <p>
 *
 * When To Stick With a Lambda
 * ---------------------------
 *  - When the lambda needs additional logic beyond a single method call.
 *  - When the type inference for `::` gets confused.
 *  - When readability suffers (overloaded methods + method references can be
 *    hard to read).
 */

public class MethodReferences {

    public static void main(String[] args) {

        // --- 1) Static method reference - Integer::parseInt ---
        Function<String, Integer> parse = Integer::parseInt;
        System.out.println("parse.apply(\"42\")   = " + parse.apply("42"));

        // --- 2) Instance method of a particular object - sb::append ---
        StringBuilder sb = new StringBuilder("start");
        java.util.function.Consumer<String> append = sb::append;
        append.accept(" + more");
        append.accept(" + extra");
        System.out.println("sb after appends    = " + sb);

        // --- 3) Instance method of an arbitrary object - String::length ---
        Function<String, Integer> length = String::length;
        System.out.println("length.apply(\"hi\") = " + length.apply("hi"));

        // --- 4) Constructor reference - StringBuilder::new ---
        Function<String, StringBuilder> wrap = StringBuilder::new;
        StringBuilder wrapped = wrap.apply("created via ::new");
        System.out.println("wrapped             = " + wrapped);

        Supplier<java.util.ArrayList<String>> mkList = java.util.ArrayList::new;
        java.util.ArrayList<String> fresh = mkList.get();
        fresh.add("hello");
        System.out.println("fresh list          = " + fresh);

        // --- 5) Combined with Streams - the real-world payoff ---
        List<String> names = Arrays.asList("Charlie", "Alice", "bob", "Dave");

        names.stream()
             .map(String::toUpperCase)      // arbitrary-object instance method ref
             .sorted()
             .forEach(System.out::println); // particular-object instance method ref

        // Sort by length using a method-reference comparator
        names.stream()
             .sorted(java.util.Comparator.comparingInt(String::length))
             .forEach(s -> System.out.println(s.length() + " - " + s));

        // --- 6) BiFunction with two arguments via static method reference ---
        BiFunction<Integer, Integer, Integer> max = Math::max;
        System.out.println("max(3, 7)           = " + max.apply(3, 7));

        // --- 7) Predicate via method reference ---
        Predicate<String> isEmpty = String::isEmpty;
        System.out.println("isEmpty(\"\")        = " + isEmpty.test(""));
        System.out.println("isEmpty(\"x\")       = " + isEmpty.test("x"));

        // OUTPUT
        // parse.apply("42")   = 42
        // sb after appends    = start + more + extra
        // length.apply("hi") = 2
        // wrapped             = created via ::new
        // fresh list          = [hello]
        // ALICE
        // BOB
        // CHARLIE
        // DAVE
        // 3 - bob
        // 4 - Dave
        // 5 - Alice
        // 7 - Charlie
        // max(3, 7)           = 7
        // isEmpty("")        = true
        // isEmpty("x")       = false
    }
}
