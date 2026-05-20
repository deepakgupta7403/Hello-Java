package Phase7_Concurrency.Multithreading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * Thread Interruption
 * -------------------
 * Java has NO safe way to forcefully stop another thread. Instead it has
 * a COOPERATIVE cancellation mechanism: the INTERRUPT FLAG.
 *
 *
 * The Three Calls
 * ---------------
 *   t.interrupt()                 - set the interrupt flag on thread t.
 *                                   If t is currently in a blocking call
 *                                   (sleep, wait, join, park, etc.) it
 *                                   throws InterruptedException AND the
 *                                   flag is CLEARED.
 *
 *   t.isInterrupted()             - read t's flag without changing it.
 *
 *   Thread.interrupted()          - STATIC. Read AND CLEAR the current
 *                                   thread's flag. Use sparingly — you
 *                                   often want to keep the flag set.
 *
 *
 * What gets interrupted
 * ---------------------
 * Built-in interruptible operations (throw InterruptedException):
 *     Thread.sleep / join / wait
 *     Object.wait
 *     BlockingQueue.put / take / offer-with-timeout
 *     CountDownLatch.await
 *     CyclicBarrier.await
 *     Semaphore.acquire
 *     Lock.lockInterruptibly / Condition.await
 *     Future.get
 *     LockSupport.park*  (sets the flag; does NOT throw)
 *
 * Plain compute loops are NOT interrupted automatically — you must
 * check the flag yourself with isInterrupted() / Thread.interrupted().
 *
 *
 * Best Practices
 * --------------
 *   1. NEVER swallow InterruptedException silently. Either:
 *        a) Restore the flag:  Thread.currentThread().interrupt();
 *        b) Wrap and rethrow as a domain exception.
 *      A library that hides the interrupt makes its callers' shutdown
 *      logic impossible.
 *
 *   2. In compute loops, sprinkle:
 *        if (Thread.currentThread().isInterrupted()) break;   // exit cleanly
 *
 *   3. Prefer interruption over a custom "stop" boolean — it integrates
 *      with every JDK blocking primitive for free.
 *
 *   4. interrupt() is the modern replacement for the deprecated
 *      Thread.stop(). Forget Thread.stop ever existed.
 */

public class ThreadInterruption {

    public static void main(String[] args) throws InterruptedException {

        section("1) Interrupting a sleeping thread");
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(5_000);
                System.out.println("woke naturally");
            } catch (InterruptedException ie) {
                System.out.println("sleeper interrupted; flag now = " +
                        Thread.currentThread().isInterrupted());
                // Restore the flag so callers up the stack notice:
                Thread.currentThread().interrupt();
            }
        }, "sleeper");
        sleeper.start();
        Thread.sleep(50);
        sleeper.interrupt();
        sleeper.join();
        System.out.println("flag after death = " + sleeper.isInterrupted()); // false (TERMINATED)

        section("2) Interrupting a compute loop — must CHECK the flag");
        Thread compute = new Thread(() -> {
            long count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 1_000_000 == 0) System.out.println("  still going, count=" + count);
            }
            System.out.println("compute exited cleanly, count=" + count);
        }, "compute");
        compute.start();
        Thread.sleep(50);
        compute.interrupt();
        compute.join();

        section("3) Interrupting BlockingQueue.take()");
        BlockingQueue<Integer> q = new ArrayBlockingQueue<>(1);
        Thread consumer = new Thread(() -> {
            try {
                Integer v = q.take();             // will block forever
                System.out.println("got " + v);
            } catch (InterruptedException ie) {
                System.out.println("take() interrupted");
                Thread.currentThread().interrupt();
            }
        }, "consumer");
        consumer.start();
        Thread.sleep(50);
        consumer.interrupt();
        consumer.join();

        section("4) Interrupted vs isInterrupted (static clears, instance does not)");
        Thread.currentThread().interrupt();
        System.out.println("isInterrupted (no clear) = " + Thread.currentThread().isInterrupted());
        System.out.println("interrupted (clears)     = " + Thread.interrupted());
        System.out.println("isInterrupted now        = " + Thread.currentThread().isInterrupted());

        section("5) LockSupport.park sets the flag but does NOT throw");
        Thread parker = new Thread(() -> {
            LockSupport.park();
            System.out.println("parker unparked; flag = " + Thread.currentThread().isInterrupted());
        }, "parker");
        parker.start();
        Thread.sleep(50);
        parker.interrupt();                       // wakes park() and sets flag
        parker.join();

        section("6) Worst practice — swallowing interrupts");
        // Don't do this:
        try { Thread.sleep(1); }
        catch (InterruptedException ie) { /* swallowed */ }
        // Better:
        try { Thread.sleep(1); }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("flag restored = " + Thread.currentThread().isInterrupted());
        }

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
