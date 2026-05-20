package Basics.Multithreading;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread Pools
 * ------------
 * Creating one Thread per task is fine for tens of tasks; for thousands
 * it's a disaster (thread creation is expensive, OS limits exist). A
 * THREAD POOL keeps a small set of WORKER threads alive and feeds them
 * tasks from a QUEUE.
 *
 *
 * ThreadPoolExecutor — the core class
 * -----------------------------------
 *   new ThreadPoolExecutor(
 *       int corePoolSize,                 - always-running workers
 *       int maximumPoolSize,              - upper bound when busy
 *       long keepAliveTime, TimeUnit unit,- idle workers above core are reaped
 *       BlockingQueue<Runnable> workQueue,- where tasks wait for a worker
 *       ThreadFactory threadFactory,     - how to make threads
 *       RejectedExecutionHandler handler - what to do when full)
 *
 *
 * How the pool routes a submitted task
 * ------------------------------------
 *   1. Fewer than corePoolSize workers? -> create a new worker for the task.
 *   2. Otherwise: try to ENQUEUE the task on the workQueue.
 *   3. Queue full? -> If under maximumPoolSize, create a new worker.
 *   4. Pool also full? -> RejectedExecutionHandler fires.
 *
 *
 * Rejection policies
 * ------------------
 *   AbortPolicy        (default) - throw RejectedExecutionException
 *   CallerRunsPolicy             - run the task on the SUBMITTING thread
 *                                  (backpressure)
 *   DiscardPolicy                - silently drop the task
 *   DiscardOldestPolicy          - drop the oldest queued task, then submit
 *
 *
 * Queue choice strongly affects behaviour
 * ---------------------------------------
 *   SynchronousQueue        - zero capacity, hands off directly. With
 *                              max=∞ this is Executors.newCachedThreadPool.
 *   LinkedBlockingQueue (unbounded) - acts like a fixed pool that never
 *                              grows. With Executors.newFixedThreadPool.
 *   ArrayBlockingQueue (bounded) - encourages the pool to grow up to max.
 *   PriorityBlockingQueue   - tasks must be Comparable.
 *
 *
 * Why Executors.* sometimes bites you
 * -----------------------------------
 *   newFixedThreadPool   - unbounded queue. Memory leak under overload.
 *   newCachedThreadPool  - unbounded workers. Thread explosion.
 *   newSingleThreadExecutor - unbounded queue.
 * For production, construct a ThreadPoolExecutor yourself with bounded
 * queue + bounded max + a sane rejection policy.
 */

public class ThreadPools {

    public static void main(String[] args) throws InterruptedException {

        section("1) ThreadPoolExecutor with bounded queue + CallerRunsPolicy");
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 4,                                       // core 2, max 4
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),                // bounded queue, capacity 2
                threadFactory("hot"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        for (int i = 0; i < 10; i++) {
            final int id = i;
            pool.execute(() -> {
                System.out.println("  task " + id + " on " + Thread.currentThread().getName());
                sleep(60);
            });
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        section("2) Show that 'cached' pools can explode");
        ThreadPoolExecutor cached = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),                   // no queue at all
                threadFactory("cached")
        );
        for (int i = 0; i < 5; i++) cached.execute(() -> sleep(40));
        System.out.println("cached pool size = " + cached.getPoolSize() + " (grew on demand)");
        cached.shutdown();
        cached.awaitTermination(5, TimeUnit.SECONDS);

        section("3) AbortPolicy throws when the pool can't accept");
        ThreadPoolExecutor strict = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1),               // queue size 1
                threadFactory("strict"),
                new ThreadPoolExecutor.AbortPolicy()
        );
        strict.execute(() -> sleep(200));   // busy
        strict.execute(() -> sleep(200));   // queued
        try {
            strict.execute(() -> sleep(200));   // rejected
        } catch (RejectedExecutionException rex) {
            System.out.println("rejected as expected: " + rex);
        }
        strict.shutdown();
        strict.awaitTermination(5, TimeUnit.SECONDS);

        section("4) prestartAllCoreThreads — warm up the pool");
        ThreadPoolExecutor warm = new ThreadPoolExecutor(
                3, 3, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory("warm"));
        warm.prestartAllCoreThreads();
        System.out.println("warm pool size = " + warm.getPoolSize() + " (3 ready before any task)");
        warm.shutdown();

        section("5) shutdown vs shutdownNow");
        ThreadPoolExecutor sd = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory("sd"));
        for (int i = 0; i < 3; i++) {
            final int id = i;
            sd.execute(() -> { System.out.println("  task " + id); sleep(60); });
        }
        sd.shutdown();                       // accept no new tasks; let queued ones finish
        sd.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("isShutdown = " + sd.isShutdown() + ", isTerminated = " + sd.isTerminated());

        section("done");
    }

    private static ThreadFactory threadFactory(String prefix) {
        AtomicInteger n = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r, prefix + "-" + n.incrementAndGet());
            t.setDaemon(false);
            return t;
        };
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
