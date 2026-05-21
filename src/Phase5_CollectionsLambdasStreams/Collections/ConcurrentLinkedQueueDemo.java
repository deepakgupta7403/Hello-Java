package Phase5_CollectionsLambdasStreams.Collections;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * java.util.concurrent.ConcurrentLinkedQueue&lt;E&gt;
 * ---------------------------------------------
 * A LOCK-FREE, UNBOUNDED FIFO queue suitable for many producers and many
 * consumers. Internally it uses a singly-linked list with atomic CAS
 * (compare-and-swap) operations - no locks, no blocking.
 * <p>
 *
 *      head -> [A] -> [B] -> [C] -> tail
 * <p>
 *
 * Why It Exists
 * -------------
 *   - LinkedList / ArrayDeque are NOT thread-safe.
 *   - Collections.synchronizedQueue / Vector lock the entire collection.
 *   - BlockingQueue blocks; sometimes you do not want to block.
 * <p>
 *
 * ConcurrentLinkedQueue is the right pick when:
 *   - Many threads call offer() and poll() concurrently.
 *   - You do not need backpressure (no max capacity).
 *   - You do not want callers to wait when the queue is empty (poll
 *     returns null - no blocking).
 * <p>
 *
 * Differences From BlockingQueue
 * ------------------------------
 *   ConcurrentLinkedQueue        BlockingQueue
 *   ---------------------        ----------------
 *   Lock-free CAS                Usually locks (BlockingQueue impls)
 *   Never blocks                 put / take block by design
 *   Unbounded                    Often bounded for backpressure
 *   poll() returns null          take() blocks until an element exists
 *   No "fair" option              Some have a fairness mode
 * <p>
 *
 * Methods That Matter
 * -------------------
 *   offer(e)             - always succeeds (unbounded)
 *   add(e)               - same as offer, here
 *   poll()               - remove head, or null if empty
 *   peek()               - look at head, or null
 *   size()               - O(n)! Walks the list, not a counter.
 *   isEmpty()            - O(1)
 *   iterator()           - weakly consistent, never throws CME
 * <p>
 *
 * Why size() Is O(n)
 * ------------------
 * The lock-free design lets many threads insert and remove without
 * coordinating. There is no maintained counter because keeping one in sync
 * would require its own CAS loop. If you need to check whether the queue
 * is empty, use isEmpty() - that one is O(1).
 * <p>
 *
 * Big-O
 * -----
 *   offer / poll / peek / isEmpty       O(1) amortised
 *   size                                O(n)
 *   contains / remove(Object)           O(n)
 */

public class ConcurrentLinkedQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Single-threaded basics - just like any other Queue");
        ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<>();
        q.offer("a"); q.offer("b"); q.offer("c");
        System.out.println("queue   = " + q);
        System.out.println("peek    = " + q.peek());
        System.out.println("poll    = " + q.poll() + "  -> " + q);
        System.out.println("isEmpty = " + q.isEmpty());

        section("2) Many producers, many consumers - lock-free in action");
        final int PRODUCERS = 4;
        final int CONSUMERS = 4;
        final int PER_PRODUCER = 5_000;

        ConcurrentLinkedQueue<Integer> shared = new ConcurrentLinkedQueue<>();
        AtomicInteger consumedCount = new AtomicInteger(0);

        Thread[] producers = new Thread[PRODUCERS];
        Thread[] consumers = new Thread[CONSUMERS];

        long start = System.nanoTime();

        for (int p = 0; p < PRODUCERS; p++) {
            final int base = p * PER_PRODUCER;
            producers[p] = new Thread(() -> {
                for (int i = 0; i < PER_PRODUCER; i++) shared.offer(base + i);
            });
            producers[p].start();
        }

        for (int c = 0; c < CONSUMERS; c++) {
            consumers[c] = new Thread(() -> {
                // Each consumer pulls until it has seen its share.
                // Using PRODUCERS * PER_PRODUCER / CONSUMERS as a target.
                int target = (PRODUCERS * PER_PRODUCER) / CONSUMERS;
                int taken  = 0;
                while (taken < target) {
                    Integer v = shared.poll();
                    if (v != null) { taken++; consumedCount.incrementAndGet(); }
                    // else: spin briefly, the producers will catch up
                }
            });
            consumers[c].start();
        }

        for (Thread t : producers) t.join();
        for (Thread t : consumers) t.join();
        long ms = (System.nanoTime() - start) / 1_000_000;

        System.out.println("produced  : " + (PRODUCERS * PER_PRODUCER));
        System.out.println("consumed  : " + consumedCount.get());
        System.out.println("leftover  : " + shared.size() + " (size() is O(n))");
        System.out.println("elapsed   : " + ms + " ms");

        section("3) Weakly consistent iterator - never throws CME");
        ConcurrentLinkedQueue<Integer> live = new ConcurrentLinkedQueue<>(
                java.util.List.of(1, 2, 3, 4, 5));
        java.util.Iterator<Integer> it = live.iterator();
        live.offer(99);                 // mutate WHILE iterating - no CME
        live.poll();
        int seen = 0;
        while (it.hasNext()) { it.next(); seen++; }
        System.out.println("iterated " + seen + " element(s), no CME thrown");

        section("4) poll on empty queue returns null - no blocking");
        ConcurrentLinkedQueue<Integer> empty = new ConcurrentLinkedQueue<>();
        long t = System.nanoTime();
        Integer v = empty.poll();
        long us = (System.nanoTime() - t);
        System.out.println("poll() returned " + v + " in " + us + " ns (instant)");

        section("5) Memory ordering note - lock-free does not mean lock-bad");
        // Offer/poll publish/observe through volatile-and-CAS, so consumers
        // see fully-constructed objects. Just don't ASSUME a particular
        // interleaving between producer events.

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
