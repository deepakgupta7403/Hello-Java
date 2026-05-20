package Phase7_Concurrency.Multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Scoped Values — Java 21 PREVIEW (JEP 446)
 * -----------------------------------------
 * A modern alternative to ThreadLocal for sharing IMMUTABLE per-call
 * data with deeply nested code. Unlike ThreadLocal:
 *
 *   - Values are SCOPE-BOUND: they exist only inside a bounded scope
 *     and are automatically unbound on exit. No leaks across tasks.
 *   - Values are IMMUTABLE. There is no .set(value) once inside.
 *     If you want a different value, open a NESTED scope.
 *   - Cheap with millions of virtual threads: scoped values don't pay
 *     the per-thread map cost.
 *   - Inherited by child threads of a StructuredTaskScope automatically.
 *
 *
 * The API in comments (preview)
 * -----------------------------
 *      public static final ScopedValue<String> USER = ScopedValue.newInstance();
 *
 *      ScopedValue.where(USER, "alice").run(() -> {
 *          // anywhere in here, USER.get() == "alice"
 *          callDeep();
 *      });
 *
 *      ScopedValue.where(USER, "alice").call(() -> doWork());
 *
 *      ScopedValue.where(USER, "alice")
 *                 .where(LOCALE, Locale.US)
 *                 .run(...);                       // bind several at once
 *
 *
 * Inside the body
 * ---------------
 *      USER.get()                  - the value (throws if unbound)
 *      USER.isBound()              - is there a value in scope?
 *      USER.orElse(defaultV)       - safe accessor
 *
 *
 * Why preview, why care
 * ---------------------
 * Scoped Values are designed to be the per-call context of choice in a
 * virtual-thread world. Each VT inherits the scope, not a private
 * ThreadLocal map. They're a great fit for tracing, security
 * principals, request IDs.
 *
 *
 * Implementation note for this repo
 * ---------------------------------
 * Because preview APIs require --enable-preview, this file shows the
 * SHAPE in comments and provides a PORTABLE alternative (ThreadLocal +
 * try/finally) that runs unchanged.
 */

public class ScopedValuesDemo {

    /** Stand-in: ThreadLocal version (portable). */
    private static final ThreadLocal<String> USER = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {

        section("1) Portable equivalent — ThreadLocal binding scope");
        runAs("alice", () -> {
            doWork();
            // nested binding
            runAs("alice-impersonating-bob", ScopedValuesDemo::doWork);
            doWork();                              // restored to "alice"
        });

        section("2) The preview form (Java 21 with --enable-preview)");
        // public static final ScopedValue<String> USER = ScopedValue.newInstance();
        //
        // ScopedValue.where(USER, "alice").run(() -> {
        //     System.out.println("user = " + USER.get());
        //     ScopedValue.where(USER, "bob").run(() -> {
        //         System.out.println("nested user = " + USER.get());
        //     });
        //     System.out.println("back to " + USER.get());
        // });
        System.out.println("(see comment above)");

        section("3) Inheritance with virtual threads (preview)");
        // ScopedValue.where(USER, "alice").run(() -> {
        //     try (var vts = Executors.newVirtualThreadPerTaskExecutor()) {
        //         vts.submit(() -> System.out.println("child sees " + USER.get())).get();
        //     }
        // });
        // PORTABLE FALLBACK: ThreadLocal is NOT inherited by children of an
        //   executor by default. With InheritableThreadLocal you'd inherit
        //   from the *submitting* thread but not from per-task wrappers.
        runAs("alice", () -> {
            ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor();
            try {
                Future<?> f = vts.submit(() -> System.out.println("child sees USER = " + USER.get()));
                f.get();
            } catch (Exception e) { System.out.println("err: " + e.getMessage()); }
            finally { vts.shutdown(); }
        });

        section("done");
    }

    /**
     * Helper that emulates ScopedValue.where(USER, name).run(body).
     *
     * It SETS the ThreadLocal, runs the body, and RESTORES the previous
     * value in a finally — guaranteeing scope-bound cleanup.
     */
    private static void runAs(String name, Runnable body) {
        String previous = USER.get();
        USER.set(name);
        try { body.run(); }
        finally {
            if (previous == null) USER.remove();
            else                  USER.set(previous);
        }
    }

    private static void doWork() {
        System.out.println("  inside work: USER = " + USER.get());
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
