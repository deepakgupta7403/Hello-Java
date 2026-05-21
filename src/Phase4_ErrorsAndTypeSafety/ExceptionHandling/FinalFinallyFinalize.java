package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

/**
 * final vs finally vs finalize
 * ----------------------------
 * Three words that look almost identical and do COMPLETELY different things.
 * This file untangles them once and for all.
 * <p>
 *
 *  Keyword     | What it is              | Where it appears          | Purpose
 *  ------------+-------------------------+---------------------------+-----------------------------
 *  final       | A modifier              | on variables, methods,    | Says "this cannot change".
 *              |                         | classes, parameters       |   - final var  -> can be assigned once
 *              |                         |                           |   - final method -> cannot be overridden
 *              |                         |                           |   - final class  -> cannot be subclassed
 *  ------------+-------------------------+---------------------------+-----------------------------
 *  finally     | A block                 | with try { ... }          | A clean-up block that ALWAYS runs.
 *              |                         |                           |   Used for closing resources.
 *  ------------+-------------------------+---------------------------+-----------------------------
 *  finalize()  | A method on Object      | overridden by a class     | Was called by the GC before
 *              |                         | (DEPRECATED for removal)  | reclaiming an object. DON'T use it.
 *              |                         |                           |   Use try-with-resources or
 *              |                         |                           |   java.lang.ref.Cleaner instead.
 * <p>
 *
 * Why finalize() is Deprecated
 * ----------------------------
 *   - Unpredictable: the JVM might NEVER call it.
 *   - Slow: postpones reclaiming the object and triggers a second GC cycle.
 *   - Unsafe: can resurrect partially-collected objects.
 *   - Removed since: deprecated for removal in Java 9, marked for actual
 *     removal in newer releases.
 * <p>
 *
 *   For deterministic clean-up use:
 *      - try-with-resources  (see TryWithResources.java)
 *      - java.lang.ref.Cleaner (since Java 9, replaces finalize properly)
 * <p>
 *
 * The three keywords visually:
 * ---------------------------
 * <p>
 *
 *   final int LIMIT = 100;          // CONSTANT
 * <p>
 *
 *   try {
 *      doWork();
 *   } finally {                     // ALWAYS-RUN cleanup
 *      cleanup();
 *   }
 * <p>
 *
 *   class Foo {
 *      @Override
 *      protected void finalize() {  // DEPRECATED hook
 *          // discouraged - don't use
 *      }
 *   }
 */

public class FinalFinallyFinalize {

    // ============================================================
    // final - immutability on variables, methods, classes
    // ============================================================
    static final int MAX_RETRIES = 3;                       // compile-time constant

    static class Parent {
        public final String name() { return "parent"; }     // cannot be overridden
    }

    static final class SealedBox { /* cannot be subclassed */ }

    static class Child extends Parent {
        // @Override public String name() { return "child"; }   // ERROR - final method
    }

    // ============================================================
    // finally - the try/catch clean-up block
    // ============================================================
    static int parseSafely(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("  parseSafely: returning -1 because '" + s + "' is not a number");
            return -1;
        } finally {
            // finally runs whether we returned normally or via the catch
            System.out.println("  parseSafely: finally ran for '" + s + "'");
        }
    }

    // ============================================================
    // finalize() - DEPRECATED; included only for completeness
    // ============================================================
    @SuppressWarnings({"deprecation", "removal"})
    static class LegacyResource {
        @Override
        protected void finalize() throws Throwable {
            System.out.println("  finalize() called on " + this);
            super.finalize();
        }
    }

    public static void main(String[] args) {

        section("1) final - constants, methods, classes");

        // final variable - assignable once
        final int retries = MAX_RETRIES;
        // retries = 5;                  // ERROR - final variable
        System.out.println("MAX_RETRIES = " + MAX_RETRIES);
        System.out.println("retries     = " + retries);

        // final method - cannot be overridden (see Child class above)
        System.out.println("Parent.name() = " + new Parent().name());
        System.out.println("Child.name()  = " + new Child().name());     // inherits the final method

        // final class - cannot be subclassed (SealedBox)
        SealedBox sb = new SealedBox();
        System.out.println("sealed box created: " + sb.getClass().getSimpleName());

        section("2) finally - always runs");
        parseSafely("42");
        parseSafely("oops");

        section("3) finalize() - DEPRECATED, do NOT rely on it");
        LegacyResource lr = new LegacyResource();
        System.out.println("created: " + lr);
        lr = null;                       // make it unreachable

        // The JVM MIGHT call finalize() during GC - it might never. We hint
        // the GC for the demo. This is exactly why finalize() is unreliable.
        System.gc();
        // Give the finaliser thread a moment - still not a guarantee.
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        section("4) Modern replacement - try-with-resources / Cleaner");
        // See TryWithResources.java for the recommended pattern.
        System.out.println("Use try-with-resources or java.lang.ref.Cleaner instead.");

        // OUTPUT (the finalize line may or may not appear - that is the point)
        // ====== 1) final - constants, methods, classes ======
        // MAX_RETRIES = 3
        // retries     = 3
        // Parent.name() = parent
        // Child.name()  = parent
        // sealed box created: SealedBox
        // ====== 2) finally - always runs ======
        //   parseSafely: finally ran for '42'
        //   parseSafely: returning -1 because 'oops' is not a number
        //   parseSafely: finally ran for 'oops'
        // ====== 3) finalize() - DEPRECATED, do NOT rely on it ======
        // created: Basics.ExceptionHandling.FinalFinallyFinalize$LegacyResource@...
        //   finalize() called on ...                <- MAYBE
        // ====== 4) Modern replacement - try-with-resources / Cleaner ======
        // Use try-with-resources or java.lang.ref.Cleaner instead.
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
