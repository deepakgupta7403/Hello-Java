package Phase7_Concurrency.Multithreading;

import java.util.concurrent.locks.LockSupport;

/**
 * Thread Lifecycle
 * ----------------
 * A Thread moves through a small set of states defined by Thread.State:
 * <p>
 *
 *   NEW              - constructed but start() has NOT been called yet.
 *   RUNNABLE         - eligible to run on a CPU. May be currently
 *                      running, may be ready and waiting for a core.
 *   BLOCKED          - waiting to acquire an INTRINSIC monitor lock
 *                      (entering a synchronized block / method whose
 *                      lock another thread holds).
 *   WAITING          - waiting indefinitely for another thread:
 *                          wait()         (no timeout)
 *                          join()         (no timeout)
 *                          LockSupport.park()
 *   TIMED_WAITING    - waiting with a timeout:
 *                          sleep(ms)
 *                          wait(ms)
 *                          join(ms)
 *                          LockSupport.parkNanos / parkUntil
 *   TERMINATED       - run() has returned (normally or via uncaught
 *                      exception). Cannot be restarted.
 * <p>
 *
 * Diagram
 * -------
 * <p>
 *
 *          start()                schedule
 *   NEW ----------> RUNNABLE <--------------> running on CPU
 *                      |  ^         (the JVM/OS does this)
 *      synchronized -->|  |
 *                      v  |
 *                   BLOCKED                 (waiting for a monitor)
 * <p>
 *
 *      wait/join/park -->|
 *                        v
 *                     WAITING / TIMED_WAITING
 *                        |
 *                  notify/timeout
 *                        v
 *                     RUNNABLE
 * <p>
 *
 *                   run() returns
 *                        v
 *                   TERMINATED
 * <p>
 *
 * Important Notes
 * ---------------
 *   1. RUNNABLE includes "ready but waiting for CPU" too. There is NO
 *      separate READY state in Java's enum.
 *   2. BLOCKED only refers to INTRINSIC LOCK contention. Threads waiting
 *      on a Lock from java.util.concurrent.locks are WAITING /
 *      TIMED_WAITING, not BLOCKED — because Lock uses LockSupport.park.
 *   3. TERMINATED is final. You cannot restart a terminated thread.
 * <p>
 *
 * This file walks through each state and prints what getState() returns.
 */

public class ThreadLifecycle {

    private static final Object MONITOR = new Object();

    public static void main(String[] args) throws InterruptedException {

        section("1) NEW — before start()");
        Thread fresh = new Thread(() -> {});
        System.out.println("state = " + fresh.getState());     // NEW

        section("2) RUNNABLE — after start()");
        Thread runnable = new Thread(() -> { while (!Thread.currentThread().isInterrupted()) {} });
        runnable.start();
        Thread.sleep(50);
        System.out.println("state = " + runnable.getState());  // RUNNABLE
        runnable.interrupt();
        runnable.join();

        section("3) TIMED_WAITING — sleeping");
        Thread sleeper = new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException ignored) {} });
        sleeper.start();
        Thread.sleep(50);
        System.out.println("state = " + sleeper.getState());   // TIMED_WAITING
        sleeper.join();

        section("4) WAITING — wait() / join() / park()");
        Thread parked = new Thread(LockSupport::park);
        parked.start();
        Thread.sleep(50);
        System.out.println("state = " + parked.getState());    // WAITING
        LockSupport.unpark(parked);
        parked.join();

        section("5) BLOCKED — waiting on a synchronized monitor");
        Thread holder = new Thread(() -> {
            synchronized (MONITOR) {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
        }, "holder");
        Thread waiter = new Thread(() -> {
            synchronized (MONITOR) {                            // will block until holder releases
                // do nothing once we get it
            }
        }, "waiter");
        holder.start();
        Thread.sleep(50);                                       // let holder grab the monitor
        waiter.start();
        Thread.sleep(50);                                       // let waiter contend
        System.out.println("holder state = " + holder.getState()); // TIMED_WAITING (inside sleep)
        System.out.println("waiter state = " + waiter.getState()); // BLOCKED
        holder.join();
        waiter.join();

        section("6) TERMINATED — after run() returns");
        Thread done = new Thread(() -> {});
        done.start();
        done.join();
        System.out.println("state = " + done.getState());       // TERMINATED

        section("7) Cannot restart a TERMINATED thread");
        try { done.start(); }
        catch (IllegalThreadStateException e) {
            System.out.println("expected: " + e);
        }

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
