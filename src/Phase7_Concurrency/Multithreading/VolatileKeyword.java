package Phase7_Concurrency.Multithreading;

/**
 * The `volatile` Keyword
 * ----------------------
 * `volatile` is a FIELD modifier that gives you two memory-model
 * guarantees — but NOT mutual exclusion.
 *
 *      private volatile boolean stop;
 *      private volatile State state;
 *
 *
 * What volatile guarantees
 * ------------------------
 *   1. VISIBILITY — a write to a volatile field is IMMEDIATELY visible
 *      to subsequent reads of the SAME field, on any thread.
 *   2. ORDERING  — actions BEFORE a volatile write happen-before any
 *      subsequent read of that volatile (the "release/acquire" pair).
 *   3. ATOMIC FOR LONG/DOUBLE — reads and writes of volatile longs and
 *      doubles are atomic even on 32-bit JVMs.
 *
 *
 * What volatile DOES NOT do
 * -------------------------
 *   - It does NOT provide mutual exclusion. `count++` on a volatile int
 *     is still racy because the increment is a read-modify-write.
 *   - It does NOT make compound operations atomic. Use an Atomic*, a
 *     lock, or synchronized for that.
 *
 *
 * The classic safe-publication pattern: DOUBLE-CHECKED LOCKING
 * ------------------------------------------------------------
 *      class Cfg {
 *          private static volatile Cfg INSTANCE;     // <-- must be volatile
 *          static Cfg get() {
 *              Cfg c = INSTANCE;
 *              if (c == null) {
 *                  synchronized (Cfg.class) {
 *                      c = INSTANCE;
 *                      if (c == null) INSTANCE = c = new Cfg();
 *                  }
 *              }
 *              return c;
 *          }
 *      }
 *
 * Without `volatile`, another thread could see a non-null INSTANCE
 * whose fields are still default values (broken initialisation).
 *
 *
 * Idiomatic uses of `volatile`
 * ----------------------------
 *   - A SINGLE-WRITER stop / cancel flag.
 *   - Publishing an IMMUTABLE reference once and snapshotting it.
 *   - State machine "current state" where every transition is a write.
 */

public class VolatileKeyword {

    /** A flag to stop the worker. Single writer (main), many readers (workers). */
    static volatile boolean stop = false;

    /** Counter — visibility-only is NOT enough. Demonstrates the limit. */
    static volatile int badCounter = 0;

    public static void main(String[] args) throws InterruptedException {

        section("1) volatile flag — worker stops as soon as we ask");
        Thread worker = new Thread(() -> {
            long n = 0;
            while (!stop) n++;
            System.out.println("worker exited cleanly after " + n + " loops");
        }, "worker");
        worker.start();
        Thread.sleep(50);
        stop = true;
        worker.join();

        section("2) volatile is NOT a substitute for atomic — count++ is still racy");
        badCounter = 0;
        Thread t1 = new Thread(() -> { for (int i = 0; i < 100_000; i++) badCounter++; });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 100_000; i++) badCounter++; });
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("volatile counter = " + badCounter + "  (expected 200000)");
        System.out.println("Fix: use AtomicInteger, synchronized, or LongAdder.");

        section("3) Safe publication of an immutable snapshot");
        Cache.setSnapshot(new Snapshot(1, 2));
        Thread reader = new Thread(() -> {
            Snapshot s = Cache.snapshot();
            System.out.println("reader sees " + s);
        }, "reader");
        reader.start();
        reader.join();

        section("4) Double-checked locking — volatile is REQUIRED");
        // Hammer get() from many threads. Without volatile, some threads
        // would race and possibly see a partly-constructed Cfg.
        Thread[] hammer = new Thread[8];
        for (int i = 0; i < hammer.length; i++) {
            hammer[i] = new Thread(() -> System.out.println("cfg = " + Cfg.get()));
            hammer[i].start();
        }
        for (Thread t : hammer) t.join();

        section("done");
    }

    /** Immutable record — safe to publish by reference (volatile field below). */
    static record Snapshot(int a, int b) {}

    /** Container for a single volatile reference — many readers, single writer. */
    static class Cache {
        private static volatile Snapshot snap;
        static void setSnapshot(Snapshot s) { snap = s; }
        static Snapshot snapshot() { return snap; }
    }

    /** Classic double-checked-locking singleton — volatile is mandatory. */
    static class Cfg {
        private static volatile Cfg INSTANCE;
        private final long built = System.nanoTime();
        static Cfg get() {
            Cfg c = INSTANCE;
            if (c == null) {
                synchronized (Cfg.class) {
                    c = INSTANCE;
                    if (c == null) INSTANCE = c = new Cfg();
                }
            }
            return c;
        }
        @Override public String toString() { return "Cfg@" + Long.toHexString(hashCode()) + " built=" + built; }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
