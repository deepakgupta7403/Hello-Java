package Basics.LambdaAndStreams;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Creation of Streams - 10+ Ways
 * ------------------------------
 * There is no single "Stream constructor". You build a Stream from a
 * source - and Java offers many sources to choose from.
 *
 *
 * The Common Routes
 * -----------------
 *   1. From a Collection             list.stream() / set.parallelStream()
 *   2. From an Array                 Arrays.stream(arr)
 *   3. Fixed values                  Stream.of("a", "b", "c")
 *   4. Empty stream                  Stream.empty()
 *   5. Builder                       Stream.&lt;String&gt;builder().add(...).build()
 *   6. Iterate                       Stream.iterate(seed, n -&gt; n + 1).limit(...)
 *   7. Generate                      Stream.generate(Math::random)
 *   8. Iterate with predicate        Stream.iterate(seed, hasNext, next)   (Java 9+)
 *   9. Concat two streams            Stream.concat(s1, s2)
 *  10. From a String split           Pattern.compile(",").splitAsStream("a,b,c")
 *  11. From an Iterable / Iterator   StreamSupport.stream(itble.spliterator(), false)
 *  12. From a single nullable value  Stream.ofNullable(x)                   (Java 9+)
 *  13. From a file                   Files.lines(path)                      (see StreamFileIO)
 *  14. Primitive ranges              IntStream.range / rangeClosed / iterate
 *
 *
 * Stream.iterate - Two Forms
 * --------------------------
 *      Stream.iterate(seed, UnaryOperator)            // INFINITE - needs .limit
 *      Stream.iterate(seed, Predicate, UnaryOperator) // FINITE   (Java 9+)
 *
 *
 * Sequential or Parallel?
 * -----------------------
 *      list.stream()               // sequential
 *      list.parallelStream()       // parallel
 *      anyStream.parallel()        // toggle to parallel
 *      anyStream.sequential()      // toggle back
 *
 * See SequentialVsParallel.java for the deep dive.
 */

public class StreamCreation {

    public static void main(String[] args) {

        section("1) From a Collection");
        List<String> names = List.of("Alice", "Bob", "Carol");
        Stream<String> s1 = names.stream();
        System.out.println("count = " + s1.count());

        section("2) From an Array");
        String[] arr = {"a", "b", "c"};
        Stream<String> s2 = Arrays.stream(arr);
        System.out.println("count = " + s2.count());

        // Primitive array versions use IntStream / DoubleStream / LongStream
        int[] ints = {10, 20, 30, 40};
        int total = Arrays.stream(ints).sum();
        System.out.println("int sum = " + total);

        section("3) Fixed values - Stream.of");
        Stream<Integer> s3 = Stream.of(1, 2, 3, 4, 5);
        System.out.println("max = " + s3.max(Integer::compare).orElse(-1));

        section("4) Empty stream");
        Stream<String> none = Stream.empty();
        System.out.println("empty count = " + none.count());

        section("5) Builder - add a few then build");
        Stream<String> built = Stream.<String>builder()
                .add("one")
                .add("two")
                .add("three")
                .build();
        built.forEach(s -> System.out.print(s + " "));
        System.out.println();

        section("6) Stream.iterate INFINITE - need .limit");
        Stream.iterate(1, n -> n + 2)         // 1, 3, 5, 7, ...
              .limit(5)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("7) Stream.iterate FINITE - Java 9+");
        Stream.iterate(1, n -> n < 100, n -> n * 2)   // 1, 2, 4, ..., 64
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("8) Stream.generate - lazy supplier");
        Stream.generate(new Random()::nextInt)        // potentially infinite
              .limit(5)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("9) Stream.concat - join two streams");
        Stream<Integer> a = Stream.of(1, 2, 3);
        Stream<Integer> b = Stream.of(4, 5, 6);
        Stream.concat(a, b).forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("10) From a String split - Pattern.splitAsStream");
        Pattern.compile(",")
               .splitAsStream("alpha,beta,gamma,,delta")
               .forEach(s -> System.out.println("  '" + s + "'"));

        section("11) From an Iterable / Iterator via StreamSupport");
        Iterable<Integer> it = List.of(10, 20, 30);
        Stream<Integer> fromIterable = StreamSupport.stream(it.spliterator(), false);
        System.out.println("from Iterable max = " + fromIterable.max(Integer::compare).orElse(-1));

        // Build a Stream from a one-shot Iterator
        java.util.Iterator<Integer> raw = List.of(100, 200, 300).iterator();
        Stream<Integer> fromIter = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(raw, Spliterator.ORDERED),
                false);
        System.out.println("from Iterator count = " + fromIter.count());

        section("12) Stream.ofNullable - one or zero elements - Java 9+");
        Stream.ofNullable("hello").forEach(s -> System.out.println("  got: " + s));
        Stream.ofNullable(null).forEach(s -> System.out.println("  got: " + s));   // no output
        System.out.println("ofNullable(null).count() = " + Stream.ofNullable(null).count());

        section("13) Map gives you THREE streams - keys / values / entries");
        Map<String, Integer> ages = Map.of("alice", 30, "bob", 25);
        ages.keySet().stream().forEach(k -> System.out.println("  key: " + k));
        ages.values().stream().forEach(v -> System.out.println("  val: " + v));
        ages.entrySet().stream().forEach(e -> System.out.println("  " + e));

        section("14) Primitive Streams - ranges");
        IntStream.range(0, 5).forEach(n -> System.out.print(n + " "));         // 0,1,2,3,4
        System.out.println();
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));    // 1,2,3,4,5
        System.out.println();
        long countOfTens = IntStream.range(0, 1000).filter(n -> n % 10 == 0).count();
        System.out.println("multiples of 10 in 0..999 = " + countOfTens);

        DoubleStream.of(1.5, 2.5, 3.5).average().ifPresent(v -> System.out.println("avg = " + v));

        section("15) Optional as a degenerate 0-or-1 stream");
        Optional<String> opt = Optional.of("present");
        opt.stream().forEach(s -> System.out.println("  optional stream: " + s));

        // OUTPUT (representative; sample of random values varies)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
