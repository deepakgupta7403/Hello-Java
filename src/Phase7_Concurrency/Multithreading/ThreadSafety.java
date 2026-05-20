package Phase7_Concurrency.Multithreading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread Safety
 * -------------
 * A class is THREAD-SAFE when it behaves correctly when used from
 * multiple threads, with NO additional synchronization required from
 * the caller (Brian Goetz).
 *
 *
 * Strategies for Thread Safety
 * ----------------------------
 *   1. IMMUTABILITY
 *        - All fields final, no mutator methods.
 *        - Once constructed safely, no other thread can change state.
 *        - Examples: String, Integer, java.time.LocalDate, records.
 *
 *   2. CONFINEMENT
 *        - Only one thread ever touches the object.
 *        - Examples: a local variable (stack confinement), ThreadLocal,
 *          GUI components on the EDT (Swing rule).
 *
 *   3. SYNCHRONIZATION
 *        - synchronized / Lock guarantees mutual exclusion + visibility.
 *        - Vector, Hashtable, Collections.synchronizedList(...) are the
 *          classic synchronized-wrapper approach (compound ops still
 *          need external synchronization).
 *
 *   4. ATOMIC PRIMITIVES
 *        - Atomic*, LongAdder, LongAccumulator — lock-free counters.
 *
 *   5. CONCURRENT COLLECTIONS
 *        - ConcurrentHashMap, CopyOnWriteArrayList, ConcurrentLinkedQueue,
 *          BlockingQueue.
 *
 *   6. VOLATILE
 *        - Visibility-only for SINGLE-WRITER fields (e.g. a stop flag).
 *
 *
 * Levels of Thread Safety (Goetz et al.)
 * --------------------------------------
 *   Immutable          - safe forever (String, BigDecimal, records).
 *   Thread-safe        - all required synchronization is internal
 *                        (ConcurrentHashMap, AtomicInteger).
 *   Conditionally safe - safe if caller follows a contract
 *                        (an Iterator over a synchronized list).
 *   Thread-compatible  - the class works in single-threaded code; the
 *                        caller adds synchronization (ArrayList).
 *   Thread-hostile     - cannot be made safe (broken on purpose).
 *
 *
 * Compound operations
 * -------------------
 * Even a "thread-safe" container can be misused if you compose calls:
 *
 *      if (!map.containsKey(k)) map.put(k, v);   // race between two calls
 *
 * Use the SINGLE atomic methods instead:
 *
 *      map.putIfAbsent(k, v);
 *      map.computeIfAbsent(k, key -> compute(key));
 */

public class ThreadSafety {

    /** A simple non-thread-safe counter. */
    static class UnsafeCounter {
        int n;
        void bump() { n++; }
    }

    /** Synchronized version. */
    static class SyncCounter {
        int n;
        synchronized void bump() { n++; }
        synchronized int get()   { return n; }
    }

    /** Lock-free version using AtomicInteger. */
    static class AtomicCounter {
        final AtomicInteger n = new AtomicInteger();
        void bump() { n.incrementAndGet(); }
        int get()   { return n.get(); }
    }

    /** Immutable point — thread-safe by construction. */
    static record Point(int x, int y) {
        Point translate(int dx, int dy) { return new Point(x + dx, y + dy); }
    }

    public static void main(String[] args) throws InterruptedException {

        section("1) Counter race condition");
        UnsafeCounter uc = new UnsafeCounter();
        runInParallel(2, 100_000, uc::bump);
        System.out.println("unsafe = " + uc.n + "   (expected 200000)");

        section("2) Fix with synchronized");
        SyncCounter sc = new SyncCounter();
        runInParallel(2, 100_000, sc::bump);
        System.out.println("sync   = " + sc.get());

        section("3) Fix with AtomicInteger (lock-free)");
        AtomicCounter ac = new AtomicCounter();
        runInParallel(2, 100_000, ac::bump);
        System.out.println("atomic = " + ac.get());

        section("4) Immutable: thread-safe by construction");
        Point p = new Point(1, 2);
        // Any number of threads can read p.x() / p.y() — safe.
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) System.out.println("  read " + p);
        };
        Thread r1 = new Thread(reader);
        Thread r2 = new Thread(reader);
        r1.start(); r2.start();
        r1.join();  r2.join();

        section("5) Synchronized-wrapper collections (still need ext. sync for iteration)");
        List<Integer> sl = Collections.synchronizedList(new ArrayList<>());
        sl.add(1); sl.add(2); sl.add(3);
        // Each add/get is atomic. ITERATING is NOT — must wrap externally:
        synchronized (sl) {
            for (int v : sl) System.out.println("  " + v);
        }

        section("6) Concurrent containers");
        Map<String,Integer> cm = new ConcurrentHashMap<>();
        cm.put("a", 1);
        // SAFE compound operation:
        cm.computeIfAbsent("b", k -> 2);
        // UNSAFE compound operation if you wrote:
        // if (!cm.containsKey("c")) cm.put("c", 3);
        System.out.println("ConcurrentHashMap = " + cm);

        List<Integer> snap = new CopyOnWriteArrayList<>(List.of(10, 20, 30));
        // Iterating a CoW list snapshots — never throws ConcurrentModificationException.
        for (int v : snap) {
            snap.add(v + 1);                      // safe; doesn't affect this iteration
        }
        System.out.println("CoWArrayList = " + snap);

        section("7) Legacy synchronized: Vector and Hashtable");
        // Synchronized but slow + their iterators throw ConcurrentModificationException.
        Vector<Integer> v = new Vector<>(List.of(1,2,3));
        Hashtable<String,Integer> h = new Hashtable<>(Map.of("a",1,"b",2));
        System.out.println("vector=" + v + ", hashtable=" + h);

        section("done");
    }

    private static void runInParallel(int threads, int iterations, Runnable task) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> { for (int k = 0; k < iterations; k++) task.run(); });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
