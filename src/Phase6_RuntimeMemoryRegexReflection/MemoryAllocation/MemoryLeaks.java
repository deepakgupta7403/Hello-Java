package Phase6_RuntimeMemoryRegexReflection.MemoryAllocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Memory Leaks in Java
 * --------------------
 * Java is garbage-collected, but the GC cannot help you if your code is
 * STILL holding a reference to data you no longer need. The garbage
 * collector reclaims only UNREACHABLE objects - so any "live" path back to
 * the heap (a static field, a long-lived collection, a registered
 * listener, ...) is a leak waiting to happen.
 *
 *
 * The Five Most Common Java Leak Patterns
 * ---------------------------------------
 *
 *   1. STATIC COLLECTIONS that grow forever
 *      A `static Map<String, Customer> CACHE = new HashMap<>();` with no
 *      eviction policy will hold every customer it ever sees. Use a
 *      bounded cache (Guava `Cache`, `Caffeine`, or your own LRU).
 *
 *   2. KEYS WITHOUT proper equals/hashCode
 *      Entries put into a HashMap can become "lost" - present in memory
 *      but unfindable - if you mutate the key after insertion.
 *
 *   3. INNER CLASSES holding the enclosing instance
 *      A non-static inner class keeps an implicit reference to its outer
 *      instance. Same with lambdas that capture `this`. If the inner
 *      object outlives the outer (e.g. registered with a long-lived
 *      executor), the outer cannot be reclaimed.
 *
 *   4. LISTENERS / CALLBACKS that you never unregister
 *      Every observer is a strong reference held by the subject. When you
 *      stop using the observer but forget to call `removeListener`, the
 *      subject keeps it alive forever.
 *
 *   5. UNCLOSED RESOURCES (streams, JDBC connections, native handles)
 *      Even Java-managed wrappers hold on to OS-level resources until
 *      `close()` runs. Use try-with-resources for everything that
 *      implements AutoCloseable.
 *
 *
 * Detecting Leaks
 * ---------------
 *   - Watch heap usage over time - a healthy app oscillates; a leaking one
 *     trends upward after every GC.
 *   - Take a heap dump (-XX:+HeapDumpOnOutOfMemoryError or `jmap -dump`)
 *     and inspect with Eclipse MAT, VisualVM, or JProfiler.
 *   - Use `jcmd <pid> GC.class_histogram` for a quick instance count.
 *
 *
 * This file demonstrates the FIRST three leak patterns and their fixes.
 */

public class MemoryLeaks {

    // ============================================================
    // LEAK 1 - A static cache that grows forever
    // ============================================================
    static final Map<String, byte[]> LEAKY_CACHE = new HashMap<>();

    // The fixed version - same idea but capped to N entries (very basic LRU).
    static final Map<String, byte[]> FIXED_CACHE = new java.util.LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
            return size() > 100;
        }
    };

    // ============================================================
    // LEAK 3 - A non-static inner class that holds the outer
    // ============================================================
    static class Outer {
        private final byte[] heavyState = new byte[1024 * 1024];     // 1 MB
        Runnable task() {
            // Non-static inner class - captures `Outer.this` implicitly.
            return new Runnable() {
                @Override public void run() {
                    // touches outer's state, hence the implicit reference
                    System.out.println("inner saw " + heavyState.length + " bytes");
                }
            };
        }
    }

    static class FixedOuter {
        private final byte[] heavyState = new byte[1024 * 1024];

        // Static nested class does NOT capture the enclosing instance.
        static class Task implements Runnable {
            @Override public void run() {
                System.out.println("static nested task running (no outer ref)");
            }
        }

        Runnable task() { return new Task(); }
    }

    public static void main(String[] args) throws Exception {

        section("1) Static collection growing forever - bad");
        for (int i = 0; i < 10_000; i++) {
            LEAKY_CACHE.put("k-" + i, new byte[1024]);          // 10 MB of junk
        }
        long mb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        System.out.printf("leaky size = %d entries, heap = %d MB%n",
                LEAKY_CACHE.size(), (rt.totalMemory() - rt.freeMemory()) / mb);

        section("2) Same scenario with a bounded LRU cache - fixed");
        for (int i = 0; i < 10_000; i++) {
            FIXED_CACHE.put("k-" + i, new byte[1024]);
        }
        System.out.printf("fixed size = %d entries, heap = %d MB%n",
                FIXED_CACHE.size(), (rt.totalMemory() - rt.freeMemory()) / mb);
        // FIXED_CACHE never grows past 100.

        section("3) Mutating a HashMap key after insertion - lost entry");
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey k = new MutableKey("alpha");
        map.put(k, "the value");
        System.out.println("contains 'alpha' BEFORE mutation: " + map.containsKey(k));
        k.name = "beta";                       // changes the hash code!
        System.out.println("contains  same  AFTER  mutation: " + map.containsKey(k));
        // The entry is still in the map, but it lives in the BUCKET FOR "alpha"
        // while the new hash points to the BUCKET FOR "beta" - a leak.
        System.out.println("map.size()                       = " + map.size());

        section("4) Non-static inner class keeps outer alive - illustration");
        Outer outer = new Outer();
        Runnable r = outer.task();          // `r` holds Outer.this -> heavyState
        outer = null;
        System.gc(); Thread.sleep(50);
        r.run();                            // still works - the outer is alive

        FixedOuter fixed = new FixedOuter();
        Runnable r2 = fixed.task();
        fixed = null;
        System.gc(); Thread.sleep(50);
        r2.run();                            // works without holding the outer

        section("5) Listener you forget to unregister");
        Subject s = new Subject();
        Listener l = msg -> System.out.println("listener got: " + msg);
        s.add(l);
        s.publish("first message");
        // We "forget" to call remove(l). Subject -> Listener -> any heavy state
        // the listener captures is kept alive forever.
        System.out.println("subject still holds " + s.size() + " listener(s)");

        section("6) Unclosed resource - native handle leak");
        // Bad - forgetting to close:
        // FileInputStream in = new FileInputStream("...");   // close was never called
        // Fix with try-with-resources:
        //
        //   try (FileInputStream in = new FileInputStream("...")) { ... }

        section("7) Long-lived thread holding an executor full of work");
        // Threads themselves are GC ROOTS - any data they reference is alive.
        // Always shut down executors.
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleAtFixedRate(() -> {}, 0, 1, TimeUnit.HOURS);
        // ... use it ...
        es.shutdown();                                // remember THIS line.
        System.out.println("executor shutdown called");

        // OUTPUT (numbers vary)
    }

    /** A small Subject/Listener pair to demo the listener-leak pattern. */
    static class Subject {
        private final List<Listener> listeners = new ArrayList<>();
        public void add(Listener l)    { listeners.add(l); }
        public void remove(Listener l) { listeners.remove(l); }
        public void publish(String msg){ for (Listener l : listeners) l.onMessage(msg); }
        public int size()              { return listeners.size(); }
    }
    interface Listener { void onMessage(String msg); }

    /** A key whose hashCode changes when the field is mutated - a leak pattern. */
    static class MutableKey {
        String name;
        MutableKey(String n) { this.name = n; }
        @Override public int hashCode() { return name.hashCode(); }
        @Override public boolean equals(Object o) {
            return o instanceof MutableKey k && k.name.equals(this.name);
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    // Keep the imports referenced so the static analyzer is quiet.
    @SuppressWarnings("unused")
    private static WeakHashMap<?, ?> wh;
    @SuppressWarnings("unused")
    private static ConcurrentHashMap<?, ?> ch;
}
