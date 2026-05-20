package Phase3_ObjectOrientation.Interfaces;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Functional Interfaces (Java 8+)
 * -------------------------------
 * A FUNCTIONAL INTERFACE is an interface with EXACTLY ONE ABSTRACT METHOD
 * (SAM = Single Abstract Method). Such interfaces can be the TARGET TYPE of
 * lambda expressions and method references.
 *
 *      @FunctionalInterface
 *      interface Calculator {
 *          int apply(int a, int b);
 *      }
 *
 *      Calculator add = (a, b) -> a + b;     // lambda
 *      Calculator max = Math::max;           // method reference
 *
 *
 * The @FunctionalInterface Annotation
 * -----------------------------------
 * Optional but recommended. It tells the compiler to ERROR-CHECK that the
 * interface has exactly one abstract method. Adding a second abstract method
 * later won't silently break callers - the compiler will refuse to compile
 * the annotation.
 *
 *
 * What Does NOT Count Toward the "one abstract method" Limit
 * ----------------------------------------------------------
 *   - default methods         (have a body)
 *   - static methods          (have a body)
 *   - private methods         (have a body, Java 9+)
 *   - methods inherited from java.lang.Object (toString, equals, hashCode ...)
 *
 *
 * The java.util.function Package - Quick Cheatsheet
 * -------------------------------------------------
 *   Function<T,R>        : R apply(T t)               x -> ...
 *   BiFunction<T,U,R>    : R apply(T t, U u)          (x,y) -> ...
 *   UnaryOperator<T>     : T apply(T t)               same in / out type
 *   BinaryOperator<T>    : T apply(T a, T b)
 *   Predicate<T>         : boolean test(T t)
 *   Consumer<T>          : void accept(T t)
 *   BiConsumer<T,U>      : void accept(T t, U u)
 *   Supplier<T>          : T get()
 *
 *   Specialised for primitives to avoid boxing:
 *      IntFunction<R>, ToIntFunction<T>, IntPredicate, IntUnaryOperator, ...
 *
 *
 * Common Use Cases
 * ----------------
 *   - Stream operations: filter / map / reduce / forEach
 *   - Comparators        Comparator.comparing(Person::age)
 *   - Strategy pattern    pass behaviour as an argument
 *   - Callbacks           event handlers, listeners
 *   - Builders / fluent APIs
 *
 *
 * Lambda Capture Rules (brief recap)
 * ----------------------------------
 *   - Lambdas can READ local variables and parameters, but they must be
 *     EFFECTIVELY FINAL (assigned once).
 *   - Lambdas freely read/write fields of the enclosing object via `this`.
 *   - `this` inside a lambda refers to the ENCLOSING class - not the lambda.
 *
 *
 * For default / static / private interface members see InterfaceIntro.java
 * and Basics/Methods/InterfaceMethods.java.
 */

public class FunctionalInterfaceDemo {

    // ============================================================
    // 1) A custom @FunctionalInterface used with a lambda
    // ============================================================
    @FunctionalInterface
    interface Calculator {
        int apply(int a, int b);

        // default + static helpers are OK and do NOT break the SAM rule
        default Calculator andThen(Calculator next) {
            return (a, b) -> next.apply(this.apply(a, b), b);
        }
        static Calculator identityLhs() { return (a, b) -> a; }
    }

    public static void main(String[] args) {

        section("Custom @FunctionalInterface + lambdas");
        Calculator add = (a, b) -> a + b;
        Calculator mul = (a, b) -> a * b;
        Calculator max = Math::max;                       // method reference
        System.out.println("add(2,3) = " + add.apply(2, 3));   // 5
        System.out.println("mul(2,3) = " + mul.apply(2, 3));   // 6
        System.out.println("max(2,3) = " + max.apply(2, 3));   // 3

        // Default helper in action - "add then multiply"
        Calculator addThenMul = add.andThen(mul);
        System.out.println("addThenMul(2,3) = " + addThenMul.apply(2, 3));   // (2+3)*3 = 15

        section("java.util.function - the standard SAM types");

        // Function<T,R> - one in, one out
        Function<String, Integer> length = String::length;
        System.out.println("length(\"hello\")    = " + length.apply("hello"));

        // BiFunction<T,U,R> - two in, one out
        BiFunction<Integer, Integer, Integer> sum = Integer::sum;
        System.out.println("sum(2, 3)            = " + sum.apply(2, 3));

        // Predicate<T> - boolean test
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        System.out.println("nonEmpty(\"\")        = " + nonEmpty.test(""));
        System.out.println("nonEmpty(\"hi\")      = " + nonEmpty.test("hi"));

        // Predicate composition - and/or/negate
        Predicate<String> shortStr  = s -> s.length() < 5;
        Predicate<String> shortNonE = nonEmpty.and(shortStr);
        System.out.println("shortNonE(\"hi\")     = " + shortNonE.test("hi"));

        // Consumer<T> - side effect, returns nothing
        Consumer<String> print = System.out::println;
        print.accept("printed via Consumer");

        // Supplier<T> - no input, one output (typically lazy)
        Supplier<String> now = () -> java.time.LocalTime.now().toString();
        System.out.println("now()                = " + now.get());

        // UnaryOperator<T> / BinaryOperator<T>
        UnaryOperator<String>  upper = String::toUpperCase;
        BinaryOperator<Integer> mlt  = (a, b) -> a * b;
        System.out.println("upper(\"java\")       = " + upper.apply("java"));
        System.out.println("mlt(3, 7)            = " + mlt.apply(3, 7));

        section("Function composition - andThen / compose");
        Function<Integer, Integer> times2    = n -> n * 2;
        Function<Integer, Integer> plus3     = n -> n + 3;
        // andThen: do `times2` then `plus3`              -> (x*2) + 3
        // compose:  do `plus3` first, then `times2`      -> (x+3) * 2
        System.out.println("times2.andThen(plus3)(5)  = " + times2.andThen(plus3).apply(5));   // 13
        System.out.println("times2.compose(plus3)(5)  = " + times2.compose(plus3).apply(5));   // 16

        section("Comparator - the everyday functional interface");
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "Dave");
        names.sort(Comparator.naturalOrder());
        System.out.println("naturalOrder = " + names);
        names.sort(Comparator.comparingInt(String::length));
        System.out.println("by length    = " + names);
        names.sort(Comparator.comparingInt(String::length).reversed());
        System.out.println("by length d  = " + names);

        section("Functional interfaces with Streams");
        String joined = List.of("alpha", "beta", "gamma", "delta")
                .stream()
                .filter(s -> s.length() > 4)            // Predicate<String>
                .map(String::toUpperCase)               // Function<String,String>
                .sorted()                               // Comparator<String> (natural)
                .collect(Collectors.joining(", "));
        System.out.println("joined = " + joined);

        section("Lambda capture - effectively final");
        int prefix = 100;
        Function<Integer, Integer> addPrefix = n -> prefix + n;
        // prefix = 200;   // ERROR if uncommented - capture must be effectively final
        System.out.println("addPrefix(5) = " + addPrefix.apply(5));

        // OUTPUT
        // (matches the inline comments above)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
