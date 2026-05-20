package Phase7_Concurrency.Multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Locks in Java
 * -------------
 * Java offers TWO families of locking primitives:
 *
 *   1. INTRINSIC LOCKS (a.k.a. monitor locks) — every Object has one.
 *      Used via `synchronized` blocks / methods and Object.wait/notify.
 *      Simple, baked into the language.
 *
 *   2. EXPLICIT LOCKS — interfaces and classes in
 *      java.util.concurrent.locks. More features, more responsibilities.
 *
 *
 * The Lock Interface
 * ------------------
 *      lock()                      - acquire, blocking
 *      lockInterruptibly()         - acquire, interruptible
 *      tryLock()                   - return immediately; true if acquired
 *      tryLock(time, unit)         - try up to a timeout
 *      unlock()                    - release
 *      newCondition()              - associated condition variable
 *
 * MUST be used with try/finally:
 *
 *      lock.lock();
 *      try { ... }
 *      finally { lock.unlock(); }
 *
 *
 * Implementations in this file
 * ----------------------------
 *   ReentrantLock        - the workhorse; reentrant; optional fairness;
 *                           multiple Conditions; timed acquire.
 *   ReentrantReadWriteLock - separate read/write locks; many readers OR
 *                            one writer.
 *   StampedLock          - optimistic reads + write/read modes; not reentrant.
 *
 * For dedicated files, see ReentrantLockDemo, ReadWriteLockDemo,
 * StampedLockDemo.
 *
 *
 * Lock vs synchronized — quick comparison
 * ---------------------------------------
 *                          synchronized            Lock
 *                          ---------------         ----------------
 *   Acquire timeout?       No                       tryLock(time,unit)
 *   Interruptible acquire? No                       lockInterruptibly
 *   Fairness?              No                       Optional in ReentrantLock
 *   Multiple conditions?   No (one per object)     Yes (newCondition)
 *   Auto-release on exit?  Yes                      No — try/finally
 *   Hard to forget?        Yes                      Easy to forget unlock
 */

public class LocksInJava {

    public static void main(String[] args) throws InterruptedException {

        section("1) ReentrantLock — basic acquire/release");
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            System.out.println("inside ReentrantLock critical section");
        } finally {
            lock.unlock();
        }

        section("2) ReentrantLock — tryLock with timeout");
        Lock contended = new ReentrantLock();
        Thread holder = new Thread(() -> {
            contended.lock();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            finally { contended.unlock(); }
        }, "holder");
        holder.start();
        Thread.sleep(30);                          // give holder a head start
        boolean got = contended.tryLock(50, TimeUnit.MILLISECONDS);
        System.out.println("tryLock(50ms) = " + got + "  (expected false)");
        if (got) contended.unlock();
        holder.join();

        section("3) ReentrantLock — interruptible acquire");
        Lock notebook = new ReentrantLock();
        Thread occupier = new Thread(() -> {
            notebook.lock();
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            finally { notebook.unlock(); }
        }, "occupier");
        occupier.start();
        Thread waiter = new Thread(() -> {
            try {
                notebook.lockInterruptibly();
                System.out.println("waiter got the lock");
                notebook.unlock();
            } catch (InterruptedException ie) {
                System.out.println("waiter interrupted before acquiring");
            }
        }, "waiter");
        waiter.start();
        Thread.sleep(30);
        waiter.interrupt();
        waiter.join();
        occupier.join();

        section("4) ReentrantLock — Condition variables");
        Lock l = new ReentrantLock();
        Condition cond = l.newCondition();
        boolean[] ready = { false };
        Thread w = new Thread(() -> {
            l.lock();
            try {
                while (!ready[0]) cond.await();
                System.out.println("w resumed");
            } catch (InterruptedException ignored) {}
            finally { l.unlock(); }
        }, "w");
        w.start();
        Thread.sleep(30);
        l.lock();
        try { ready[0] = true; cond.signal(); }
        finally { l.unlock(); }
        w.join();

        section("5) ReadWriteLock — many readers, one writer");
        ReadWriteLock rw = new ReentrantReadWriteLock();
        rw.readLock().lock();
        try { System.out.println("reading"); }
        finally { rw.readLock().unlock(); }
        rw.writeLock().lock();
        try { System.out.println("writing"); }
        finally { rw.writeLock().unlock(); }

        section("6) StampedLock — optimistic read");
        StampedLock sl = new StampedLock();
        long stamp = sl.tryOptimisticRead();
        long x = 1, y = 2;        // pretend these are guarded fields
        if (!sl.validate(stamp)) {
            // someone wrote during the optimistic read — re-read under a lock
            stamp = sl.readLock();
            try { /* re-read */ }
            finally { sl.unlockRead(stamp); }
        }
        System.out.println("optimistic read x+y = " + (x + y));

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
