package Basics.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * java.util.concurrent.ConcurrentHashMap&lt;K, V&gt;
 * --------------------------------------------
 * The thread-safe, high-throughput replacement for both Hashtable and
 * Collections.synchronizedMap. Internally it uses LOCK STRIPING + CAS - so
 * many threads can read and write concurrently without one big mutex.
 *
 *
 * Why It Exists
 * -------------
 *   - Hashtable is synchronized on every method, killing throughput.
 *   - HashMap is not thread-safe at all.
 *   - Collections.synchronizedMap wraps a HashMap with one global lock,
 *     which serialises all access.
 *
 * ConcurrentHashMap aims for the throughput of HashMap with the safety
 * of Hashtable.
 *
 *
 * Key Properties
 * --------------
 *   - Thread-safe for ALL operations.
 *   - Iterators are WEAKLY CONSISTENT - they reflect the state at some
 *     point and never throw ConcurrentModificationException, but may or
 *     may not see concurrent updates.
 *   - Does NOT allow null keys or null values (a deliberate safety
 *     decision - "no value" can be reported with getOrDefault).
 *   - Aggregate operations (mappingCount, forEach, search, reduce) take
 *     a "parallelism threshold" - 1 means use the common ForkJoinPool.
 *
 *
 * Atomic Idioms You'll Use Constantly
 * -----------------------------------
 *   map.putIfAbsent(k, v)              - thread-safe "insert if missing"
 *   map.computeIfAbsent(k, fn)         - lazy init (single-flight)
 *   map.compute(k, fn)                 - atomic transform of one entry
 *   map.merge(k, value, mergeFn)       - the canonical counter pattern
 *   map.replace(k, oldV, newV)         - CAS-style update
 *
 *
 * Big-O (Expected)
 * ----------------
 *   get / put / remove                          O(1) average
 *   contains / containsKey / size               O(1) (size is approximate at
 *                                                       any given instant)
 *   mappingCount()                              long-typed exact count
 *
 *
 * When NOT To Use It
 * ------------------
 *   - Single-threaded code -> HashMap is faster and accepts null.
 *   - You need a sorted concurrent map -> ConcurrentSkipListMap.
 */

public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Basic API - looks like HashMap");
        ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        m.putIfAbsent("a", 99);                   // already present - no change
        System.out.println("map = " + m);
        System.out.println("get(a) = " + m.get("a"));

        section("2) Nulls REJECTED at runtime");
        try { m.put(null, 1); }
        catch (NullPointerException e) { System.out.println("put(null, ...) -> NPE"); }
        try { m.put("k", null); }
        catch (NullPointerException e) { System.out.println("put(..., null) -> NPE"); }

        section("3) Atomic 'increment counter' with merge");
        ConcurrentHashMap<String, Long> counts = new ConcurrentHashMap<>();
        String text = "the quick brown fox jumps over the lazy dog the the";
        for (String w : text.split(" ")) {
            counts.merge(w, 1L, Long::sum);
        }
        System.out.println("counts = " + counts);

        section("4) computeIfAbsent for lazy single-flight init");
        ConcurrentHashMap<String, java.util.List<Integer>> index = new ConcurrentHashMap<>();
        for (int n : new int[]{2, 3, 5, 7, 9}) {
            String key = (n % 2 == 0) ? "even" : "odd";
            // computeIfAbsent is the THREAD-SAFE version of get-or-create.
            index.computeIfAbsent(key, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(n);
        }
        System.out.println("index = " + index);

        section("5) Many threads hammering one map - safe and fast");
        ConcurrentHashMap<Integer, LongAdder> shared = new ConcurrentHashMap<>();
        final int THREADS = 8, PER_THREAD = 50_000;
        Thread[] ts = new Thread[THREADS];
        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            ts[t] = new Thread(() -> {
                for (int i = 0; i < PER_THREAD; i++) {
                    int bucket = i % 16;
                    shared.computeIfAbsent(bucket, k -> new LongAdder()).increment();
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        long ms = (System.nanoTime() - start) / 1_000_000;
        long total = shared.values().stream().mapToLong(LongAdder::sum).sum();
        System.out.println("threads=" + THREADS + " perThread=" + PER_THREAD);
        System.out.println("expected total = " + ((long) THREADS * PER_THREAD));
        System.out.println("actual total   = " + total);
        System.out.println("elapsed         = " + ms + " ms");

        section("6) Weakly consistent iterator - no CME");
        ConcurrentHashMap<Integer, Integer> live = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) live.put(i, i * 10);
        java.util.Iterator<Map.Entry<Integer, Integer>> it = live.entrySet().iterator();
        live.put(99, 990);                       // mutate WHILE iterating
        live.remove(0);
        int seen = 0;
        while (it.hasNext()) { it.next(); seen++; }
        System.out.println("iterated " + seen + " entries, no CME thrown");

        section("7) Performance vs Hashtable");
        final int N = 200_000;
        long t = System.nanoTime();
        java.util.Hashtable<Integer, Integer> ht = new java.util.Hashtable<>();
        for (int i = 0; i < N; i++) ht.put(i, i);
        long htMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<>();
        for (int i = 0; i < N; i++) chm.put(i, i);
        long chmMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("Hashtable         put(" + N + "): " + htMs  + " ms");
        System.out.println("ConcurrentHashMap put(" + N + "): " + chmMs + " ms  (modern, faster)");

        section("8) HashMap is NOT a safe substitute under concurrent writes");
        // Concurrent writes to a plain HashMap can corrupt it. We do NOT
        // demonstrate this here (the corruption can hang the JVM in older
        // releases) - the lesson is "use ConcurrentHashMap when threading".

        @SuppressWarnings("unused")
        HashMap<Integer, Integer> unsafe = new HashMap<>();

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
