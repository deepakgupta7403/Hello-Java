package Phase6_RuntimeMemoryRegexReflection.MemoryAllocation;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

/**
 * Types of JVM Garbage Collectors
 * -------------------------------
 * OpenJDK / HotSpot ships several different garbage collectors. They have
 * different trade-offs between THROUGHPUT (total work done) and LATENCY
 * (longest pause). You pick one with a JVM flag at launch time.
 * <p>
 *
 * The Big Five (HotSpot / OpenJDK)
 * --------------------------------
 * <p>
 *
 *   1. SERIAL GC                    -XX:+UseSerialGC
 *      - Single-threaded. Stops the world to mark + sweep.
 *      - Tiny memory footprint.
 *      - DEFAULT for small heaps / single-core / client-class machines.
 *      - Use when : embedded devices, very low resource environments.
 * <p>
 *
 *   2. PARALLEL GC (a.k.a. "throughput")  -XX:+UseParallelGC
 *      - Multi-threaded young + old collection. Stops the world.
 *      - Optimised for THROUGHPUT, not for pause time.
 *      - Was the default in JDK 8.
 *      - Use when : batch jobs / scientific computing where total time
 *                    matters more than tail latency.
 * <p>
 *
 *   3. G1 GC ("Garbage First")              -XX:+UseG1GC
 *      - DEFAULT since Java 9.
 *      - Splits the heap into many regions; collects the "garbagey-est"
 *        regions first to meet a TARGET PAUSE goal.
 *      - Multi-threaded, mostly concurrent, with short stop-the-world phases.
 *      - Use when : interactive servers up to ~ 100 GB heap.
 * <p>
 *
 *   4. ZGC ("Z Garbage Collector")          -XX:+UseZGC
 *      - Concurrent, region-based, NUMA-aware. Pause times often < 1 ms,
 *        regardless of heap size.
 *      - PRODUCTION-READY since Java 15. GENERATIONAL since Java 21
 *        ("ZGC: Generational" - much better throughput for typical apps).
 *      - Use when : huge heaps (multi-TB), strict latency requirements.
 *      - JDK 21 flag: -XX:+UseZGC -XX:+ZGenerational
 * <p>
 *
 *   5. SHENANDOAH                            -XX:+UseShenandoahGC
 *      - Concurrent, low-pause collector from Red Hat. Pauses similar to ZGC.
 *      - Available in Eclipse Temurin and OpenJDK distributions.
 *      - Use when : low-latency servers; alternative to ZGC.
 * <p>
 *
 * Two "Special" Collectors
 * ------------------------
 * <p>
 *
 *   6. EPSILON                              -XX:+UseEpsilonGC
 *      - The "do nothing" collector. Allocates but NEVER reclaims.
 *      - For performance testing, very short-lived programs, and
 *        understanding allocator overhead without GC noise.
 * <p>
 *
 *   7. CMS (Concurrent Mark-Sweep)
 *      - Old low-pause collector. REMOVED in Java 14. Use G1 / ZGC instead.
 * <p>
 *
 * How To Pick a Collector
 * -----------------------
 *   - If you do not know - leave the default (G1 in modern JDKs).
 *   - "I have a strict p99 latency SLA"             -> ZGC or Shenandoah.
 *   - "I want max throughput and don't care about pauses"
 *                                                   -> Parallel.
 *   - "I have a tiny embedded device"                -> Serial.
 * <p>
 *
 * Useful Flags (just-the-flags cheatsheet)
 * ----------------------------------------
 *   -Xms2g                          initial heap
 *   -Xmx4g                          max heap
 *   -XX:+UseG1GC                    pick G1 (default in modern JDKs)
 *   -XX:MaxGCPauseMillis=200        pause target (a hint, not a guarantee)
 *   -XX:+UseZGC -XX:+ZGenerational  ZGC generational (Java 21)
 *   -Xlog:gc*                       turn on detailed GC logging
 *   -XX:+HeapDumpOnOutOfMemoryError dump heap to a file on OOM
 *   -XX:HeapDumpPath=/path/dump.hprof
 * <p>
 *
 * Inspecting the Live Collector
 * -----------------------------
 * main() prints which collectors are actually active in the JVM running
 * this code, plus their MBean names. The output is the easiest way to see
 * which collector is in use without parsing CLI flags.
 */

public class GarbageCollectors {

    public static void main(String[] args) {

        section("1) Collectors registered in THIS JVM");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("  %-30s collections=%d  time=%d ms%n",
                    gc.getName(),
                    gc.getCollectionCount(),
                    gc.getCollectionTime());
        }
        // Names you might see:
        //   "G1 Young Generation"            -> G1 GC
        //   "G1 Old Generation"
        //   "ZGC"                            -> ZGC (non-generational)
        //   "ZGC Major Pauses" / "Minor"     -> ZGC Generational (Java 21)
        //   "PS Scavenge" / "PS MarkSweep"   -> Parallel GC
        //   "Copy" / "MarkSweepCompact"      -> Serial GC
        //   "Shenandoah Pauses"              -> Shenandoah
        //   "Epsilon"                        -> Epsilon GC

        section("2) Heap memory pools (each GC names them differently)");
        for (java.lang.management.MemoryPoolMXBean pool :
                ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.printf("  %-32s type=%-9s mgr=%s%n",
                    pool.getName(),
                    pool.getType(),
                    String.join(",", pool.getMemoryManagerNames()));
        }

        section("3) Trigger work and watch counts climb");
        long beforeCount = totalCount();
        long beforeTime  = totalTime();
        churn();
        long afterCount = totalCount();
        long afterTime  = totalTime();
        System.out.println("collections delta = " + (afterCount - beforeCount));
        System.out.println("time delta (ms)   = " + (afterTime  - beforeTime));

        section("4) Reminder - the JVM was launched with some flags");
        // Print the relevant flags so you can correlate the active collector
        // with the command line.
        for (String name : new String[]{
                "java.vm.name",
                "java.vm.version",
                "java.runtime.version",
                "sun.management.compiler",
                "java.vm.info"}) {
            System.out.println("  " + name + " = " + System.getProperty(name));
        }

        // OUTPUT (varies wildly by collector)
    }

    /** Make some short-lived garbage so the GC has something to do. */
    private static void churn() {
        for (int i = 0; i < 5_000; i++) {
            byte[] junk = new byte[64 * 1024];      // 64 KB each
            junk[0] = (byte) i;                      // touch a byte
        }
    }

    private static long totalCount() {
        long t = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            t += Math.max(0, gc.getCollectionCount());
        }
        return t;
    }

    private static long totalTime() {
        long t = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            t += Math.max(0, gc.getCollectionTime());
        }
        return t;
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
