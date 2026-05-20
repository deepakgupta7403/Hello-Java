package Phase7_Concurrency.Multithreading;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier
 * -------------
 * A RESETTABLE barrier for a FIXED number of "parties." Each party
 * calls await(), which blocks. When all N parties have arrived, they
 * are RELEASED at the same time, and the barrier resets for the next
 * round.
 *
 *      CyclicBarrier b = new CyclicBarrier(N);
 *      CyclicBarrier b = new CyclicBarrier(N, barrierAction);
 *
 * Optionally an action runs ONCE per round, on the last-arriving
 * thread, BEFORE any party returns from await().
 *
 *
 * Use cases
 * ---------
 *   - Parallel iterative algorithms (every worker computes a slice; all
 *     wait for the round to finish before starting the next).
 *   - Game-loop tick synchronisation across worker threads.
 *   - Simulation steps.
 *
 *
 * Important methods
 * -----------------
 *   await()                - wait at the barrier
 *   await(time, unit)      - timed wait
 *   getParties()           - N (fixed)
 *   getNumberWaiting()     - how many have already arrived
 *   reset()                - break and reset the barrier (waiters throw
 *                              BrokenBarrierException)
 *
 *
 * BrokenBarrier semantics
 * -----------------------
 * If ANY party fails the round (timeout, interrupt, exception thrown
 * from barrier action), ALL waiters get BrokenBarrierException and the
 * barrier needs to be reset() before re-use. This makes the failure
 * mode explicit instead of silent stalls.
 */

public class CyclicBarrierDemo {

    public static void main(String[] args) throws Exception {

        section("1) Three rounds of work, all workers lockstep per round");
        int N = 4;
        int rounds = 3;
        CyclicBarrier barrier = new CyclicBarrier(N, () ->
                System.out.println("  >>> barrier action — round complete <<<"));

        Thread[] workers = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            workers[i] = new Thread(() -> {
                try {
                    for (int r = 1; r <= rounds; r++) {
                        Thread.sleep(30 + 10 * id);
                        System.out.println("  worker " + id + " finished round " + r);
                        barrier.await();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }, "w-" + i);
        }
        for (Thread t : workers) t.start();
        for (Thread t : workers) t.join();
        System.out.println("all rounds done");

        section("2) BrokenBarrierException on timeout");
        CyclicBarrier flaky = new CyclicBarrier(2);
        Thread arrives = new Thread(() -> {
            try {
                flaky.await(100, TimeUnit.MILLISECONDS);
                System.out.println("(unexpected) arrived");
            } catch (Exception e) {
                System.out.println("arrives: " + e.getClass().getSimpleName());
            }
        }, "arrives");
        arrives.start();
        arrives.join();                                       // never paired — times out
        // The barrier is now BROKEN. New await() throws BrokenBarrierException.
        try { flaky.await(50, TimeUnit.MILLISECONDS); }
        catch (Exception e) { System.out.println("after break: " + e.getClass().getSimpleName()); }
        flaky.reset();
        System.out.println("after reset, isBroken? " + flaky.isBroken() + ", parties=" + flaky.getParties());

        section("3) Mini lockstep simulation — sum partial slices then combine each round");
        int slices = 3;
        int rounds3 = 3;
        int[] data = { 1, 2, 3, 4, 5, 6 };                 // 6 items
        int[] partialSums = new int[slices];
        CyclicBarrier b = new CyclicBarrier(slices, () -> {
            int total = 0;
            for (int v : partialSums) total += v;
            System.out.println("  round total = " + total);
        });

        Thread[] sl = new Thread[slices];
        for (int s = 0; s < slices; s++) {
            final int idx = s;
            sl[s] = new Thread(() -> {
                try {
                    for (int r = 0; r < rounds3; r++) {
                        int sum = 0;
                        for (int k = idx * 2; k < idx * 2 + 2; k++) sum += data[k] * (r + 1);
                        partialSums[idx] = sum;
                        b.await();                             // round boundary
                    }
                } catch (Exception ignored) {}
            }, "slice-" + s);
        }
        for (Thread t : sl) t.start();
        for (Thread t : sl) t.join();

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
