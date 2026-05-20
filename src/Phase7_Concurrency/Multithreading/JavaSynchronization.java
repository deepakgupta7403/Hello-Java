package Phase7_Concurrency.Multithreading;

/**
 * Java Synchronization
 * --------------------
 * SYNCHRONIZATION is the discipline of letting only ONE thread at a time
 * touch shared state, so that updates from different threads compose
 * sanely. In Java the canonical primitive is the `synchronized` keyword
 * — a built-in lock attached to every object (called the MONITOR or
 * INTRINSIC LOCK).
 *
 *
 * Three forms of `synchronized`
 * -----------------------------
 *   1. Synchronized INSTANCE method  - locks `this`.
 *
 *           public synchronized void m() { ... }
 *
 *   2. Synchronized STATIC method    - locks the Class object.
 *
 *           public static synchronized void m() { ... }   // locks YourClass.class
 *
 *   3. Synchronized BLOCK             - locks any object reference.
 *
 *           synchronized (lockObj) { ... }
 *
 *
 * What synchronized guarantees
 * ----------------------------
 *   MUTUAL EXCLUSION - only one thread at a time inside the same lock.
 *   VISIBILITY       - on lock acquire, the thread sees every change
 *                       made before the matching release. (JMM
 *                       happens-before: release → subsequent acquire.)
 *   ORDERING         - the JIT/CPU cannot reorder reads & writes across
 *                       the lock boundaries in observable ways.
 *
 *
 * What synchronized does NOT do
 * -----------------------------
 *   - Make individual operations atomic on their own (it's the BLOCK
 *     that is atomic, not the calls inside).
 *   - Prevent deadlock — that's on you.
 *   - Time out. Use ReentrantLock.tryLock if you need that.
 *
 *
 * Reentrancy
 * ----------
 * Java monitors are REENTRANT — the SAME thread can lock the same
 * monitor multiple times without deadlocking itself. Each acquire
 * needs a matching release.
 *
 *
 * Common rules
 * ------------
 *   - Always lock the SAME object for accesses to the same state.
 *   - DON'T synchronize on a String literal, Integer, Boolean — pooled
 *     and shared with unrelated code.
 *   - DON'T hold a lock while calling unknown code (callbacks,
 *     overrides, lambdas you don't own) — invitation to deadlock.
 *   - Keep critical sections SHORT.
 *
 *
 * Class lock vs object lock
 * -------------------------
 *   - Instance synchronized methods on different instances do NOT
 *     contend with each other.
 *   - Static synchronized methods on the same class ALWAYS contend.
 *   - An instance synchronized method and a static synchronized method
 *     on the SAME class lock DIFFERENT monitors and do NOT contend.
 */

public class JavaSynchronization {

    /** Shared counter — unsynchronized version for contrast. */
    static int unsafe = 0;

    /** Same counter but bumped inside a synchronized block. */
    static int safe = 0;
    static final Object SAFE_LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {

        section("1) WITHOUT synchronization — lost updates");
        unsafe = 0;
        Thread a1 = new Thread(() -> { for (int i = 0; i < 100_000; i++) unsafe++; });
        Thread a2 = new Thread(() -> { for (int i = 0; i < 100_000; i++) unsafe++; });
        a1.start(); a2.start();
        a1.join();  a2.join();
        System.out.println("unsafe = " + unsafe + " (expected 200000 — usually less)");

        section("2) WITH synchronized block — correct count");
        safe = 0;
        Thread b1 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                synchronized (SAFE_LOCK) { safe++; }
            }
        });
        Thread b2 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                synchronized (SAFE_LOCK) { safe++; }
            }
        });
        b1.start(); b2.start();
        b1.join();  b2.join();
        System.out.println("safe   = " + safe + " (always 200000)");

        section("3) synchronized INSTANCE method locks `this`");
        Counter c = new Counter();
        Runnable bumpC = () -> { for (int i = 0; i < 100_000; i++) c.bump(); };
        Thread t1 = new Thread(bumpC), t2 = new Thread(bumpC);
        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("Counter instance = " + c.get() + " (200000)");

        section("4) synchronized STATIC method locks the Class object");
        Runnable bumpS = () -> { for (int i = 0; i < 100_000; i++) StaticCounter.bump(); };
        StaticCounter.reset();
        Thread s1 = new Thread(bumpS), s2 = new Thread(bumpS);
        s1.start(); s2.start();
        s1.join();  s2.join();
        System.out.println("StaticCounter    = " + StaticCounter.get() + " (200000)");

        section("5) Reentrancy: a thread can enter the same monitor twice");
        Reentrant r = new Reentrant();
        r.outer();         // outer takes the lock, then calls inner which takes it AGAIN

        section("6) Two different locks don't contend");
        // synchronized(a) and synchronized(b) on different objects run in parallel.
        DualCounters dc = new DualCounters();
        Thread d1 = new Thread(() -> { for (int i = 0; i < 100_000; i++) dc.bumpA(); });
        Thread d2 = new Thread(() -> { for (int i = 0; i < 100_000; i++) dc.bumpB(); });
        d1.start(); d2.start();
        d1.join();  d2.join();
        System.out.println("A = " + dc.a + ", B = " + dc.b);

        section("7) Anti-pattern — synchronizing on a pooled object");
        // DON'T do this. "hello" is interned; any other piece of code that
        // synchronizes on the same literal contends with you.
        // synchronized ("hello") { ... }     // <- BAD

        section("done");
    }

    // ---------------------------- Helpers ----------------------------

    static class Counter {
        private int n;
        public synchronized void bump() { n++; }       // locks `this`
        public synchronized int get()   { return n; }
    }

    static class StaticCounter {
        private static int n;
        public static synchronized void bump()  { n++; }       // locks StaticCounter.class
        public static synchronized int get()    { return n; }
        public static synchronized void reset() { n = 0; }
    }

    static class Reentrant {
        public synchronized void outer() {
            System.out.println("outer acquired " + this);
            inner();
        }
        public synchronized void inner() {
            System.out.println("inner re-acquired (reentrant)");
        }
    }

    static class DualCounters {
        private final Object lockA = new Object();
        private final Object lockB = new Object();
        int a, b;
        void bumpA() { synchronized (lockA) { a++; } }
        void bumpB() { synchronized (lockB) { b++; } }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
