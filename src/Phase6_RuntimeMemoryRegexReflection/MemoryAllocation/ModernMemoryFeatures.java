package Phase6_RuntimeMemoryRegexReflection.MemoryAllocation;

import java.lang.ref.Cleaner;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Modern Memory Features (Java 9 -> 21)
 * -------------------------------------
 * The JVM and JDK keep getting better at memory. A short tour of the
 * changes that matter most to everyday Java code.
 *
 *
 * Java 9
 * ------
 *   - COMPACT STRINGS              String now stores LATIN-1 chars as one
 *                                  byte each (instead of always two). Cuts
 *                                  String memory roughly in half for ASCII
 *                                  text. Automatic - no code changes needed.
 *   - java.lang.ref.Cleaner        Reliable replacement for finalize() to
 *                                  clean up native / off-heap resources.
 *   - String.intern() table moves to native memory (Metaspace).
 *
 *
 * Java 10
 * -------
 *   - APPLICATION CLASS-DATA SHARING (AppCDS)
 *     Pre-load a shared archive of your app classes for faster startup
 *     and smaller per-JVM footprint.
 *
 *
 * Java 11
 * -------
 *   - EPSILON GC                   "do-nothing" allocator for benchmarks.
 *   - ZGC                          Experimental low-pause collector.
 *
 *
 * Java 14
 * -------
 *   - HELPFUL NULLPOINTEREXCEPTIONS (related to memory only loosely - tells
 *     you which dereference was null).
 *   - Foreign-Memory Access API (incubator) - precursor to the Foreign
 *     Function & Memory API.
 *
 *
 * Java 15
 * -------
 *   - ZGC and Shenandoah                       PRODUCTION-READY.
 *
 *
 * Java 16
 * -------
 *   - Records                      Smaller, immutable data carriers.
 *   - Foreign Linker API           (incubator)
 *
 *
 * Java 17 (LTS)
 * -------------
 *   - Sealed classes / records mature.
 *   - Strongly Encapsulate JDK Internals (sun.misc.Unsafe etc.) - moves Java
 *     away from low-level memory hacks.
 *
 *
 * Java 19 / 20
 * ------------
 *   - VIRTUAL THREADS (preview)    Cheap (a few KB each) threads scheduled
 *                                  on a small pool of OS carrier threads.
 *                                  Changes the memory cost of "one thread
 *                                  per request".
 *
 *
 * Java 21 (LTS)
 * -------------
 *   - VIRTUAL THREADS              Final. Each virtual thread uses a few KB
 *                                  of heap instead of a per-thread 512 KB
 *                                  OS stack - millions of threads become
 *                                  practical.
 *   - GENERATIONAL ZGC             ZGC now has a young / old generational
 *                                  split. Same pause-time goals (sub-ms)
 *                                  with better throughput and less memory
 *                                  overhead.
 *                                  Flag: -XX:+UseZGC -XX:+ZGenerational
 *   - Foreign Function & Memory API (preview, finalized in Java 22)
 *                                  Safe, supported replacement for JNI and
 *                                  Unsafe for OFF-HEAP memory access.
 *
 *
 * Things You Can Actually Run
 * ---------------------------
 * The demos below show:
 *
 *   1. Compact-string effect on byte usage (informational - JVM does it).
 *   2. Cleaner-based cleanup of a "resource".
 *   3. Off-heap direct ByteBuffer.
 *   4. Memory cost of platform threads vs virtual threads.
 */

public class ModernMemoryFeatures {

    public static void main(String[] args) throws Exception {

        section("1) Compact Strings (Java 9+) - ASCII is half the size");
        // We can't directly query the internal coder, but we can compare
        // ASCII vs non-ASCII strings indirectly via String.length / getBytes.
        String ascii   = "Hello, World!";
        String unicode = "नमस्ते Hello Γειά";
        System.out.printf("ascii   length=%d   UTF-8 bytes=%d%n",
                ascii.length(), ascii.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        System.out.printf("unicode length=%d   UTF-8 bytes=%d%n",
                unicode.length(), unicode.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        System.out.println("(internally, ASCII strings use 1 byte / char since Java 9)");

        section("2) Cleaner - the modern, deterministic-ish replacement for finalize()");
        Cleaner cleaner = Cleaner.create();
        // The cleanup action MUST NOT hold a strong reference to the
        // "registered" object - if it did, the object would never become
        // unreachable and the Cleaner would never fire.
        Resource res = new Resource("conn-1");
        cleaner.register(res, () -> System.out.println("  cleaning up conn-1"));
        res = null;                            // drop the strong reference
        System.gc(); Thread.sleep(100);

        section("3) Direct (off-heap) ByteBuffer - sits OUTSIDE the Java heap");
        ByteBuffer direct = ByteBuffer.allocateDirect(4 * 1024 * 1024);  // 4 MB off-heap
        direct.putInt(42);
        direct.flip();
        System.out.println("direct buffer capacity = " + direct.capacity() + " bytes");
        System.out.println("first int             = " + direct.getInt());
        // Direct buffers are reclaimed via Cleaner when their wrapper becomes
        // unreachable. They are tuneable with -XX:MaxDirectMemorySize.

        section("4) Virtual threads (Java 21) - tiny memory cost each");
        // We use reflection so this file ALSO compiles cleanly on Java 17 -
        // the API only exists in Java 21. On Java 21 the demo runs; on
        // older JVMs it prints a polite message and skips.
        try {
            Method factory = java.util.concurrent.Executors
                    .class.getMethod("newVirtualThreadPerTaskExecutor");
            ExecutorService es = (ExecutorService) factory.invoke(null);
            try {
                java.util.List<Future<Integer>> futures = new java.util.ArrayList<>();
                long start = System.currentTimeMillis();
                for (int i = 0; i < 50_000; i++) {
                    final int id = i;
                    futures.add(es.submit(() -> id * 2));
                }
                int total = 0;
                for (Future<Integer> f : futures) total += f.get();
                long ms = System.currentTimeMillis() - start;
                System.out.println("ran 50 000 virtual threads in " + ms + " ms, sum = " + total);
            } finally {
                es.shutdown();
            }
        } catch (NoSuchMethodException nsme) {
            System.out.println("(Java 21+ required for newVirtualThreadPerTaskExecutor - skipping)");
        }

        section("5) Generational ZGC (Java 21) - turn it on via JVM flags");
        // You don't toggle this from code - launch the JVM with:
        //   java -XX:+UseZGC -XX:+ZGenerational ...
        // After launch, the GC pool names reveal which collector is in use:
        for (var gc : java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("  GC pool name : " + gc.getName());
        }
        System.out.println("  On a default JVM you'll see G1 pools; flip the flags to see ZGC.");

        // OUTPUT
        // (matches the inline comments above)
    }

    /** A pretend native resource we want Cleaner to free. */
    static class Resource {
        final String name;
        Resource(String name) {
            this.name = name;
            System.out.println("opened " + name);
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
