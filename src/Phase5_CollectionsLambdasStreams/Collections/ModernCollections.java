package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modern Collection Features (Java 8 -> 21)
 * -----------------------------------------
 * The collections framework has had many additions over the years - factory
 * methods, stream collectors, sequenced types. This file is a focused tour.
 * <p>
 *
 * Java 8 - Streams + functional Map APIs
 * --------------------------------------
 *   Collection.stream() / parallelStream() / forEach / removeIf
 *   Map.getOrDefault / putIfAbsent / computeIfAbsent / computeIfPresent
 *      / compute / merge / forEach / replaceAll
 *   Collectors.toList / toSet / toMap / groupingBy / partitioningBy
 * <p>
 *
 * Java 9 - Immutable factory methods
 * ----------------------------------
 *   List.of(...), Set.of(...), Map.of(...), Map.ofEntries(Map.entry(k, v))
 *   List.copyOf / Set.copyOf / Map.copyOf
 * <p>
 *
 * Java 10 - Collectors.toUnmodifiable*
 * ------------------------------------
 *   Collectors.toUnmodifiableList / toUnmodifiableSet / toUnmodifiableMap
 *   List.copyOf / Set.copyOf / Map.copyOf  (also Java 10)
 * <p>
 *
 * Java 11 - Collection.toArray(IntFunction)
 * -----------------------------------------
 *   String[] arr = list.toArray(String[]::new);
 * <p>
 *
 * Java 16 - Stream.toList()
 * -------------------------
 *   stream.toList()                            (returns an unmodifiable list)
 * <p>
 *
 * Java 21 - Sequenced Collections
 * -------------------------------
 *   SequencedCollection&lt;E&gt;            implemented by List, Deque,
 *                                       LinkedHashSet
 *   SequencedSet&lt;E&gt;                   implemented by LinkedHashSet
 *   SequencedMap&lt;K, V&gt;                implemented by LinkedHashMap, TreeMap
 * <p>
 *
 *   New methods: getFirst, getLast, addFirst, addLast, reversed,
 *                 putFirst, putLast, firstEntry, lastEntry, pollFirstEntry,
 *                 pollLastEntry.
 * <p>
 *
 *   See Basics/ModernJava/SequencedCollections.java for the deep dive.
 */

public class ModernCollections {

    public static void main(String[] args) {

        section("1) Immutable factories - List.of / Set.of / Map.of");
        List<Integer> li = List.of(1, 2, 3);
        Set<String>   si = Set.of("a", "b", "c");
        Map<String, Integer> mi = Map.of("a", 1, "b", 2);
        System.out.println("List.of = " + li);
        System.out.println("Set.of  = " + si);
        System.out.println("Map.of  = " + mi);
        try { li.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("List.of is immutable - add() rejected");
        }

        section("2) Map.ofEntries / Map.entry for >10 keys");
        Map<String, Integer> big = Map.ofEntries(
                Map.entry("a", 1),
                Map.entry("b", 2),
                Map.entry("c", 3)
        );
        System.out.println("ofEntries = " + big);

        section("3) Defensive snapshots - List.copyOf etc.");
        List<Integer> mutable = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> snap    = List.copyOf(mutable);
        mutable.add(4);
        System.out.println("mutable = " + mutable);
        System.out.println("snap    = " + snap + "  (unchanged)");
        try { snap.add(5); }
        catch (UnsupportedOperationException e) {
            System.out.println("snap is immutable - add() rejected");
        }

        section("4) Collectors - the workhorses");

        // toList / toSet  (return mutable defaults)
        List<Integer> stream1 = Stream.of(1, 2, 3, 4).collect(Collectors.toList());
        Set<Integer>  stream2 = Stream.of(1, 2, 3, 3, 4).collect(Collectors.toSet());
        System.out.println("toList = " + stream1 + ",  toSet = " + stream2);

        // toMap
        Map<String, Integer> nameLen = Stream.of("alice", "bob", "carol")
                .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("toMap = " + nameLen);

        // groupingBy
        Map<Integer, List<String>> byLen = Stream.of("a", "bb", "cc", "ddd", "ee")
                .collect(Collectors.groupingBy(String::length));
        System.out.println("groupingBy length = " + byLen);

        // groupingBy + counting -> frequency table
        Map<String, Long> freq = Stream.of("the", "a", "the", "of", "the", "a")
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        System.out.println("frequency table = " + freq);

        // partitioningBy
        Map<Boolean, List<Integer>> parts = Stream.of(1, 2, 3, 4, 5)
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("partitioning = " + parts);

        // joining
        String joined = Stream.of("a", "b", "c")
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("joining = " + joined);

        section("5) Unmodifiable collectors (Java 10+)");
        List<Integer> unmod = Stream.of(1, 2, 3).collect(Collectors.toUnmodifiableList());
        try { unmod.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("toUnmodifiableList is immutable");
        }

        section("6) Stream.toList() (Java 16+)");
        List<Integer> easy = Stream.of(1, 2, 3).toList();    // unmodifiable
        System.out.println("Stream.toList = " + easy);

        section("7) Collection.toArray(IntFunction) (Java 11+)");
        List<String> names = List.of("alice", "bob", "carol");
        String[] asArr = names.toArray(String[]::new);
        System.out.println("toArray(IntFunction) length = " + asArr.length);

        section("8) Java 21 SequencedCollection / Sequenced Set / Sequenced Map");
        // Use reflection so this file also compiles on JDK 17.
        Set<Integer> lhs = new LinkedHashSet<>(List.of(2, 4, 6, 8));
        Map<String, Integer> lhm = new LinkedHashMap<>();
        lhm.put("a", 1); lhm.put("b", 2); lhm.put("c", 3);

        // SequencedCollection: getFirst / getLast / reversed
        try {
            var first = lhs.getClass().getMethod("getFirst");
            var last  = lhs.getClass().getMethod("getLast");
            var rev   = lhs.getClass().getMethod("reversed");
            System.out.println("LinkedHashSet getFirst = " + first.invoke(lhs));
            System.out.println("LinkedHashSet getLast  = " + last.invoke(lhs));
            System.out.println("LinkedHashSet reversed = " + rev.invoke(lhs));
        } catch (NoSuchMethodException e) {
            System.out.println("(Java 21+ needed for SequencedCollection on LinkedHashSet)");
        } catch (Exception e) {
            System.out.println("reflection error: " + e.getMessage());
        }

        // SequencedMap: firstEntry / lastEntry / putFirst / putLast / reversed
        try {
            var firstE = LinkedHashMap.class.getMethod("firstEntry");
            var lastE  = LinkedHashMap.class.getMethod("lastEntry");
            System.out.println("LinkedHashMap firstEntry = " + firstE.invoke(lhm));
            System.out.println("LinkedHashMap lastEntry  = " + lastE.invoke(lhm));
        } catch (NoSuchMethodException e) {
            System.out.println("(Java 21+ needed for SequencedMap on LinkedHashMap)");
        } catch (Exception e) {
            System.out.println("reflection error: " + e.getMessage());
        }

        // TreeMap is also a SequencedMap in Java 21.
        TreeMap<Integer, String> tm = new TreeMap<>();
        tm.put(1, "a"); tm.put(3, "c"); tm.put(2, "b");
        System.out.println("TreeMap order = " + tm);

        section("9) Map.entry - immutable key/value pair (Java 9+)");
        Map.Entry<String, Integer> e = Map.entry("key", 42);
        System.out.println("entry = " + e);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused")
    private static HashSet<?> keepImport;
}
