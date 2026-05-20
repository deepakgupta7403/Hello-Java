package Basics.Multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Callable<V> and Future<V>
 * -------------------------
 * Runnable is great when the work has NO RETURN VALUE and never throws
 * checked exceptions. When you need a RESULT, use:
 *
 *      @FunctionalInterface
 *      public interface Callable<V> {
 *          V call() throws Exception;
 *      }
 *
 * Submit it to an ExecutorService and you get a Future<V>:
 *
 *      Future<V> f = executor.submit(callable);
 *      V result = f.get();        // blocks until ready
 *
 *
 * Future contract
 * ---------------
 *   get()                     - block until done; throw if task failed.
 *   get(time, unit)           - block up to a timeout (TimeoutException).
 *   cancel(boolean mayInterrupt) - request cancellation. If true and the
 *                              task is running, interrupt the worker.
 *   isDone()                  - finished normally, exceptionally, or cancelled.
 *   isCancelled()
 *
 *
 * What .get() throws
 * ------------------
 *   InterruptedException    - the caller was interrupted while waiting.
 *   ExecutionException      - the task threw an exception. The cause is
 *                              the original exception.
 *   CancellationException   - the task was cancelled.
 *   TimeoutException        - (timed version only) deadline passed.
 *
 *
 * FutureTask
 * ----------
 * A Runnable + Future. Useful when you want to run a Callable on a raw
 * Thread (without an ExecutorService) and still grab the result:
 *
 *      FutureTask<Integer> task = new FutureTask<>(() -> 42);
 *      new Thread(task).start();
 *      int x = task.get();
 *
 *
 * Modern alternatives
 * -------------------
 *   - CompletableFuture — composable async chains, no blocking get().
 *   - StructuredTaskScope (Java 21 preview) — fan-out + result handling
 *     as a single unit.
 */

public class CallableAndFuture {

    public static void main(String[] args) throws Exception {

        section("1) Callable returns a value");
        Callable<Integer> square = () -> {
            Thread.sleep(100);
            return 7 * 7;
        };

        try (ExecutorService es = Executors.newFixedThreadPool(2)) {

            Future<Integer> f = es.submit(square);
            System.out.println("isDone before get? " + f.isDone());
            int v = f.get();                         // blocks
            System.out.println("result = " + v + ", isDone? " + f.isDone());

            section("2) Callable can throw checked exceptions");
            Future<String> oops = es.submit(() -> {
                if (Math.random() < 2) throw new java.io.IOException("disk gone");
                return "never";
            });
            try {
                oops.get();
            } catch (ExecutionException ee) {
                System.out.println("ExecutionException; cause = " + ee.getCause());
            }

            section("3) get(timeout) — give up after a deadline");
            Future<String> slow = es.submit(() -> { Thread.sleep(500); return "done"; });
            try {
                slow.get(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException te) {
                System.out.println("timed out; cancelling");
                slow.cancel(true);
            }

            section("4) cancel(mayInterruptIfRunning=true) — interrupt the worker");
            Future<Integer> longRun = es.submit(() -> {
                int sum = 0;
                for (int i = 0; i < 1_000_000_000 && !Thread.currentThread().isInterrupted(); i++) {
                    sum += i;
                }
                return sum;
            });
            Thread.sleep(50);
            boolean cancelled = longRun.cancel(true);
            System.out.println("cancel requested = " + cancelled);
            try { longRun.get(); }
            catch (CancellationException ce) { System.out.println("got CancellationException"); }

            section("5) Fan out + fan in with invokeAll");
            List<Callable<Integer>> jobs = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                final int n = i;
                jobs.add(() -> { Thread.sleep(80); return n * n; });
            }
            long t0 = System.currentTimeMillis();
            List<Future<Integer>> results = es.invokeAll(jobs);
            int total = 0;
            for (Future<Integer> r : results) total += r.get();
            System.out.println("sum of squares = " + total + " in " +
                    (System.currentTimeMillis() - t0) + " ms (parallel ~80ms)");
        }

        section("6) FutureTask on a raw Thread (no ExecutorService)");
        FutureTask<String> task = new FutureTask<>(() -> {
            Thread.sleep(80);
            return "hello from FutureTask";
        });
        new Thread(task, "future-task").start();
        System.out.println(task.get());

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
