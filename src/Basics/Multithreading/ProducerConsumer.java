package Basics.Multithreading;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Producer-Consumer Pattern
 * -----------------------------
 * Producers and consumers communicate via a SHARED, BOUNDED queue.
 *
 *     producer -> [ . . . . ] -> consumer
 *
 * Bounding lets the system push back: if consumers fall behind, the
 * queue fills, and producers block until there's space. The pattern
 * shows up everywhere — task pipelines, log aggregators, web crawlers,
 * media players.
 *
 *
 * Three implementations in this file
 * ----------------------------------
 *   1. wait/notifyAll on a hand-rolled buffer (old-school).
 *   2. ReentrantLock with two Conditions (notEmpty, notFull).
 *   3. BlockingQueue from java.util.concurrent (idiomatic modern).
 *
 *
 * Key correctness rules
 * ---------------------
 *   - Take/put under the same lock as the size check.
 *   - Loop the predicate (spurious wakeups, racing waiters).
 *   - Signal the correct side: producers signal "not empty", consumers
 *     signal "not full". Using two Conditions lets you wake the exact
 *     side that's waiting; wait/notifyAll wakes everyone.
 *   - Don't hold the lock while doing the heavy producer / consumer
 *     work — only the buffer access.
 */

public class ProducerConsumer {

    public static void main(String[] args) throws InterruptedException {

        section("1) Hand-rolled buffer with wait/notifyAll");
        runDemo(new SyncBuffer<>(3), 8, 2);

        section("2) ReentrantLock with two Conditions");
        runDemo(new LockBuffer<>(3), 8, 2);

        section("3) BlockingQueue — idiomatic modern");
        runDemo(new BqBuffer<>(3), 8, 2);

        section("done");
    }

    interface Buffer<E> {
        void put(E e) throws InterruptedException;
        E take() throws InterruptedException;
    }

    // -------- 1. wait/notifyAll --------
    static class SyncBuffer<E> implements Buffer<E> {
        private final Queue<E> q = new LinkedList<>();
        private final int cap;
        SyncBuffer(int cap) { this.cap = cap; }
        public synchronized void put(E e) throws InterruptedException {
            while (q.size() == cap) wait();
            q.add(e);
            notifyAll();                 // wake potential consumers
        }
        public synchronized E take() throws InterruptedException {
            while (q.isEmpty()) wait();
            E v = q.remove();
            notifyAll();                 // wake potential producers
            return v;
        }
    }

    // -------- 2. ReentrantLock + Conditions --------
    static class LockBuffer<E> implements Buffer<E> {
        private final Queue<E> q = new LinkedList<>();
        private final int cap;
        private final Lock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull  = lock.newCondition();
        LockBuffer(int cap) { this.cap = cap; }
        public void put(E e) throws InterruptedException {
            lock.lock();
            try {
                while (q.size() == cap) notFull.await();
                q.add(e);
                notEmpty.signal();              // signal exactly one consumer
            } finally { lock.unlock(); }
        }
        public E take() throws InterruptedException {
            lock.lock();
            try {
                while (q.isEmpty()) notEmpty.await();
                E v = q.remove();
                notFull.signal();               // signal exactly one producer
                return v;
            } finally { lock.unlock(); }
        }
    }

    // -------- 3. BlockingQueue --------
    static class BqBuffer<E> implements Buffer<E> {
        private final BlockingQueue<E> q;
        BqBuffer(int cap) { this.q = new ArrayBlockingQueue<>(cap); }
        public void put(E e) throws InterruptedException { q.put(e); }
        public E take() throws InterruptedException      { return q.take(); }
    }

    // -------- runner --------
    private static <E> void runDemo(Buffer<Integer> buf, int itemCount, int consumers) throws InterruptedException {
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= itemCount; i++) {
                try {
                    buf.put(i);
                    System.out.println("[prod] " + i);
                    Thread.sleep(10);
                } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
            }
            // sentinel for each consumer
            for (int i = 0; i < consumers; i++) {
                try { buf.put(-1); } catch (InterruptedException ignored) {}
            }
        }, "producer");

        Thread[] cons = new Thread[consumers];
        for (int c = 0; c < consumers; c++) {
            final int id = c;
            cons[c] = new Thread(() -> {
                while (true) {
                    try {
                        int v = buf.take();
                        if (v == -1) break;     // sentinel
                        System.out.println("       [cons-" + id + "] " + v);
                        Thread.sleep(30);
                    } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
                }
            }, "cons-" + c);
        }

        producer.start();
        for (Thread t : cons) t.start();
        producer.join();
        for (Thread t : cons) t.join();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
