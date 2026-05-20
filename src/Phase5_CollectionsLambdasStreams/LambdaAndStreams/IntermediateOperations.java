package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Intermediate Operations - The Lazy Transformations
 * --------------------------------------------------
 * Intermediate operations RETURN A NEW STREAM and do not run until a
 * terminal operation pulls them. Most are STATELESS - they process each
 * element independently. A few are STATEFUL and must see multiple elements
 * to decide.
 *
 *
 * Stateless
 * ---------
 *      filter(Predicate)       keep only elements matching the predicate
 *      map(Function)           transform T -&gt; R
 *      mapToInt / mapToLong / mapToDouble
 *      flatMap(Function)       T -&gt; Stream&lt;R&gt;, flatten one level
 *      peek(Consumer)          side effect on each element (debug)
 *
 *
 * Stateful
 * --------
 *      sorted()                natural order (T must be Comparable)
 *      sorted(Comparator)      custom order
 *      distinct()              de-dup using equals
 *      limit(long n)           keep first n
 *      skip(long n)            drop first n
 *      takeWhile(Predicate)    take while the predicate holds   (Java 9+)
 *      dropWhile(Predicate)    drop while the predicate holds   (Java 9+)
 *
 *
 * mapMulti (Java 16+) - 1 element to MANY without an intermediate Stream
 * ---------------------------------------------------------------------
 *      stream.mapMulti((t, downstream) -&gt; {
 *          if (cond) downstream.accept(t);
 *          downstream.accept(other);
 *      });
 *
 * It is faster than flatMap when each element produces only a small,
 * computable set of results.
 *
 *
 * One element per row - the table
 * -------------------------------
 *      OP            TYPE       STATEFUL?    SHORT-CIRCUIT?
 *      filter        T -&gt; T     no           no
 *      map           T -&gt; R     no           no
 *      flatMap       T -&gt; R*    no           no
 *      peek          T -&gt; T     no           no
 *      sorted        T -&gt; T     YES          no
 *      distinct      T -&gt; T     YES          no
 *      limit         T -&gt; T     YES          YES
 *      skip          T -&gt; T     YES          no
 *      takeWhile     T -&gt; T     YES          YES   (J9+)
 *      dropWhile     T -&gt; T     YES          no    (J9+)
 *      mapMulti      T -&gt; R*    no           no    (J16+)
 */

public class IntermediateOperations {

    public static void main(String[] args) {

        section("1) filter - keep elements that match");
        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        List<Integer> evens = nums.stream()
                                  .filter(n -> n % 2 == 0)
                                  .collect(Collectors.toList());
        System.out.println("evens = " + evens);

        section("2) map - one-to-one transformation");
        List<Integer> lengths = Stream.of("alpha", "beta", "gamma")
                                      .map(String::length)
                                      .collect(Collectors.toList());
        System.out.println("lengths = " + lengths);

        section("3) flatMap - one-to-many, then flatten");
        List<List<Integer>> nested = List.of(
                List.of(1, 2, 3),
                List.of(4, 5),
                List.of(6, 7, 8, 9)
        );
        List<Integer> flat = nested.stream()
                                   .flatMap(List::stream)
                                   .collect(Collectors.toList());
        System.out.println("flat = " + flat);

        // flatMap to split sentences into words
        List<String> words = Stream.of("the quick brown", "fox jumps over", "the lazy dog")
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .collect(Collectors.toList());
        System.out.println("words = " + words);

        section("4) sorted - natural and custom");
        List<Integer> sortedAsc = nums.stream().sorted().collect(Collectors.toList());
        List<Integer> sortedDesc = nums.stream()
                                       .sorted(Comparator.reverseOrder())
                                       .collect(Collectors.toList());
        System.out.println("asc  = " + sortedAsc);
        System.out.println("desc = " + sortedDesc);

        // Custom comparator on objects
        record P(String name, int age) {}
        List<P> people = List.of(
                new P("Charlie", 30),
                new P("Alice",   28),
                new P("Bob",     34)
        );
        people.stream()
              .sorted(Comparator.comparingInt(P::age))
              .forEach(System.out::println);

        section("5) distinct - remove duplicates");
        List<Integer> u = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3)
                                .distinct()
                                .collect(Collectors.toList());
        System.out.println("distinct = " + u);

        section("6) skip + limit - pagination idiom");
        List<Integer> page2 = Stream.iterate(1, n -> n + 1)
                                    .skip(10)             // skip page 1
                                    .limit(5)              // 5 items per page
                                    .collect(Collectors.toList());
        System.out.println("page 2 = " + page2);

        section("7) takeWhile / dropWhile (Java 9+)");
        // Both stop / start based on a CONDITION rather than an INDEX.
        List<Integer> taken = Stream.of(1, 2, 3, 4, 5, 1, 2)
                                    .takeWhile(n -> n < 4)
                                    .collect(Collectors.toList());          // [1, 2, 3]
        List<Integer> dropped = Stream.of(1, 2, 3, 4, 5, 1, 2)
                                      .dropWhile(n -> n < 4)
                                      .collect(Collectors.toList());          // [4, 5, 1, 2]
        System.out.println("takeWhile = " + taken);
        System.out.println("dropWhile = " + dropped);
        // takeWhile stops at the first element that FAILS the predicate -
        // unlike filter, which keeps scanning.

        section("8) peek - SIDE-EFFECT debugging only");
        long count = nums.stream()
                .peek(n -> System.out.println("  before filter : " + n))
                .filter(n -> n > 2)
                .peek(n -> System.out.println("    after filter  : " + n))
                .count();
        System.out.println("count = " + count);

        section("9) mapMulti (Java 16+) - faster than flatMap for tiny expansions");
        List<Integer> expanded = Stream.of(1, 2, 3)
                .<Integer>mapMulti((n, sink) -> {
                    sink.accept(n);
                    sink.accept(n * 10);
                })
                .collect(Collectors.toList());
        System.out.println("mapMulti = " + expanded);     // [1, 10, 2, 20, 3, 30]

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
