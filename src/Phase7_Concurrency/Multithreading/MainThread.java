package Phase7_Concurrency.Multithreading;

/**
 * The Main Thread
 * ---------------
 * When you launch a Java program, the JVM creates a thread named "main"
 * and calls your public static void main(String[]) on it. Everything
 * runs on that thread until you spawn another one.
 *
 *
 * Properties of the main thread
 * -----------------------------
 *   - Name              : "main"
 *   - Group             : "main" (ThreadGroup)
 *   - Daemon            : false (it KEEPS the JVM alive)
 *   - Default priority  : 5 (Thread.NORM_PRIORITY)
 *   - Uncaught handler  : ThreadGroup default, which prints to System.err
 *
 *
 * Special role
 * ------------
 *   - The JVM CONTINUES TO RUN as long as ANY non-daemon thread is alive.
 *     If you never spawn a non-daemon thread, the JVM exits when main()
 *     returns. If you do spawn non-daemons, the JVM waits for them too.
 *
 *   - main() returning does NOT immediately terminate other threads —
 *     they keep running until they finish or call System.exit.
 *
 *
 * Background JVM threads
 * ----------------------
 * Even a "trivial" Hello World runs alongside the JVM's own threads:
 *   - Reference Handler  - drives Reference / Cleaner
 *   - Finalizer          - runs finalize() (deprecated)
 *   - Signal Dispatcher  - delivers OS signals
 *   - Common-Cleaner     - java.lang.ref.Cleaner workers
 *   - Notification Thread - JMX
 *   - GC threads         - depends on collector (G1, ZGC, ...)
 *
 * They are DAEMON threads so the JVM exits once your user threads do.
 */

public class MainThread {

    public static void main(String[] args) throws InterruptedException {

        section("1) Inspect the main thread");
        Thread main = Thread.currentThread();
        System.out.println("name     = " + main.getName());
        System.out.println("group    = " + main.getThreadGroup().getName());
        System.out.println("daemon   = " + main.isDaemon());
        System.out.println("priority = " + main.getPriority());
        System.out.println("state    = " + main.getState());
        System.out.println("id       = " + main.threadId());

        section("2) Rename main");
        // Useful for log lines and dumps. Doesn't change behaviour.
        main.setName("application-main");
        System.out.println("new name = " + Thread.currentThread().getName());

        section("3) Set a default uncaught handler");
        Thread.setDefaultUncaughtExceptionHandler((t, ex) ->
                System.err.println("UNCAUGHT in " + t.getName() + " -> " + ex));
        // Fire one off to see it triggered:
        Thread boomer = new Thread(() -> { throw new RuntimeException("kaboom"); }, "boomer");
        boomer.start();
        boomer.join();

        section("4) Spawned non-daemon threads keep the JVM alive after main returns");
        Thread tail = new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            System.out.println("tail still here, even though main() has likely returned");
        }, "tail");
        tail.start();
        // We DON'T join. main() will return — but the JVM waits for `tail`.
        System.out.println("main returning, but `tail` is non-daemon and will finish");

        section("5) Snapshot of all live threads right now");
        Thread.getAllStackTraces().keySet().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(t -> System.out.printf("  %-30s daemon=%-5b state=%s%n",
                        t.getName(), t.isDaemon(), t.getState()));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
