package Phase7_Concurrency.Multithreading;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService
 * ------------------------
 * Delayed and recurring tasks without writing your own scheduler.
 *
 *      ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
 *
 *
 * The methods
 * -----------
 *   schedule(Runnable, delay, unit)         - run once after delay
 *   schedule(Callable<V>, delay, unit)      - run once and produce V
 *   scheduleAtFixedRate(r, initial, period, unit)
 *                                            - START every `period`, regardless
 *                                              of how long previous ran
 *                                              (catches up after a slow run).
 *   scheduleWithFixedDelay(r, initial, delay, unit)
 *                                            - WAIT `delay` AFTER each run finishes
 *                                              (no catch-up).
 *
 *
 * Difference at a glance
 *
 *      atFixedRate( period=100 ms ):
 *          start 0   100   200   300 ...    even if a run took 150ms,
 *                                            the next still starts at 200.
 *
 *      withFixedDelay( delay=100 ms ):
 *          run, wait 100, run, wait 100, ...
 *
 *
 * Cancelling
 * ----------
 *   ScheduledFuture<?> f = ses.scheduleAtFixedRate(...);
 *   f.cancel(/* mayInterrupt= * / false);
 *
 *
 * Failure semantics
 * -----------------
 *   - If a periodic task THROWS, subsequent runs are suppressed and the
 *     ScheduledFuture's get() will rethrow. ALWAYS catch inside the task
 *     if you want it to keep running.
 *
 *
 * Java 21
 * -------
 *   - You can pass a virtual-thread factory to the scheduler if you want
 *     each scheduled run to execute on a virtual thread:
 *
 *         Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
 */

public class ScheduledExecutorDemo {

    public static void main(String[] args) throws Exception {

        try (ScheduledExecutorService ses = Executors.newScheduledThreadPool(2)) {

            section("1) schedule — one-shot delay");
            long t0 = System.currentTimeMillis();
            ScheduledFuture<String> one = ses.schedule(
                    () -> "fired @ " + (System.currentTimeMillis() - t0) + " ms",
                    100, TimeUnit.MILLISECONDS);
            System.out.println(one.get());

            section("2) scheduleAtFixedRate — every 80ms regardless of duration");
            int[] runs = { 0 };
            long start = System.currentTimeMillis();
            ScheduledFuture<?> tick = ses.scheduleAtFixedRate(() -> {
                runs[0]++;
                System.out.println("  tick #" + runs[0] + " at " + (System.currentTimeMillis() - start) + "ms");
            }, 0, 80, TimeUnit.MILLISECONDS);
            Thread.sleep(350);
            tick.cancel(false);
            System.out.println("ran " + runs[0] + " times");

            section("3) scheduleWithFixedDelay — wait AFTER each run");
            long s2 = System.currentTimeMillis();
            ScheduledFuture<?> tock = ses.scheduleWithFixedDelay(() -> {
                System.out.println("  tock at " + (System.currentTimeMillis() - s2) + "ms");
                sleep(40);    // pretend the run takes work
            }, 0, 80, TimeUnit.MILLISECONDS);
            Thread.sleep(400);
            tock.cancel(false);

            section("4) Failure suppresses further runs unless you catch");
            int[] fragileRuns = { 0 };
            ScheduledFuture<?> fragile = ses.scheduleAtFixedRate(() -> {
                fragileRuns[0]++;
                if (fragileRuns[0] == 2) throw new RuntimeException("boom");
                System.out.println("  fragile #" + fragileRuns[0]);
            }, 0, 50, TimeUnit.MILLISECONDS);
            Thread.sleep(300);
            System.out.println("fragile runs total = " + fragileRuns[0] + " (should be 2 — failure killed it)");
            try { fragile.get(50, TimeUnit.MILLISECONDS); }
            catch (Exception e) { System.out.println("future rethrew: " + e.getClass().getSimpleName()); }

            section("5) Catch inside the task to keep running");
            int[] robustRuns = { 0 };
            ScheduledFuture<?> robust = ses.scheduleAtFixedRate(() -> {
                try {
                    robustRuns[0]++;
                    if (robustRuns[0] == 2) throw new RuntimeException("controlled");
                    System.out.println("  robust #" + robustRuns[0]);
                } catch (Throwable t) {
                    System.out.println("  caught locally: " + t.getMessage());
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
            Thread.sleep(300);
            robust.cancel(false);
            System.out.println("robust runs = " + robustRuns[0] + " (continued past failure)");

            section("done");
        }   // shutdown via AutoCloseable
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
