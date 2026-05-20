package Basics.Multithreading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * java.util.concurrent.locks.ReadWriteLock
 * ----------------------------------------
 * Two locks for the price of one:
 *
 *   READ LOCK   - many threads may hold it simultaneously.
 *   WRITE LOCK  - exclusive; no readers or writers can be active.
 *
 * Use when reads VASTLY OUTNUMBER writes — a configuration cache,
 * lookup table, in-memory index.
 *
 *
 * The canonical implementation
 * ----------------------------
 *      ReadWriteLock rw = new ReentrantReadWriteLock();
 *      Lock r = rw.readLock();
 *      Lock w = rw.writeLock();
 *
 *      r.lock();  try { ...read...  } finally { r.unlock(); }
 *      w.lock();  try { ...write... } finally { w.unlock(); }
 *
 *
 * Important properties
 * --------------------
 *   - REENTRANT — a thread holding the write lock can also acquire the
 *     read lock and vice versa for downgrading.
 *   - LOCK DOWNGRADING is allowed (write → read).
 *   - LOCK UPGRADING is NOT (read → write would deadlock if multiple
 *     readers tried). Release the read lock first.
 *   - FAIR option behaves like ReentrantLock's.
 *   - Both locks share a single CONDITION namespace — only the WRITE
 *     lock can produce Conditions.
 *
 *
 * Pitfalls
 * --------
 *   1. Write starvation. Under heavy read load, writers may wait
 *      forever. Use a FAIR ReentrantReadWriteLock if that's a risk.
 *   2. Overhead. The bookkeeping is more expensive than a single lock.
 *      For tiny critical sections you can lose throughput.
 *   3. Consider StampedLock or ConcurrentHashMap as alternatives.
 */

public class ReadWriteLockDemo {

    /** Tiny thread-safe map with read/write split. */
    static class RwMap<K, V> {
        private final ReadWriteLock rw = new ReentrantReadWriteLock();
        private final Lock r = rw.readLock();
        private final Lock w = rw.writeLock();
        private final Map<K, V> data = new HashMap<>();
        public V get(K key) {
            r.lock();
            try { return data.get(key); }
            finally { r.unlock(); }
        }
        public void put(K key, V value) {
            w.lock();
            try { data.put(key, value); }
            finally { w.unlock(); }
        }
        public int size() {
            r.lock();
            try { return data.size(); }
            finally { r.unlock(); }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) Many readers run concurrently, one writer is exclusive");
        RwMap<String, Integer> map = new RwMap<>();
        for (int i = 0; i < 5; i++) map.put("k" + i, i);

        // 4 readers, 1 writer
        Thread[] readers = new Thread[4];
        for (int i = 0; i < readers.length; i++) {
            final int id = i;
            readers[i] = new Thread(() -> {
                for (int k = 0; k < 5; k++) {
                    Integer v = map.get("k" + (k % 5));
                    System.out.println("  reader " + id + " saw k" + (k%5) + "=" + v);
                    try { Thread.sleep(20); } catch (InterruptedException ignored) {}
                }
            }, "reader-" + i);
        }
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                map.put("k" + i, i + 100);
                System.out.println("  writer updated k" + i);
                try { Thread.sleep(40); } catch (InterruptedException ignored) {}
            }
        }, "writer");

        for (Thread t : readers) t.start();
        writer.start();
        for (Thread t : readers) t.join();
        writer.join();

        section("2) Lock downgrade — write lock then acquire read lock then release write");
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        rw.writeLock().lock();
        try {
            // do write
            rw.readLock().lock();                   // downgrade — still own write
            rw.writeLock().unlock();                // release write, keep read
            try {
                System.out.println("now reading under read lock; can't upgrade back to write");
            } finally {
                rw.readLock().unlock();
            }
        } catch (Throwable t) {
            // if anything went wrong before the explicit unlock above
            if (rw.isWriteLockedByCurrentThread()) rw.writeLock().unlock();
        }

        section("3) DO NOT try to upgrade — would deadlock");
        // rw.readLock().lock();
        // rw.writeLock().lock();   // <-- this thread is also a reader; ANOTHER
        //                          //     reader prevents the write lock acquire.

        section("4) Fair vs unfair under contention");
        ReentrantReadWriteLock fair = new ReentrantReadWriteLock(true);
        // In a fair rwlock, a queued writer prevents new readers from
        // jumping ahead of it. Helps writer-starvation but slower.
        System.out.println("fair = " + fair.isFair());

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
