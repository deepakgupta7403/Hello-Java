package Phase7_Concurrency.Multithreading;

import java.time.Duration;
import java.time.Instant;

/**
 * Thread.sleep(...)
 * -----------------
 * Static method on Thread that PAUSES the CURRENT thread for at least
 * the specified amount of time. Two overloads:
 *
 *      Thread.sleep(long millis)
 *      Thread.sleep(long millis, int nanos)
 *      Thread.sleep(Duration d)            // Java 19+
 *
 *
 * Key facts
 * ---------
 *   1. Sleep is for the CURRENT thread only. There is no "sleep that
 *      other thread" call. If you need that, signal it and have IT call
 *      sleep / wait on something.
 *
 *   2. Sleep gives up the CPU. The thread enters TIMED_WAITING.
 *
 *   3. Sleep does NOT release locks. If you fell asleep inside a
 *      synchronized block you still own the monitor — every other thread
 *      waiting on it stays blocked.
 *
 *   4. Sleep is INTERRUPTIBLE. If another thread calls .interrupt() on
 *      this thread, sleep throws InterruptedException and clears the
 *      interrupt flag. You should usually restore it.
 *
 *   5. Sleep duration is a LOWER BOUND, not exact. You can sleep longer
 *      than requested (OS scheduling, GC, etc.) but not less.
 *
 *
 * What sleep is NOT for
 * ---------------------
 *   - Waiting for a condition: use wait()/notify or LockSupport.park or
 *     a CountDownLatch.
 *   - Throttling: use a real rate limiter or Semaphore.
 *   - Coordination: use join() / barriers / latches.
 *
 * Polling with sleep is rarely correct and almost always wasteful.
 */

public class ThreadSleepMethod {

    public static void main(String[] args) throws InterruptedException {

        section("1) Basic sleep");
        Instant before = Instant.now();
        Thread.sleep(100);
        System.out.println("slept ~" + Duration.between(before, Instant.now()).toMillis() + " ms");

        section("2) Duration overload (Java 19+)");
        Thread.sleep(Duration.ofMillis(150));
        System.out.println("Duration.ofMillis(150) — done");

        section("3) sleep is interruptible");
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(5_000);
                System.out.println("WOULD have slept 5s");
            } catch (InterruptedException ie) {
                System.out.println("interrupted! flag is now = " + Thread.currentThread().isInterrupted());
                // Best practice: re-set the flag because catch CLEARED it.
                Thread.currentThread().interrupt();
                System.out.println("flag restored = " + Thread.currentThread().isInterrupted());
            }
        }, "sleeper");
        t.start();
        Thread.sleep(100);                        // let it enter sleep
        t.interrupt();
        t.join();

        section("4) sleep does NOT release locks");
        Object lock = new Object();
        Thread holder = new Thread(() -> {
            synchronized (lock) {
                System.out.println("holder acquired the lock, now sleeping for 200ms");
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                System.out.println("holder releasing lock");
            }
        }, "holder");
        Thread waiter = new Thread(() -> {
            long t0 = System.currentTimeMillis();
            synchronized (lock) {
                System.out.println("waiter got lock after " + (System.currentTimeMillis() - t0) + " ms");
            }
        }, "waiter");
        holder.start();
        Thread.sleep(30);                         // ensure holder grabs first
        waiter.start();
        holder.join(); waiter.join();

        section("5) Sleeping zero — like yield");
        Thread.sleep(0);                          // not a no-op; gives the scheduler a chance
        System.out.println("Thread.sleep(0) returned");

        section("6) sleep is for the CURRENT thread");
        // There is no Thread#sleepOther — you cannot directly pause another
        // thread. You'd have to cooperate (signal it via a flag, then it
        // chooses to sleep).
        Thread other = new Thread(() -> System.out.println("running normally"));
        other.start(); other.join();

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
