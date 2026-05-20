package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Chained Exceptions
 * ------------------
 * CHAINING is the practice of wrapping a low-level exception inside a
 * higher-level one while keeping the original around as the "cause". You
 * end up with a clear, domain-meaningful exception on top AND the
 * underlying technical detail for debugging.
 *
 *      try {
 *          loadFromDatabase();
 *      } catch (SQLException sql) {
 *          throw new ServiceUnavailableException("user lookup failed", sql);
 *      }
 *
 *
 * Why Chain Instead of Swallow?
 * -----------------------------
 *   - Swallowing (catch-and-ignore) hides bugs forever - never do this.
 *   - Re-throwing the SAME low-level exception leaks implementation details
 *     to the caller ("oh now I have to know about SQL").
 *   - Wrapping converts to a more meaningful type while preserving the full
 *     diagnostic trail.
 *
 *
 * API In Throwable
 * ----------------
 *   Throwable(String message, Throwable cause)        // recommended constructor
 *   Throwable(Throwable cause)                        // shortcut
 *   initCause(Throwable cause)                        // for legacy exceptions
 *                                                     // without (msg, cause) ctor
 *   Throwable getCause()                              // walk back the chain
 *   Throwable getRootCause()                          // NOT in JDK -
 *                                                     // libraries like Guava
 *                                                     // / commons-lang offer it
 *
 *
 * Stack-Trace Anatomy of a Chained Exception
 * ------------------------------------------
 *      Exception in thread "main" ServiceUnavailableException: user lookup failed
 *          at App.main(App.java:9)
 *      Caused by: java.sql.SQLException: connection refused
 *          at Database.connect(Database.java:42)
 *          at App.main(App.java:7)
 *
 * Each "Caused by" is one level deeper into the chain. Read top-down to go
 * from the highest-level diagnosis to the root cause.
 *
 *
 * SUPPRESSED Exceptions vs CAUSES
 * --------------------------------
 * Causes and suppressed exceptions are different:
 *
 *   - CAUSE       : "this happened BECAUSE of that earlier thing"
 *                   - one cause per exception, chained.
 *   - SUPPRESSED  : "this exception was thrown WHILE another was already
 *                   in flight"
 *                   - many suppressed exceptions per exception.
 *
 * Most commonly produced by try-with-resources when the close() of a resource
 * throws AFTER the body has already thrown a different exception. The body's
 * exception "wins" and the close() exception is added as suppressed.
 *
 * See TryWithResources.java for the suppression demo.
 */

public class ChainedException {

    public static void main(String[] args) {

        section("1) Catch low-level, throw high-level with the cause");
        try {
            loadUser("bad-id");
        } catch (UserLookupException e) {
            System.out.println("top      : " + e);
            System.out.println("cause    : " + e.getCause());
        }

        section("2) Walking the chain manually");
        try {
            multiLevel();
        } catch (RuntimeException top) {
            int depth = 0;
            for (Throwable cur = top; cur != null; cur = cur.getCause()) {
                System.out.println("  level " + (depth++) + ": "
                        + cur.getClass().getSimpleName() + " - " + cur.getMessage());
            }
        }

        section("3) initCause - for legacy exceptions without the (msg, cause) constructor");
        Exception legacy = new Exception("top level");
        try {
            // imagine `legacy` was created by older code without a cause ctor
            legacy.initCause(new IOException("real reason"));
            throw legacy;
        } catch (Exception e) {
            System.out.println("message: " + e.getMessage());
            System.out.println("cause  : " + e.getCause());
        }

        section("4) Suppressed vs cause - two different lists");
        Throwable bodyError = new RuntimeException("body failed");
        Throwable closeErr1 = new RuntimeException("close 1 failed");
        Throwable closeErr2 = new RuntimeException("close 2 failed");
        bodyError.addSuppressed(closeErr1);
        bodyError.addSuppressed(closeErr2);

        System.out.println("message       : " + bodyError.getMessage());
        System.out.println("cause         : " + bodyError.getCause());
        System.out.println("suppressed[]  : ");
        for (Throwable s : bodyError.getSuppressed()) {
            System.out.println("    " + s.getMessage());
        }

        section("5) printStackTrace shows the chain natively");
        try {
            multiLevel();
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);          // notice the "Caused by:" lines
        }

        // OUTPUT (representative)
        // ====== 1) Catch low-level, throw high-level with the cause ======
        // top      : Basics.ExceptionHandling.ChainedException$UserLookupException: lookup failed for id 'bad-id'
        // cause    : java.lang.NumberFormatException: For input string: "bad-id"
        // ====== 2) Walking the chain manually ======
        //   level 0: RuntimeException - top
        //   level 1: SQLException     - sql
        //   level 2: IOException      - io
        // ====== 3) initCause - for legacy exceptions without the (msg, cause) constructor ======
        // message: top level
        // cause  : java.io.IOException: real reason
        // ====== 4) Suppressed vs cause - two different lists ======
        // message       : body failed
        // cause         : null
        // suppressed[]  :
        //     close 1 failed
        //     close 2 failed
        // ====== 5) printStackTrace shows the chain natively ======
        // java.lang.RuntimeException: top
        //     ...
        // Caused by: java.sql.SQLException: sql
        //     ...
        // Caused by: java.io.IOException: io
        //     ...
    }

    /**
     * loadUser catches the low-level NumberFormatException and wraps it inside
     * a domain-meaningful UserLookupException. Callers get to react to the
     * high-level type without knowing why under the hood.
     */
    static void loadUser(String idAsString) throws UserLookupException {
        try {
            Integer.parseInt(idAsString);
        } catch (NumberFormatException low) {
            throw new UserLookupException("lookup failed for id '" + idAsString + "'", low);
        }
    }

    /** Build a 3-deep cause chain so we can walk it. */
    static void multiLevel() {
        try {
            sql();
        } catch (SQLException sql) {
            throw new RuntimeException("top", sql);
        }
    }
    static void sql() throws SQLException {
        try {
            io();
        } catch (IOException io) {
            throw new SQLException("sql", io);
        }
    }
    static void io() throws IOException {
        throw new IOException("io");
    }

    /** Domain-specific exception used by loadUser. */
    static class UserLookupException extends Exception {
        public UserLookupException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
