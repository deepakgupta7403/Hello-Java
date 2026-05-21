package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.IOException;
import java.sql.SQLException;

/**
 * try, catch, finally - The Building Blocks
 * -----------------------------------------
 * `try` marks a block of code that may throw an exception. `catch` clauses
 * decide what to do when one is thrown. `finally` runs no matter what -
 * normal completion, return, or thrown exception.
 * <p>
 *
 *      try {
 *          // protected code
 *      } catch (ExceptionType e) {
 *          // handle it
 *      } finally {
 *          // ALWAYS runs (except System.exit / JVM crash)
 *      }
 * <p>
 *
 * Six Forms You Need To Know
 * --------------------------
 *  1. Single catch                              try { ... } catch (E e) { ... }
 *  2. Multiple catches                          one per exception type
 *  3. Multi-catch (Java 7+)                     catch (A | B | C e) { ... }
 *  4. try / finally with no catch               cleanup but propagate
 *  5. Nested try                                inner try inside a catch or another try
 *  6. try-with-resources (Java 7+, see TryWithResources.java)
 * <p>
 *
 * Catch Ordering Rule
 * -------------------
 * More SPECIFIC catches must come BEFORE more GENERAL ones. A
 * `catch (Exception)` will swallow an IOException, so listing
 * `catch (IOException)` AFTER it is a compile error ("unreachable").
 * <p>
 *
 * Multi-Catch Restrictions
 * ------------------------
 *   - The types listed must NOT have a subclass relationship between them.
 *     `catch (Exception | IOException e)` is illegal because IOException IS-A
 *     Exception - the broader type already covers the narrower one.
 *   - Inside a multi-catch, the exception variable is IMPLICITLY FINAL - you
 *     cannot reassign it. Its type is the COMMON SUPERTYPE of the listed
 *     exceptions.
 * <p>
 *
 * finally Caveats
 * ---------------
 *   - finally runs even if the try BLOCK has a `return` - it executes BEFORE
 *     the value is actually returned.
 *   - A `return` inside finally OVERRIDES any earlier return value - usually
 *     a BUG to be avoided.
 *   - finally does NOT run if the JVM dies (System.exit, kill -9, OS crash).
 *   - finally also does not run if the thread is killed by an uncatchable
 *     condition (e.g. Thread.stop on an outdated JVM).
 * <p>
 *
 * try Cannot Be Empty
 * -------------------
 * A try block must be followed by at least one catch or a finally (or be a
 * try-with-resources). A bare `try { ... }` does not compile.
 */

public class TryCatchBlock {

    public static void main(String[] args) {

        section("1) Single catch");
        try {
            int n = Integer.parseInt("abc");        // NumberFormatException
            System.out.println(n);
        } catch (NumberFormatException e) {
            System.out.println("caught NFE: " + e.getMessage());
        }

        section("2) Multiple catches - specific first");
        for (String input : new String[]{"42", "abc", null}) {
            try {
                int n = Integer.parseInt(input);
                System.out.println("parsed = " + n);
            } catch (NumberFormatException e) {                 // specific
                System.out.println("not a number: " + input);
            } catch (NullPointerException e) {                  // also specific
                System.out.println("input was null");
            } catch (Exception e) {                             // generic last
                System.out.println("other failure: " + e);
            }
        }

        section("3) Multi-catch (Java 7+)");
        for (int i = 0; i < 3; i++) {
            try {
                mayThrow(i);
            } catch (IOException | SQLException e) {
                // `e` is effectively final and typed as the common supertype
                System.out.println("caught I/O or SQL: "
                        + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        section("4) try / finally without catch");
        try {
            System.out.println("doing work...");
            // No catch here - if mayThrow threw, finally would still run
            // and the exception would propagate to the caller.
        } finally {
            System.out.println("finally: closing resources");
        }

        section("5) Nested try");
        try {
            try {
                throw new RuntimeException("inner failure");
            } catch (IllegalArgumentException ignore) {
                // inner does NOT handle RuntimeException - it propagates
                System.out.println("inner caught nothing");
            }
        } catch (RuntimeException e) {
            System.out.println("outer caught: " + e.getMessage());
        }

        section("6) finally runs even on a return - watch the order");
        System.out.println("returned = " + returnDemo());

        section("7) BAD: a return inside finally overrides the value");
        System.out.println("returned = " + returnFromFinally());

        section("8) The try without catch but with finally - exception propagates");
        try {
            propagating();
        } catch (RuntimeException e) {
            System.out.println("propagated: " + e.getMessage());
        }

        // OUTPUT
        // ====== 1) Single catch ======
        // caught NFE: For input string: "abc"
        // ====== 2) Multiple catches - specific first ======
        // parsed = 42
        // not a number: abc
        // input was null
        // ====== 3) Multi-catch (Java 7+) ======
        // caught I/O or SQL: IOException - io failure
        // caught I/O or SQL: SQLException - sql failure
        // (no exception for i=2)
        // ====== 4) try / finally without catch ======
        // doing work...
        // finally: closing resources
        // ====== 5) Nested try ======
        // outer caught: inner failure
        // ====== 6) finally runs even on a return - watch the order ======
        // try body
        // finally body
        // returned = 1
        // ====== 7) BAD: a return inside finally overrides the value ======
        // try returns 1, finally overrides
        // returned = 99
        // ====== 8) The try without catch but with finally - exception propagates ======
        // cleanup ran
        // propagated: still propagates
    }

    /** Throws different exception types based on input - used by multi-catch demo. */
    static void mayThrow(int code) throws IOException, SQLException {
        switch (code) {
            case 0 -> throw new IOException ("io failure");
            case 1 -> throw new SQLException("sql failure");
            default -> { /* no throw - falls through */ }
        }
    }

    /** finally runs BEFORE the value is actually returned. */
    static int returnDemo() {
        try {
            System.out.println("try body");
            return 1;
        } finally {
            System.out.println("finally body");
            // No `return` here - the original `return 1` stands.
        }
    }

    /** A return inside finally HIDES the try's return - almost always a bug. */
    @SuppressWarnings("finally")
    static int returnFromFinally() {
        try {
            System.out.println("try returns 1, finally overrides");
            return 1;
        } finally {
            return 99;               // wins - returns 99
        }
    }

    /** try / finally without catch - finally still runs, exception still propagates. */
    static void propagating() {
        try {
            throw new RuntimeException("still propagates");
        } finally {
            System.out.println("cleanup ran");
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
