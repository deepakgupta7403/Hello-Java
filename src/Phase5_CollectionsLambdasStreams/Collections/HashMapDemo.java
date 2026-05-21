package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * java.util.HashMap&lt;K, V&gt; - The Default Map Implementation
 * --------------------------------------------------------
 * HashMap stores entries in an ARRAY OF BUCKETS, indexed by the key's
 * hashCode. Each bucket is either empty, a single entry, a small linked
 * list, or - once a bucket gets too long - a balanced TREE (since Java 8,
 * for keys that implement Comparable). This last detail is why the worst
 * case is now O(log n) instead of O(n).
 * <p>
 *
 * Why It Exists
 * -------------
 * You frequently need "given a key, give me its value" with O(1)
 * lookup. HashMap is the simplest, fastest general-purpose answer.
 * <p>
 *
 * When To Use It
 * --------------
 *   - Default for any key -> value lookup, no ordering needed.
 *   - Caches.
 *   - Indexes / groupings (group records by some field).
 *   - Counters / frequency tables.
 * <p>
 *
 * Big-O (with a good hash distribution)
 * -------------------------------------
 *   get / put / remove / containsKey                O(1) average
 *   iteration                                       O(capacity + size)
 *   worst case (all keys collide)                    O(log n) since Java 8
 *                                                    (tree-bucket fallback)
 * <p>
 *
 * The equals/hashCode Contract (Critical)
 * ---------------------------------------
 * For any KEY in a HashMap you MUST override hashCode and equals
 * consistently. The Map can't find an entry whose key has a different hash
 * than it had when inserted. Records (Java 16+) generate correct
 * equals/hashCode for you.
 * <p>
 *
 * Allows Null
 * -----------
 * HashMap allows ONE null KEY and ANY number of null VALUES. Hashtable,
 * ConcurrentHashMap, and Map.of(...) forbid nulls.
 * <p>
 *
 * Capacity / Load Factor
 * ----------------------
 *   - initialCapacity (default 16)
 *   - loadFactor       (default 0.75)
 * When size / capacity > loadFactor the table doubles. Pre-size to the
 * expected count to avoid rehashing in a hot loop.
 * <p>
 *
 * Constructors
 * ------------
 *   new HashMap&lt;&gt;()
 *   new HashMap&lt;&gt;(int initialCapacity)
 *   new HashMap&lt;&gt;(int initialCapacity, float loadFactor)
 *   new HashMap&lt;&gt;(Map&lt;? extends K, ? extends V&gt; m)
 */

public class HashMapDemo {

    /** Demonstrates a key with proper equals + hashCode. */
    record CityKey(String country, String name) {}

    public static void main(String[] args) {

        section("1) Constructors + put / get / remove");
        HashMap<String, Integer> m = new HashMap<>();
        m.put("alpha", 1);
        m.put("beta",  2);
        m.put("alpha", 11);                       // overwrites; returns the old 1
        System.out.println("after puts        = " + m);
        System.out.println("get(alpha)         = " + m.get("alpha"));
        m.remove("beta");
        System.out.println("after remove(beta) = " + m);

        section("2) Bulk - putAll + clear");
        HashMap<String, Integer> bulk = new HashMap<>();
        bulk.putAll(Map.of("a", 1, "b", 2, "c", 3));
        System.out.println("bulk     = " + bulk);
        bulk.clear();
        System.out.println("cleared  = " + bulk);

        section("3) containsKey / containsValue / size / isEmpty");
        HashMap<String, Integer> q = new HashMap<>(Map.of("a", 1, "b", 2));
        System.out.println("containsKey(a)    = " + q.containsKey("a"));
        System.out.println("containsValue(99) = " + q.containsValue(99));
        System.out.println("size              = " + q.size());
        System.out.println("isEmpty           = " + q.isEmpty());

        section("4) getOrDefault");
        System.out.println("getOrDefault(a, 0) = " + q.getOrDefault("a", 0));
        System.out.println("getOrDefault(z, 0) = " + q.getOrDefault("z", 0));

        section("5) putIfAbsent");
        q.putIfAbsent("a", 99);          // exists; no change
        q.putIfAbsent("d", 4);            // missing; inserted
        System.out.println("after putIfAbsent = " + q);

        section("6) computeIfAbsent - lazy init, classic group-by pattern");
        String[] words = {"apple", "ant", "banana", "blueberry", "cherry"};
        HashMap<Character, java.util.List<String>> byFirst = new HashMap<>();
        for (String w : words) {
            byFirst.computeIfAbsent(w.charAt(0), k -> new java.util.ArrayList<>()).add(w);
        }
        System.out.println("group by first letter = " + byFirst);

        section("7) computeIfPresent / compute / merge - frequency table");
        String text = "the quick brown fox jumps over the lazy dog the the";
        HashMap<String, Integer> freq = new HashMap<>();
        for (String w : text.split("\\s+")) {
            freq.merge(w, 1, Integer::sum);          // counter idiom
        }
        System.out.println("word frequencies = " + freq);

        section("8) Views - keySet / values / entrySet are LIVE");
        HashMap<String, Integer> v = new HashMap<>(Map.of("a", 1, "b", 2, "c", 3));
        v.keySet().remove("b");
        System.out.println("after keySet().remove(b) = " + v);

        section("9) forEach + replaceAll");
        v.forEach((k, val) -> System.out.println("  " + k + " -> " + val));
        v.replaceAll((k, val) -> val * 10);
        System.out.println("after replaceAll(*10)   = " + v);

        section("10) Custom-class keys - use records or override equals + hashCode");
        HashMap<CityKey, Integer> population = new HashMap<>();
        population.put(new CityKey("IN", "Mumbai"),   12_700_000);
        population.put(new CityKey("IN", "Delhi"),    16_800_000);
        population.put(new CityKey("US", "New York"), 8_300_000);
        // Even though we use a NEW key instance, the record's auto-generated
        // equals + hashCode mean the lookup succeeds.
        System.out.println("Mumbai pop = " + population.get(new CityKey("IN", "Mumbai")));

        section("11) Null key + null values");
        HashMap<String, String> nullable = new HashMap<>();
        nullable.put(null, "the null-key value");
        nullable.put("a",  null);
        System.out.println("null key  -> " + nullable.get(null));
        System.out.println("null value -> " + nullable.get("a"));
        // Map.of(...) and ConcurrentHashMap would REJECT both above.

        section("12) Iteration via entrySet (preferred for k+v together)");
        for (Map.Entry<String, Integer> e : v.entrySet()) {
            System.out.println("  " + e.getKey() + " => " + e.getValue());
        }

        // OUTPUT (representative; HashMap ordering may vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    /** Marker import retained for documentation; suppress unused warning. */
    @SuppressWarnings("unused") private static Objects keep;
}
