package Phase7_Concurrency.Multithreading;

import java.time.Duration;

/**
 * Thread.join(...)
 * ----------------
 * Wait for another thread to finish.
 *
 *      otherThread.join();             // wait forever
 *      otherThread.join(ms);           // wait up to ms milliseconds
 *      otherThread.join(ms, nanos);    // ditto, plus nanos
 *      otherThread.join(Duration d);   // Java 19+
 *
 * The CURRENT thread is parked until either:
 *   (a) `otherThread` reaches TERMINATED, or
 *   (b) the optional timeout elapses, or
 *   (c) the current thread is interrupted (InterruptedException).
 *
 *
 * Why join?
 * ---------
 *   - Coordinate "wait for this background task to finish" without busy
 *     waiting.
 *   - Compute fan-out + fan-in patterns.
 *   - Make sure a worker has done its job before reading the result it
 *     produced (the JMM guarantees a happens-before edge from the joined
 *     thread's last action to the joiner's wake-up).
 *
 *
 * Memory model promise
 * --------------------
 * Everything `otherThread` did BEFORE finishing is visible to the caller
 * of `otherThread.join()` AFTER the join returns. This is one of the
 * cleanest happens-before relationships you get for free.
 *
 *
 * Pitfalls
 * --------
 *   - Joining the current thread (Thread.currentThread().join()) hangs.
 *     Don't.
 *   - join(0) means "wait indefinitely", NOT "don't wait."
 *   - Like other waits, join is interruptible — handle the exception.
 *   - For many tasks, use an ExecutorService + Future.get() instead of
 *     juggling Threads and joins.
 */

public class ThreadJoinMethod {

    public static void main(String[] args) throws InterruptedException {

        section("1) join() — wait for one worker");
        int[] result = new int[1];
        Thread worker = new Thread(() -> {
            sleepy(150);
            result[0] = 42;                      // produced inside the worker
        }, "compute");
        worker.start();
        worker.join();                            // happens-before edge
        System.out.println("result = " + result[0] + "  (safely visible)");

        section("2) join(timeout) — give up after a while");
        Thread slow = new Thread(() -> sleepy(500), "slow");
        slow.start();
        long t0 = System.currentTimeMillis();
        slow.join(100);                           // wait up to 100ms
        long waited = System.currentTimeMillis() - t0;
        System.out.println("waited ~" + waited + " ms, slow alive? " + slow.isAlive());
        slow.join();                              // now wait the rest

        section("3) join(Duration) — Java 19+");
        Thread d = new Thread(() -> sleepy(80), "duration");
        d.start();
        d.join(Duration.ofMillis(500));
        System.out.println("d finished, alive = " + d.isAlive());

        section("4) Fan-out / fan-in: many workers + many joins");
        int n = 4;
        Thread[] workers = new Thread[n];
        int[] partial = new int[n];
        for (int i = 0; i < n; i++) {
            final int idx = i;
            workers[i] = new Thread(() -> {
                sleepy(50 + idx * 20);
                partial[idx] = idx * idx;
            }, "w-" + i);
            workers[i].start();
        }
        for (Thread w : workers) w.join();        // wait for all
        int sum = 0;
        for (int v : partial) sum += v;
        System.out.println("sum of partials = " + sum);

        section("5) join is interruptible");
        Thread forever = new Thread(() -> sleepy(60_000), "forever");
        forever.start();
        Thread joiner = new Thread(() -> {
            try { forever.join(); }
            catch (InterruptedException ie) {
                System.out.println("joiner was interrupted");
                Thread.currentThread().interrupt();
            }
        }, "joiner");
        joiner.start();
        sleepy(50);
        joiner.interrupt();                       // wake the joiner
        joiner.join();
        forever.interrupt();                      // and cleanup
        forever.join();

        section("done");
    }

    private static void sleepy(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
