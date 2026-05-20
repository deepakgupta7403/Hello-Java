package Basics.Multithreading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * java.util.concurrent.locks.ReentrantLock
 * ----------------------------------------
 * A drop-in upgrade from `synchronized` with these extras:
 *
 *   - tryLock()                 - non-blocking attempt
 *   - tryLock(time, unit)       - timed attempt
 *   - lockInterruptibly()       - cancel mid-acquire
 *   - constructor(boolean fair) - FIFO acquisition order
 *   - newCondition() x N        - multiple condition variables
 *
 * It is REENTRANT — the same thread can acquire it multiple times, each
 * needing a matching unlock(). It is NOT held forever if your thread
 * dies: but if your code forgets to unlock(), nobody else can ever
 * acquire it. ALWAYS use try/finally.
 *
 *
 * Fair vs unfair
 * --------------
 *   Unfair (default): a new thread may "barge" ahead of a queued waiter.
 *                      Higher throughput. Possible STARVATION.
 *   Fair:             waiters acquire in FIFO order. Lower throughput.
 *                      Predictable.
 *
 *
 * Conditions
 * ----------
 * Each ReentrantLock can have ANY NUMBER of Condition objects. Each is
 * its own wait queue. Classic example: a bounded buffer with notEmpty
 * and notFull conditions instead of a single notifyAll firing for both.
 *
 *      Lock        lock     = new ReentrantLock();
 *      Condition   notEmpty = lock.newCondition();
 *      Condition   notFull  = lock.newCondition();
 *
 * Condition methods:
 *      await()                 / signal()
 *      await(time, unit)       / signalAll()
 *      awaitUntil(deadline)
 *      awaitUninterruptibly()
 *
 *
 * Diagnostics
 * -----------
 *   isLocked()              - is anyone holding it?
 *   isHeldByCurrentThread() - am I holding it?
 *   getHoldCount()          - how many times have I locked it?
 *   getQueueLength()        - approx waiters
 */

public class ReentrantLockDemo {

    public static void main(String[] args) throws InterruptedException {

        section("1) Basic lock / unlock with try-finally");
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            System.out.println("inside critical section, holdCount=" + lock.getHoldCount());
        } finally { lock.unlock(); }

        section("2) Reentrancy");
        ReentrantLock l2 = new ReentrantLock();
        l2.lock();
        try {
            l2.lock();
            try {
                System.out.println("holdCount = " + l2.getHoldCount());  // 2
            } finally { l2.unlock(); }
        } finally { l2.unlock(); }

        section("3) tryLock — non-blocking");
        ReentrantLock l3 = new ReentrantLock();
        Thread holder = new Thread(() -> {
            l3.lock();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            finally { l3.unlock(); }
        }, "holder");
        holder.start();
        Thread.sleep(30);
        boolean got = l3.tryLock();
        System.out.println("tryLock() while held = " + got);
        if (got) l3.unlock();
        holder.join();

        section("4) tryLock(timeout)");
        ReentrantLock l4 = new ReentrantLock();
        Thread squatter = new Thread(() -> {
            l4.lock();
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            finally { l4.unlock(); }
        }, "squatter");
        squatter.start();
        Thread.sleep(30);
        boolean got2 = l4.tryLock(50, TimeUnit.MILLISECONDS);
        System.out.println("tryLock(50ms) = " + got2);
        if (got2) l4.unlock();
        squatter.join();

        section("5) lockInterruptibly — cancel a waiting acquire");
        ReentrantLock l5 = new ReentrantLock();
        Thread blocker = new Thread(() -> {
            l5.lock();
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            finally { l5.unlock(); }
        }, "blocker");
        blocker.start();
        Thread waiter = new Thread(() -> {
            try {
                l5.lockInterruptibly();
                System.out.println("waiter got it (shouldn't)");
                l5.unlock();
            } catch (InterruptedException ie) {
                System.out.println("waiter interrupted while waiting");
            }
        }, "waiter");
        waiter.start();
        Thread.sleep(30);
        waiter.interrupt();
        waiter.join();
        blocker.join();

        section("6) Fair lock — FIFO order");
        ReentrantLock fair = new ReentrantLock(true);
        Thread[] hot = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            hot[i] = new Thread(() -> {
                fair.lock();
                try {
                    System.out.println("  fair acquired by " + idx);
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
                finally { fair.unlock(); }
            });
        }
        for (Thread t : hot) t.start();
        for (Thread t : hot) t.join();

        section("7) Two Conditions — bounded buffer");
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(2);
        Thread prod = new Thread(() -> {
            for (int i = 1; i <= 4; i++) {
                try { bb.put(i); System.out.println("  put " + i); }
                catch (InterruptedException ignored) {}
            }
        });
        Thread cons = new Thread(() -> {
            for (int i = 1; i <= 4; i++) {
                try { System.out.println("  take " + bb.take()); Thread.sleep(30); }
                catch (InterruptedException ignored) {}
            }
        });
        prod.start(); cons.start();
        prod.join();  cons.join();

        section("done");
    }

    /** Bounded buffer using one lock and two Conditions. */
    static class BoundedBuffer<E> {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull  = lock.newCondition();
        private final Queue<E> q = new LinkedList<>();
        private final int cap;
        BoundedBuffer(int cap) { this.cap = cap; }
        void put(E e) throws InterruptedException {
            lock.lock();
            try {
                while (q.size() == cap) notFull.await();
                q.add(e);
                notEmpty.signal();
            } finally { lock.unlock(); }
        }
        E take() throws InterruptedException {
            lock.lock();
            try {
                while (q.isEmpty()) notEmpty.await();
                E v = q.remove();
                notFull.signal();
                return v;
            } finally { lock.unlock(); }
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
