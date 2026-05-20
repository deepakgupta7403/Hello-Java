package Phase7_Concurrency.Multithreading;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool & ForkJoinTask
 * ---------------------------
 * A SPECIALISED thread pool for DIVIDE-AND-CONQUER algorithms. Workers
 * use WORK-STEALING: when a worker's own deque runs dry, it steals
 * tasks from the back of a busier worker's deque. This is what makes
 * recursive task decomposition fast.
 *
 *
 * Two task base classes
 * ---------------------
 *   RecursiveAction       - no result; subclass and implement compute()
 *   RecursiveTask<V>      - returns V; subclass and implement compute() : V
 *
 *
 * Inside compute()
 * ----------------
 *   1. BASE CASE — small enough? Compute directly.
 *   2. SPLIT     — divide into subtasks.
 *   3. FORK      — submit one subtask asynchronously.
 *   4. COMPUTE   — run the other subtask on the CURRENT worker.
 *   5. JOIN      — wait for the forked subtask and combine results.
 *
 *      Pattern:
 *          ForkJoinTask<V> left = leftTask.fork();
 *          V rightResult = rightTask.compute();
 *          V leftResult = left.join();
 *          return combine(leftResult, rightResult);
 *
 *
 * The Common Pool
 * ---------------
 *   ForkJoinPool.commonPool() is shared by:
 *     - parallel streams (.parallelStream / .parallel())
 *     - CompletableFuture default-executor async stages
 *     - your own fork/join tasks if you don't supply a pool
 *
 *   Default parallelism = availableProcessors() - 1. Heavy work in one
 *   place starves the others. For dedicated workloads, create your own
 *   ForkJoinPool.
 *
 *
 * Don't do these
 * --------------
 *   - DON'T BLOCK on I/O inside a fork/join task — you'll stall the pool.
 *     Use ManagedBlocker if you must, or use a different executor.
 *   - DON'T HOLD LOCKS inside compute() — work-stealing pulls tasks
 *     across workers; reentrancy assumptions can break.
 */

public class ForkJoinPoolDemo {

    /** Sum 1..n using fork/join. */
    static class Sum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long lo, hi;          // inclusive..exclusive
        Sum(long lo, long hi) { this.lo = lo; this.hi = hi; }

        @Override protected Long compute() {
            long size = hi - lo;
            if (size <= THRESHOLD) {
                long s = 0;
                for (long i = lo; i < hi; i++) s += i;
                return s;
            }
            long mid = lo + size / 2;
            Sum left  = new Sum(lo,  mid);
            Sum right = new Sum(mid, hi);
            ForkJoinTask<Long> leftFuture = left.fork();     // run async
            long rightResult = right.compute();              // run inline
            long leftResult  = leftFuture.join();
            return leftResult + rightResult;
        }
    }

    /** Increment every element of an array in parallel — no result. */
    static class Bump extends RecursiveAction {
        private static final int THRESHOLD = 50_000;
        private final int[] data;
        private final int lo, hi;
        Bump(int[] data, int lo, int hi) { this.data = data; this.lo = lo; this.hi = hi; }
        @Override protected void compute() {
            if (hi - lo <= THRESHOLD) {
                for (int i = lo; i < hi; i++) data[i]++;
                return;
            }
            int mid = (lo + hi) >>> 1;
            invokeAll(new Bump(data, lo, mid), new Bump(data, mid, hi));
        }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) RecursiveTask — parallel sum");
        long n = 10_000_000L;
        long expected = n * (n - 1) / 2;          // sum 0..n-1
        long t = System.nanoTime();
        long parallel = new Sum(0, n).invoke();
        long parMs = (System.nanoTime() - t) / 1_000_000;
        System.out.println("parallel sum = " + parallel + " in " + parMs + " ms");

        t = System.nanoTime();
        long serial = 0;
        for (long i = 0; i < n; i++) serial += i;
        long seqMs = (System.nanoTime() - t) / 1_000_000;
        System.out.println("serial   sum = " + serial   + " in " + seqMs + " ms");
        System.out.println("equal? " + (parallel == expected));

        section("2) RecursiveAction — bump all elements of a large array");
        int[] data = new int[1_000_000];
        ForkJoinPool.commonPool().invoke(new Bump(data, 0, data.length));
        System.out.println("first=" + data[0] + ", mid=" + data[500_000] + ", last=" + data[data.length - 1]);

        section("3) Use a DEDICATED pool to avoid contending with the common pool");
        int cores = Runtime.getRuntime().availableProcessors();
        ForkJoinPool dedicated = new ForkJoinPool(cores);
        try {
            long s = dedicated.invoke(new Sum(0, 1_000_000));
            System.out.println("dedicated sum = " + s);
        } finally {
            dedicated.shutdown();
            dedicated.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        }

        section("4) Common pool parallelism");
        System.out.println("commonPool parallelism = " + ForkJoinPool.commonPool().getParallelism());
        System.out.println("(Set via -Djava.util.concurrent.ForkJoinPool.common.parallelism=N)");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
