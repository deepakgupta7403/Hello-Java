package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Exception Handling and Method Overriding
 * ----------------------------------------
 * When a subclass OVERRIDES a method, there are strict rules about what
 * exceptions the override may declare in its `throws` clause.
 *
 *
 * The Rule (Checked Exceptions Only)
 * ----------------------------------
 *   An override may throw:
 *     - the SAME checked exceptions as the parent, OR
 *     - SUBCLASSES of those checked exceptions, OR
 *     - NOTHING (declare fewer checked exceptions than the parent).
 *
 *   It may NOT throw:
 *     - a NEW BROADER checked exception that the parent did not declare,
 *     - a checked exception that is unrelated to those the parent declared,
 *     - a checked exception that is a SUPERTYPE of one the parent declared.
 *
 * Why this rule? CALLER COMPATIBILITY. Code that calls the parent type only
 * knows about the parent's `throws` clause. If a subclass introduced a brand-
 * new checked exception, callers wouldn't be required to handle it - they'd
 * be surprised at runtime.
 *
 *
 * Unchecked Exceptions
 * --------------------
 * The rule does NOT apply to unchecked exceptions (RuntimeException and its
 * descendants). A subclass override may throw any RuntimeException it likes,
 * declared or not. Callers were already on the hook for unchecked exceptions.
 *
 *
 * What Counts as "Same or Subclass"?
 * ----------------------------------
 *
 *      Parent declares throws IOException
 *
 *      OK   :  throws  (nothing)
 *      OK   :  throws  IOException
 *      OK   :  throws  FileNotFoundException    (subclass of IOException)
 *      BAD  :  throws  Exception                 (broader)
 *      BAD  :  throws  SQLException              (unrelated checked)
 *      OK   :  throws  IOException, RuntimeException   (RTE allowed anywhere)
 *
 *
 * Interface Methods
 * -----------------
 * Same rule. An interface method's `throws` clause constrains every
 * implementor. If a class implements multiple interfaces that declare the
 * same method with DIFFERENT throws clauses, the implementation must obey
 * the INTERSECTION of those clauses (or omit `throws` entirely).
 *
 *
 * Constructors
 * ------------
 * The same rule does NOT apply between constructors of different classes.
 * Each constructor freely declares whatever it needs - though the parent's
 * constructor that you call via super(...) must of course have its throws
 * accounted for.
 */

public class ExceptionInOverriding {

    // ============================================================
    // 1) Parent declares a checked exception
    // ============================================================
    static class Parent {
        public void read() throws IOException {
            throw new IOException("parent read failed");
        }
    }

    // ============================================================
    // 2) Legal overrides
    // ============================================================

    /** Throws the SAME checked type. */
    static class Same extends Parent {
        @Override
        public void read() throws IOException {                 // OK
            throw new IOException("same-type override");
        }
    }

    /** Throws a SUBCLASS of the parent's checked type. */
    static class Narrower extends Parent {
        @Override
        public void read() throws FileNotFoundException {       // OK - subclass of IOException
            throw new FileNotFoundException("narrower override");
        }
    }

    /** Throws NOTHING - perfectly legal. */
    static class Silent extends Parent {
        @Override
        public void read() {                                    // OK
            System.out.println("silent override - no throws");
        }
    }

    /** Throws an UNCHECKED exception not declared by the parent - always allowed. */
    static class UncheckedOnly extends Parent {
        @Override
        public void read() {                                    // OK - RTE doesn't need declaring
            throw new IllegalStateException("unchecked override");
        }
    }

    // ============================================================
    // 3) ILLEGAL overrides - uncomment to see compile errors
    // ============================================================
    /*
    static class Broader extends Parent {
        @Override
        public void read() throws Exception {                   // ERROR: Exception is broader than IOException
            throw new Exception();
        }
    }
    static class Unrelated extends Parent {
        @Override
        public void read() throws java.sql.SQLException {       // ERROR: unrelated checked type
            throw new java.sql.SQLException();
        }
    }
    */

    // ============================================================
    // 4) The reason the rule exists - caller compatibility
    // ============================================================
    static void useReader(Parent reader) {
        try {
            reader.read();                  // caller only knows about IOException
        } catch (IOException io) {
            System.out.println("handled IO: " + io.getMessage());
        }
        // If a subclass could throw a NEW checked exception, this caller
        // would be silently incomplete - the rule prevents that.
    }

    public static void main(String[] args) {

        section("Polymorphic calls via the parent reference");
        useReader(new Same());
        useReader(new Narrower());
        useReader(new Silent());
        try {
            useReader(new UncheckedOnly());
        } catch (RuntimeException rte) {
            System.out.println("caller still saw unchecked: " + rte.getMessage());
        }

        section("Interface override - same rule applies");
        Closeable c1 = new SilentCloseable();
        Closeable c2 = new ThrowingCloseable();
        try {
            c1.close();
            c2.close();
        } catch (IOException io) {
            System.out.println("interface override threw: " + io.getMessage());
        }

        // OUTPUT
        // ====== Polymorphic calls via the parent reference ======
        // handled IO: same-type override
        // handled IO: narrower override
        // silent override - no throws
        // caller still saw unchecked: unchecked override
        // ====== Interface override - same rule applies ======
        // SilentCloseable closed quietly
        // interface override threw: closing failed
    }

    /** A simple interface whose method declares a checked exception. */
    interface Closeable {
        void close() throws IOException;
    }

    /** Implementor that doesn't throw - LEGAL (narrower than IOException). */
    static class SilentCloseable implements Closeable {
        @Override
        public void close() {
            System.out.println("SilentCloseable closed quietly");
        }
    }

    /** Implementor that does throw the declared type - LEGAL (same). */
    static class ThrowingCloseable implements Closeable {
        @Override
        public void close() throws IOException {
            throw new IOException("closing failed");
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
