package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Method References (Java 8+) - in Stream Context
 * -----------------------------------------------
 * A METHOD REFERENCE is a compact replacement for a lambda whose body just
 * calls an existing method. The syntax is `Target::method`.
 *
 *      // these two are equivalent
 *      list.stream().map(s -&gt; s.toUpperCase());
 *      list.stream().map(String::toUpperCase);
 *
 *
 * The Four Forms
 * --------------
 *
 *   1. STATIC method                Class::staticMethod
 *
 *         stream.map(Integer::parseInt);
 *         stream.mapToInt(Integer::parseInt);
 *
 *   2. INSTANCE of a specific OBJECT     instance::method
 *
 *         System.out::println              // calls println on System.out
 *
 *         List&lt;String&gt; out = new ArrayList&lt;&gt;();
 *         stream.forEach(out::add);        // calls add on this specific List
 *
 *   3. INSTANCE method of an ARBITRARY object of a type   Class::method
 *
 *         stream.map(String::toUpperCase); // first arg is the receiver
 *         stream.filter(String::isEmpty);
 *         list.sort(String::compareTo);
 *
 *   4. CONSTRUCTOR                  Class::new
 *
 *         stream.toArray(String[]::new);
 *         Supplier&lt;ArrayList&lt;String&gt;&gt; mk = ArrayList::new;
 *         BiFunction&lt;Integer, Integer, int[]&gt; arrays = int[]::new;   // hmm - this is tricky
 *         Function&lt;String, StringBuilder&gt; wrap = StringBuilder::new;
 *
 *
 * Why Prefer Method References?
 * -----------------------------
 *   - More readable than `s -&gt; s.toUpperCase()`.
 *   - Reads almost like English: "stream of names, sorted by length".
 *   - The compiler resolves the SAM target the same way as a lambda.
 *
 *
 * When To Keep The Lambda
 * -----------------------
 *   - The body does more than one method call.
 *   - The receiver/argument order is unusual.
 *   - The method is overloaded and the inference becomes ambiguous.
 *
 *
 * See Also
 * --------
 *   Basics/Methods/MethodReferences.java   - the general-purpose tour.
 *   This file focuses on USING them in Stream pipelines.
 */

public class MethodReferences {

    record Person(String name, int age) {}

    public static void main(String[] args) {

        section("1) STATIC method ref - Class::staticMethod");
        List<Integer> parsed = Arrays.stream(new String[]{"1", "2", "3", "4"})
                .map(Integer::parseInt)              // s -> Integer.parseInt(s)
                .collect(Collectors.toList());
        System.out.println("parsed = " + parsed);

        section("2) INSTANCE of a SPECIFIC object - instance::method");
        // Send each element to System.out's println - that one System.out object.
        Arrays.asList("alpha", "beta", "gamma")
              .forEach(System.out::println);          // s -> System.out.println(s)

        // Same idea - collect into a specific destination
        ArrayList<String> dest = new ArrayList<>();
        Arrays.asList("x", "y", "z").forEach(dest::add);  // s -> dest.add(s)
        System.out.println("dest = " + dest);

        section("3) INSTANCE method of an ARBITRARY object - Class::method");
        List<String> upper = Arrays.asList("alpha", "beta")
                .stream()
                .map(String::toUpperCase)             // s -> s.toUpperCase()
                .collect(Collectors.toList());
        System.out.println("upper = " + upper);

        // The "first arg becomes the receiver" pattern is what lets sorting
        // by a method ref work:
        List<String> words = new ArrayList<>(Arrays.asList("kiwi", "fig", "apple", "date"));
        words.sort(String::compareTo);
        System.out.println("sorted = " + words);

        section("4) CONSTRUCTOR ref - Class::new");
        // Supplier<T>      -> zero-arg constructor
        Supplier<ArrayList<String>> mkList = ArrayList::new;
        ArrayList<String> fresh = mkList.get();
        System.out.println("fresh list class = " + fresh.getClass().getSimpleName());

        // Function<T,R>     -> one-arg constructor
        Function<String, StringBuilder> wrap = StringBuilder::new;
        StringBuilder sb = wrap.apply("seeded");
        System.out.println("sb = " + sb);

        // BiFunction<T,U,R> -> two-arg constructor
        BiFunction<String, Integer, Person> mkPerson = Person::new;
        Person p = mkPerson.apply("Alice", 30);
        System.out.println("p = " + p);

        // Array constructor - special form
        String[] arr = words.stream().toArray(String[]::new);   // n -> new String[n]
        System.out.println("toArray size = " + arr.length);

        section("5) Class::method with Comparator.comparing");
        // Comparator.comparing takes a Function key extractor - method refs are perfect.
        List<Person> people = Arrays.asList(
                new Person("Charlie", 30),
                new Person("Alice",   28),
                new Person("Bob",     34)
        );
        Comparator<Person> byName = Comparator.comparing(Person::name);
        Comparator<Person> byAge  = Comparator.comparingInt(Person::age);

        List<Person> a = new ArrayList<>(people); a.sort(byName);
        List<Person> b = new ArrayList<>(people); b.sort(byAge);
        System.out.println("by name = " + a);
        System.out.println("by age  = " + b);

        section("6) When to KEEP the lambda - multi-step bodies");
        // A method ref would lose clarity here:
        List<String> normalized = Arrays.asList("  alpha", "BETA  ", "Gamma")
                .stream()
                .map(s -> s.strip().toLowerCase())   // two calls - lambda wins
                .collect(Collectors.toList());
        System.out.println("normalized = " + normalized);

        section("7) When overloading makes a method ref ambiguous");
        // String.valueOf is overloaded for char[]/int/long/double/Object - the
        // compiler can infer one based on the Function's target T. It is
        // legal, but if the target type is unclear, fall back to a lambda.
        Function<Integer, String> intStr = String::valueOf;
        System.out.println("valueOf(42) = " + intStr.apply(42));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
