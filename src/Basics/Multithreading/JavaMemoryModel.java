package Basics.Multithreading;

/**
 * Java Memory Model (JMM)
 * -----------------------
 * The JMM is the formal specification of how reads and writes from
 * different threads interact. Without it, a program is at the mercy of
 * the JIT compiler, the CPU's memory subsystem, and the OS scheduler —
 * any of which may REORDER or CACHE memory operations.
 *
 *
 * The Two Concerns
 * ----------------
 *   ATOMICITY   - is the read/write a single indivisible step?
 *                 (long and double WITHOUT volatile are NOT guaranteed
 *                  to be atomic on 32-bit platforms.)
 *   VISIBILITY  - when does a thread see another thread's write?
 *                 (Without synchronization, possibly never.)
 *
 *
 * Happens-Before
 * --------------
 * The JMM defines a partial order called HAPPENS-BEFORE. If action A
 * happens-before action B, then A's effects are visible to B.
 *
 * The standard happens-before edges:
 *   1. Program order within a single thread.
 *   2. Monitor unlock → subsequent lock of the SAME monitor.
 *   3. volatile write → subsequent volatile read of the SAME field.
 *   4. Thread.start() → first action of the started thread.
 *   5. Last action of a thread → Thread.join() returning in another.
 *   6. Default initialization (the JLS-defined zero-values) →
 *      any subsequent action.
 *   7. Transitively: A→B and B→C implies A→C.
 *
 * If there is NO happens-before edge between two actions on different
 * threads, the JVM is allowed to reorder / cache them. Bugs that result:
 *
 *   - The reader sees stale data forever (e.g., never-ending loop on
 *     a non-volatile flag).
 *   - The reader sees the new value of one field but the OLD value of
 *     another, even if the writer wrote them in order.
 *   - Constructor-leak: another thread sees a partially-initialised
 *     object via an unsafe publication.
 *
 *
 * Safe Publication
 * ----------------
 * Common idioms that publish an object safely:
 *   - Initialize from a static initializer.
 *   - Store into a `final` field of a properly constructed object.
 *   - Store into a `volatile` field.
 *   - Store into a field protected by a lock.
 *   - Use a thread-safe container (ConcurrentHashMap etc.).
 *
 *
 * `final` Fields — Special Rule
 * -----------------------------
 * Final fields, once a constructor returns, are guaranteed visible to
 * any thread that gets a reference to the object — even without
 * synchronization. This is why immutable objects "just work."
 *
 *
 * Tools in this file
 * ------------------
 *   1. Visibility bug — non-volatile stop flag never sees the update.
 *   2. Fix with volatile.
 *   3. Long write tearing (in practice, hard to demonstrate on 64-bit).
 *   4. Reordering — write-then-write can appear to a reader in either order.
 *   5. final field safe-publication.
 */

public class JavaMemoryModel {

    /** Non-volatile flag. The reader may never see writes to it. */
    static boolean stopNonVolatile = false;

    /** Volatile flag. The reader is GUARANTEED to see writes. */
    static volatile boolean stopVolatile = false;

    /** Non-volatile longs left for reordering demo. */
    static long a, b;

    public static void main(String[] args) throws InterruptedException {

        section("1) Visibility — without volatile, reader may NEVER stop");
        // This demo is racy by design. Some JVMs / JITs WILL show the
        // hang; some won't. Either way, the program is wrong.
        Thread eagerReader = new Thread(() -> {
            long n = 0;
            while (!stopNonVolatile) n++;
            System.out.println("reader exited after " + n + " loops (you got lucky)");
        }, "no-volatile");
        eagerReader.setDaemon(true);            // so the JVM doesn't hang if it spins
        eagerReader.start();
        Thread.sleep(50);
        stopNonVolatile = true;
        // give the reader a tiny grace period; if it's still alive, we abandon it.
        eagerReader.join(500);
        if (eagerReader.isAlive()) {
            System.out.println("reader is STILL spinning — visibility bug demonstrated");
        }

        section("2) Visibility fix — volatile flag is seen immediately");
        Thread goodReader = new Thread(() -> {
            long n = 0;
            while (!stopVolatile) n++;
            System.out.println("goodReader exited after " + n + " loops");
        }, "volatile");
        goodReader.start();
        Thread.sleep(50);
        stopVolatile = true;
        goodReader.join();

        section("3) happens-before via Thread.join");
        // join guarantees the calling thread sees everything the joined
        // thread did. No volatile, no synchronized needed for `result`.
        int[] result = new int[1];
        Thread writer = new Thread(() -> result[0] = 99, "writer");
        writer.start();
        writer.join();                            // happens-before edge
        System.out.println("read after join = " + result[0]);

        section("4) Reordering — writes may not appear in source order to other threads");
        // This is famously hard to observe consistently, but the JMM
        // permits it. Use volatile or synchronized to forbid.
        a = 0; b = 0;
        Thread W = new Thread(() -> { a = 1; b = 2; });
        // a reader may see (a=0,b=2) — i.e., the second write before the first.

        // (No assertion here — just narration. See VolatileKeyword.java for
        //  a concrete demonstration with the publication idiom.)
        W.start(); W.join();
        System.out.println("after join: a=" + a + ", b=" + b);

        section("5) Safe publication via `final`");
        Immutable im = new Immutable(7, 9);
        Thread t = new Thread(() -> System.out.println("sees " + im.x + ", " + im.y));
        t.start(); t.join();

        section("done");
    }

    /** Object with final fields — safe to share without further synchronization. */
    static class Immutable {
        final int x;
        final int y;
        Immutable(int x, int y) { this.x = x; this.y = y; }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
