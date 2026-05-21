package Phase7_Concurrency.Multithreading;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Semaphore
 * ---------
 * A counter that hands out PERMITS. acquire() blocks until a permit is
 * available; release() returns one. With N permits you can let UP TO N
 * threads through a section at once — classic resource-limiting tool.
 * <p>
 *
 *      Semaphore s = new Semaphore(N);          // N permits
 *      s.acquire();
 *      try { ...use the resource... }
 *      finally { s.release(); }
 * <p>
 *
 * What semaphores model
 * ---------------------
 *   - A POOL of N identical resources (DB connections, HTTP threads,
 *     printers, GPU slots).
 *   - A CAP on concurrency — at most N requests in flight.
 *   - A signalling mechanism — release() can be called more times than
 *     acquire() (creating MORE permits than you started with).
 * <p>
 *
 * Methods
 * -------
 *   acquire()                - take one permit, blocking
 *   acquireUninterruptibly() - ignore interrupts while waiting
 *   tryAcquire()             - non-blocking attempt
 *   tryAcquire(time, unit)   - timed attempt
 *   release()                - return one permit
 *   acquire(n) / release(n)  - permits in bulk
 *   availablePermits()       - approx free count
 *   drainPermits()           - take all available, return how many
 * <p>
 *
 * Fair vs unfair
 * --------------
 *   new Semaphore(N)         - unfair, faster; possible starvation
 *   new Semaphore(N, true)   - FIFO order; slower
 * <p>
 *
 * "Binary semaphore" trick
 * ------------------------
 *   new Semaphore(1) acts like a lock, but UNLIKE a ReentrantLock the
 *   thread that releases need NOT be the one that acquired. Useful for
 *   handing off control between threads (e.g., one thread acquires,
 *   another releases).
 * <p>
 *
 * Pitfalls
 * --------
 *   - Forgetting release() — try/finally always.
 *   - Releasing MORE than you acquired — silent permit inflation.
 */

public class SemaphoreDemo {

    public static void main(String[] args) throws Exception {

        section("1) Resource pool — at most 3 in flight at once");
        Semaphore slots = new Semaphore(3);
        AtomicInteger inFlight = new AtomicInteger();
        Thread[] req = new Thread[8];
        for (int i = 0; i < req.length; i++) {
            final int id = i;
            req[i] = new Thread(() -> {
                try {
                    slots.acquire();
                    int n = inFlight.incrementAndGet();
                    System.out.println("  req " + id + " started (inFlight=" + n + ")");
                    Thread.sleep(80);
                    inFlight.decrementAndGet();
                } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                finally { slots.release(); }
            }, "req-" + id);
        }
        for (Thread t : req) t.start();
        for (Thread t : req) t.join();

        section("2) tryAcquire — bail out if no permit immediately");
        Semaphore tight = new Semaphore(1);
        tight.acquire();                                  // occupy the only permit
        boolean got = tight.tryAcquire();
        System.out.println("tryAcquire while empty = " + got);
        boolean gotTimed = tight.tryAcquire(50, TimeUnit.MILLISECONDS);
        System.out.println("tryAcquire(50ms) = " + gotTimed);
        tight.release();

        section("3) Binary semaphore — release from a different thread (handoff)");
        Semaphore handoff = new Semaphore(0);              // start empty
        Thread consumer = new Thread(() -> {
            try {
                handoff.acquire();
                System.out.println("  consumer woke up");
            } catch (InterruptedException ignored) {}
        }, "cons");
        consumer.start();
        Thread.sleep(50);
        System.out.println("producer signalling");
        handoff.release();                                 // signal
        consumer.join();

        section("4) Bulk permits — turnstile letting 2 threads through at a time");
        Semaphore turnstile = new Semaphore(2);
        for (int round = 1; round <= 3; round++) {
            Thread a = new Thread(() -> doThroughTurnstile(turnstile), "a");
            Thread b = new Thread(() -> doThroughTurnstile(turnstile), "b");
            Thread c = new Thread(() -> doThroughTurnstile(turnstile), "c");
            a.start(); b.start(); c.start();
            a.join();  b.join();  c.join();
            System.out.println("end of round " + round + ", permits=" + turnstile.availablePermits());
        }

        section("5) Fair semaphore");
        Semaphore fair = new Semaphore(1, true);
        System.out.println("isFair() = " + fair.isFair() + " (FIFO order)");

        section("done");
    }

    private static void doThroughTurnstile(Semaphore s) {
        try {
            s.acquire();
            System.out.println("  " + Thread.currentThread().getName() + " through");
            Thread.sleep(30);
        } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        finally { s.release(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
