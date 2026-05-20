package Phase6_RuntimeMemoryRegexReflection.MemoryAllocation;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;

/**
 * Types of Memory Areas Allocated by the JVM
 * ------------------------------------------
 * The Java Virtual Machine specification divides runtime memory into five
 * conceptual areas:
 *
 *
 *   1. METHOD AREA  (a.k.a. Metaspace since Java 8)
 *      - SHARED across all threads.
 *      - Holds CLASS METADATA: bytecode of methods, runtime constant pool,
 *        static fields, the Class<?> object itself, JIT-compiled code.
 *      - Pre-Java 8 it was a fixed "PermGen" inside the heap; from Java 8
 *        it lives in NATIVE memory (Metaspace) and grows by default.
 *      - Tuning: -XX:MaxMetaspaceSize.
 *
 *
 *   2. HEAP
 *      - SHARED across all threads.
 *      - The ONLY place ordinary objects and arrays are allocated.
 *      - Subdivided by every modern GC into GENERATIONS:
 *          Young    -> Eden + Survivor0 + Survivor1
 *          Old (Tenured)
 *      - Tuning: -Xms (initial), -Xmx (max), -XX:NewSize, etc.
 *
 *
 *   3. JVM STACK         (also called "Java Stack")
 *      - PER-THREAD.
 *      - One STACK FRAME per active method call:
 *            local variables, operand stack, return address.
 *      - When the frame's method returns, the frame is popped - locals
 *        disappear.
 *      - StackOverflowError = the stack grew too deep (usually unbounded
 *        recursion). OutOfMemoryError("unable to create new native thread")
 *        = OS refused another thread.
 *      - Tuning: -Xss (per-thread stack size).
 *
 *
 *   4. PC REGISTER
 *      - PER-THREAD.
 *      - Holds the ADDRESS of the bytecode instruction currently being
 *        executed by that thread. For native methods it is undefined.
 *      - You never touch this directly; the JVM uses it internally.
 *
 *
 *   5. NATIVE METHOD STACK
 *      - PER-THREAD.
 *      - Used by NATIVE (C/C++) code reached via JNI. Has nothing to do
 *        with your Java stack frames.
 *
 *
 * Heap Generations (HotSpot / G1 / ZGC view)
 * ------------------------------------------
 *
 *      Young Gen                        Old Gen
 *      +--------+---------+---------+   +------------------+
 *      |  Eden  | Surv 0  | Surv 1  |   |  Tenured         |
 *      +--------+---------+---------+   +------------------+
 *      new objects start here -->       long-lived stuff here
 *
 *   - Objects are born in EDEN.
 *   - Minor GC promotes survivors to S0 / S1 alternately.
 *   - Survive enough minor GCs and the object is promoted to OLD.
 *   - The "generational hypothesis": most objects die young.
 *
 *
 * Beyond the Spec
 * ---------------
 * In addition to the five JVM-spec areas, the running process also has:
 *   - CODE CACHE        - JIT-compiled native code lives here.
 *   - DIRECT BUFFERS    - off-heap NIO byte buffers (ByteBuffer.allocateDirect).
 *   - THREAD STACKS     - native stacks owned by the OS.
 *
 * This main() prints what the running JVM reports for each of these.
 */

public class JvmMemoryAreas {

    public static void main(String[] args) {
        long MB = 1024 * 1024;

        section("1) Heap vs Non-Heap totals (MemoryMXBean)");
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        printUsage("heap     ", mem.getHeapMemoryUsage());
        printUsage("non-heap ", mem.getNonHeapMemoryUsage());

        section("2) Per-pool breakdown (each tells you exactly which area it is)");
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage u = pool.getUsage();
            System.out.printf("  %-32s type=%-9s used=%5d MB  committed=%5d MB%n",
                    pool.getName(),
                    pool.getType(),                     // HEAP or NON_HEAP
                    u.getUsed() / MB,
                    u.getCommitted() / MB);
        }

        section("3) Stack size in this thread");
        Thread me = Thread.currentThread();
        System.out.println("current thread: " + me.getName());
        System.out.println("stackTrace depth (frames currently active): "
                + me.getStackTrace().length);

        section("4) Trigger a StackOverflowError - the JVM stack is finite");
        try {
            depth(0);
        } catch (StackOverflowError e) {
            System.out.println("caught after recursive depth " + lastDepth);
        }

        section("5) Class metadata lives in Metaspace - load 200 classes worth of data");
        // We simply force-load a bunch of unrelated classes to nudge Metaspace.
        // This is informational - the numbers above will reflect any growth.
        try {
            Class.forName("java.util.HashMap");
            Class.forName("java.util.LinkedHashMap");
            Class.forName("java.util.WeakHashMap");
            Class.forName("java.util.concurrent.ConcurrentHashMap");
        } catch (ClassNotFoundException ignored) {}
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getName().toLowerCase().contains("metaspace")) {
                System.out.printf("  %s: used = %d MB%n",
                        pool.getName(), pool.getUsage().getUsed() / MB);
            }
        }

        // OUTPUT (sample - varies)
        // ====== 1) Heap vs Non-Heap totals (MemoryMXBean) ======
        // heap     : used =     8 MB  committed =   256 MB  max =  4096 MB
        // non-heap : used =    20 MB  committed =    24 MB  max =     0 MB
        // ====== 2) Per-pool breakdown (each tells you exactly which area it is) ======
        //   G1 Eden Space                 type=HEAP      used=    4 MB  committed=  256 MB
        //   G1 Survivor Space             type=HEAP      used=    0 MB  committed=    0 MB
        //   G1 Old Gen                    type=HEAP      used=    4 MB  committed=  256 MB
        //   Metaspace                     type=NON_HEAP  used=   12 MB  committed=   12 MB
        //   CodeHeap 'non-nmethods'       type=NON_HEAP  used=    1 MB  committed=    2 MB
        //   ...
    }

    private static void printUsage(String label, MemoryUsage u) {
        long MB = 1024 * 1024;
        System.out.printf("%s: used = %5d MB  committed = %5d MB  max = %5d MB%n",
                label,
                u.getUsed() / MB,
                u.getCommitted() / MB,
                u.getMax() / MB);
    }

    // Track how deep the recursion got before the stack overflowed.
    private static int lastDepth = 0;
    private static void depth(int n) {
        lastDepth = n;
        depth(n + 1);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
