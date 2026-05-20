package Basics.Multithreading;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Virtual Threads — Java 21 (JEP 444, finalised)
 * ----------------------------------------------
 * A virtual thread is a thread implemented by the JVM rather than the
 * OS. It looks and behaves exactly like a normal Thread, but:
 *
 *   - Creating one is CHEAP (microseconds, kilobytes).
 *   - The JVM can multiplex MILLIONS of them on a small pool of
 *     CARRIER threads (platform threads, typically equal to your
 *     CPU count).
 *   - When a virtual thread blocks on an I/O call (or other JDK
 *     blocking primitive), the JVM UNMOUNTS it from its carrier so
 *     the carrier can run another virtual thread.
 *
 * Net effect: you write old-fashioned synchronous, blocking code with
 * Thread.sleep / readLine / Socket.read, and the JVM gives you the
 * scalability of an event-loop runtime.
 *
 *
 * Creating them
 * -------------
 *   Thread.startVirtualThread(Runnable r)
 *   Thread.ofVirtual().name("vt-1").start(r)
 *   Thread.ofVirtual().factory()                  - ThreadFactory
 *   Executors.newVirtualThreadPerTaskExecutor()  - one VT per task
 *
 *
 * What they're great at
 * ---------------------
 *   - I/O-bound fan-out: thousands of concurrent HTTP/DB calls.
 *   - Per-request handling without "reactive" obfuscation.
 *   - Replacing thread pools whose only purpose was to limit thread
 *     COUNT (not to limit upstream concurrency).
 *
 *
 * What they don't help with
 * -------------------------
 *   - Pure CPU-bound work. The carrier count is your real parallelism.
 *   - Code that PINS the virtual thread to its carrier (see below).
 *
 *
 * Pinning
 * -------
 * A virtual thread is pinned to its carrier (can't be unmounted) when:
 *   - It is inside a `synchronized` block (Java 21).
 *   - It calls a native method via JNI.
 *
 * For high-throughput VT code, replace synchronized critical sections
 * around long-running operations with ReentrantLock. (Future Java
 * releases plan to lift the synchronized-pinning restriction.)
 *
 *
 * ThreadLocal and virtual threads
 * -------------------------------
 * Each VT has its own ThreadLocal map. With millions of VTs, that can
 * be expensive. Prefer Scoped Values (Java 21 preview) for per-call
 * context.
 */

public class VirtualThreads {

    public static void main(String[] args) throws Exception {

        section("1) Smallest possible virtual thread");
        Thread.startVirtualThread(() ->
                System.out.println("hi from " + Thread.currentThread()));
        Thread.sleep(50);

        section("2) Builders — platform vs virtual");
        Thread platform = Thread.ofPlatform().name("classic-1").start(() ->
                System.out.println("platform: " + Thread.currentThread()));
        Thread virtual = Thread.ofVirtual().name("v-1").start(() ->
                System.out.println("virtual:  " + Thread.currentThread()));
        platform.join();
        virtual.join();

        section("3) Spawning 10,000 virtual threads — each sleeps 100ms");
        long t0 = System.currentTimeMillis();
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                futures.add(vts.submit(() -> {
                    try { Thread.sleep(Duration.ofMillis(100)); } catch (InterruptedException ignored) {}
                    return null;
                }));
            }
            for (Future<?> f : futures) f.get();
        }
        System.out.println("10000 VTs sleeping 100ms each finished in "
                + (System.currentTimeMillis() - t0) + " ms (much faster than 10000 platform threads)");

        section("4) Detecting whether a thread is virtual");
        Thread.ofVirtual().start(() -> {
            Thread me = Thread.currentThread();
            System.out.println("isVirtual = " + me.isVirtual() + " on " + me);
        }).join();

        section("5) Pinning warning — synchronized around a slow op");
        // Run with -Djdk.tracePinnedThreads=full to see traces.
        Object lock = new Object();
        Thread pinned = Thread.ofVirtual().start(() -> {
            synchronized (lock) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                System.out.println("ran inside synchronized; this would PIN the carrier");
            }
        });
        pinned.join();

        section("6) Use ReentrantLock instead of synchronized in hot paths");
        // No pinning — the VT can unmount while sleeping inside the lock.
        java.util.concurrent.locks.ReentrantLock l = new java.util.concurrent.locks.ReentrantLock();
        Thread better = Thread.ofVirtual().start(() -> {
            l.lock();
            try {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                System.out.println("ran inside ReentrantLock — no pinning");
            } finally { l.unlock(); }
        });
        better.join();

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
