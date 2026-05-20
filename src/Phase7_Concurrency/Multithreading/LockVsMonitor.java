package Phase7_Concurrency.Multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock vs Monitor — Concurrency Primitives Compared
 * -------------------------------------------------
 * "Monitor" here means the INTRINSIC LOCK on every Object, accessed via
 * `synchronized`, with `wait/notify/notifyAll` as the condition variable.
 *
 * "Lock" means the `java.util.concurrent.locks.Lock` family — typically
 * `ReentrantLock` with `Condition` objects.
 *
 *
 * Side-by-side table
 * ------------------
 *                              Monitor (synchronized)        Lock (j.u.c.locks)
 *                              -----------------------       -------------------
 *   Acquire                    synchronized (obj) { ... }    lock.lock()
 *   Release                    auto on block exit            try/finally + lock.unlock()
 *   Timed acquire              no                            tryLock(time, unit)
 *   Interruptible acquire      no                            lockInterruptibly()
 *   Fair acquire               no                            new ReentrantLock(true)
 *   Reentrant                  yes                           yes (ReentrantLock)
 *   Condition variables        wait/notify(All) — one set    newCondition() × N
 *   Performance                great after biased lock removal; depends on JVM
 *                                                            comparable; sometimes faster
 *   Read/write split?          no                            ReentrantReadWriteLock
 *   Optimistic read?           no                            StampedLock
 *
 *
 * When to use which
 * -----------------
 *   - SMALL, OBVIOUS critical section → synchronized. Less to type, less
 *     to forget.
 *   - Need ANY of: timed acquire, interruptible acquire, fairness,
 *     multiple conditions, read/write separation → Lock family.
 *   - "Drop-in" parity: ReentrantLock matches synchronized semantics
 *     including reentrancy.
 *
 *
 * Don't mix them
 * --------------
 * Guarding the same field with BOTH synchronized and a Lock is a recipe
 * for confusion and lock-ordering bugs. Pick one mechanism per field.
 */

public class LockVsMonitor {

    /** Same counter, two implementations. */
    static class MonitorCounter {
        private int n;
        public synchronized void bump() { n++; }
        public synchronized int  get()  { return n; }
    }

    static class LockCounter {
        private final Lock lock = new ReentrantLock();
        private int n;
        public void bump() {
            lock.lock();
            try { n++; }
            finally { lock.unlock(); }
        }
        public int get() {
            lock.lock();
            try { return n; }
            finally { lock.unlock(); }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) Equivalent behaviour — same correct count");
        MonitorCounter mc = new MonitorCounter();
        LockCounter    lc = new LockCounter();
        Runnable bumpM = () -> { for (int i = 0; i < 100_000; i++) mc.bump(); };
        Runnable bumpL = () -> { for (int i = 0; i < 100_000; i++) lc.bump(); };
        Thread tm1 = new Thread(bumpM); Thread tm2 = new Thread(bumpM);
        Thread tl1 = new Thread(bumpL); Thread tl2 = new Thread(bumpL);
        tm1.start(); tm2.start(); tl1.start(); tl2.start();
        tm1.join();  tm2.join();  tl1.join();  tl2.join();
        System.out.println("Monitor counter = " + mc.get());
        System.out.println("Lock    counter = " + lc.get());

        section("2) Lock CAN time out; monitor cannot");
        Lock lock = new ReentrantLock();
        Thread holder = new Thread(() -> {
            lock.lock();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            finally { lock.unlock(); }
        }, "holder");
        holder.start();
        Thread.sleep(30);                  // ensure holder grabs the lock
        boolean got = lock.tryLock(50, TimeUnit.MILLISECONDS);
        System.out.println("tryLock(50ms) = " + got);
        if (got) lock.unlock();
        holder.join();

        section("3) Multiple Conditions on one Lock");
        Lock l = new ReentrantLock();
        Condition condA = l.newCondition();
        Condition condB = l.newCondition();
        // With a monitor you can only have ONE condition (the object itself).
        // With a Lock you can have as many as you like.
        System.out.println("created " + condA + " and " + condB);

        section("4) DON'T mix monitor + Lock on the same field");
        // Imagine the developer who would write this:
        //
        //     synchronized (this) { this.value = 42; }
        //     // ... in another method ...
        //     lock.lock(); try { this.value++; } finally { lock.unlock(); }
        //
        // Now `value` is guarded by two different things. There is no
        // mutual exclusion between the two paths — race city. Pick ONE.

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
