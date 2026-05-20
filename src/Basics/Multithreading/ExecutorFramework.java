package Basics.Multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The Executor Framework
 * ----------------------
 * Introduced in Java 5, the framework decouples WORK from the THREAD
 * that runs it. The hierarchy:
 *
 *      Executor                 - execute(Runnable)
 *         |
 *      ExecutorService          - submit, invokeAll, invokeAny, shutdown
 *         |
 *      ScheduledExecutorService - schedule(...), scheduleAtFixedRate, ...
 *
 *
 * Why use it
 * ----------
 *   - Reuse threads (no spin-up cost per task).
 *   - Bounded resources (no thread bombs).
 *   - Structured result handling (Future/Callable).
 *   - Built-in shutdown semantics.
 *
 *
 * Common factories (Executors.*)
 * ------------------------------
 *   newFixedThreadPool(n)       - n workers, unbounded queue.
 *   newCachedThreadPool()       - 0..∞ workers, SynchronousQueue, 60s reap.
 *   newSingleThreadExecutor()   - guaranteed serial execution.
 *   newScheduledThreadPool(n)   - delayed and periodic tasks.
 *   newVirtualThreadPerTaskExecutor()       - Java 21, one VT per task.
 *   newWorkStealingPool(parallelism)        - ForkJoinPool wrapper.
 *
 *
 * Java 19+ goodies
 * ----------------
 *   - ExecutorService now implements AutoCloseable — use try-with-resources.
 *   - newVirtualThreadPerTaskExecutor — perfect for I/O-bound fan-out.
 *
 *
 * Key methods
 * -----------
 *   execute(Runnable)
 *   submit(Runnable)        -> Future<?>
 *   submit(Callable<V>)     -> Future<V>
 *   submit(Runnable, V)     -> Future<V>
 *   invokeAll(Collection)   -> wait for all, return list of Futures
 *   invokeAny(Collection)   -> wait for the first SUCCESS, cancel rest
 *   shutdown / shutdownNow / awaitTermination / isShutdown / isTerminated
 */

public class ExecutorFramework {

    public static void main(String[] args) throws Exception {

        section("1) newFixedThreadPool — submit + Future");
        try (ExecutorService es = Executors.newFixedThreadPool(3)) {

            Future<Integer> f = es.submit(() -> {
                Thread.sleep(80);
                return 21 * 2;
            });
            System.out.println("answer = " + f.get());

            section("2) execute (fire-and-forget Runnable)");
            es.execute(() -> System.out.println("  ran on " + Thread.currentThread().getName()));
            es.execute(() -> System.out.println("  ran on " + Thread.currentThread().getName()));
            Thread.sleep(100);

            section("3) invokeAll — wait for ALL");
            List<Callable<Integer>> jobs = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                final int n = i;
                jobs.add(() -> { Thread.sleep(60); return n * n; });
            }
            long t0 = System.currentTimeMillis();
            List<Future<Integer>> all = es.invokeAll(jobs);
            int sum = 0; for (Future<Integer> r : all) sum += r.get();
            System.out.println("sum of squares = " + sum + " in "
                    + (System.currentTimeMillis() - t0) + " ms");

            section("4) invokeAny — first successful wins, cancel rest");
            String winner = es.invokeAny(List.of(
                    () -> { Thread.sleep(150); return "slow";  },
                    () -> { Thread.sleep(50);  return "fast";  },
                    () -> { Thread.sleep(300); return "slower"; }
            ));
            System.out.println("invokeAny winner = " + winner);
        }   // try-with-resources -> shutdown + awaitTermination

        section("5) newCachedThreadPool — grows on demand");
        try (ExecutorService cached = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 5; i++) {
                final int id = i;
                cached.execute(() -> { sleep(60); System.out.println("  cached " + id); });
            }
        }

        section("6) newSingleThreadExecutor — strict serial");
        try (ExecutorService one = Executors.newSingleThreadExecutor()) {
            for (int i = 0; i < 3; i++) {
                final int id = i;
                one.execute(() -> { System.out.println("  serial " + id); sleep(30); });
            }
        }

        section("7) newVirtualThreadPerTaskExecutor — Java 21");
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> fs = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                final int id = i;
                fs.add(vts.submit(() -> { Thread.sleep(50); return id; }));
            }
            int total = 0;
            for (Future<Integer> f : fs) total += f.get();
            System.out.println("VT sum = " + total + " (20 cheap virtual threads)");
        }

        section("8) Manual shutdown semantics");
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.execute(() -> sleep(80));
        es.execute(() -> sleep(80));
        es.shutdown();                               // accept no more
        System.out.println("isShutdown=" + es.isShutdown());
        if (!es.awaitTermination(2, TimeUnit.SECONDS)) {
            es.shutdownNow();                         // try to interrupt
        }
        System.out.println("isTerminated=" + es.isTerminated());

        section("done");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
