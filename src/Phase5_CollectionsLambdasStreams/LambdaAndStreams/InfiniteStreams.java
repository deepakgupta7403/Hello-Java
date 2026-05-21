package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Infinite Streams
 * ----------------
 * A stream is INFINITE when its source can produce values forever. The
 * stream itself is fine; the program crashes only if you forget to bound
 * it with a short-circuit operation.
 * <p>
 *
 * Two Ways To Build One
 * ---------------------
 * <p>
 *
 *   Stream.iterate(seed, UnaryOperator)
 *      Produces: seed, op(seed), op(op(seed)), ...
 *      Example : Stream.iterate(1, n -&gt; n * 2)
 * <p>
 *
 *   Stream.generate(Supplier)
 *      Produces: supplier.get(), supplier.get(), ...
 *      Example : Stream.generate(Math::random)
 * <p>
 *
 * Bounding an Infinite Stream
 * ---------------------------
 * The short-circuit intermediate / terminal operations are:
 * <p>
 *
 *      limit(n)                    keep the first n
 *      takeWhile(predicate)        keep until the predicate fails    (Java 9+)
 *      anyMatch / allMatch /       short-circuit terminals
 *        noneMatch
 *      findFirst / findAny
 * <p>
 *
 * Forget all of these and the JVM will run until you ctrl-C it.
 * <p>
 *
 * Finite Version of iterate (Java 9+)
 * -----------------------------------
 *      Stream.iterate(seed, hasNext, next)
 * <p>
 *
 *      Example: Stream.iterate(1, n -&gt; n &lt; 100, n -&gt; n * 2)
 *               -&gt; 1, 2, 4, 8, ..., 64
 * <p>
 *
 * The middle argument is a PREDICATE applied to each element. When it
 * returns false the stream ends - no `.limit` needed.
 * <p>
 *
 * Order Matters
 * -------------
 *   Stream.iterate(0, n -&gt; n + 1)
 *         .filter(n -&gt; n % 7 == 0)
 *         .limit(5)                  // OK - limit short-circuits filter
 * <p>
 *
 *   Stream.iterate(0, n -&gt; n + 1)
 *         .limit(100)                // OK - finite stream
 *         .filter(n -&gt; n % 7 == 0)   // also fine
 * <p>
 *
 *   Stream.iterate(0, n -&gt; n + 1)
 *         .filter(n -&gt; false)
 *         .findFirst();              // BAD - never finds anything, never stops
 */

public class InfiniteStreams {

    public static void main(String[] args) {

        section("1) Stream.iterate + limit");
        Stream.iterate(1, n -> n * 2)        // 1, 2, 4, 8, ...
              .limit(8)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("2) Stream.generate + limit - random ids");
        Stream.generate(() -> UUID.randomUUID().toString())
              .limit(3)
              .forEach(System.out::println);

        section("3) IntStream.iterate (primitives) and rangeClosed");
        IntStream.iterate(1, n -> n + 2)
                 .limit(5)
                 .forEach(n -> System.out.print(n + " "));        // 1 3 5 7 9
        System.out.println();

        IntStream.rangeClosed(1, 10).forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("4) Three-arg iterate (Java 9+) - FINITE, no limit needed");
        Stream.iterate(1, n -> n < 100, n -> n * 2)
              .forEach(n -> System.out.print(n + " "));            // 1 2 4 8 16 32 64
        System.out.println();

        section("5) takeWhile - stop at the first failing element");
        Stream.iterate(1, n -> n + 1)
              .takeWhile(n -> n * n < 100)
              .forEach(n -> System.out.print(n + " "));            // 1..9
        System.out.println();

        section("6) Combining infinite + short-circuit terminal");
        int firstPrimeOver100 = IntStream.iterate(2, n -> n + 1)
                .filter(InfiniteStreams::isPrime)
                .filter(p -> p > 100)
                .findFirst()
                .orElse(-1);
        System.out.println("first prime > 100 = " + firstPrimeOver100);

        section("7) Pitfall - filter that NEVER matches on an infinite stream");
        // Stream.iterate(0, n -> n + 1).filter(n -> n < 0).findFirst()
        //     <- would loop forever. We don't run it; just illustrate.
        System.out.println("(commented to avoid infinite loop)");

        section("8) generate with a counter via AtomicInteger");
        AtomicInteger c = new AtomicInteger();
        Stream.generate(c::incrementAndGet)
              .limit(5)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();

        section("9) Random stream - bounded ints");
        new Random(42).ints(5, 0, 100)             // (count, fromInclusive, toExclusive)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        // OUTPUT (representative)
    }

    /** Tiny primality test - just enough for the demo. */
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
