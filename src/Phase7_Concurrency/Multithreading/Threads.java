package Phase7_Concurrency.Multithreading;

/**
 * java.lang.Thread
 * ----------------
 * Thread is the JVM-level representation of a thread of execution.
 * Every Java program starts with one Thread (the "main" thread). All
 * other threads must be CREATED explicitly.
 * <p>
 *
 * Two Ways To Define Work
 * -----------------------
 *   1. EXTEND Thread             - override run(). Couples your code to
 *                                  the Thread class. Cannot also extend
 *                                  another class.
 * <p>
 *
 *   2. IMPLEMENT Runnable        - pass to a Thread / ExecutorService.
 *                                  PREFERRED — separates the work from
 *                                  the mechanism. Works with thread pools.
 * <p>
 *
 * Important Constructors
 * ----------------------
 *   new Thread()                            - no-op run() by default.
 *   new Thread(Runnable r)                  - run r.run() on a new thread.
 *   new Thread(Runnable r, String name)     - give it a debugging-friendly name.
 *   new Thread(ThreadGroup g, Runnable r)   - join a thread group (rarely used).
 * <p>
 *
 * Important Instance Methods
 * --------------------------
 *   start()           - schedules the thread and calls run() on it.
 *                       Throwing IllegalThreadStateException if called twice.
 *   run()             - the work itself. DO NOT CALL DIRECTLY — that runs on
 *                       the current thread, not a new one.
 *   join()            - wait for the thread to finish.
 *   interrupt()       - request the thread to stop (cooperative).
 *   isAlive()         - has it started and not finished?
 *   getId()           - JVM-unique id for this thread.
 *   getName() / setName(...)
 *   getPriority() / setPriority(int)
 *   setDaemon(boolean) - daemon threads don't keep the JVM alive.
 *   getState()        - lifecycle state (NEW, RUNNABLE, ...).
 * <p>
 *
 * Important Static Methods
 * ------------------------
 *   currentThread()   - the running thread.
 *   sleep(ms)         - pause this thread (releases CPU, KEEPS LOCKS).
 *   yield()           - hint to the scheduler: I'm OK to wait.
 *   onSpinWait()      - hint inside a busy-wait loop (Java 9+).
 *   activeCount()     - number of active threads in the current group.
 * <p>
 *
 * Static Methods Java 19+ — Virtual Threads
 * -----------------------------------------
 *   ofPlatform()      - builder for a classic OS-backed thread.
 *   ofVirtual()       - builder for a virtual (user-mode) thread.
 *   startVirtualThread(Runnable) - one-shot virtual thread.
 * <p>
 *
 * Naming, IDs, Equality
 * ---------------------
 * Two Thread objects are equal only via reference equality (==). Names
 * are NOT unique. Always set a meaningful name — it shows up in stack
 * traces and profilers.
 */

public class Threads {

    public static void main(String[] args) throws InterruptedException {

        section("1) Extending Thread");
        MyThread t1 = new MyThread("ext-1");
        t1.start();
        t1.join();

        section("2) Implementing Runnable");
        // Same work, but composable with thread pools, lambdas, etc.
        Thread t2 = new Thread(new MyTask(), "run-1");
        t2.start();
        t2.join();

        section("3) Runnable as a lambda");
        Thread t3 = new Thread(() -> System.out.println("lambda task on " +
                Thread.currentThread().getName()), "lambda-1");
        t3.start();
        t3.join();

        section("4) Thread identity and properties");
        Thread t4 = new Thread(() -> {}, "props");
        System.out.println("id        = " + t4.threadId());      // Java 19+ replaces getId
        System.out.println("name      = " + t4.getName());
        System.out.println("priority  = " + t4.getPriority());
        System.out.println("daemon    = " + t4.isDaemon());
        System.out.println("alive     = " + t4.isAlive());
        System.out.println("state     = " + t4.getState());

        section("5) Inspect the main thread");
        Thread main = Thread.currentThread();
        System.out.println(main.getName() + " state = " + main.getState() +
                ", priority = " + main.getPriority() +
                ", daemon   = " + main.isDaemon());

        section("6) Calling start() twice throws IllegalThreadStateException");
        Thread once = new Thread(() -> {}, "once");
        once.start();
        once.join();
        try { once.start(); }
        catch (IllegalThreadStateException ex) {
            System.out.println("expected: " + ex);
        }

        section("7) Java 21 platform vs virtual builders");
        Thread platform = Thread.ofPlatform().name("platform-1").unstarted(() ->
                System.out.println("hi from " + Thread.currentThread()));
        platform.start();
        platform.join();

        Thread virtual = Thread.ofVirtual().name("virt-1").start(() ->
                System.out.println("hi from " + Thread.currentThread()));
        virtual.join();

        section("done");
    }

    /** Variant 1: extending Thread directly. */
    static class MyThread extends Thread {
        MyThread(String name) { super(name); }
        @Override public void run() {
            System.out.println("MyThread running on " + getName());
        }
    }

    /** Variant 2: implementing Runnable — preferred in modern Java. */
    static class MyTask implements Runnable {
        @Override public void run() {
            System.out.println("MyTask running on " + Thread.currentThread().getName());
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
