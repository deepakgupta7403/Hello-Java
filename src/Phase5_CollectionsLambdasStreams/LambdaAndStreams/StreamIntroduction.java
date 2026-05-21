package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Java Streams - Introduction
 * ---------------------------
 * A STREAM is a sequence of values from a source that supports a chain of
 * functional-style operations. Streams are NOT data structures: they
 * compute on data from a source (a Collection, array, file, generator, ...)
 * and emit results.
 * <p>
 *
 *      List&lt;Integer&gt; nums = List.of(1, 2, 3, 4, 5);
 * <p>
 *
 *      int sumOfSquaresOfEvens = nums.stream()
 *                                   .filter(n -&gt; n % 2 == 0)
 *                                   .mapToInt(n -&gt; n * n)
 *                                   .sum();
 * <p>
 *
 * That one expression DECLARES the result; the JVM figures out HOW to
 * compute it.
 * <p>
 *
 * Stream Properties
 * -----------------
 *   - LAZY            Intermediate operations do nothing until a terminal
 *                     operation runs. The pipeline is fused and consumed
 *                     in one pass.
 * <p>
 *
 *   - ONE-SHOT        Once a stream's terminal operation has run, the
 *                     stream is consumed - you cannot iterate it again.
 * <p>
 *
 *   - DECLARATIVE     "What" not "how" - the JVM gets to optimise.
 * <p>
 *
 *   - POSSIBLY        Streams can have an ENCOUNTER ORDER (e.g. List) or be
 *     ORDERED         unordered (e.g. HashSet). Order affects parallelism
 *                     behaviour and methods like findFirst vs findAny.
 * <p>
 *
 *   - INDEPENDENT     A Stream does not modify its source. Filtering does
 *                     not remove from the underlying List.
 * <p>
 *
 * Streams vs Loops
 * ----------------
 *      int total = 0;
 *      for (int n : nums) if (n % 2 == 0) total += n * n;     // imperative
 * <p>
 *
 *      int total = nums.stream()
 *                      .filter(n -&gt; n % 2 == 0)
 *                      .mapToInt(n -&gt; n * n)
 *                      .sum();                                 // declarative
 * <p>
 *
 * Streams shine for COMPOSITIONS (filter + map + reduce). Plain loops still
 * win for very simple work or when you need fine-grained control.
 * <p>
 *
 * The Big Picture - The Files in This Folder
 * ------------------------------------------
 *   LambdaExpressions.java       - the function syntax streams rely on
 *   MethodReferences.java        - `::` shortcut for "lambda that calls a method"
 *   StreamIntroduction.java      (this file)
 *   StreamCreation.java          - 8+ ways to build a stream
 *   StreamPipeline.java          - Source / Intermediate / Terminal
 *   IntermediateOperations.java  - filter / map / sorted / distinct / ...
 *   TerminalOperations.java      - forEach / collect / reduce / count / ...
 *   CollectorsClass.java         - Collectors.toList / groupingBy / ...
 *   SequentialVsParallel.java    - parallel streams, ordering, pitfalls
 *   InfiniteStreams.java         - generate / iterate / limit / takeWhile
 *   PrimitiveStreams.java        - IntStream / LongStream / DoubleStream
 *   StreamVsCollection.java      - when to use which
 *   StreamFileIO.java            - Files.lines, Files.list, etc.
 *   ModernStreams.java           - Java 9 -&gt; 21 additions
 *   Examples/                    - real-world worked projects
 */

public class StreamIntroduction {

    public static void main(String[] args) {

        section("1) Five-line stream pipeline");
        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        int sumSqEven = nums.stream()
                            .filter(n -> n % 2 == 0)
                            .mapToInt(n -> n * n)
                            .sum();
        System.out.println("sum of squares of evens = " + sumSqEven);

        section("2) Streams are LAZY - peek shows it");
        long count = Stream.of("a", "b", "c", "d", "e")
                .peek(s -> System.out.println("  source emits: " + s))
                .filter(s -> {
                    System.out.println("    filter sees: " + s);
                    return s.compareTo("c") > 0;
                })
                .peek(s -> System.out.println("    passed filter: " + s))
                .count();
        System.out.println("count = " + count);
        // Notice each element flows ALL THE WAY through the pipeline before
        // the next one starts - "fused" not "batched".

        section("3) Streams are ONE-SHOT");
        Stream<Integer> once = Stream.of(1, 2, 3);
        once.forEach(n -> System.out.print(n + " "));
        System.out.println();
        try {
            once.forEach(System.out::println);          // boom
        } catch (IllegalStateException e) {
            System.out.println("re-using a stream -> IllegalStateException: " + e.getMessage());
        }

        section("4) Streams do NOT mutate the source");
        List<Integer> original = new java.util.ArrayList<>(List.of(1, 2, 3));
        List<Integer> filtered = original.stream()
                                         .filter(n -> n > 1)
                                         .collect(Collectors.toList());
        System.out.println("original (unchanged) = " + original);
        System.out.println("filtered            = " + filtered);

        section("5) Three sources side-by-side");
        long fromCollection = List.of(1, 2, 3).stream().count();
        long fromArray      = Arrays.stream(new int[]{10, 20, 30}).count();
        long fromStreamOf   = Stream.of("a", "b", "c", "d").count();
        System.out.println("collection count = " + fromCollection);
        System.out.println("array      count = " + fromArray);
        System.out.println("of(...)    count = " + fromStreamOf);

        section("6) Infinite streams are fine - until you forget to limit");
        IntStream.iterate(1, n -> n + 1)               // 1, 2, 3, ...
                 .limit(5)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("7) The cost - allocation + indirection - prefer loops for hot tight inner work");
        // Streams add a small per-pipeline overhead. For most code that is
        // negligible. For a million inner-loop iterations of trivial math,
        // a plain for loop may still be faster.

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
