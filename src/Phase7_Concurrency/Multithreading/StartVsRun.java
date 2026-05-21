package Phase7_Concurrency.Multithreading;

/**
 * Thread.start() vs Thread.run()
 * ------------------------------
 * One of the most common interview / debugging traps in Java.
 * <p>
 *
 *   start()  - Asks the JVM to create a new OS thread (or virtual thread)
 *              and run() will be invoked ON THAT NEW THREAD.
 * <p>
 *
 *   run()    - Just a normal method. Calling it directly executes the body
 *              on the CURRENT thread — no new thread is created at all.
 * <p>
 *
 * Why It Matters
 * --------------
 * Code written for concurrency (printing thread names, holding locks,
 * sleeping) APPEARS to "work" if you call run() — it just runs serially
 * on the caller. No parallelism, no throughput, no concurrency bugs to
 * catch. The bug only shows when load arrives.
 * <p>
 *
 * Other Rules
 * -----------
 *   - start() can be called ONLY ONCE per Thread instance. A second call
 *     throws IllegalThreadStateException.
 *   - run() can be called any number of times like any normal method.
 *   - You override run() to define the work, but you NEVER call it. The
 *     JVM calls it for you when start() spins up the new thread.
 * <p>
 *
 * In Modern Java
 * --------------
 *   - The same distinction applies to virtual threads. Use
 *     Thread.startVirtualThread(r) or Thread.ofVirtual().start(r) instead
 *     of constructing-and-starting two-step.
 *   - Prefer ExecutorService.execute / submit — it removes the start/run
 *     confusion entirely.
 */

public class StartVsRun {

    public static void main(String[] args) throws InterruptedException {

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("  step " + i + " on " + Thread.currentThread().getName());
                pause(30);
            }
        };

        section("1) start() — new thread");
        // The task runs on a thread named "worker"; main is free to do other work.
        Thread good = new Thread(task, "worker");
        good.start();
        for (int i = 0; i < 3; i++) {
            System.out.println("main step " + i + " on " + Thread.currentThread().getName());
            pause(30);
        }
        good.join();

        section("2) run() — same thread, NO concurrency");
        // task.run() blocks main for the entire run. Output is serial.
        Thread bad = new Thread(task, "worker");
        bad.run();                              // <<-- THE TRAP
        System.out.println("main continues only after task.run() returned");

        section("3) start() twice = IllegalThreadStateException");
        Thread once = new Thread(task, "once");
        once.start();
        once.join();
        try {
            once.start();
        } catch (IllegalThreadStateException ex) {
            System.out.println("second start() threw: " + ex);
        }

        section("4) run() can be called many times");
        Thread reusable = new Thread(task, "reusable");
        reusable.run();
        reusable.run();
        System.out.println("each run() executed on " + Thread.currentThread().getName());

        section("5) virtual-thread equivalent");
        Thread v = Thread.ofVirtual().name("v-task").start(task);
        v.join();

        section("done");
    }

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
