package Phase7_Concurrency.Multithreading;

/**
 * Daemon Threads
 * --------------
 * A thread can be a USER thread (default) or a DAEMON thread. The
 * difference is only one thing — and it's about JVM SHUTDOWN:
 * <p>
 *
 *      The JVM EXITS when all USER threads have finished.
 *      Any remaining DAEMON threads are killed abruptly.
 * <p>
 *
 * Use cases
 * ---------
 *   - Background maintenance: GC helpers, log flushing, metrics
 *     sampling, cache eviction.
 *   - Anything that shouldn't keep the JVM alive on its own.
 * <p>
 *
 * Rules
 * -----
 *   1. setDaemon(true) MUST be called BEFORE start().
 *      Calling it later throws IllegalThreadStateException.
 *   2. A new thread INHERITS the daemon flag of its creator.
 *   3. Daemons get NO orderly shutdown when the JVM dies — finally
 *     blocks may not run, resources may not be closed. So:
 *        DO NOT use daemons for tasks that MUST clean up
 *        (DB writers, file appenders, network flushes).
 * <p>
 *
 * Daemons in the JDK
 * ------------------
 * Many JVM internals are daemons by default:
 *   - GC threads, JIT compiler, Reference Handler, Finalizer
 *   - Common-ForkJoin pool's workers (when not user-set)
 *   - Timer threads (if you opt in)
 * <p>
 *
 * Daemons and ExecutorService
 * ---------------------------
 * By default, threads from Executors.newXxxThreadPool are USER threads.
 * That's why a forgotten ExecutorService that isn't shutdown() can keep
 * your JVM running. Use a ThreadFactory that returns daemons if you want
 * shutdown-on-exit semantics.
 */

public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {

        section("1) Default is USER thread");
        Thread normal = new Thread(() -> {});
        System.out.println("default daemon = " + normal.isDaemon());     // false

        section("2) setDaemon must come BEFORE start()");
        Thread d = new Thread(() -> {});
        d.setDaemon(true);
        System.out.println("daemon = " + d.isDaemon());
        d.start();
        try {
            d.setDaemon(false);
        } catch (IllegalThreadStateException e) {
            System.out.println("expected: " + e);
        }
        d.join();

        section("3) A daemon runs but doesn't keep the JVM alive");
        // Spawn a daemon that prints forever. The JVM will exit when main
        // returns, abandoning the daemon mid-stride.
        Thread heartbeat = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println("  heartbeat " + i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException ignored) {}
        }, "heartbeat");
        heartbeat.setDaemon(true);
        heartbeat.start();
        Thread.sleep(200);
        System.out.println("main returning — heartbeat will be terminated abruptly");

        section("4) Daemon WARNING — finally may not run on JVM shutdown");
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            } finally {
                System.out.println("  finally in daemon (may or may not appear)");
            }
        }, "fin");
        t.setDaemon(true);
        t.start();

        section("5) Child inherits daemon flag from parent");
        Thread parent = new Thread(() -> {
            Thread child = new Thread(() -> {});
            System.out.println("parent daemon = " + Thread.currentThread().isDaemon());
            System.out.println("child  daemon = " + child.isDaemon());
        }, "parent");
        parent.setDaemon(true);
        parent.start();
        parent.join();

        section("done (main returning)");
        // The JVM will not wait for the daemons spawned above.
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
