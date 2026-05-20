package Basics.Multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock Framework vs Thread Synchronization (`synchronized`)
 * ---------------------------------------------------------
 * Same goal — guard shared state. Different tradeoffs.
 *
 * This file builds a tiny BANK ACCOUNT three ways:
 *
 *   1. UnsafeAccount        - no synchronization at all (race condition).
 *   2. SyncAccount          - `synchronized` methods.
 *   3. LockAccount          - explicit ReentrantLock with tryLock.
 *
 * Then it measures and compares the three.
 *
 *
 * Decision Cheatsheet
 * -------------------
 *                                          synchronized    Lock framework
 *                                          ------------    --------------
 *   I need timed acquire                                    yes
 *   I need to give up if interrupted                        yes
 *   I need fair queueing                                    yes
 *   I need multiple condition variables                     yes
 *   I want the simplest code possible       yes
 *   It's a short critical section            yes
 *   I want auto-release on exit              yes
 *
 *
 * Modern Practical Rule of Thumb
 * ------------------------------
 *   - Default to `synchronized` when you only need plain mutual
 *     exclusion.
 *   - Reach for ReentrantLock the moment you say "I need tryLock with a
 *     timeout" or "I have two different conditions on the same lock."
 *   - For pure counters use Atomic* / LongAdder.
 *   - For "many readers, occasional writer" use ReentrantReadWriteLock or
 *     StampedLock.
 */

public class LockFrameworkVsSync {

    // ---- (1) Unsafe ----
    static class UnsafeAccount {
        double balance;
        void deposit(double a) { balance += a; }
        boolean withdraw(double a) {
            if (balance >= a) { balance -= a; return true; }
            return false;
        }
    }

    // ---- (2) synchronized ----
    static class SyncAccount {
        double balance;
        synchronized void deposit(double a) { balance += a; }
        synchronized boolean withdraw(double a) {
            if (balance >= a) { balance -= a; return true; }
            return false;
        }
    }

    // ---- (3) Lock + tryLock ----
    static class LockAccount {
        private final ReentrantLock lock = new ReentrantLock();
        double balance;
        void deposit(double a) {
            lock.lock();
            try { balance += a; }
            finally { lock.unlock(); }
        }
        boolean withdrawWithTimeout(double a, long ms) throws InterruptedException {
            if (!lock.tryLock(ms, TimeUnit.MILLISECONDS)) return false;
            try {
                if (balance >= a) { balance -= a; return true; }
                return false;
            } finally { lock.unlock(); }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) Unsafe — race condition");
        UnsafeAccount un = new UnsafeAccount();
        bench("unsafe", 4, 100_000, () -> un.deposit(1));
        System.out.println("unsafe balance = " + un.balance + "  (expected 400000)");

        section("2) synchronized — correct");
        SyncAccount sy = new SyncAccount();
        bench("sync  ", 4, 100_000, () -> sy.deposit(1));
        System.out.println("sync   balance = " + sy.balance);

        section("3) ReentrantLock — correct + supports tryLock");
        LockAccount lk = new LockAccount();
        bench("lock  ", 4, 100_000, () -> lk.deposit(1));
        System.out.println("lock   balance = " + lk.balance);

        section("4) Lock-only feature: tryLock with timeout");
        // Simulate contention then attempt withdraw with a 50ms deadline.
        LockAccount busy = new LockAccount();
        busy.balance = 1_000;
        Thread holder = new Thread(() -> {
            busy.deposit(0);                     // grab the lock briefly
            try {
                busy.withdrawWithTimeout(0, 200);    // hold for ~0s, but the test really
                                                       // shouldn't matter — we use a manual
                                                       // hand-hold via raw lock for demo:
            } catch (InterruptedException ignored) {}
        }, "holder");
        // Cleaner demonstration: grab lock by hand, sleep, release.
        ReentrantLock raw = new ReentrantLock();
        Thread squatter = new Thread(() -> {
            raw.lock();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            finally { raw.unlock(); }
        }, "squatter");
        squatter.start();
        Thread.sleep(30);
        boolean got = raw.tryLock(50, TimeUnit.MILLISECONDS);
        System.out.println("acquire within 50ms? " + got + "   (false — squatter held it longer)");
        if (got) raw.unlock();
        squatter.join();

        section("5) When NOT to use either — counter belongs to AtomicLong");
        AtomicLong atomic = new AtomicLong();
        bench("atomic", 4, 100_000, atomic::incrementAndGet);
        System.out.println("atomic = " + atomic.get());

        section("done");
    }

    private static void bench(String label, int threads, int iters, Runnable task) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        long t0 = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> { for (int k = 0; k < iters; k++) task.run(); });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("[" + label + "] " + threads + "x" + iters + " ops in " + ms + " ms");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
