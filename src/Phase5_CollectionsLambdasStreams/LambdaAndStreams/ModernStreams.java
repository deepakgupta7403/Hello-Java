package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modern Stream Features (Java 9 -&gt; 21)
 * ------------------------------------
 * A short tour of the upgrades the Stream API has received over the years.
 * <p>
 *
 * Java 9
 * ------
 *   takeWhile(Predicate)                         keep until predicate fails
 *   dropWhile(Predicate)                         skip while predicate holds
 *   Stream.iterate(seed, hasNext, next)          FINITE iterate
 *   Stream.ofNullable(T)                         zero-or-one stream
 *   Optional.stream()                            Optional -&gt; Stream of 0 or 1
 * <p>
 *
 * Java 10
 * -------
 *   Collectors.toUnmodifiableList / Set / Map    immutable results
 * <p>
 *
 * Java 11
 * -------
 *   String.lines()                                  Stream&lt;String&gt; of lines
 *   Files.readString / Files.writeString            (file I/O - see StreamFileIO)
 *   Collection.toArray(IntFunction)                 typed array conversion
 * <p>
 *
 * Java 12
 * -------
 *   Collectors.teeing(c1, c2, combiner)             split into TWO collectors, combine
 * <p>
 *
 * Java 16
 * -------
 *   Stream.toList()                                  unmodifiable List - the new default
 *   Stream.mapMulti                                  flatMap alternative for tiny expansions
 * <p>
 *
 * Java 21
 * -------
 *   Sequenced Collections - getFirst/getLast/reversed on List/Deque/LinkedHashSet
 *   Pattern matching in switch for stream classification
 *   (Gatherers - preview in Java 22; not yet final in 21)
 * <p>
 *
 * Examples Below
 * --------------
 * Each section uses the new feature in a small, focused snippet.
 */

public class ModernStreams {

    public static void main(String[] args) {

        section("1) takeWhile / dropWhile (Java 9+)");
        List<Integer> taken = Stream.of(1, 2, 3, 4, 5, 1, 2)
                .takeWhile(n -> n < 4)
                .toList();
        List<Integer> dropped = Stream.of(1, 2, 3, 4, 5, 1, 2)
                .dropWhile(n -> n < 4)
                .toList();
        System.out.println("takeWhile = " + taken);
        System.out.println("dropWhile = " + dropped);

        section("2) Stream.iterate(seed, hasNext, next) - FINITE (Java 9+)");
        List<Integer> pow2 = Stream.iterate(1, n -> n < 100, n -> n * 2).toList();
        System.out.println("pow2 = " + pow2);

        section("3) Stream.ofNullable - one or zero (Java 9+)");
        long present = Stream.ofNullable("x").count();
        long absent  = Stream.ofNullable(null).count();
        System.out.println("ofNullable(x)   count = " + present);
        System.out.println("ofNullable(null) count = " + absent);

        section("4) Optional.stream (Java 9+) - flatMap over many Optionals");
        List<Optional<String>> maybeNames = List.of(
                Optional.of("alice"), Optional.empty(), Optional.of("bob"));
        List<String> realNames = maybeNames.stream()
                .flatMap(Optional::stream)              // unpacks each Optional safely
                .toList();
        System.out.println("realNames = " + realNames);

        section("5) Collectors.toUnmodifiable* (Java 10+)");
        List<Integer> immutable = Stream.of(1, 2, 3)
                .collect(Collectors.toUnmodifiableList());
        try { immutable.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("toUnmodifiableList - rejected add()");
        }

        section("6) String.lines (Java 11+) - one line per stream element");
        long lineCount = "alpha\nbeta\ngamma\ndelta".lines().count();
        System.out.println("lines = " + lineCount);

        section("7) Collectors.teeing (Java 12+) - two collectors in one pass");
        record Stats(double avg, long count) {}
        Stats r = Stream.of(1, 2, 3, 4, 5).collect(Collectors.teeing(
                Collectors.averagingInt(n -> n),
                Collectors.counting(),
                Stats::new));
        System.out.println("stats = " + r);

        section("8) Stream.toList (Java 16+) - the new go-to");
        // Returns an UNMODIFIABLE list. Shorter than collect(Collectors.toList()).
        List<Integer> easy = Stream.of(1, 2, 3, 4, 5).filter(n -> n > 2).toList();
        System.out.println("toList = " + easy);
        try { easy.add(99); }
        catch (UnsupportedOperationException e) {
            System.out.println("toList result is IMMUTABLE - rejected add()");
        }

        section("9) Stream.mapMulti (Java 16+) - flatMap for tiny expansions");
        List<Integer> expanded = Stream.of(1, 2, 3)
                .<Integer>mapMulti((n, sink) -> {
                    sink.accept(n);
                    sink.accept(n * 10);
                })
                .toList();
        System.out.println("mapMulti = " + expanded);    // 1, 10, 2, 20, 3, 30

        section("10) Java 21 - pattern matching switch on stream output");
        record Customer(String name, String tier) {}
        List<Customer> custs = List.of(
                new Customer("Alice", "GOLD"),
                new Customer("Bob",   "SILVER"),
                new Customer("Carol", "GOLD"),
                new Customer("Dave",  "BRONZE")
        );
        custs.stream()
             .map(c -> switch (c.tier()) {
                 case "GOLD"   -> c.name() + " (premium)";
                 case "SILVER" -> c.name() + " (mid)";
                 case "BRONZE" -> c.name() + " (basic)";
                 default       -> c.name();
             })
             .forEach(System.out::println);

        section("11) Sequenced collections (Java 21) play nicely with streams");
        // LinkedHashSet is now a SequencedSet - we can read first/last after collecting.
        var unique = Stream.of("c", "a", "b", "a", "c", "d")
                           .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        try {
            var first = unique.getClass().getMethod("getFirst").invoke(unique);
            var last  = unique.getClass().getMethod("getLast").invoke(unique);
            System.out.println("first = " + first + ", last = " + last);
        } catch (Exception e) {
            System.out.println("(Java 21 sequenced collections required)");
        }

        section("12) Map.entry inside a stream - immutable pair builder");
        Map<String, Integer> ages = Stream.of("alice", "bob", "carol")
                .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("ages = " + ages);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
