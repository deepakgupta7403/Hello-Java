package Phase5_CollectionsLambdasStreams.Collections;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * java.util.TreeMap&lt;K, V&gt; - Sorted Map (Red-Black Tree)
 * -----------------------------------------------------
 * TreeMap keeps its entries in SORTED order by key. It uses either the
 * key's natural ordering (key must implement Comparable) or a Comparator
 * you supply at construction.
 *
 *
 * Why It Exists
 * -------------
 *   - Iteration is always in key order, no extra sort needed.
 *   - "Range queries" - all entries between two keys - are O(log n).
 *   - "Neighbour queries" - floorKey, ceilingKey, etc. - are O(log n) and
 *     unique to TreeMap.
 *
 *
 * When To Use It
 * --------------
 *   - You need entries sorted by key on every iteration.
 *   - You answer questions like "biggest key &lt;= X" or "all entries between
 *     A and B".
 *   - You want a stable iteration order without writing a Comparator at
 *     each iteration site.
 *
 *
 * Big-O
 * -----
 *   put / get / remove / containsKey                          O(log n)
 *   iteration (in order)                                      O(n)
 *   first / last / floor / ceiling / lower / higher           O(log n)
 *   subMap / headMap / tailMap (range views)                  O(log n) to create
 *
 *
 * Implements: NavigableMap, SortedMap, Map
 *
 *
 * Key Methods Beyond the Map Contract
 * -----------------------------------
 *   - SortedMap:
 *        firstKey() / lastKey()
 *        headMap(toKey)            - strictly less
 *        tailMap(fromKey)          - fromKey inclusive
 *        subMap(fromKey, toKey)    - fromKey inclusive, toKey exclusive
 *        comparator()
 *
 *   - NavigableMap:
 *        firstEntry / lastEntry / pollFirstEntry / pollLastEntry
 *        floorKey(k) / ceilingKey(k) / lowerKey(k) / higherKey(k)
 *        floorEntry / ceilingEntry / lowerEntry / higherEntry
 *        descendingMap / descendingKeySet
 *        navigableHeadMap / Tail / SubMap (with inclusive flags)
 */

public class TreeMapDemo {

    public static void main(String[] args) {

        section("1) Natural-order sort by key");
        TreeMap<String, Integer> ages = new TreeMap<>();
        ages.put("charlie", 30);
        ages.put("alice",   25);
        ages.put("bob",     28);
        for (var e : ages.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        System.out.println("firstKey = " + ages.firstKey());
        System.out.println("lastKey  = " + ages.lastKey());

        section("2) Custom Comparator at construction");
        TreeMap<String, Integer> byLen = new TreeMap<>(
                Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
        );
        byLen.put("kiwi", 1);
        byLen.put("apple", 2);
        byLen.put("fig",  3);
        byLen.put("date", 4);
        System.out.println("byLen = " + byLen);

        section("3) Range views (LIVE views - share state with the parent)");
        TreeMap<Integer, String> events = new TreeMap<>();
        events.put(9,  "standup");
        events.put(11, "meeting");
        events.put(13, "lunch");
        events.put(14, "review");
        events.put(17, "demo");

        // morning meetings - everything before 12
        Map<Integer, String> morning = events.headMap(12);
        // afternoon - 13 onwards
        Map<Integer, String> afternoon = events.tailMap(13);
        // 11..14 (14 excluded)
        Map<Integer, String> midday = events.subMap(11, 14);

        System.out.println("morning   = " + morning);
        System.out.println("afternoon = " + afternoon);
        System.out.println("midday    = " + midday);

        section("4) Neighbour queries via NavigableMap");
        NavigableMap<Integer, String> nm = events;
        System.out.println("floorKey(15)   = " + nm.floorKey(15));      // 14
        System.out.println("ceilingKey(15) = " + nm.ceilingKey(15));    // 17
        System.out.println("lowerKey(11)   = " + nm.lowerKey(11));      // 9
        System.out.println("higherKey(11)  = " + nm.higherKey(11));     // 13
        System.out.println("firstEntry     = " + nm.firstEntry());
        System.out.println("lastEntry      = " + nm.lastEntry());

        section("5) pollFirstEntry / pollLastEntry - remove and return");
        System.out.println("pollFirstEntry = " + nm.pollFirstEntry() + "  remaining=" + nm.size());
        System.out.println("pollLastEntry  = " + nm.pollLastEntry()  + "  remaining=" + nm.size());

        section("6) descendingMap - reverse view");
        TreeMap<Integer, String> small = new TreeMap<>();
        small.put(1, "a"); small.put(2, "b"); small.put(3, "c");
        System.out.println("ascending  = " + small);
        System.out.println("descending = " + small.descendingMap());

        section("7) Iteration in key order");
        for (Map.Entry<Integer, String> e : small.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }

        section("8) Null keys are NOT allowed (TreeMap needs comparable keys)");
        try {
            new TreeMap<String, Integer>().put(null, 1);
        } catch (NullPointerException e) {
            System.out.println("TreeMap.put(null, ...) -> NullPointerException");
        }

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
