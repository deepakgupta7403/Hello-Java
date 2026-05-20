package Phase7_Concurrency.Multithreading;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Multithreading Complete Tutorial
 * --------------------------------
 * One file, end-to-end, touching every major topic in the section.
 * Pair this with the dedicated files for depth.
 *
 *
 * Table of contents
 * -----------------
 *   1.  Spawning a thread          (Threads, Runnable)
 *   2.  Lifecycle states           (ThreadLifecycle)
 *   3.  start vs run               (StartVsRun)
 *   4.  Join / sleep / interrupt   (ThreadJoinMethod, ThreadSleepMethod, ThreadInterruption)
 *   5.  Priority / daemon          (ThreadPriority, DaemonThread)
 *   6.  Synchronized + thread safety (JavaSynchronization, ThreadSafety)
 *   7.  volatile + JMM             (VolatileKeyword, JavaMemoryModel)
 *   8.  wait/notify + producer/consumer (WaitNotifyNotifyAll, ProducerConsumer)
 *   9.  ThreadLocal                (ThreadLocalDemo)
 *  10.  Atomics                    (AtomicVariables)
 *  11.  Locks + Conditions         (LocksInJava, ReentrantLockDemo)
 *  12.  Read/Write + Stamped       (ReadWriteLockDemo, StampedLockDemo)
 *  13.  Deadlock + livelock        (DeadlockDemo, RaceConditionStarvationLivelock)
 *  14.  Thread pools / Executor    (ThreadPools, ExecutorFramework)
 *  15.  Scheduled / ForkJoin       (ScheduledExecutorDemo, ForkJoinPoolDemo)
 *  16.  CompletableFuture          (CompletableFutureDemo)
 *  17.  Synchronizers              (CountDownLatch, CyclicBarrier, Semaphore, Phaser)
 *  18.  Java 21: Virtual threads   (VirtualThreads)
 *  19.  Java 21: Structured concurrency / Scoped values
 *  20.  Putting it together: tiny pipeline
 */

public class MultithreadingCompleteTutorial {

    public static void main(String[] args) throws Exception {

        section("1) Spawn a thread");
        Thread t = new Thread(() ->
                System.out.println("hi from " + Thread.currentThread().getName()), "first");
        t.start();
        t.join();

        section("2) Lifecycle snapshot");
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
        }, "sleeper");
        sleeper.start();
        Thread.sleep(20);
        System.out.println("sleeper state = " + sleeper.getState());      // TIMED_WAITING
        sleeper.join();
        System.out.println("after join, state = " + sleeper.getState()); // TERMINATED

        section("3) Synchronized + race condition fix");
        AtomicInteger unsafe = new AtomicInteger();
        Object lock = new Object();
        int[] safe = { 0 };
        Runnable mix = () -> {
            for (int i = 0; i < 50_000; i++) {
                unsafe.incrementAndGet();                  // atomic
                synchronized (lock) { safe[0]++; }         // synchronized
            }
        };
        Thread a = new Thread(mix), b = new Thread(mix);
        a.start(); b.start(); a.join(); b.join();
        System.out.println("atomic = " + unsafe.get() + ", sync = " + safe[0]);

        section("4) Producer / consumer with wait/notifyAll");
        var buf = new java.util.LinkedList<Integer>();
        int CAP = 3;
        Thread prod = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    synchronized (buf) {
                        while (buf.size() == CAP) buf.wait();
                        buf.add(i); System.out.println("  put " + i);
                        buf.notifyAll();
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        });
        Thread cons = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    int v;
                    synchronized (buf) {
                        while (buf.isEmpty()) buf.wait();
                        v = buf.remove();
                        buf.notifyAll();
                    }
                    System.out.println("  got " + v);
                    Thread.sleep(20);
                }
            } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        });
        prod.start(); cons.start(); prod.join(); cons.join();

        section("5) ReentrantLock + tryLock");
        ReentrantLock rl = new ReentrantLock();
        rl.lock();
        try { System.out.println("got it, holdCount=" + rl.getHoldCount()); }
        finally { rl.unlock(); }

        section("6) Executor framework + Future");
        try (ExecutorService es = Executors.newFixedThreadPool(2)) {
            int sum = es.submit(() -> {
                int s = 0; for (int i = 1; i <= 100; i++) s += i; return s;
            }).get();
            System.out.println("sum 1..100 = " + sum);
        }

        section("7) Synchronizers — CountDownLatch + Semaphore");
        CountDownLatch start = new CountDownLatch(1);
        Semaphore slots = new Semaphore(2);
        Thread[] hot = new Thread[5];
        for (int i = 0; i < hot.length; i++) {
            final int id = i;
            hot[i] = new Thread(() -> {
                try {
                    start.await();
                    slots.acquire();
                    try {
                        System.out.println("  task " + id + " in critical section");
                        Thread.sleep(30);
                    } finally { slots.release(); }
                } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            });
            hot[i].start();
        }
        Thread.sleep(50);
        start.countDown();                                  // fire the starting gun
        for (Thread x : hot) x.join();

        section("8) CompletableFuture pipeline");
        CompletableFuture<Integer> pipeline = CompletableFuture
                .supplyAsync(() -> 21)
                .thenApply(n -> n * 2)
                .thenApply(n -> n + 0);
        System.out.println("pipeline = " + pipeline.get());

        section("9) Virtual threads — 5000 sleeping in parallel");
        long t0 = System.currentTimeMillis();
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> jobs = new java.util.ArrayList<>();
            for (int i = 0; i < 5_000; i++) {
                jobs.add(CompletableFuture.runAsync(() -> {
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                }, vts));
            }
            for (CompletableFuture<Void> f : jobs) f.get();
        }
        System.out.println("5000 virtual sleepers done in " + (System.currentTimeMillis() - t0) + " ms");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
