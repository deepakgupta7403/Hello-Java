package Phase7_Concurrency.Multithreading;

import java.util.concurrent.atomic.*;

/**
 * java.util.concurrent.atomic
 * ---------------------------
 * Lock-free counters / references built on COMPARE-AND-SET (CAS) — a
 * hardware instruction that atomically updates a memory cell only if it
 * still matches an expected old value.
 * <p>
 *
 * Compared with `synchronized`, atomics:
 *   + Scale better under contention for SIMPLE updates (counters, refs).
 *   + Never block — no risk of deadlock from atomic ops.
 *   - Cannot atomically update MULTIPLE variables; use a lock for that.
 *   - Under HEAVY contention, CAS-retry loops still spin; use LongAdder /
 *     DoubleAdder for hot counters.
 * <p>
 *
 * The Family
 * ----------
 *   AtomicBoolean
 *   AtomicInteger / AtomicLong
 *   AtomicReference<V>
 *   AtomicIntegerArray / AtomicLongArray / AtomicReferenceArray<V>
 *   AtomicStampedReference<V>      - reference + integer "stamp" to defeat ABA
 *   AtomicMarkableReference<V>     - reference + boolean "mark"
 *   LongAdder / DoubleAdder        - high-throughput counter (striped cells)
 *   LongAccumulator / DoubleAccumulator - LongAdder with arbitrary binary op
 *   AtomicIntegerFieldUpdater etc. - reflection-based update of volatile fields
 * <p>
 *
 * Common Methods
 * --------------
 *   get / set                                    - plain read/write
 *   lazySet                                       - relaxed write, eventually visible
 *   compareAndSet(expect, update)                - CAS; true if it swapped
 *   incrementAndGet / decrementAndGet / addAndGet
 *   updateAndGet(UnaryOperator)                  - lambda-driven CAS loop
 *   accumulateAndGet(x, BinaryOperator)          - lambda-driven CAS with arg
 * <p>
 *
 * The ABA Problem
 * ---------------
 * CAS only sees "is the value still X?" — not "has it changed and changed
 * back?" If you need ABA-detection, use AtomicStampedReference (stamp
 * increases on every update).
 */

public class AtomicVariables {

    public static void main(String[] args) throws InterruptedException {

        section("1) AtomicInteger as a counter");
        AtomicInteger ai = new AtomicInteger();
        runMany(8, 100_000, ai::incrementAndGet);
        System.out.println("AtomicInteger = " + ai.get());

        section("2) AtomicLong updateAndGet — lambda CAS loop");
        AtomicLong sum = new AtomicLong();
        runMany(4, 50_000, () -> sum.updateAndGet(x -> x + 2));
        System.out.println("sum = " + sum.get());

        section("3) compareAndSet — the primitive");
        AtomicInteger cas = new AtomicInteger(10);
        System.out.println("CAS 10 -> 20 succeeds = " + cas.compareAndSet(10, 20));
        System.out.println("CAS 10 -> 30 fails    = " + cas.compareAndSet(10, 30));
        System.out.println("value now             = " + cas.get());

        section("4) AtomicBoolean as a one-shot flag");
        AtomicBoolean started = new AtomicBoolean();
        Runnable starter = () -> {
            if (started.compareAndSet(false, true)) {
                System.out.println("  " + Thread.currentThread().getName() + " started!");
            } else {
                System.out.println("  " + Thread.currentThread().getName() + " saw already-started");
            }
        };
        Thread x = new Thread(starter, "x"); Thread y = new Thread(starter, "y");
        x.start(); y.start(); x.join(); y.join();

        section("5) AtomicReference<V> — atomic immutable updates");
        AtomicReference<int[]> ref = new AtomicReference<>(new int[]{1, 2});
        ref.updateAndGet(arr -> new int[]{arr[0] + 1, arr[1] + 1});
        int[] now = ref.get();
        System.out.println("ref = [" + now[0] + ", " + now[1] + "]");

        section("6) AtomicIntegerArray — atomic ops per slot");
        AtomicIntegerArray slots = new AtomicIntegerArray(4);
        runMany(8, 1_000, () -> slots.incrementAndGet((int)(Math.random() * 4)));
        for (int i = 0; i < slots.length(); i++) System.out.print(slots.get(i) + " ");
        System.out.println();

        section("7) LongAdder vs AtomicLong under contention");
        AtomicLong al = new AtomicLong();
        LongAdder la = new LongAdder();
        long t0 = System.nanoTime();
        runMany(8, 1_000_000, al::incrementAndGet);
        long alMs = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        runMany(8, 1_000_000, la::increment);
        long laMs = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("AtomicLong : " + al.get() + " in " + alMs + " ms");
        System.out.println("LongAdder  : " + la.sum() + " in " + laMs + " ms");
        System.out.println("(LongAdder usually wins under heavy write contention.)");

        section("8) DoubleAdder + LongAccumulator with a custom op");
        DoubleAdder da = new DoubleAdder();
        runMany(4, 1_000, () -> da.add(0.5));
        System.out.println("DoubleAdder sum = " + da.sum());

        LongAccumulator max = new LongAccumulator(Long::max, Long.MIN_VALUE);
        runMany(4, 1_000, () -> max.accumulate((long)(Math.random() * 1_000_000)));
        System.out.println("LongAccumulator max = " + max.get());

        section("9) ABA — AtomicStampedReference");
        AtomicStampedReference<String> asr = new AtomicStampedReference<>("A", 0);
        int[] stamp = new int[1];
        String snapshot = asr.get(stamp);
        // Someone else: A -> B -> A. The stamp went 0 -> 1 -> 2.
        asr.compareAndSet("A", "B", 0, 1);
        asr.compareAndSet("B", "A", 1, 2);
        // Plain CAS would not notice this. The stamped CAS does:
        boolean swapped = asr.compareAndSet(snapshot, "C", stamp[0], stamp[0] + 1);
        System.out.println("ABA-safe CAS swapped? " + swapped + " (should be false)");

        section("done");
    }

    private static void runMany(int threads, int iters, Runnable task) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> { for (int k = 0; k < iters; k++) task.run(); });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
