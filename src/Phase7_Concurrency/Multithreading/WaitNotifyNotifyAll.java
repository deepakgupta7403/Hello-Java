package Phase7_Concurrency.Multithreading;

/**
 * Object.wait / notify / notifyAll
 * --------------------------------
 * The original Java inter-thread signalling primitives. They are
 * INSTANCE methods on `Object` — every object's intrinsic monitor lock
 * doubles as a condition variable.
 * <p>
 *
 * Rules
 * -----
 *   1. You MUST own the monitor lock when calling these methods. Always
 *      inside a `synchronized` block on the same object you're waiting on.
 *   2. wait() RELEASES the monitor while suspended; notify() wakes one
 *      waiter, notifyAll() wakes all of them. The woken thread then has
 *      to re-acquire the monitor before returning from wait().
 *   3. Always loop the predicate:
 * <p>
 *
 *           while (!ready) lock.wait();
 * <p>
 *
 *      Spurious wakeups exist; predicates can also change between
 *      notify and the wait return.
 * <p>
 *
 * The three methods
 * -----------------
 *   wait()                  - wait indefinitely
 *   wait(long ms)           - wait up to ms milliseconds
 *   wait(long ms, int nanos)
 * <p>
 *
 *   notify()                - wake ONE waiter (which one is undefined)
 *   notifyAll()             - wake ALL waiters
 * <p>
 *
 * notify vs notifyAll
 * -------------------
 *   - Use notifyAll when waiters may be waiting on DIFFERENT predicates
 *     of the same object — only the threads whose predicate is now true
 *     will exit; the others go back to waiting.
 *   - notify is a micro-optimisation for one-predicate cases. It is
 *     easy to introduce missed-signal bugs if you reach for it without
 *     thinking through every waiter.
 * <p>
 *
 * Modern alternative: Lock + Condition
 * ------------------------------------
 *   Lock lock = new ReentrantLock();
 *   Condition cond = lock.newCondition();
 * <p>
 *
 *   lock.lock();
 *   try {
 *       while (!ready) cond.await();
 *       ...
 *   } finally { lock.unlock(); }
 * <p>
 *
 * Conditions let you have MULTIPLE wait-sets per lock (e.g. notFull /
 * notEmpty in a bounded buffer).
 */

public class WaitNotifyNotifyAll {

    private static final Object SIGNAL = new Object();
    private static boolean ready = false;

    public static void main(String[] args) throws InterruptedException {

        section("1) wait / notify — single waiter");
        ready = false;
        Thread waiter = new Thread(() -> {
            synchronized (SIGNAL) {
                while (!ready) {
                    try { SIGNAL.wait(); } catch (InterruptedException ignored) {}
                }
                System.out.println("waiter saw ready");
            }
        }, "waiter");
        waiter.start();
        Thread.sleep(50);
        synchronized (SIGNAL) {
            ready = true;
            SIGNAL.notify();
        }
        waiter.join();

        section("2) notify vs notifyAll — multiple waiters, single predicate");
        ready = false;
        Thread[] waiters = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int id = i;
            waiters[i] = new Thread(() -> {
                synchronized (SIGNAL) {
                    while (!ready) {
                        try { SIGNAL.wait(); } catch (InterruptedException ignored) {}
                    }
                    System.out.println("  waiter " + id + " released");
                }
            }, "w-" + i);
            waiters[i].start();
        }
        Thread.sleep(50);
        synchronized (SIGNAL) {
            ready = true;
            SIGNAL.notifyAll();                  // wake them all
        }
        for (Thread t : waiters) t.join();

        section("3) Bounded buffer using wait/notifyAll (mini producer/consumer)");
        Buffer<Integer> buf = new Buffer<>(2);
        Thread prod = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try { buf.put(i); }
                catch (InterruptedException ignored) {}
                System.out.println("  produced " + i + ", size=" + buf.size());
            }
        }, "prod");
        Thread cons = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    int v = buf.take();
                    System.out.println("  consumed " + v + ", size=" + buf.size());
                    Thread.sleep(40);
                } catch (InterruptedException ignored) {}
            }
        }, "cons");
        prod.start(); cons.start();
        prod.join();  cons.join();

        section("4) wait without holding the lock = IllegalMonitorStateException");
        try {
            SIGNAL.wait();
        } catch (IllegalMonitorStateException e) {
            System.out.println("expected: " + e);
        }

        section("done");
    }

    /** Hand-built bounded buffer using wait/notifyAll. */
    static class Buffer<E> {
        private final Object[] items;
        private int count, head, tail;
        Buffer(int cap) { this.items = new Object[cap]; }

        synchronized void put(E e) throws InterruptedException {
            while (count == items.length) wait();        // wait until NOT full
            items[tail] = e;
            tail = (tail + 1) % items.length;
            count++;
            notifyAll();                                  // wake potential takers
        }

        @SuppressWarnings("unchecked")
        synchronized E take() throws InterruptedException {
            while (count == 0) wait();                    // wait until NOT empty
            E e = (E) items[head];
            items[head] = null;
            head = (head + 1) % items.length;
            count--;
            notifyAll();                                  // wake potential putters
            return e;
        }

        synchronized int size() { return count; }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
