package Basics.Collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * java.util.LinkedHashMap&lt;K, V&gt; - HashMap with Predictable Order
 * --------------------------------------------------------------
 * LinkedHashMap extends HashMap. Internally it weaves a doubly-linked list
 * through its entries so iteration follows a predictable order. The hash-
 * table operations remain O(1); the linked list just adds a few pointers.
 *
 *
 * Two Order Modes
 * ---------------
 *   1. INSERTION ORDER (default)
 *      Entries iterated in the order put() first added them.
 *
 *   2. ACCESS ORDER       new LinkedHashMap&lt;&gt;(16, 0.75f, true)
 *      Every get() / put() moves that entry to the END. Perfect for LRU.
 *
 *
 * Why It Exists
 * -------------
 *   - You want O(1) lookup AND a deterministic iteration order
 *     (insertion or access).
 *   - You want to build a simple LRU CACHE by overriding removeEldestEntry.
 *   - You want JSON / config dumps to iterate in the order keys were added.
 *
 *
 * Big-O
 * -----
 *   put / get / remove / containsKey       O(1)  (same as HashMap)
 *   iteration                              O(size)
 *
 *
 * Constructors
 * ------------
 *   new LinkedHashMap&lt;&gt;()
 *   new LinkedHashMap&lt;&gt;(int initialCapacity)
 *   new LinkedHashMap&lt;&gt;(int initialCapacity, float loadFactor)
 *   new LinkedHashMap&lt;&gt;(int initialCapacity, float loadFactor, boolean accessOrder)
 *   new LinkedHashMap&lt;&gt;(Map&lt;? extends K, ? extends V&gt; m)
 *
 *
 * Java 21 - SequencedMap
 * ----------------------
 * LinkedHashMap implements the new SequencedMap interface (Java 21):
 * firstEntry, lastEntry, putFirst, putLast, pollFirstEntry, pollLastEntry,
 * reversed(). See Basics/ModernJava/SequencedCollections.java.
 */

public class LinkedHashMapDemo {

    public static void main(String[] args) {

        section("1) Insertion order - iteration is deterministic");
        LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
        m.put("third",  3);
        m.put("first",  1);
        m.put("second", 2);
        for (var e : m.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }

        section("2) Updating a key does NOT change its insertion position");
        LinkedHashMap<String, Integer> u = new LinkedHashMap<>();
        u.put("a", 1); u.put("b", 2); u.put("c", 3);
        u.put("a", 99);                      // value changes; order unchanged
        System.out.println("after update : " + u);

        section("3) Access-order mode (the LRU foundation)");
        LinkedHashMap<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
        lru.put("a", 1); lru.put("b", 2); lru.put("c", 3);
        lru.get("a");                        // accessing 'a' moves it to the end
        lru.get("b");                        // and now 'b' is at the end
        System.out.println("after gets   : " + lru);   // c, a, b

        section("4) A simple LRU cache built on LinkedHashMap");
        // We override removeEldestEntry - the JDK calls this on every put
        // and removes the eldest entry when we return true.
        final int CAPACITY = 3;
        LinkedHashMap<String, Integer> cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > CAPACITY;
            }
        };
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);                   // evicts the eldest 'a'
        System.out.println("cache now    : " + cache);
        cache.get("b");                       // 'b' becomes most recently used
        cache.put("e", 5);                    // evicts the eldest, which is now 'c'
        System.out.println("after touches: " + cache);

        section("5) All HashMap methods still work");
        LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>(Map.of("x", 1, "y", 2, "z", 3));
        lhm.merge("x", 10, Integer::sum);
        lhm.computeIfAbsent("w", k -> 4);
        System.out.println("lhm = " + lhm);

        section("6) Java 21 SequencedMap - first/last/reversed (via reflection so it compiles on 17)");
        try {
            var firstM = LinkedHashMap.class.getMethod("firstEntry");
            var lastM  = LinkedHashMap.class.getMethod("lastEntry");
            var revM   = LinkedHashMap.class.getMethod("reversed");
            System.out.println("firstEntry = " + firstM.invoke(lhm));
            System.out.println("lastEntry  = " + lastM.invoke(lhm));
            System.out.println("reversed   = " + revM.invoke(lhm));
        } catch (NoSuchMethodException e) {
            System.out.println("(Java 21+ needed for SequencedMap - skipped)");
        } catch (Exception e) {
            System.out.println("reflection error: " + e.getMessage());
        }

        // OUTPUT (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
