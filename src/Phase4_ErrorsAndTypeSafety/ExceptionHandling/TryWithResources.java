package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * try-with-resources (Java 7+, enhanced in Java 9)
 * ------------------------------------------------
 * Resources like file handles, database connections, sockets, and locks must
 * be CLOSED whether the work succeeds or fails. The classic pattern was
 * <p>
 *
 *      Reader r = null;
 *      try {
 *          r = new FileReader("x");
 *          ...
 *      } finally {
 *          if (r != null) r.close();         // and close() might throw too!
 *      }
 * <p>
 *
 * try-with-resources reduces all that boilerplate to:
 * <p>
 *
 *      try (Reader r = new FileReader("x")) {
 *          ...
 *      }
 * <p>
 *
 * The compiler generates the close() call AND handles the exception
 * semantics correctly.
 * <p>
 *
 * The Contract
 * ------------
 *   - The resource type must implement java.lang.AutoCloseable (or its
 *     subinterface java.io.Closeable).
 *   - The resource is closed at the END of the try block, BEFORE any catch
 *     or finally clauses run.
 *   - Resources are closed in REVERSE order of declaration.
 *   - If both the body and close() throw, the BODY's exception is "primary"
 *     and close()'s exception is added as a SUPPRESSED exception.
 * <p>
 *
 * Java 9 Enhancement - "effectively final" resources
 * --------------------------------------------------
 * Pre-Java-9 you had to declare the variable inside the parentheses:
 * <p>
 *
 *      try (Resource r = open()) { ... }
 * <p>
 *
 * Since Java 9, you can use a variable that is already EFFECTIVELY FINAL:
 * <p>
 *
 *      Resource r = open();
 *      try (r) { ... }
 * <p>
 *
 * AutoCloseable vs Closeable
 * --------------------------
 *   AutoCloseable.close()  throws Exception   (anything)
 *   Closeable.close()      throws IOException (narrower, for I/O streams)
 * <p>
 *
 * For your own resources, implement AutoCloseable unless they are truly I/O.
 * <p>
 *
 * Suppressed Exceptions
 * ---------------------
 * Demonstrated in main(). The mechanism uses Throwable.addSuppressed and
 * Throwable.getSuppressed - the same API that ChainedException.java showed.
 */

public class TryWithResources {

    public static void main(String[] args) throws Exception {

        section("1) Basic try-with-resources");
        try (BufferedReader br = new BufferedReader(new StringReader("hello\nworld"))) {
            System.out.println(br.readLine());
            System.out.println(br.readLine());
        }
        // br.close() was called automatically right after the body.

        section("2) Multiple resources - closed in REVERSE order");
        try (Resource a = new Resource("A");
             Resource b = new Resource("B");
             Resource c = new Resource("C")) {
            a.use(); b.use(); c.use();
        }
        // Output order on close: C, B, A.

        section("3) Java 9+: effectively-final resource declared OUTSIDE the try");
        Resource preMade = new Resource("preMade");
        try (preMade) {
            preMade.use();
        }
        // preMade.close() was called.

        section("4) Body throws, close() throws - body wins, close() is SUPPRESSED");
        try {
            try (Resource r = new BadCloseResource("badClose")) {
                throw new IllegalStateException("body failure");
            }
        } catch (Exception primary) {
            System.out.println("primary  : " + primary.getMessage());
            System.out.println("suppressed:");
            for (Throwable s : primary.getSuppressed()) {
                System.out.println("    " + s.getMessage());
            }
        }

        section("5) AutoCloseable can throw any Exception in close()");
        try (Risky r = new Risky()) {
            r.work();
        } catch (Exception e) {
            System.out.println("caught: " + e.getMessage());
        }

        section("6) try-with-resources can still have catch and finally");
        try (Resource r = new Resource("R")) {
            r.use();
            throw new RuntimeException("oops");
        } catch (RuntimeException e) {
            System.out.println("caught: " + e.getMessage());
            // resource has already been closed BEFORE this catch ran
        } finally {
            System.out.println("finally still runs");
        }

        // OUTPUT
        // ====== 1) Basic try-with-resources ======
        // hello
        // world
        // ====== 2) Multiple resources - closed in REVERSE order ======
        // opened A
        // opened B
        // opened C
        // used A
        // used B
        // used C
        // closed C
        // closed B
        // closed A
        // ====== 3) Java 9+: effectively-final resource declared OUTSIDE the try ======
        // opened preMade
        // used preMade
        // closed preMade
        // ====== 4) Body throws, close() throws - body wins, close() is SUPPRESSED ======
        // opened badClose
        // primary  : body failure
        // suppressed:
        //     close failed for badClose
        // ====== 5) AutoCloseable can throw any Exception in close() ======
        // caught: close failed for Risky
        // ====== 6) try-with-resources can still have catch and finally ======
        // opened R
        // used R
        // closed R
        // caught: oops
        // finally still runs
    }

    // ============================================================
    // A simple AutoCloseable resource
    // ============================================================
    static class Resource implements AutoCloseable {
        protected final String name;
        Resource(String name) {
            this.name = name;
            System.out.println("opened " + name);
        }
        public void use() { System.out.println("used " + name); }
        @Override public void close() {
            System.out.println("closed " + name);
        }
    }

    /** A resource whose close() ALWAYS throws - used to demonstrate suppression. */
    static class BadCloseResource extends Resource {
        BadCloseResource(String name) { super(name); }
        @Override public void close() {
            throw new RuntimeException("close failed for " + name);
        }
    }

    /** AutoCloseable.close() may throw any Exception, not just IOException. */
    static class Risky implements AutoCloseable {
        void work() { System.out.println("Risky working..."); }
        @Override public void close() throws IOException {
            throw new IOException("close failed for Risky");
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
