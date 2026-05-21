package Phase7_Concurrency.Multithreading;

/**
 * Multithreading Introduction
 * ---------------------------
 * A PROCESS is an instance of a running program. The JVM itself runs as
 * one OS process. Inside that process, work is performed by THREADS —
 * lightweight, independent paths of execution that share the process's
 * heap and code segment but have their own STACK and PROGRAM COUNTER.
 * <p>
 *
 * MULTITHREADING is the ability of a CPU (or several CPUs) to provide
 * multiple threads of execution concurrently. A single Java program can
 * spawn many threads to do work in parallel, react to events, or keep a
 * UI responsive.
 * <p>
 *
 * Concurrency vs Parallelism
 * --------------------------
 *   CONCURRENCY  - dealing with many things at once (structure).
 *                  Threads take turns on a single core, time-sliced by
 *                  the OS scheduler.
 * <p>
 *
 *   PARALLELISM  - doing many things at once (execution).
 *                  Multiple cores run multiple threads simultaneously.
 * <p>
 *
 * Java's concurrency primitives let you express both.
 * <p>
 *
 * Why Multithreading?
 * -------------------
 *   - UTILISE THE CPU      - keep all cores busy while one thread waits
 *                            for I/O.
 *   - RESPONSIVENESS       - keep the UI / event loop alive while
 *                            background work happens.
 *   - THROUGHPUT           - process many requests at the same time
 *                            (servers, schedulers, pipelines).
 *   - MODEL THE PROBLEM    - some problems are naturally concurrent
 *                            (producer/consumer, game loops, sensors).
 * <p>
 *
 * Threads vs Processes
 * --------------------
 *                       Process                    Thread
 *                       -------                    ------
 *   Memory             Own address space          Shared with siblings
 *   Creation cost      Heavy                       Light
 *   Communication      IPC (pipes, sockets...)    Shared variables
 *   Crash blast        Isolated                    Whole process dies
 * <p>
 *
 * What Java Gives You
 * -------------------
 *   1. java.lang.Thread           - the low-level building block.
 *   2. java.lang.Runnable         - a unit of work to give a Thread.
 *   3. java.util.concurrent.*     - executors, futures, queues, locks,
 *                                   atomics, synchronizers (Java 5+).
 *   4. java.util.concurrent.locks - Lock, ReadWriteLock, StampedLock.
 *   5. Virtual Threads (Java 21)  - millions of cheap user-mode threads.
 *   6. Structured Concurrency     - JEP 453 preview, treats a group of
 *                                   tasks as a single unit of work.
 *   7. Scoped Values              - JEP 446 preview, immutable
 *                                   alternative to ThreadLocal.
 * <p>
 *
 * Hello, threads!
 * ---------------
 * The smallest possible multithreaded program runs two threads — the
 * MAIN thread you started in, and one you create yourself.
 */

public class MultithreadingIntroduction {

    public static void main(String[] args) throws InterruptedException {

        section("1) The main thread is already running");
        System.out.println("main thread name = " + Thread.currentThread().getName());

        section("2) Start a new thread that prints concurrently with main");
        // A Thread is created with a Runnable describing the work to run.
        Thread worker = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  worker step " + i +
                        "  (on " + Thread.currentThread().getName() + ")");
                pause(50);
            }
        }, "worker-1");
        worker.start();                       // schedule + run() in a NEW thread

        for (int i = 1; i <= 3; i++) {
            System.out.println("main step " + i +
                    "  (on " + Thread.currentThread().getName() + ")");
            pause(50);
        }

        // Wait for the worker to finish before exiting main.
        worker.join();

        section("3) CPU information");
        System.out.println("available processors = " + Runtime.getRuntime().availableProcessors());
        System.out.println("active thread count  = " + Thread.activeCount());

        section("4) Concurrency vs parallelism in one demo");
        // Two threads counting — they will interleave on output. On a
        // multi-core machine both may run truly in parallel.
        Thread a = new Thread(() -> count("A"), "thread-A");
        Thread b = new Thread(() -> count("B"), "thread-B");
        a.start(); b.start();
        a.join();  b.join();

        section("done");
        // Expected (interleaving varies):
        //   thread-A 1
        //   thread-B 1
        //   thread-A 2
        //   thread-B 2
        //   ...
    }

    private static void count(String tag) {
        for (int i = 1; i <= 3; i++) {
            System.out.println("  " + tag + " " + i);
            pause(30);
        }
    }

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
