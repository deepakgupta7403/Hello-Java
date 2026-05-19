package Basics.LambdaAndStreams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lambda Expressions (Java 8+)
 * ----------------------------
 * A LAMBDA EXPRESSION is a short, anonymous function you can pass around
 * like a value. Lambdas let you write functional-style code WITHOUT writing
 * a class.
 *
 *      Runnable r = () -&gt; System.out.println("hello");      // no params, one statement
 *      Comparator&lt;String&gt; c = (a, b) -&gt; a.length() - b.length();
 *      Function&lt;Integer, Integer&gt; sq = n -&gt; n * n;
 *
 *
 * Syntax Forms
 * ------------
 *      ()           -&gt; expr           // no parameters
 *      x            -&gt; expr           // single param, no parens, no type
 *      (x)          -&gt; expr           // single param with parens
 *      (x, y)       -&gt; expr           // multiple params
 *      (int x, int y) -&gt; expr         // explicit parameter types
 *      x            -&gt; { ...; return v; }   // block body
 *
 *
 * Target Typing
 * -------------
 * A lambda has no intrinsic type. The compiler infers its type from the
 * CONTEXT (the assignment, parameter, or return position):
 *
 *      Runnable      r = () -&gt; ...     // SAM = run
 *      Callable&lt;Integer&gt; c = () -&gt; 42  // SAM = call returning Integer
 *
 * The compiler picks the FUNCTIONAL INTERFACE (single abstract method) that
 * matches.
 *
 *
 * Variable Capture - "Effectively Final"
 * --------------------------------------
 * A lambda can READ local variables from its enclosing scope but ONLY if
 * those variables are EFFECTIVELY FINAL - i.e. never reassigned after the
 * initial assignment.
 *
 *      int n = 5;
 *      Runnable r = () -&gt; System.out.println(n);   // OK
 *      // n = 10;                                   // would make `n` non-final
 *                                                   // and break the lambda
 *
 * Lambdas freely READ and MUTATE fields of the enclosing object.
 *
 *
 * `this` Inside a Lambda
 * ----------------------
 * Unlike an anonymous class, `this` inside a lambda refers to the
 * ENCLOSING instance - NOT the lambda. This was a deliberate change to
 * make lambdas behave like inline code.
 *
 *
 * Lambda vs Anonymous Inner Class
 * -------------------------------
 *   - Lambda is more concise.
 *   - Lambda has no state, no `this` of its own.
 *   - Lambda only works for FUNCTIONAL interfaces (one abstract method).
 *   - Anonymous classes can implement multiple methods, hold fields, etc.
 *
 *
 * The Built-In Functional Interfaces (java.util.function)
 * -------------------------------------------------------
 *      Function&lt;T, R&gt;       R apply(T)
 *      BiFunction&lt;T, U, R&gt;  R apply(T, U)
 *      Predicate&lt;T&gt;         boolean test(T)
 *      Consumer&lt;T&gt;          void accept(T)
 *      Supplier&lt;T&gt;          T get()
 *      UnaryOperator&lt;T&gt;     T apply(T)
 *      BinaryOperator&lt;T&gt;    T apply(T, T)
 *      Runnable             void run()
 *      Callable&lt;V&gt;          V call() throws Exception
 *      Comparator&lt;T&gt;        int compare(T, T)
 *      Plus primitive-specialised variants: IntFunction, ToIntFunction, ...
 *
 *
 * See Also
 * --------
 *   MethodReferences.java         - the `::` shortcut for "lambda that
 *                                   just calls a method"
 *   StreamIntroduction.java       - the place lambdas really shine
 */

public class LambdaExpressions {

    // A field used to demo that lambdas can mutate enclosing instance state.
    private int totalProcessed = 0;

    public static void main(String[] args) {
        new LambdaExpressions().run();
    }

    public void run() {

        section("1) The five most common forms");
        Runnable               r1 = () -> System.out.println("zero-arg lambda");                 // no params
        Consumer<String>       c1 = s -> System.out.println("single: " + s);                     // one param, no parens
        Consumer<String>       c2 = (s) -> System.out.println("single (parens): " + s);
        BiFunction<Integer,Integer,Integer> bf = (a, b) -> a + b;                                 // multi-param
        Function<Integer,Integer> sq = n -> { int r = n * n; return r; };                         // block body

        r1.run();
        c1.accept("hello");
        c2.accept("world");
        System.out.println("bf(3,4) = " + bf.apply(3, 4));
        System.out.println("sq(5)   = " + sq.apply(5));

        section("2) Target typing - same lambda text, different inferred type");
        // Same `() -> 42` body picks up a different functional interface
        // depending on what we assign it to.
        Supplier<Integer>  asSup    = () -> 42;
        java.util.concurrent.Callable<Integer> asCallable = () -> 42;
        System.out.println("Supplier  : " + asSup.get());
        try { System.out.println("Callable : " + asCallable.call()); } catch (Exception ignored) {}

        section("3) Built-in functional interfaces - cheat-sheet");
        Predicate<Integer>      nonNeg     = n -> n >= 0;
        Function<String, Integer> lenOf    = String::length;            // method-ref form (see other file)
        UnaryOperatorDemo(s -> s.toUpperCase());                         // we accept a UnaryOperator below
        BinaryOperator<Integer> sum        = Integer::sum;
        System.out.println("nonNeg(-3) = " + nonNeg.test(-3));
        System.out.println("len(hello) = " + lenOf.apply("hello"));
        System.out.println("sum(2, 3)  = " + sum.apply(2, 3));

        section("4) Variable capture - effectively final");
        int n = 5;
        Runnable greet = () -> System.out.println("captured n = " + n);
        greet.run();
        // n = 6;                  // ERROR if uncommented - would make `n` non-final
        // greet.run();             // and break the lambda

        section("5) `this` inside a lambda = the ENCLOSING object");
        Runnable mut = () -> {
            totalProcessed++;                       // touches the outer field
            System.out.println("inside lambda: this == LambdaExpressions => "
                    + (this instanceof LambdaExpressions));
        };
        mut.run(); mut.run(); mut.run();
        System.out.println("totalProcessed = " + totalProcessed);

        section("6) Lambdas vs anonymous classes");
        // Anonymous class - has its own `this`, can declare fields, etc.
        Runnable anon = new Runnable() {
            int callCount = 0;
            @Override public void run() { callCount++; System.out.println("anon call " + callCount); }
        };
        anon.run(); anon.run();
        // Same job with a lambda - cannot hold callCount as a lambda field;
        // capture an external (effectively final) array if you really must.
        int[] callCount = {0};
        Runnable lambda = () -> { callCount[0]++; System.out.println("lambda call " + callCount[0]); };
        lambda.run(); lambda.run();

        section("7) Lambdas + Comparator - the most common real-world use");
        List<String> names = new java.util.ArrayList<>(Arrays.asList("Charlie", "Alice", "Bob"));
        names.sort((a, b) -> a.compareTo(b));
        System.out.println("alpha order = " + names);
        names.sort(Comparator.comparingInt(String::length));
        System.out.println("by length   = " + names);

        section("8) Lambdas in collection callbacks");
        Arrays.asList(1, 2, 3).forEach(x -> System.out.println("  forEach -> " + x));

        // OUTPUT (representative)
    }

    /** Accepts a UnaryOperator<String> shown via lambda. */
    private static void UnaryOperatorDemo(java.util.function.UnaryOperator<String> op) {
        System.out.println("UnaryOperator(java) = " + op.apply("java"));
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused")
    private static UnaryOperatorJustToKeepImport keepImport;
    private static class UnaryOperatorJustToKeepImport {}
}
