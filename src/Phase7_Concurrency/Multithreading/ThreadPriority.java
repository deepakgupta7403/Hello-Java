package Phase7_Concurrency.Multithreading;

/**
 * Thread Priority
 * ---------------
 * Every Thread has an integer PRIORITY between 1 and 10 (inclusive):
 * <p>
 *
 *      Thread.MIN_PRIORITY   = 1
 *      Thread.NORM_PRIORITY  = 5     (the default)
 *      Thread.MAX_PRIORITY   = 10
 * <p>
 *
 * Priority is a HINT to the OS scheduler that this thread should be
 * preferred over lower-priority ones. It is NOT a guarantee.
 * <p>
 *
 * What actually happens
 * ---------------------
 *   - The JVM maps Java's 1..10 to native OS priorities. Each OS has a
 *     different range and policy:
 *         Linux  - mapping varies, often a small effect or none in CFS.
 *         Windows - mapped to thread priority levels (idle..time-critical).
 *         macOS  - QoS-influenced; not a strict ordering.
 *   - On most modern desktops/servers, priority has very little effect.
 *     Do NOT rely on it for correctness.
 * <p>
 *
 * Inheritance
 * -----------
 * A new Thread inherits its priority from the thread that created it
 * (unless explicitly set before start()).
 * <p>
 *
 * Validity
 * --------
 *   setPriority(int) throws IllegalArgumentException if out of [1, 10].
 *   setPriority is also capped by the parent ThreadGroup's max priority.
 * <p>
 *
 * When NOT to use priorities
 * --------------------------
 *   - To "make this thread go faster than another." It rarely does.
 *   - To ensure ordering or fairness — use a queue / synchronizer.
 *   - To prevent starvation — priority-based starvation is a thing.
 * <p>
 *
 * When priorities can help
 * ------------------------
 *   - To run a low-priority background task (Thread.MIN_PRIORITY) so it
 *     doesn't crowd out interactive work.
 *   - On dedicated real-time-ish systems where you actually control the
 *     OS scheduler. On general-purpose JVMs, treat priorities as polite
 *     hints only.
 */

public class ThreadPriority {

    public static void main(String[] args) throws InterruptedException {

        section("1) Constants and defaults");
        System.out.println("MIN  = " + Thread.MIN_PRIORITY);
        System.out.println("NORM = " + Thread.NORM_PRIORITY);
        System.out.println("MAX  = " + Thread.MAX_PRIORITY);
        System.out.println("main priority = " + Thread.currentThread().getPriority());

        section("2) Get/set");
        Thread t = new Thread(() -> {}, "p-demo");
        System.out.println("initial = " + t.getPriority());
        t.setPriority(Thread.MAX_PRIORITY);
        System.out.println("after MAX = " + t.getPriority());

        section("3) Out-of-range throws");
        try { t.setPriority(11); }
        catch (IllegalArgumentException e) { System.out.println("expected: " + e); }

        section("4) Inheritance — child inherits parent's priority");
        Thread parent = new Thread(() -> {
            Thread child = new Thread(() -> {}, "child");
            System.out.println("parent priority = " + Thread.currentThread().getPriority());
            System.out.println("child  priority = " + child.getPriority());
        }, "parent");
        parent.setPriority(7);
        parent.start();
        parent.join();

        section("5) HIGH vs LOW — observe (or fail to observe) effect");
        // Two threads compute; the higher-priority one MAY get more cycles.
        // On many systems you'll see NO difference. Don't depend on this.
        long[] counts = new long[2];
        Thread hi = new Thread(() -> counts[0] = busy(800));
        Thread lo = new Thread(() -> counts[1] = busy(800));
        hi.setPriority(Thread.MAX_PRIORITY);
        lo.setPriority(Thread.MIN_PRIORITY);
        hi.start(); lo.start();
        hi.join();  lo.join();
        System.out.println("hi count = " + counts[0]);
        System.out.println("lo count = " + counts[1]);
        System.out.println("(any difference is OS-dependent and not guaranteed)");

        section("6) Background task pattern");
        Thread cleanup = new Thread(() -> {
            // pretend GC, log flushing, cache refresh, etc.
            for (int i = 0; i < 3; i++) {
                System.out.println("  cleanup pass " + i);
                try { Thread.sleep(30); } catch (InterruptedException ignored) {}
            }
        }, "cleanup");
        cleanup.setPriority(Thread.MIN_PRIORITY);
        cleanup.setDaemon(true);                    // don't keep JVM alive
        cleanup.start();
        cleanup.join();

        section("done");
    }

    /** Count as many increments as fit in ms milliseconds. */
    private static long busy(long ms) {
        long end = System.currentTimeMillis() + ms;
        long n = 0;
        while (System.currentTimeMillis() < end) n++;
        return n;
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
