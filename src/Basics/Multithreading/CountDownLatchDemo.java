package Basics.Multithreading;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch
 * --------------
 * A ONE-SHOT counter you initialise with N. Threads call await() to
 * block until the counter reaches zero. Other threads call countDown()
 * to decrement.
 *
 *      CountDownLatch ready = new CountDownLatch(N);
 *      ready.await();              // blocks until count is 0
 *      ready.countDown();          // decrement
 *
 *
 * Two classic uses
 * ----------------
 *   1. STARTING GATE — main thread holds workers behind a latch of 1.
 *      When everything is set up, main calls countDown() and they all
 *      sprint at once.
 *
 *   2. COMPLETION GATE — main thread waits behind a latch of N workers.
 *      Each worker calls countDown() on finish. main wakes when the
 *      last one is done. Like join() across N threads, but expressed
 *      explicitly.
 *
 *
 * Important properties
 * --------------------
 *   - ONE-SHOT. The count never resets. For repeated use, see
 *     CyclicBarrier / Phaser.
 *   - await() is INTERRUPTIBLE.
 *   - getCount() returns the current value; for monitoring only.
 *   - countDown() past zero is harmless (count stays at 0).
 *
 *
 * Pitfalls
 * --------
 *   - Forgetting to countDown() in a worker that threw — wrap in
 *     try/finally so the latch always fires.
 *   - Initial count != number of workers if some die early — same fix.
 */

public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Starting gate — main holds workers behind a count-1 latch");
        CountDownLatch start = new CountDownLatch(1);
        int N = 4;
        Thread[] runners = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            runners[i] = new Thread(() -> {
                try {
                    start.await();                            // wait at the gate
                } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
                System.out.println("  runner " + id + " sprints at " + System.currentTimeMillis() % 100_000);
            }, "runner-" + i);
        }
        for (Thread t : runners) t.start();
        Thread.sleep(100);                                    // do setup work
        System.out.println("opening the gate");
        start.countDown();                                    // all of them released together
        for (Thread t : runners) t.join();

        section("2) Completion gate — main waits for N workers");
        CountDownLatch done = new CountDownLatch(N);
        for (int i = 0; i < N; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Thread.sleep((id + 1) * 50);
                    System.out.println("  worker " + id + " finished");
                } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                finally { done.countDown(); }                  // ALWAYS run countDown
            }, "w-" + id).start();
        }
        done.await();
        System.out.println("all workers reported in");

        section("3) await(timeout, unit) — give up after a deadline");
        CountDownLatch slow = new CountDownLatch(1);
        boolean inTime = slow.await(50, TimeUnit.MILLISECONDS);
        System.out.println("count went to 0 in 50ms? " + inTime);

        section("4) Latch is one-shot — extra countDowns are no-ops");
        CountDownLatch oneShot = new CountDownLatch(2);
        oneShot.countDown();
        oneShot.countDown();
        oneShot.countDown();                                   // ignored
        System.out.println("getCount() = " + oneShot.getCount());

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
