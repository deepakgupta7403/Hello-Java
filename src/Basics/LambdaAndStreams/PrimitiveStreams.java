package Basics.LambdaAndStreams;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Primitive Streams - IntStream, LongStream, DoubleStream
 * -------------------------------------------------------
 * Java has specialised streams for the three "numeric" primitives so you
 * can crunch numbers WITHOUT boxing every element into Integer/Long/Double.
 * That saves both memory and time.
 *
 *
 * Why They Exist
 * --------------
 *   - Stream&lt;Integer&gt; boxes each int into an Integer (heap allocation).
 *     A million-element stream allocates a million Integer objects.
 *   - IntStream stores the primitives directly. No boxing.
 *
 *
 * Specialised Methods
 * -------------------
 *   sum()                                int / long / double
 *   average() / min() / max()            return OptionalInt / OptionalLong / OptionalDouble
 *   summaryStatistics()                  IntSummaryStatistics (count + min + max + sum + avg)
 *   forEach(IntConsumer)                 primitive-friendly callback
 *   mapToInt / mapToLong / mapToDouble   from a Stream&lt;T&gt; to a primitive stream
 *   mapToObj                             primitive stream -&gt; Stream&lt;T&gt;
 *   boxed()                              IntStream -&gt; Stream&lt;Integer&gt;
 *   range(start, endExclusive)           [start, end)
 *   rangeClosed(start, end)              [start, end]
 *
 *
 * Conversions in a Picture
 * ------------------------
 *
 *      Stream&lt;Integer&gt;  ==  mapToInt   ==&gt;  IntStream
 *      IntStream         ==  boxed       ==&gt;  Stream&lt;Integer&gt;
 *      IntStream         ==  mapToObj    ==&gt;  Stream&lt;R&gt;
 *      IntStream         ==  asLongStream ==&gt;  LongStream
 *      IntStream         ==  asDoubleStream ==&gt;  DoubleStream
 *
 *
 * Tip
 * ---
 * Always use mapToInt/Long/Double when you intend to call sum/average/etc.
 * Stream.&lt;Integer&gt;reduce(Integer::sum) works but allocates Integer objects
 * every step.
 */

public class PrimitiveStreams {

    public static void main(String[] args) {

        section("1) IntStream basics");
        // 0..9 with forEach
        IntStream.range(0, 10).forEach(n -> System.out.print(n + " "));
        System.out.println();
        // 1..10 closed
        IntStream.rangeClosed(1, 10).forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("2) Sum / min / max / average");
        IntStream s1 = IntStream.of(3, 1, 4, 1, 5, 9, 2, 6);
        System.out.println("sum  = " + IntStream.of(3, 1, 4, 1, 5, 9, 2, 6).sum());
        System.out.println("min  = " + IntStream.of(3, 1, 4, 1, 5, 9, 2, 6).min().getAsInt());
        System.out.println("max  = " + IntStream.of(3, 1, 4, 1, 5, 9, 2, 6).max().getAsInt());
        System.out.println("avg  = " + IntStream.of(3, 1, 4, 1, 5, 9, 2, 6).average().orElse(0));
        s1.close();   // not strictly needed - streams are lazy and one-shot

        section("3) summaryStatistics - count/sum/min/max/avg in ONE pass");
        IntSummaryStatistics stats = IntStream.of(3, 1, 4, 1, 5, 9, 2, 6).summaryStatistics();
        System.out.println("stats = " + stats);

        section("4) From Stream<T> to primitive: mapToInt / mapToLong / mapToDouble");
        List<String> words = List.of("alpha", "beta", "gamma");
        int totalLen = words.stream().mapToInt(String::length).sum();
        System.out.println("totalLen = " + totalLen);

        section("5) From primitive to Stream<T>: mapToObj");
        Stream<String> labels = IntStream.rangeClosed(1, 5).mapToObj(n -> "item-" + n);
        labels.forEach(s -> System.out.print(s + "  "));
        System.out.println();

        section("6) Boxing / unboxing between primitive and boxed streams");
        Stream<Integer> boxed = IntStream.range(1, 5).boxed();
        List<Integer> list = boxed.collect(Collectors.toList());
        System.out.println("list = " + list);

        // Other direction - Stream<Integer> back to IntStream
        int sum2 = list.stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum2 = " + sum2);

        section("7) LongStream + DoubleStream - same shape, wider types");
        long factorial10 = LongStream.rangeClosed(1, 10).reduce(1L, (a, b) -> a * b);
        System.out.println("10! = " + factorial10);

        DoubleStream.of(1.5, 2.5, 3.5)
                    .average()
                    .ifPresent(a -> System.out.println("avg = " + a));

        section("8) Performance teaser - boxing vs primitive");
        final int N = 5_000_000;
        long t = System.nanoTime();
        long s = Stream.iterate(1, n -> n + 1).limit(N).mapToInt(Integer::intValue).sum();
        long boxedMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        long s2x = IntStream.rangeClosed(1, N).sum();
        long primMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("Stream<Integer> sum : " + boxedMs + " ms");
        System.out.println("IntStream      sum : " + primMs + " ms");

        section("9) toArray on a primitive stream gives an int[] / long[] / double[]");
        int[] sqs = IntStream.range(1, 6).map(n -> n * n).toArray();
        System.out.println("sqs = " + Arrays.toString(sqs));

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
