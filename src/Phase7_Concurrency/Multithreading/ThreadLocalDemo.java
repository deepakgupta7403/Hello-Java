package Phase7_Concurrency.Multithreading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadLocal&lt;T&gt;
 * --------------
 * A per-thread variable. Every thread that reads a given ThreadLocal
 * sees ITS OWN copy of the value. Threads cannot accidentally share
 * state because there is no shared storage to corrupt.
 * <p>
 *
 * Why ThreadLocal?
 * ----------------
 *   - CONFINEMENT — escape the need for synchronization by making the
 *     data thread-private.
 *   - PER-REQUEST CONTEXT — store the current user, locale, trace id,
 *     transaction, security principal — accessible deep in the call
 *     stack without passing them as parameters.
 *   - PER-THREAD CACHING — keep a non-thread-safe helper (e.g.
 *     SimpleDateFormat) one per thread to dodge synchronization.
 * <p>
 *
 * The API
 * -------
 *   ThreadLocal<T>            - new ThreadLocal<>()
 *   withInitial(Supplier<T>)  - lazy default supplier
 *   T get()                   - return this thread's value
 *   void set(T)               - set this thread's value
 *   void remove()             - delete this thread's value
 * <p>
 *
 *   InheritableThreadLocal<T> - child threads inherit a (shallow) copy
 *                               from their parent at construction.
 * <p>
 *
 * Pitfalls
 * --------
 *   1. MEMORY LEAKS in thread pools. Workers live forever; a value set
 *      on one task is still set on the next. Always remove() at the end
 *      of a unit of work (try / finally), or wrap submissions.
 *   2. InheritableThreadLocal copies the REFERENCE, not the value's
 *      internal state. Mutating it from a child still affects the
 *      parent's object.
 *   3. Hard to reason about across asynchronous boundaries. If you hop
 *      threads with CompletableFuture / executors / virtual threads,
 *      consider Scoped Values (Java 21 preview) instead.
 * <p>
 *
 * When NOT to use
 * ---------------
 *   - For things that should propagate across asynchronous calls,
 *     consider Scoped Values (Java 21 preview).
 *   - For sharing data between threads — that's the OPPOSITE of what
 *     ThreadLocal is for; use a shared structure with proper sync.
 */

public class ThreadLocalDemo {

    /** Each thread will see its own counter. */
    private static final ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);

    /** SimpleDateFormat is NOT thread-safe. One per thread = safe. */
    private static final ThreadLocal<SimpleDateFormat> fmt =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss.SSS"));

    /** Tracing context — populated per request and read deep in the stack. */
    private static final ThreadLocal<String> traceId = new ThreadLocal<>();

    /** Inheritable example. */
    private static final InheritableThreadLocal<String> tag = new InheritableThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {

        section("1) Per-thread counter — no synchronization needed");
        Runnable bumpAndPrint = () -> {
            for (int i = 0; i < 5; i++) counter.set(counter.get() + 1);
            System.out.println("  " + Thread.currentThread().getName() + " local count = " + counter.get());
        };
        Thread a = new Thread(bumpAndPrint, "A");
        Thread b = new Thread(bumpAndPrint, "B");
        a.start(); b.start(); a.join(); b.join();
        System.out.println("main's own counter = " + counter.get() + "  (still 0)");

        section("2) ThreadLocal SimpleDateFormat avoids contention");
        Runnable nowFmt = () -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("  " + Thread.currentThread().getName() + " -> "
                        + fmt.get().format(new Date()));
                try { Thread.sleep(30); } catch (InterruptedException ignored) {}
            }
        };
        Thread c = new Thread(nowFmt, "C");
        Thread d = new Thread(nowFmt, "D");
        c.start(); d.start(); c.join(); d.join();

        section("3) Tracing context — passed implicitly down the call stack");
        Thread req1 = new Thread(() -> handleRequest("req-001"), "req-1");
        Thread req2 = new Thread(() -> handleRequest("req-002"), "req-2");
        req1.start(); req2.start();
        req1.join();  req2.join();

        section("4) InheritableThreadLocal — child inherits the parent's value");
        tag.set("from-main");
        Thread child = new Thread(() ->
                System.out.println("child sees tag = " + tag.get()), "child");
        child.start(); child.join();

        section("5) Thread-pool leak — value persists across tasks if not removed");
        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger tasks = new AtomicInteger();
        pool.scheduleAtFixedRate(() -> {
            // first task sets it, later tasks would see leftover value
            if (counter.get() == 0) counter.set(99);
            System.out.println("  pool task #" + tasks.incrementAndGet() + " sees " + counter.get());
            if (tasks.get() >= 3) counter.remove();    // <- cleanup pattern
        }, 0, 60, TimeUnit.MILLISECONDS);
        Thread.sleep(250);
        pool.shutdownNow();

        section("done");
    }

    private static void handleRequest(String id) {
        traceId.set(id);
        try {
            doBusinessLogic();
        } finally {
            traceId.remove();                  // ALWAYS clean up
        }
    }

    private static void doBusinessLogic() {
        // The trace id is available without being passed in as a parameter.
        System.out.println("  thread=" + Thread.currentThread().getName() + " traceId=" + traceId.get());
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
