package Phase5_CollectionsLambdasStreams.Collections;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * java.util.concurrent.ConcurrentSkipListSet&lt;E&gt;
 * ---------------------------------------------
 * The thread-safe twin of TreeSet. It is BUILT on a skip list - a
 * probabilistic data structure that supports O(log n) sorted operations
 * with cheap LOCK-FREE concurrent access (it uses CAS, not locks).
 *
 *      Backed by:   ConcurrentSkipListMap (similar relationship to
 *                   TreeSet -> TreeMap)
 *      Implements:  NavigableSet&lt;E&gt;, SortedSet&lt;E&gt;, Set&lt;E&gt;
 *      Concurrency: lock-free reads, weakly-consistent iterators
 *
 *
 * Why It Exists
 * -------------
 *   - TreeSet is FAST but NOT thread-safe.
 *   - Collections.synchronizedSortedSet wraps a TreeSet with one big lock;
 *     contention crushes throughput under load.
 *   - ConcurrentSkipListSet allows many threads to read and write
 *     concurrently with no global lock.
 *
 *
 * When To Use It
 * --------------
 *   - You need a SORTED set shared by multiple threads.
 *   - You want NavigableSet-style queries (floor / ceiling / range / etc.)
 *     in concurrent code.
 *   - Read-heavy or moderately write-heavy scenarios.
 *
 *
 * When NOT To Use It
 * ------------------
 *   - Single-threaded code -> TreeSet is faster, simpler.
 *   - You need contains/add to be the absolute fastest in a multi-threaded
 *     context AND you do not need ordering -> a ConcurrentHashMap's
 *     KeySet view is faster, but unordered.
 *
 *
 * Big-O
 * -----
 *   add / remove / contains / size                 O(log n) expected
 *   first / last / floor / ceiling / lower / higher  O(log n) expected
 *   iteration                                      O(n)
 *
 *
 * Weakly Consistent Iterators
 * ---------------------------
 * Iterators do NOT throw ConcurrentModificationException. They reflect the
 * state of the set AT SOME POINT after the iterator was created, and may
 * or may not see concurrent updates. They never break, just blur.
 *
 *
 * Null is NOT Allowed
 * -------------------
 * Skip-list comparison must order all elements; null has no place in that.
 */

public class ConcurrentSkipListSetDemo {

    public static void main(String[] args) throws Exception {

        section("1) Same API as TreeSet, but thread-safe");
        ConcurrentSkipListSet<Integer> s = new ConcurrentSkipListSet<>();
        for (int n : new int[]{5, 1, 4, 2, 3, 1}) s.add(n);   // 1 only kept once
        System.out.println("set         = " + s);              // sorted, no dupes
        System.out.println("first       = " + s.first());
        System.out.println("last        = " + s.last());
        System.out.println("floor(2)    = " + s.floor(2));
        System.out.println("ceiling(2)  = " + s.ceiling(2));
        System.out.println("headSet(3)  = " + s.headSet(3));
        System.out.println("tailSet(3)  = " + s.tailSet(3));

        section("2) Concurrent inserts from many threads");
        final int THREADS = 8;
        final int PER_THREAD = 1_000;
        ConcurrentSkipListSet<Integer> shared = new ConcurrentSkipListSet<>();
        Thread[] ts = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            final int base = t * PER_THREAD;
            ts[t] = new Thread(() -> {
                for (int n = 0; n < PER_THREAD; n++) shared.add(base + n);
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("total inserted = " + shared.size() +
                "  (expected " + (THREADS * PER_THREAD) + ")");
        System.out.println("first / last   = " + shared.first() + " / " + shared.last());

        section("3) Weakly consistent iterator - no CME, just blur");
        ConcurrentSkipListSet<Integer> live = new ConcurrentSkipListSet<>(
                java.util.List.of(1, 2, 3, 4, 5));
        java.util.Iterator<Integer> it = live.iterator();
        // mutate concurrently in this same thread - a TreeSet would throw
        live.add(99);
        live.remove(2);
        int seen = 0;
        while (it.hasNext()) { it.next(); seen++; }
        System.out.println("iterated " + seen + " element(s), no CME thrown");

        section("4) descendingSet and descendingIterator");
        System.out.println("descending set = " + live.descendingSet());

        section("5) pollFirst / pollLast - atomic remove-and-return");
        ConcurrentSkipListSet<Integer> q = new ConcurrentSkipListSet<>(
                java.util.List.of(10, 20, 30, 40, 50));
        System.out.println("pollFirst = " + q.pollFirst() + "  set=" + q);
        System.out.println("pollLast  = " + q.pollLast()  + "  set=" + q);

        section("6) Performance teaser - one big lock vs lock-free");
        // We compare:
        //   - Collections.synchronizedSortedSet(new TreeSet<>())
        //   - ConcurrentSkipListSet
        // Hammered by THREADS threads.

        final int N = 200_000;
        java.util.SortedSet<Integer> synced =
                java.util.Collections.synchronizedSortedSet(new java.util.TreeSet<>());
        ConcurrentSkipListSet<Integer> csl = new ConcurrentSkipListSet<>();

        long t = System.nanoTime();
        runConcurrent(THREADS, N / THREADS, v -> { synced.add(v); synced.contains(v); });
        long syncedMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        runConcurrent(THREADS, N / THREADS, v -> { csl.add(v); csl.contains(v); });
        long cslMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("synchronizedSortedSet: " + syncedMs + " ms");
        System.out.println("ConcurrentSkipListSet: " + cslMs    + " ms  (usually faster under contention)");

        // OUTPUT (timings vary)
    }

    /** Spin up `threads` threads each running `task` over `each` integers. */
    private static void runConcurrent(int threads, int each, java.util.function.IntConsumer task) throws Exception {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int base = i * each;
            ts[i] = new Thread(() -> {
                for (int n = 0; n < each; n++) task.accept(base + n);
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
