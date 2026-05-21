package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Sequential vs Parallel Streams
 * ------------------------------
 * Every Stream is one of:
 * <p>
 *
 *   SEQUENTIAL - one thread walks the pipeline element by element.
 *                The DEFAULT mode.
 * <p>
 *
 *   PARALLEL   - the source is SPLIT into chunks, each chunk processed on
 *                a worker thread, and the results are MERGED at the end.
 * <p>
 *
 * How to Get Each
 * ---------------
 *      list.stream()                       // sequential
 *      list.parallelStream()               // parallel from the start
 *      anyStream.parallel()                 // toggle on
 *      anyStream.sequential()               // toggle back
 * <p>
 *
 * What Drives the Parallelism
 * ---------------------------
 * Parallel streams run on the SHARED ForkJoinPool.commonPool. By default
 * its size is `Runtime.getRuntime().availableProcessors() - 1`. That means
 * EVERY parallel stream in your JVM shares the same workers. Long-running
 * tasks in one parallel pipeline can starve another.
 * <p>
 *
 * When Parallel Streams Help
 * --------------------------
 *   - Large data sets (millions of elements).
 *   - CPU-bound, INDEPENDENT per-element work (each element's compute is
 *     unrelated to its neighbours).
 *   - The source SPLITS efficiently. ArrayList, arrays, IntStream.range
 *     split well. LinkedList, Stream.iterate(...) do not.
 *   - The terminal op is well-defined for parallel: reduce, collect with
 *     proper collectors. forEach is OK, forEachOrdered is slow.
 * <p>
 *
 * When Parallel HURTS
 * -------------------
 *   - The data set is small.
 *   - The per-element work is cheap.
 *   - The source is hard to split (LinkedList, iterators).
 *   - You need ENCOUNTER ORDER (forEachOrdered serialises everything).
 *   - Your lambdas have SIDE EFFECTS or non-thread-safe state.
 * <p>
 *
 * Side-Effect Trap
 * ----------------
 * Parallel streams running side-effecting code is a data-race waiting to
 * happen. Use proper reductions / collectors instead.
 * <p>
 *
 * Order Differences
 * -----------------
 *   findFirst                preserves encounter order in parallel (slow)
 *   findAny                  parallel-friendly - may return any matching
 *   forEach                  in parallel, no order guarantee
 *   forEachOrdered           preserves encounter order even in parallel
 * <p>
 *
 * Rule of Thumb
 * -------------
 * Use sequential unless you have measured a real speed-up with parallel.
 */

public class SequentialVsParallel {

    public static void main(String[] args) {

        section("1) Same data - sequential and parallel");
        List<Integer> nums = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());

        System.out.print("sequential: ");
        nums.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("parallel  : ");
        nums.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println("   <- order NOT guaranteed");

        System.out.print("forEachOrdered (parallel): ");
        nums.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println("   <- order preserved (slower)");

        section("2) Side-effect TRAP - shared mutable list");
        // BAD - the result list grows in parallel without synchronisation:
        List<Integer> sink = new ArrayList<>();
        IntStream.range(0, 10_000)
                .parallel()
                .forEach(sink::add);                      // race condition
        System.out.println("naive sink size = " + sink.size() + "   (expected 10000)");
        // Sometimes you get 10_000, sometimes 9_998, sometimes an exception.
        // Use a proper collector instead:
        List<Integer> safeSink = IntStream.range(0, 10_000)
                .parallel()
                .boxed()
                .collect(Collectors.toList());
        System.out.println("safe sink size  = " + safeSink.size());

        section("3) Thread-safe counters - AtomicInteger");
        AtomicInteger counter = new AtomicInteger();
        IntStream.range(0, 1_000_000).parallel().forEach(n -> counter.incrementAndGet());
        System.out.println("safe count = " + counter.get());

        section("4) Performance - heavy per-element work benefits from parallel");
        final int N = 200_000;
        // A deliberately CPU-bound function
        java.util.function.IntUnaryOperator heavy = n -> {
            int x = 0;
            for (int i = 0; i < 200; i++) x += (int) Math.sqrt(n + i);
            return x;
        };

        long t = System.nanoTime();
        int sumSeq = IntStream.range(0, N).map(heavy).sum();
        long seqMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        int sumPar = IntStream.range(0, N).parallel().map(heavy).sum();
        long parMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("seq sum = " + sumSeq + " in " + seqMs + " ms");
        System.out.println("par sum = " + sumPar + " in " + parMs + " ms  (cores=" +
                Runtime.getRuntime().availableProcessors() + ")");

        section("5) Performance - cheap work + small data: parallel is SLOWER");
        t = System.nanoTime();
        int s1 = IntStream.range(0, 1_000).map(n -> n + 1).sum();
        long m1 = (System.nanoTime() - t) / 1_000_000;
        t = System.nanoTime();
        int s2 = IntStream.range(0, 1_000).parallel().map(n -> n + 1).sum();
        long m2 = (System.nanoTime() - t) / 1_000_000;
        System.out.println("seq small : " + m1 + " ms");
        System.out.println("par small : " + m2 + " ms  (almost always slower due to overhead)");

        section("6) Splittability matters");
        // LinkedList splits BADLY - no random access.
        java.util.LinkedList<Integer> linked = new java.util.LinkedList<>(IntStream.range(0, 200_000).boxed().toList());
        t = System.nanoTime();
        long c1 = linked.parallelStream().filter(n -> n % 3 == 0).count();
        long lm = (System.nanoTime() - t) / 1_000_000;

        java.util.ArrayList<Integer> array = new java.util.ArrayList<>(linked);
        t = System.nanoTime();
        long c2 = array.parallelStream().filter(n -> n % 3 == 0).count();
        long am = (System.nanoTime() - t) / 1_000_000;

        System.out.println("LinkedList parallel : " + lm + " ms, count=" + c1);
        System.out.println("ArrayList  parallel : " + am + " ms, count=" + c2 + "   (splits much better)");

        section("7) findFirst vs findAny in parallel");
        java.util.Optional<Integer> any = IntStream.range(0, 1_000_000)
                .parallel().boxed()
                .filter(n -> n > 500_000)
                .findAny();
        System.out.println("findAny -> " + any.orElse(-1));
        // findFirst on a parallel stream forces ordering - more work.

        section("8) Bottom line");
        System.out.println("Start sequential. Measure. Switch to parallel only if it's faster.");

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused") private static Stream<?> keep;
}
