package Basics.LambdaAndStreams;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream Pipeline - Source / Intermediate / Terminal
 * --------------------------------------------------
 * A STREAM PIPELINE is the chain that takes data from a source through a
 * series of transformations to a final result. It always has THREE parts:
 *
 *      +---------+    +---------------------------+    +-------------+
 *      | SOURCE  | -&gt; | Intermediate Op (lazy)... | -&gt; | Terminal Op |
 *      +---------+    +---------------------------+    +-------------+
 *      List/Array      filter, map, sorted,            forEach, collect,
 *      Stream.of(...)  distinct, skip, limit,          reduce, count,
 *      Files.lines     peek, flatMap, ...              anyMatch, findFirst
 *
 *
 * Rules
 * -----
 *   - Exactly ONE source.
 *   - ZERO OR MORE intermediate operations - they are LAZY and return new streams.
 *   - EXACTLY ONE terminal operation - it produces a side effect or a value.
 *   - Once the terminal operation has run, the stream is CLOSED.
 *
 *
 * Lazy Evaluation
 * ---------------
 * Intermediate operations DO NOTHING until a terminal operation pulls. The
 * pipeline is then FUSED: each element flows through every step before the
 * next one starts (not "filter all then map all then collect all").
 *
 *      sourceList.stream()
 *                .filter(...)
 *                .map(...)
 *                .collect(...);     // only HERE does anything actually run
 *
 *
 * Short-Circuit Operations
 * ------------------------
 * Some terminal operations can stop early as soon as they have the answer:
 *
 *      findFirst, findAny
 *      anyMatch, allMatch, noneMatch
 *      limit (intermediate but short-circuit)
 *
 * This is what makes infinite streams usable.
 *
 *
 * Statefulness
 * ------------
 *   - STATELESS intermediates - filter, map, peek, flatMap
 *     They can run independently per element.
 *
 *   - STATEFUL intermediates - sorted, distinct, limit, skip
 *     They must see other elements to decide. They cost more, especially in
 *     parallel.
 *
 *
 * Reading the Pipeline
 * --------------------
 * Read top-down like a recipe:
 *
 *      "Start with the people list.
 *       Keep only the engineers.
 *       Get each person's salary.
 *       Sum them up."
 *
 *      people.stream()
 *            .filter(p -&gt; p.role() == Role.ENGINEER)
 *            .mapToDouble(Person::salary)
 *            .sum();
 */

public class StreamPipeline {

    record Person(String name, String role, double salary) {}

    public static void main(String[] args) {

        section("1) The three pieces, labelled");
        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        int sumSqEven = nums.stream()                                // <-- SOURCE
                .filter(n -> n % 2 == 0)                             // <-- INTERMEDIATE
                .map(n -> n * n)                                     // <-- INTERMEDIATE
                .reduce(0, Integer::sum);                            // <-- TERMINAL
        System.out.println("sum of squares of evens = " + sumSqEven);

        section("2) Pipeline is LAZY - peek illustrates the per-element flow");
        long count = Stream.of("alpha", "beta", "gamma", "delta")
                .peek(s -> System.out.println("  peek source : " + s))
                .filter(s -> {
                    System.out.println("    filter check : " + s);
                    return s.startsWith("a") || s.startsWith("b");
                })
                .peek(s -> System.out.println("    peek passed : " + s))
                .count();
        System.out.println("count = " + count);
        // Notice each item flows through filter+peek BEFORE the next item
        // even starts at the source.

        section("3) Short-circuiting - findFirst stops as soon as it finds one");
        Stream.of(1, 2, 3, 4, 5, 6)
              .peek(n -> System.out.println("  visiting " + n))
              .filter(n -> n > 3)
              .findFirst()
              .ifPresent(n -> System.out.println("found = " + n));
        // The stream stops after the first match (4) - it never visits 5/6.

        section("4) Building up to a useful query");
        List<Person> staff = List.of(
                new Person("Alice", "ENG",   90_000),
                new Person("Bob",   "ENG",   85_000),
                new Person("Carol", "PM",   110_000),
                new Person("Dave",  "ENG",   95_000),
                new Person("Eve",   "PM",   105_000)
        );
        double totalEngSalary = staff.stream()
                .filter(p -> p.role().equals("ENG"))
                .mapToDouble(Person::salary)
                .sum();
        System.out.println("total engineer salary = " + totalEngSalary);

        List<String> sortedNames = staff.stream()
                .map(Person::name)
                .sorted()
                .collect(Collectors.toList());
        System.out.println("sorted names = " + sortedNames);

        section("5) Terminals 'close' the stream");
        Stream<Integer> once = Stream.of(1, 2, 3);
        System.out.println("count = " + once.count());
        try {
            once.count();                            // boom
        } catch (IllegalStateException e) {
            System.out.println("re-use error: " + e.getMessage());
        }

        section("6) Stateless vs Stateful intermediates");
        // sorted() / distinct() / skip() / limit() must hold elements internally.
        // For parallel streams they often degrade performance.
        long distinctEvens = Stream.of(1, 2, 2, 3, 3, 3, 4, 4, 5)
                .distinct()                       // stateful
                .filter(n -> n % 2 == 0)
                .count();
        System.out.println("distinctEvens = " + distinctEvens);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
