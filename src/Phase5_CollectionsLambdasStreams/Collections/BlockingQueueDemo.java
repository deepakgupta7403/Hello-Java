package Phase5_CollectionsLambdasStreams.Collections;

import java.util.concurrent.*;

/**
 * java.util.concurrent.BlockingQueue&lt;E&gt;
 * -------------------------------------
 * BlockingQueue is a Queue that BLOCKS the caller when an operation cannot
 * proceed immediately:
 * <p>
 *
 *      put(e)   - blocks the producer if the queue is FULL
 *      take()   - blocks the consumer if the queue is EMPTY
 * <p>
 *
 * That makes BlockingQueue the FUNDAMENTAL BUILDING BLOCK for the
 * producer/consumer pattern in concurrent code.
 * <p>
 *
 * Four Operation Modes Per Operation
 * ----------------------------------
 * <p>
 *
 *                Throws            Returns special   Blocks            Times out
 *                ----------------- ----------------- ----------------- -----------------------------
 *   insert     | add(e)           | offer(e)        | put(e)          | offer(e, time, TimeUnit)
 *   remove     | remove()         | poll()          | take()          | poll(time, TimeUnit)
 *   peek       | element()        | peek()          | —               | —
 * <p>
 *
 * Implementations
 * ---------------
 *   ArrayBlockingQueue   - bounded, array-backed, fair option, FIFO
 *   LinkedBlockingQueue  - optionally bounded, linked nodes, FIFO,
 *                          separate locks for head and tail (good
 *                          throughput on producer/consumer)
 *   SynchronousQueue     - capacity ZERO! every put hands directly to a take
 *   PriorityBlockingQueue- unbounded, heap-ordered, blocking
 *   DelayQueue           - elements have a delay; take blocks until expiry
 *   LinkedTransferQueue  - extended transfer semantics (Java 7+)
 * <p>
 *
 * Why It Exists
 * -------------
 * Without BlockingQueue you would write loops like:
 * <p>
 *
 *      while (queue.isEmpty()) Thread.sleep(1);   // CPU-burning poll
 * <p>
 *
 * BlockingQueue offloads that wait into the OS scheduler - the thread
 * sleeps until there is something to do, no CPU spent waiting.
 * <p>
 *
 * When To Use Which
 * -----------------
 *   - Bounded capacity (backpressure)     ->  ArrayBlockingQueue
 *   - High-throughput producer/consumer   ->  LinkedBlockingQueue
 *   - Direct hand-off, no buffering       ->  SynchronousQueue
 *   - Priority order                       ->  PriorityBlockingQueue
 *   - Delayed-action scheduling            ->  DelayQueue
 * <p>
 *
 * Demo: A small producer / consumer with two threads.
 */

public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) ArrayBlockingQueue - bounded, fair");
        final int CAPACITY = 3;
        BlockingQueue<Integer> bq = new ArrayBlockingQueue<>(CAPACITY, /*fair*/ true);

        // Fill it up to capacity, then attempt one more with each method family.
        for (int i = 0; i < CAPACITY; i++) bq.put(i);
        System.out.println("filled to capacity = " + bq);

        // offer returns false instead of throwing when full
        System.out.println("offer(99)               = " + bq.offer(99));
        // add throws when full
        try { bq.add(99); }
        catch (IllegalStateException e) {
            System.out.println("add(99) -> " + e.getClass().getSimpleName());
        }
        // offer with timeout
        long t = System.currentTimeMillis();
        boolean ok = bq.offer(99, 50, TimeUnit.MILLISECONDS);
        System.out.println("offer(99, 50ms)          = " + ok +
                "  (waited ~" + (System.currentTimeMillis() - t) + " ms)");

        // Drain
        System.out.print("drained: ");
        Integer v;
        while ((v = bq.poll()) != null) System.out.print(v + " ");
        System.out.println();

        section("2) Producer / Consumer with put / take");
        BlockingQueue<String> pipe = new LinkedBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    String msg = "item-" + i;
                    pipe.put(msg);                  // blocks if pipe is full
                    System.out.println("produced " + msg);
                    Thread.sleep(30);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "producer");

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    String msg = pipe.take();       // blocks if pipe is empty
                    System.out.println("consumed " + msg);
                    Thread.sleep(70);                // slower than producer
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        section("3) LinkedBlockingQueue - unbounded by default");
        BlockingQueue<Integer> lbq = new LinkedBlockingQueue<>();       // Integer.MAX_VALUE capacity
        for (int i = 0; i < 10_000; i++) lbq.offer(i);
        System.out.println("LinkedBlockingQueue size = " + lbq.size());

        section("4) SynchronousQueue - zero capacity, direct hand-off");
        // Put blocks until another thread is ready to take. We hand one over
        // here using a tiny background thread.
        BlockingQueue<String> sq = new SynchronousQueue<>();
        new Thread(() -> {
            try {
                Thread.sleep(50);
                sq.put("handed-off");
            } catch (InterruptedException ignored) {}
        }).start();
        long t2 = System.currentTimeMillis();
        String got = sq.take();
        System.out.println("got '" + got + "' after ~" + (System.currentTimeMillis() - t2) + " ms");

        section("5) drainTo - move everything to another collection at once");
        java.util.List<Integer> dump = new java.util.ArrayList<>();
        lbq.drainTo(dump, 5);          // move at most 5 items
        System.out.println("drained 5    = " + dump);
        System.out.println("queue size   = " + lbq.size());

        // OUTPUT (timings + interleaving vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
