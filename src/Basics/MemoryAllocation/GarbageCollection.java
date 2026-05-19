package Basics.MemoryAllocation;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.Cleaner;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Garbage Collection (GC)
 * -----------------------
 * Java's garbage collector reclaims memory occupied by objects that are no
 * longer "reachable" from the program. You never `free` or `delete`
 * yourself - the JVM does it for you, on a schedule it controls.
 *
 *
 * Reachability - The Core Rule
 * ----------------------------
 * An object is REACHABLE if some chain of references can be traced to it
 * from a GC ROOT. GC roots include:
 *
 *   - Local variables in active stack frames (stack roots).
 *   - Static fields of loaded classes.
 *   - Active JNI references from native code.
 *   - Currently executing threads.
 *
 * Anything else - no path back to a root - is UNREACHABLE and may be
 * collected at any time.
 *
 *
 * The Classic Mark-and-Sweep
 * --------------------------
 *   1. MARK   - traverse the object graph from every root and mark every
 *               reachable object.
 *   2. SWEEP  - walk the heap and reclaim space occupied by anything not
 *               marked. (Modern collectors also COMPACT to keep the heap
 *               un-fragmented.)
 *
 *
 * The Generational Hypothesis
 * ---------------------------
 *   - Most objects DIE YOUNG.
 *   - Long-lived objects rarely reference young ones.
 *
 * Modern collectors exploit this by splitting the heap into a YOUNG
 * generation (cheap, frequent "minor" GCs) and an OLD generation
 * (expensive "major" GCs, but rarely needed).
 *
 *
 * Three Kinds of References (java.lang.ref)
 * -----------------------------------------
 *   STRONG       - the normal kind. As long as one strong reference exists,
 *                  the GC cannot reclaim the object.
 *   SOFT         - may be cleared at the GC's discretion when memory is
 *                  tight. Good for memory-sensitive caches.
 *   WEAK         - cleared on the next GC. The classic example is
 *                  WeakHashMap, where map ENTRIES disappear when no other
 *                  code holds the KEY.
 *   PHANTOM      - never returns the referent; useful for resource cleanup
 *                  via java.lang.ref.Cleaner.
 *
 *
 * Cleaner (Java 9+) - the Modern Replacement for finalize()
 * ----------------------------------------------------------
 * `Object.finalize()` is unreliable, deprecated, and slow. Use Cleaner for
 * deterministic, predictable cleanup of off-heap or native resources.
 *
 *
 * What System.gc() Does
 * ---------------------
 * It is only a HINT. The JVM is free to ignore it. Useful in
 * micro-benchmarks; almost never useful in real applications.
 */

public class GarbageCollection {

    public static void main(String[] args) throws Exception {

        section("1) The collectors registered in this JVM");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("  " + gc.getName() + "  (collections so far: "
                                       + gc.getCollectionCount() + ")");
        }

        section("2) An unreachable object becomes eligible for GC");
        Object o = new Object();
        System.out.println("o = " + o);
        WeakReference<Object> watcher = new WeakReference<>(o);
        o = null;                                     // drop the strong ref
        encourageGc();
        System.out.println("after o=null + gc, watcher.get() = " + watcher.get());

        section("3) Strong reference keeps the object alive");
        Object strong = new Object();
        WeakReference<Object> w2 = new WeakReference<>(strong);
        encourageGc();
        System.out.println("with strong ref alive, w2.get() = " + w2.get());

        section("4) WeakReference vs SoftReference");
        SoftReference<byte[]> soft = new SoftReference<>(new byte[10 * 1024 * 1024]);
        WeakReference<byte[]> weak = new WeakReference<>(new byte[10 * 1024 * 1024]);
        encourageGc();
        // Weak refs are cleared on the next GC.
        // Soft refs survive normal GC; only collected under memory pressure.
        System.out.println("weak.get() after gc = " + (weak.get() == null ? "null (cleared)" : "kept"));
        System.out.println("soft.get() after gc = " + (soft.get() == null ? "null (cleared)" : "kept"));

        section("5) WeakHashMap - automatic eviction when keys go away");
        java.util.Map<Object, String> map = new WeakHashMap<>();
        Object key = new Object();
        map.put(key, "the value");
        System.out.println("size before = " + map.size());
        key = null;                                   // remove the strong ref
        encourageGc();
        System.out.println("size after  = " + map.size() + " (entry was auto-removed)");

        section("6) Cleaner - modern replacement for finalize()");
        Cleaner cleaner = Cleaner.create();
        Object owner = new Object();
        cleaner.register(owner, () -> System.out.println("  cleanup task ran"));
        owner = null;
        encourageGc();
        Thread.sleep(100);     // give Cleaner's daemon thread a moment

        section("7) Counting GC cycles before and after a big allocation burst");
        long start = totalCollections();
        for (int i = 0; i < 200; i++) {
            byte[] junk = new byte[1024 * 1024];
            junk[0] = 1;
        }
        long end = totalCollections();
        System.out.println("collections triggered by burst: " + (end - start));

        // OUTPUT (representative)
        // ====== 1) The collectors registered in this JVM ======
        //   G1 Young Generation  (collections so far: 0)
        //   G1 Old Generation    (collections so far: 0)
        // ====== 2) An unreachable object becomes eligible for GC ======
        // o = java.lang.Object@1540e19d
        // after o=null + gc, watcher.get() = null
        // ====== 3) Strong reference keeps the object alive ======
        // with strong ref alive, w2.get() = java.lang.Object@<addr>
        // ====== 4) WeakReference vs SoftReference ======
        // weak.get() after gc = null (cleared)
        // soft.get() after gc = kept                  (under low memory, would be null)
        // ====== 5) WeakHashMap - automatic eviction when keys go away ======
        // size before = 1
        // size after  = 0 (entry was auto-removed)
        // ====== 6) Cleaner - modern replacement for finalize() ======
        //   cleanup task ran
        // ====== 7) Counting GC cycles before and after a big allocation burst ======
        // collections triggered by burst: 7
    }

    /** Nudge the GC. Calls + small wait - still only a hint. */
    private static void encourageGc() {
        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
    }

    /** Total GC collections across all registered collectors. */
    private static long totalCollections() {
        long total = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            total += Math.max(0, gc.getCollectionCount());
        }
        return total;
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    // Suppress unused warning for the import we keep for documentation
    @SuppressWarnings("unused")
    private static HashMap<?, ?> ignore;
}
