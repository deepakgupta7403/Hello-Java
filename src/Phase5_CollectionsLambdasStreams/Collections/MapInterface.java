package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * java.util.Map&lt;K, V&gt; - Key-Value Lookup
 * --------------------------------------
 * A Map stores ENTRIES of KEY -> VALUE pairs. Keys are UNIQUE; values may
 * repeat. Maps are NOT part of the Collection hierarchy - they have their
 * own root because their operations work on PAIRS, not single elements.
 *
 *
 *      +-----------+        +----------+
 *      |  "alice"  |  ->    |   30     |
 *      |  "bob"    |  ->    |   25     |
 *      |  "carol"  |  ->    |   28     |
 *      +-----------+        +----------+
 *           keys              values
 *
 *
 * The Method Catalogue (Java 8+ included)
 * ---------------------------------------
 *
 *   Basic CRUD
 *      put(K, V)              - returns the previous value, or null
 *      get(Object key)        - returns value or null
 *      remove(Object key)     - returns value or null
 *      containsKey(Object)
 *      containsValue(Object)
 *      size(), isEmpty(), clear()
 *
 *   Bulk
 *      putAll(Map)
 *
 *   Views
 *      keySet()       - Set&lt;K&gt;
 *      values()       - Collection&lt;V&gt;
 *      entrySet()     - Set&lt;Map.Entry&lt;K,V&gt;&gt;
 *
 *   Defaults (Java 8+)
 *      getOrDefault(key, default)
 *      putIfAbsent(key, value)
 *      computeIfAbsent(key, mappingFn)        - LAZY init of values
 *      computeIfPresent(key, fn)              - update if present
 *      compute(key, fn)                       - either create or update
 *      merge(key, value, mergeFn)             - combine new value with old
 *      forEach(BiConsumer)                    - iterate pairs
 *      replace(key, value) / replace(key, old, new)
 *      replaceAll(BiFunction)
 *
 *   Static factories (Java 9+)
 *      Map.of(k1, v1, ..., k10, v10)          - immutable, no nulls
 *      Map.ofEntries(Map.entry(k, v), ...)
 *      Map.entry(k, v)
 *      Map.copyOf(otherMap)
 *
 *
 * Implementations
 * ---------------
 *   HashMap         - hash table.   O(1).  NO order guarantee.
 *   LinkedHashMap   - hash table + linked list. INSERTION (or access) order.
 *   TreeMap         - red-black tree. SORTED by key.  O(log n).
 *   ConcurrentHashMap - lock-striped, thread-safe.
 *   EnumMap         - super-fast, enum keys only.
 *   Hashtable       - LEGACY synchronized hash table - avoid.
 *
 *
 * Picking The Right Map
 * ---------------------
 *   - You just need key -> value     ->  HashMap (the default).
 *   - Iteration in INSERTION order   ->  LinkedHashMap.
 *   - Iteration in KEY-SORTED order  ->  TreeMap.
 *   - Concurrent access              ->  ConcurrentHashMap.
 *   - Enum keys                       ->  EnumMap.
 *   - Compile-time constant content  ->  Map.of(...).
 */

public class MapInterface {

    public static void main(String[] args) {

        section("1) Basic CRUD");
        Map<String, Integer> ages = new HashMap<>();
        ages.put("alice", 30);
        ages.put("bob",   25);
        ages.put("alice", 31);                       // overrides; returns 30
        System.out.println("ages              = " + ages);
        System.out.println("get(alice)        = " + ages.get("alice"));
        System.out.println("get(unknown)      = " + ages.get("unknown"));
        System.out.println("containsKey(bob)  = " + ages.containsKey("bob"));
        System.out.println("containsValue(31) = " + ages.containsValue(31));
        System.out.println("size              = " + ages.size());
        ages.remove("alice");
        System.out.println("after remove      = " + ages);

        section("2) The three VIEW methods - keySet / values / entrySet");
        Map<String, Integer> scores = new HashMap<>();
        scores.put("a", 1); scores.put("b", 2); scores.put("c", 3);
        System.out.println("keySet()   = " + scores.keySet());
        System.out.println("values()   = " + scores.values());
        System.out.println("entrySet() = " + scores.entrySet());

        // Views are LIVE - changes propagate.
        scores.keySet().remove("a");
        System.out.println("after keySet().remove(a) = " + scores);

        section("3) getOrDefault");
        System.out.println("getOrDefault(b, 0)       = " + scores.getOrDefault("b", 0));
        System.out.println("getOrDefault(unknown, 0) = " + scores.getOrDefault("unknown", 0));

        section("4) putIfAbsent");
        scores.putIfAbsent("b", 999);       // exists - no change
        scores.putIfAbsent("d", 4);          // missing - inserted
        System.out.println("after putIfAbsent        = " + scores);

        section("5) computeIfAbsent - the lazy-init idiom");
        Map<String, java.util.List<Integer>> index = new HashMap<>();
        for (int n : new int[]{2, 3, 5, 7, 9}) {
            String key = (n % 2 == 0) ? "even" : "odd";
            // If the key has no list yet, create one; either way, add n to it.
            index.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(n);
        }
        System.out.println("index = " + index);

        section("6) computeIfPresent / compute / merge");
        Map<String, Integer> counts = new HashMap<>();
        counts.put("a", 1);
        counts.computeIfPresent("a", (k, v) -> v + 10);
        counts.compute("b", (k, v) -> v == null ? 1 : v + 1);
        // merge: typical "increment counter" pattern
        for (String s : new String[]{"x", "x", "y", "x"}) {
            counts.merge(s, 1, Integer::sum);
        }
        System.out.println("counts = " + counts);

        section("7) forEach / replaceAll");
        scores.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        scores.replaceAll((k, v) -> v * 10);
        System.out.println("after replaceAll(*10) = " + scores);

        section("8) Map.of / Map.ofEntries / Map.entry - immutable factories");
        Map<String, Integer> imm = Map.of("a", 1, "b", 2);
        Map<String, Integer> big = Map.ofEntries(
                Map.entry("x", 24),
                Map.entry("y", 25),
                Map.entry("z", 26)
        );
        System.out.println("imm = " + imm);
        System.out.println("big = " + big);
        try { imm.put("c", 3); }
        catch (UnsupportedOperationException e) {
            System.out.println("Map.of is IMMUTABLE - put rejected");
        }

        section("9) Implementations side-by-side");
        Map<String, Integer> h  = new HashMap<>();
        Map<String, Integer> lh = new LinkedHashMap<>();
        Map<String, Integer> tr = new TreeMap<>();
        for (String k : new String[]{"banana", "apple", "cherry"}) {
            h.put(k, k.length()); lh.put(k, k.length()); tr.put(k, k.length());
        }
        System.out.println("HashMap        = " + h);     // some order
        System.out.println("LinkedHashMap  = " + lh);    // insertion order
        System.out.println("TreeMap        = " + tr);    // sorted by key

        // OUTPUT (matches inline comments; HashMap order may vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
