package Basics.MemoryAllocation;

/**
 * Java Memory Management - Introduction
 * -------------------------------------
 * "Memory management" is everything the JVM does between you writing
 * `new Customer()` and the OS getting the bytes back: where the object
 * lives, when it can be reclaimed, and which subsystem of the JVM owns
 * the space.
 *
 *
 * Why You Care (even though Java is "garbage-collected")
 * ------------------------------------------------------
 *   - YOU still pick the data structures - they decide allocation rate.
 *   - YOU configure heap size and the garbage collector.
 *   - YOU can introduce LEAKS that the GC cannot fix (see MemoryLeaks.java).
 *   - Understanding the layout helps you tune for latency or throughput.
 *
 *
 * The Big Picture
 * ---------------
 *
 *      +-----------------------------------------------+
 *      |                  JVM Process                  |
 *      |                                               |
 *      |  +-----------------------+                    |
 *      |  |     Method Area       |  class data,       |
 *      |  | (Metaspace since 8)   |  static fields,    |
 *      |  +-----------------------+  constant pool     |
 *      |                                               |
 *      |  +-----------------------+                    |
 *      |  |       Heap            |  ALL objects,      |
 *      |  |                       |  arrays            |
 *      |  +-----------------------+                    |
 *      |                                               |
 *      |  +-----------+ +-----------+   per-thread     |
 *      |  | Thread A  | | Thread B  |   stack frames,  |
 *      |  | Stack     | | Stack     |   local vars     |
 *      |  +-----------+ +-----------+                  |
 *      |                                               |
 *      |  +-----------+ +-----------+                  |
 *      |  | PC reg A  | | PC reg B  |   current        |
 *      |  +-----------+ +-----------+   bytecode addr  |
 *      |                                               |
 *      |  +-----------+ +-----------+                  |
 *      |  | Native    | | Native    |   C/C++ stacks   |
 *      |  | Stack A   | | Stack B   |   for JNI calls  |
 *      |  +-----------+ +-----------+                  |
 *      +-----------------------------------------------+
 *
 *   - Method Area & Heap are SHARED by all threads.
 *   - Stack, PC, Native stack are PER-THREAD.
 *
 *
 * Lifecycle of a Java Object
 * --------------------------
 *   1. CREATION   - `new` allocates space on the heap (the "young generation").
 *   2. USE        - method calls move references between stack frames.
 *   3. UNREACHABILITY - no live reference points to the object anymore.
 *   4. RECLAMATION - the garbage collector eventually reclaims the space
 *                    and may compact / move the surviving objects.
 *
 *
 * Things the Programmer Does NOT Control
 * --------------------------------------
 *   - WHEN the GC runs. `System.gc()` is just a HINT.
 *   - The exact ADDRESS of an object - it can move between GC cycles.
 *   - Stack frame layout. The JVM manages it.
 *
 *
 * What This Folder Covers
 * -----------------------
 *   MemoryManagementIntro.java   (this file)
 *   ObjectsInMemory.java         - the in-memory layout of an object
 *   JvmMemoryAreas.java          - Method Area / Heap / Stack / PC / Native
 *   StackVsHeap.java             - the most-asked interview question
 *   GarbageCollection.java       - reachability, mark-and-sweep, generations
 *   GarbageCollectors.java       - Serial / Parallel / G1 / ZGC / Shenandoah
 *   MemoryLeaks.java             - patterns that fool the GC and fixes
 *   ModernMemoryFeatures.java    - Java 9..21 (Cleaner, Compact Strings,
 *                                  Generational ZGC, virtual-thread memory)
 */

public class MemoryManagementIntro {

    public static void main(String[] args) {

        section("1) Inspect the JVM running this code");
        System.out.println("Java version  : " + System.getProperty("java.version"));
        System.out.println("Vendor        : " + System.getProperty("java.vendor"));
        System.out.println("JVM name      : " + System.getProperty("java.vm.name"));
        System.out.println("JVM version   : " + System.getProperty("java.vm.version"));

        section("2) Heap sizes from the Runtime API");
        Runtime rt = Runtime.getRuntime();
        long mb = 1024 * 1024;
        System.out.printf("max heap   : %5d MB   (-Xmx)%n",  rt.maxMemory()   / mb);
        System.out.printf("total heap : %5d MB%n",          rt.totalMemory() / mb);
        System.out.printf("free heap  : %5d MB%n",          rt.freeMemory()  / mb);
        System.out.printf("used heap  : %5d MB%n",
                (rt.totalMemory() - rt.freeMemory()) / mb);
        System.out.println("processors : " + rt.availableProcessors());

        section("3) Default garbage collector");
        for (java.lang.management.GarbageCollectorMXBean gc :
                java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("  GC : " + gc.getName()
                    + "    (collections so far: " + gc.getCollectionCount() + ")");
        }

        section("4) Allocate something and watch the used-heap rise");
        long usedBefore = rt.totalMemory() - rt.freeMemory();
        // Allocate ~ 20 MB of integers
        int[] big = new int[5_000_000];
        big[0] = 1;                          // keep reference live
        long usedAfter = rt.totalMemory() - rt.freeMemory();
        System.out.printf("before allocate : %5d MB%n", usedBefore / mb);
        System.out.printf("after  allocate : %5d MB   (~20 MB int[5_000_000])%n", usedAfter / mb);

        section("5) System.gc() is only a HINT");
        big = null;                          // drop the reference
        System.gc();                          // hint, not a guarantee
        long usedAfterGc = rt.totalMemory() - rt.freeMemory();
        System.out.printf("after  gc       : %5d MB   (may or may not drop)%n", usedAfterGc / mb);

        // OUTPUT (numbers vary by your machine - the SHAPE of the output is the point)
        // ====== 1) Inspect the JVM running this code ======
        // Java version  : 21.0.2
        // Vendor        : Eclipse Adoptium
        // JVM name      : OpenJDK 64-Bit Server VM
        // JVM version   : 21.0.2+13-LTS
        // ====== 2) Heap sizes from the Runtime API ======
        // max heap   :  4096 MB   (-Xmx)
        // total heap :   256 MB
        // free heap  :   248 MB
        // used heap  :     8 MB
        // processors : 8
        // ====== 3) Default garbage collector ======
        //   GC : G1 Young Generation    (collections so far: 0)
        //   GC : G1 Old Generation      (collections so far: 0)
        // ====== 4) Allocate something and watch the used-heap rise ======
        // before allocate :     8 MB
        // after  allocate :    28 MB   (~20 MB int[5_000_000])
        // ====== 5) System.gc() is only a HINT ======
        // after  gc       :     5 MB   (may or may not drop)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
