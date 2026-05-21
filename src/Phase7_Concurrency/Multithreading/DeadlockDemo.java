package Phase7_Concurrency.Multithreading;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Deadlock in Multithreading
 * --------------------------
 * Two or more threads each holding a lock the other needs. None ever
 * progresses.
 * <p>
 *
 *      Thread 1: holds A, waiting for B
 *      Thread 2: holds B, waiting for A
 * <p>
 *
 * The Four Conditions (Coffman, 1971)
 * -----------------------------------
 *   1. MUTUAL EXCLUSION  - resources are non-shareable.
 *   2. HOLD-AND-WAIT     - a thread holding one resource asks for another.
 *   3. NO PREEMPTION     - resources can only be released voluntarily.
 *   4. CIRCULAR WAIT     - a cycle of threads each waiting on the next.
 * <p>
 *
 * Breaking ANY of the four eliminates deadlock.
 * <p>
 *
 * How to prevent deadlock
 * -----------------------
 *   1. LOCK ORDERING. Define a global order on locks; always acquire in
 *      that order. (E.g. order by System.identityHashCode.)
 *   2. TRY-LOCK WITH TIMEOUT. ReentrantLock.tryLock(time, unit) — bail
 *      out and retry if you couldn't get all locks.
 *   3. ONE BIG LOCK. Simpler if performance allows.
 *   4. LOCK-FREE DATA STRUCTURES. ConcurrentHashMap, Atomic*.
 *   5. STRUCTURED TASK SCOPES (Java 21 preview) — make hierarchical
 *      cancellation explicit.
 * <p>
 *
 * Detecting deadlock at runtime
 * -----------------------------
 *   ThreadMXBean.findDeadlockedThreads() — JMX-level API that returns
 *   the thread ids stuck in a deadlock. Useful in monitoring / liveness
 *   probes.
 * <p>
 *
 * This file demonstrates:
 *   1. A textbook deadlock.
 *   2. ThreadMXBean detection.
 *   3. Fix via global lock ordering.
 *   4. Fix via tryLock with backoff.
 */

public class DeadlockDemo {

    private static final Object A = new Object();
    private static final Object B = new Object();

    public static void main(String[] args) throws InterruptedException {

        section("1) Reproduce a deadlock");
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                sleep(50);
                synchronized (B) {
                    System.out.println("t1 got both (shouldn't print)");
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (B) {
                sleep(50);
                synchronized (A) {
                    System.out.println("t2 got both (shouldn't print)");
                }
            }
        }, "t2");
        t1.start(); t2.start();
        Thread.sleep(300);

        section("2) ThreadMXBean detects it");
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = mx.findDeadlockedThreads();
        if (ids != null) {
            ThreadInfo[] infos = mx.getThreadInfo(ids);
            for (ThreadInfo i : infos) {
                System.out.println("  DEADLOCKED: " + i.getThreadName()
                        + " waiting on " + i.getLockName()
                        + " owned by " + i.getLockOwnerName());
            }
        } else {
            System.out.println("  not detected yet — try again");
        }

        // Forcibly stop the demo threads (interrupt does NOT release a
        // monitor wait — they will be killed only when the JVM exits).
        t1.interrupt();
        t2.interrupt();

        section("3) Fix: global lock ordering");
        // We sort by identityHashCode and ALWAYS lock the lower-hash
        // object first. Either thread now acquires the same order.
        Thread o1 = new Thread(() -> orderedTransfer(A, B), "o1");
        Thread o2 = new Thread(() -> orderedTransfer(B, A), "o2");
        o1.start(); o2.start();
        o1.join();  o2.join();
        System.out.println("ordered transfers completed");

        section("4) Fix: tryLock with timed backoff");
        ReentrantLock la = new ReentrantLock();
        ReentrantLock lb = new ReentrantLock();
        Thread r1 = new Thread(() -> tryTransfer(la, lb), "r1");
        Thread r2 = new Thread(() -> tryTransfer(lb, la), "r2");
        r1.start(); r2.start();
        r1.join();  r2.join();
        System.out.println("tryLock transfers completed (no deadlock possible)");

        section("done");
        // The deadlocked threads from section 1 are still BLOCKED.
        // The JVM will exit because they are user threads? Actually they
        // ARE user threads, so we have to System.exit to leave cleanly.
        System.exit(0);
    }

    /** Always lock the smaller-hash object first. Deadlock-free. */
    private static void orderedTransfer(Object x, Object y) {
        Object first  = System.identityHashCode(x) <= System.identityHashCode(y) ? x : y;
        Object second = (first == x) ? y : x;
        synchronized (first) {
            synchronized (second) {
                System.out.println("  " + Thread.currentThread().getName() + " did work");
            }
        }
    }

    /** Acquire both locks via tryLock; if can't, release and retry. */
    private static void tryTransfer(ReentrantLock la, ReentrantLock lb) {
        while (true) {
            if (la.tryLock()) {
                try {
                    if (lb.tryLock()) {
                        try {
                            System.out.println("  " + Thread.currentThread().getName() + " did work");
                            return;
                        } finally { lb.unlock(); }
                    }
                } finally { la.unlock(); }
            }
            try { TimeUnit.MILLISECONDS.sleep(5); } catch (InterruptedException ignored) {}
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
