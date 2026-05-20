package Basics.Multithreading;

/**
 * java.lang.Runnable
 * ------------------
 * The simplest description of "a unit of work that can be run by a
 * thread." It is a FUNCTIONAL interface with one method:
 *
 *      @FunctionalInterface
 *      public interface Runnable {
 *          void run();
 *      }
 *
 *   - No arguments
 *   - No return value
 *   - No checked exceptions (you must catch / wrap)
 *
 *
 * Why prefer Runnable over extending Thread?
 * ------------------------------------------
 *   1. SEPARATION OF CONCERNS — describes the WORK without coupling it
 *      to a Thread.
 *   2. COMPOSITION — can be passed to Thread, ExecutorService, Timer,
 *      ScheduledExecutorService, ForkJoinPool, etc.
 *   3. INHERITANCE — you can extend any other class you like.
 *   4. LAMBDAS — Runnable r = () -> ...; is concise and capture-friendly.
 *
 *
 * Variants you should know
 * ------------------------
 *   Runnable     - void run(), no exceptions, used by Thread / Executor.execute
 *   Callable<V>  - V call() throws Exception. Used by ExecutorService.submit.
 *   RunnableFuture / FutureTask - Runnable + Future, used internally.
 *
 *
 * Common patterns
 * ---------------
 *   - As a constructor argument:  new Thread(runnable).start()
 *   - As a method argument:       executor.submit(runnable)
 *   - Composing two:              Runnable both = () -> { a.run(); b.run(); };
 *   - Decorating:                 wrap to log start/stop, time, retry
 */

public class RunnableInterface {

    public static void main(String[] args) throws InterruptedException {

        section("1) Classic implementation");
        Runnable classic = new Runnable() {
            @Override public void run() {
                System.out.println("classic Runnable on " + Thread.currentThread().getName());
            }
        };
        new Thread(classic, "classic").start();

        section("2) Lambda — the modern way");
        Runnable lambda = () -> System.out.println("lambda Runnable on " + Thread.currentThread().getName());
        new Thread(lambda, "lambda").start();

        section("3) Method reference");
        Runnable ref = RunnableInterface::work;
        new Thread(ref, "ref").start();

        Thread.sleep(50);   // let the three above run before next demos

        section("4) Composing runnables");
        Runnable a = () -> System.out.println("  step A");
        Runnable b = () -> System.out.println("  step B");
        Runnable both = chain(a, b);
        new Thread(both, "compose").start();
        Thread.sleep(50);

        section("5) Decorating a Runnable");
        Runnable noisy = decorateWithTiming(() -> {
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            System.out.println("  inside the wrapped task");
        }, "noisy");
        new Thread(noisy).start();
        Thread.sleep(150);

        section("6) Runnable cannot throw checked exceptions");
        // run() declares no `throws`. You must catch checked exceptions
        // and wrap them as runtime exceptions if you want to propagate.
        Runnable explodes = () -> {
            try {
                Thread.sleep(50);
                throw new java.io.IOException("checked!");
            } catch (Exception e) {
                throw new RuntimeException("wrapped", e);
            }
        };
        Thread blew = new Thread(explodes, "blew");
        blew.setUncaughtExceptionHandler((t, ex) ->
                System.out.println("  caught from " + t.getName() + " -> " + ex.getMessage()));
        blew.start();
        blew.join();

        section("7) Use Runnable with ExecutorService (preview — see ExecutorFramework.java)");
        var es = java.util.concurrent.Executors.newFixedThreadPool(2);
        es.execute(() -> System.out.println("from pool: " + Thread.currentThread().getName()));
        es.execute(() -> System.out.println("from pool: " + Thread.currentThread().getName()));
        es.shutdown();
        es.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);

        section("done");
    }

    private static void work() {
        System.out.println("static method ref on " + Thread.currentThread().getName());
    }

    /** Chain runnables together: first.run(); second.run(); */
    private static Runnable chain(Runnable first, Runnable second) {
        return () -> { first.run(); second.run(); };
    }

    /** Wrap any Runnable with timing + tagged log lines. */
    private static Runnable decorateWithTiming(Runnable r, String tag) {
        return () -> {
            long t0 = System.nanoTime();
            try { r.run(); }
            finally {
                long ms = (System.nanoTime() - t0) / 1_000_000;
                System.out.println("[" + tag + "] took " + ms + " ms");
            }
        };
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
