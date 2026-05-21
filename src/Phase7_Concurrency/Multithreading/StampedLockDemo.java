package Phase7_Concurrency.Multithreading;

import java.util.concurrent.locks.StampedLock;

/**
 * java.util.concurrent.locks.StampedLock  (Java 8+)
 * -------------------------------------------------
 * Three modes:
 * <p>
 *
 *   WRITE LOCK     - exclusive (like a write lock).
 *   READ LOCK      - shared (like a read lock).
 *   OPTIMISTIC READ- NO lock acquired. Returns a "stamp"; the reader
 *                    validates the stamp after reading. If a writer
 *                    completed in the meantime, the validation fails
 *                    and you fall back to a real read lock.
 * <p>
 *
 * Optimistic reads are CHEAP — basically a volatile read — but you must
 * COPY the fields you read to LOCAL VARIABLES before validating. If you
 * dereference a stale reference during the optimistic window you can
 * get inconsistent or even unsafe values.
 * <p>
 *
 * Key API
 * -------
 *   long writeLock()                 -> stamp
 *   unlockWrite(stamp)
 *   long readLock()                  -> stamp
 *   unlockRead(stamp)
 *   long tryOptimisticRead()         -> stamp (0 means a writer is active)
 *   boolean validate(stamp)          -> true if no writer since the stamp
 *   long tryConvertToWriteLock(stamp)-> upgrade (returns 0 on failure)
 *   long tryConvertToReadLock(stamp) -> downgrade
 *   boolean isWriteLocked()
 * <p>
 *
 * Important differences from ReadWriteLock
 * ----------------------------------------
 *   - NOT REENTRANT. The same thread acquiring twice deadlocks.
 *   - No Conditions.
 *   - Often faster under read-heavy contention.
 *   - Slightly more code to use correctly.
 * <p>
 *
 * Pattern: read-mostly Point class
 * --------------------------------
 *      class Point {
 *          private double x, y;
 *          private final StampedLock sl = new StampedLock();
 * <p>
 *
 *          double distanceFromOrigin() {
 *              long stamp = sl.tryOptimisticRead();
 *              double localX = x, localY = y;            // COPY locally
 *              if (!sl.validate(stamp)) {                // fall back to real read
 *                  stamp = sl.readLock();
 *                  try {
 *                      localX = x; localY = y;
 *                  } finally { sl.unlockRead(stamp); }
 *              }
 *              return Math.hypot(localX, localY);
 *          }
 * <p>
 *
 *          void move(double dx, double dy) {
 *              long stamp = sl.writeLock();
 *              try { x += dx; y += dy; }
 *              finally { sl.unlockWrite(stamp); }
 *          }
 *      }
 */

public class StampedLockDemo {

    static class Point {
        private double x, y;
        private final StampedLock sl = new StampedLock();

        double distanceFromOrigin() {
            long stamp = sl.tryOptimisticRead();
            double localX = x, localY = y;
            if (!sl.validate(stamp)) {
                stamp = sl.readLock();
                try {
                    localX = x; localY = y;
                } finally { sl.unlockRead(stamp); }
            }
            return Math.hypot(localX, localY);
        }

        void move(double dx, double dy) {
            long stamp = sl.writeLock();
            try { x += dx; y += dy; }
            finally { sl.unlockWrite(stamp); }
        }

        /** Conditional move: only if currently at origin. Demonstrates conversion. */
        void moveIfAtOrigin(double newX, double newY) {
            long stamp = sl.readLock();
            try {
                while (x == 0.0 && y == 0.0) {
                    long ws = sl.tryConvertToWriteLock(stamp);
                    if (ws != 0L) { stamp = ws; x = newX; y = newY; break; }
                    else { sl.unlockRead(stamp); stamp = sl.writeLock(); }
                }
            } finally {
                sl.unlock(stamp);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) Read-mostly Point — optimistic reads usually succeed");
        Point p = new Point();
        p.move(3, 4);
        System.out.println("distance = " + p.distanceFromOrigin());

        section("2) Stress test — readers see consistent (x, y)");
        Point pt = new Point();
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 1_000; i++) {
                pt.move(0.001, -0.001);
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
        }, "writer");
        Thread[] readers = new Thread[4];
        for (int i = 0; i < readers.length; i++) {
            final int id = i;
            readers[i] = new Thread(() -> {
                double sum = 0;
                for (int k = 0; k < 5_000; k++) sum += pt.distanceFromOrigin();
                System.out.println("  reader " + id + " sum = " + sum);
            }, "reader-" + i);
        }
        writer.start();
        for (Thread t : readers) t.start();
        writer.join();
        for (Thread t : readers) t.join();

        section("3) tryConvertToWriteLock (upgrade) — allowed once you hold a read stamp");
        Point pp = new Point();
        pp.moveIfAtOrigin(7, 9);
        System.out.println("after conditional move: distance = " + pp.distanceFromOrigin());

        section("4) StampedLock is NOT reentrant");
        StampedLock sl = new StampedLock();
        long s = sl.writeLock();
        // long s2 = sl.writeLock();   // <-- DEADLOCKS (same thread)
        sl.unlockWrite(s);
        System.out.println("released exclusive stamp");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
