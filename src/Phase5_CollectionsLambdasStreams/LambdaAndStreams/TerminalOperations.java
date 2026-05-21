package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Terminal Operations - The Pipeline's Final Step
 * -----------------------------------------------
 * A TERMINAL operation kicks off the pipeline and produces a side effect
 * or a value. After a terminal op runs, the stream is CLOSED.
 * <p>
 *
 * Common Terminal Operations
 * --------------------------
 * <p>
 *
 * Side-effecting
 * forEach(Consumer)
 * forEachOrdered(Consumer)             - preserves encounter order, even in parallel
 * <p>
 *
 * Reduction / aggregation
 * reduce(identity, BinaryOperator)
 * reduce(BinaryOperator) -&gt; Optional&lt;T&gt;
 * reduce(identity, accumulator, combiner)
 * collect(Collector)
 * count()
 * min(Comparator) / max(Comparator) -&gt; Optional
 * sum / average / IntStream.sum / .summaryStatistics  (primitive streams)
 * <p>
 *
 * Existence / search (SHORT-CIRCUIT)
 * anyMatch(Predicate)                  - true if any element matches
 * allMatch(Predicate)                  - true if every element matches
 * noneMatch(Predicate)                 - true if no element matches
 * findFirst()                          - Optional of first element
 * findAny()                            - Optional of any element (parallel-friendly)
 * <p>
 *
 * Conversion
 * toArray() / toArray(IntFunction)
 * toList()                             - Java 16+, returns unmodifiable List
 * iterator()                            - bridge back to Iterator
 * <p>
 *
 * Reduce - Three Forms Explained
 * ------------------------------
 * <p>
 *
 * 1. T reduce(T identity, BinaryOperator&lt;T&gt; op)
 * <p>
 *
 * int sum = stream.reduce(0, Integer::sum);
 * <p>
 *
 * 2. Optional&lt;T&gt; reduce(BinaryOperator&lt;T&gt; op)
 * <p>
 *
 * Optional&lt;Integer&gt; max = stream.reduce(Integer::max);
 * <p>
 *
 * 3. &lt;U&gt; U reduce(U identity, BiFunction&lt;U, T, U&gt; accumulator,
 * BinaryOperator&lt;U&gt; combiner)
 * <p>
 *
 * The "U is a different type" form, useful for parallel streams.
 * <p>
 *
 * Match Operations Short-Circuit
 * ------------------------------
 * anyMatch / allMatch / noneMatch return as soon as the answer is decided.
 * On infinite streams this is the difference between a few steps and a
 * runaway loop.
 */

public class TerminalOperations {

    public static void main(String[] args) {

        section("1) forEach (and forEachOrdered)");
        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        nums.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        // forEachOrdered preserves ordering even with parallel streams
        nums.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();

        section("2) count");
        long evens = nums.stream().filter(n -> n % 2 == 0).count();
        System.out.println("evens count = " + evens);

        section("3) reduce - three forms");
        int sum = nums.stream().reduce(0, Integer::sum);                // form 1
        Optional<Integer> max = nums.stream().reduce(Integer::max);     // form 2
        System.out.println("sum         = " + sum);
        System.out.println("max         = " + max.orElse(-1));

        // form 3 - U is different from T
        int totalLength = Stream.of("alpha", "beta", "gamma")
                .reduce(0,                                              // identity (U)
                        (acc, s) -> acc + s.length(),                   // accumulator U + T -> U
                        Integer::sum);                                   // combiner U + U -> U
        System.out.println("totalLength = " + totalLength);

        section("4) min / max with Comparator");
        Optional<String> longest = Stream.of("alpha", "beta", "g")
                .max(Comparator.comparingInt(String::length));
        System.out.println("longest = " + longest.orElse("-"));

        section("5) anyMatch / allMatch / noneMatch (short-circuit)");
        boolean anyEven = nums.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = nums.stream().allMatch(n -> n > 0);
        boolean noneNegative = nums.stream().noneMatch(n -> n < 0);
        System.out.println("anyEven  = " + anyEven);
        System.out.println("allPos   = " + allPositive);
        System.out.println("noneNeg  = " + noneNegative);

        section("6) findFirst / findAny - Optional results");
        Optional<Integer> firstOver3 = nums.stream().filter(n -> n > 3).findFirst();
        Optional<Integer> anyOver3 = nums.parallelStream().filter(n -> n > 3).findAny();
        System.out.println("firstOver3 = " + firstOver3.orElse(-1));
        System.out.println("anyOver3   = " + anyOver3.orElse(-1));

        section("7) collect to List / Set / Map / Joined String");
        List<Integer> dbl = nums.stream().map(n -> n * 2).collect(Collectors.toList());
        java.util.Set<Integer> uniq = Stream.of(1, 2, 2, 3).collect(Collectors.toSet());
        Map<String, Integer> ages = Stream.of("alice", "bob")
                .collect(Collectors.toMap(s -> s, String::length));
        String joined = nums.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
        System.out.println("dbl    = " + dbl);
        System.out.println("uniq   = " + uniq);
        System.out.println("ages   = " + ages);
        System.out.println("joined = " + joined);

        section("8) Stream.toList (Java 16+) - UNMODIFIABLE result");
        List<Integer> immutable = nums.stream().filter(n -> n > 2).toList();
        System.out.println("toList = " + immutable);
        try {
            immutable.add(99);
        } catch (UnsupportedOperationException e) {
            System.out.println("toList result is immutable - add rejected");
        }

        section("9) toArray - object vs typed");
        Object[] objs = nums.stream().toArray();
        Integer[] typed = nums.stream().toArray(Integer[]::new);
        System.out.println("objs len = " + objs.length + ", typed len = " + typed.length);

        section("10) Primitive stream specialised terminals");
        int[] ints = {1, 2, 3, 4, 5};
        IntSummaryStatistics st = Arrays.stream(ints).summaryStatistics();
        System.out.println("count=" + st.getCount()
                + " min=" + st.getMin()
                + " max=" + st.getMax()
                + " sum=" + st.getSum()
                + " avg=" + st.getAverage());

        java.util.OptionalInt firstEvenBig = IntStream.range(0, 1_000_000)
                .filter(n -> n % 2 == 0 && n > 999)
                .findFirst();
        System.out.println("first even > 999 = "
                + (firstEvenBig.isPresent() ? firstEvenBig.getAsInt() : -1));

        section("11) reduce on an EMPTY stream");
        Optional<Integer> nope = Stream.<Integer>empty().reduce(Integer::max);
        System.out.println("empty.reduce max = " + nope);    // Optional.empty
        int safe = Stream.<Integer>empty().reduce(0, Integer::sum);
        System.out.println("empty.reduce(0)  = " + safe);    // 0

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
