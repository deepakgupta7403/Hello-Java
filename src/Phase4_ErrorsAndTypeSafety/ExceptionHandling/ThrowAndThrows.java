package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.IOException;

/**
 * throw vs throws
 * ---------------
 * Two single-letter-different keywords with very different roles.
 * <p>
 *
 *      throw       (STATEMENT)   - actually RAISES an exception object now.
 *      throws      (CLAUSE)      - DECLARES that a method may PROPAGATE
 *                                   certain checked exceptions to its caller.
 * <p>
 *
 * throw - the statement
 * ---------------------
 *      throw new IllegalArgumentException("bad input");
 * <p>
 *
 *   - Used at the point you decide something is wrong.
 *   - The argument must be a Throwable instance (always an exception object).
 *   - Execution jumps out of the current code path until a matching `catch`
 *     in the call stack handles it.
 * <p>
 *
 * throws - the clause
 * -------------------
 *      void readFile() throws IOException, SQLException { ... }
 * <p>
 *
 *   - Goes at the END of a method signature.
 *   - REQUIRED for any CHECKED exceptions the method may propagate.
 *   - OPTIONAL for unchecked exceptions (you may list them as documentation,
 *     but the compiler does not require it).
 *   - Subclasses must follow the OVERRIDING RULES (see ExceptionInOverriding.java).
 * <p>
 *
 * Exception Propagation
 * ---------------------
 * When `throw` runs, the JVM looks up the call stack for the nearest matching
 * `catch`:
 * <p>
 *
 *      main() ---> a() ---> b() ---> c() throws ex
 *                                       ^
 *                                       finds no catch, propagates up...
 *      main() ---> a()  catches ex
 * <p>
 *
 * Any method on the path that mentions the checked exception in its `throws`
 * clause is letting it bubble up. The first `catch` block on the path handles
 * it.
 * <p>
 *
 * Note - "Throw early, catch late"
 * --------------------------------
 * Validate inputs at the BOUNDARY of your code and `throw` immediately.
 * Catch at the OUTERMOST layer that can do something meaningful (a user
 * message, a retry, a log entry). Don't catch deep inside and swallow.
 */

public class ThrowAndThrows {

    // ============================================================
    // 1) `throw` to validate input
    // ============================================================
    static int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("cannot divide " + a + " by zero");
        }
        return a / b;
    }

    // ============================================================
    // 2) `throws` to declare a checked exception
    // ============================================================
    static String readUsername(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("could not contact the auth service");
        }
        return "Deepak";
    }

    // ============================================================
    // 3) Exception propagation through a call chain
    // ============================================================
    static void outer() throws IOException {
        middle();                                   // propagates whatever middle throws
    }
    static void middle() throws IOException {
        inner();                                    // propagates inner's exception
    }
    static void inner() throws IOException {
        throw new IOException("inner failure");     // ORIGIN
    }

    // ============================================================
    // 4) Re-throwing - decide what to do, then throw again
    // ============================================================
    static void parseConfig(String raw) {
        try {
            Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            System.out.println("logging '" + raw + "' as bad config");
            // Re-throw a more meaningful exception WITHOUT losing the original.
            // We pass `e` as the cause - see ChainedException.java for details.
            throw new IllegalArgumentException("bad config value: " + raw, e);
        }
    }

    // ============================================================
    // 5) Declaring a method with multiple `throws` types
    // ============================================================
    static void multiThrow(int code) throws IOException, java.sql.SQLException, InterruptedException {
        switch (code) {
            case 1 -> throw new IOException("io trouble");
            case 2 -> throw new java.sql.SQLException("sql trouble");
            case 3 -> throw new InterruptedException("interrupted");
            default -> System.out.println("multiThrow(" + code + ") ok");
        }
    }

    public static void main(String[] args) {

        section("1) Using throw");
        System.out.println("10 / 2 = " + divide(10, 2));
        try {
            divide(10, 0);
        } catch (ArithmeticException ex) {
            System.out.println("caught: " + ex.getMessage());
        }

        section("2) Using throws (the caller MUST handle the checked one)");
        try {
            System.out.println("user = " + readUsername(false));
            readUsername(true);                     // boom
        } catch (IOException e) {
            System.out.println("caught IO: " + e.getMessage());
        }

        section("3) Exception propagation - exception originates DEEP and bubbles");
        try {
            outer();
        } catch (IOException e) {
            System.out.println("caught (origin in inner()): " + e.getMessage());
        }

        section("4) Re-throw - wrap and re-fire (preserving the cause)");
        try {
            parseConfig("not-a-number");
        } catch (IllegalArgumentException e) {
            System.out.println("re-thrown: " + e.getMessage());
            System.out.println("  cause   : " + e.getCause());
        }

        section("5) A method that declares MULTIPLE checked types in `throws`");
        for (int code = 0; code <= 3; code++) {
            try {
                multiThrow(code);
            } catch (IOException | java.sql.SQLException | InterruptedException e) {
                System.out.println("caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        // OUTPUT
        // ====== 1) Using throw ======
        // 10 / 2 = 5
        // caught: cannot divide 10 by zero
        // ====== 2) Using throws (the caller MUST handle the checked one) ======
        // user = Deepak
        // caught IO: could not contact the auth service
        // ====== 3) Exception propagation - exception originates DEEP and bubbles ======
        // caught (origin in inner()): inner failure
        // ====== 4) Re-throw - wrap and re-fire (preserving the cause) ======
        // logging 'not-a-number' as bad config
        // re-thrown: bad config value: not-a-number
        //   cause   : java.lang.NumberFormatException: For input string: "not-a-number"
        // ====== 5) A method that declares MULTIPLE checked types in `throws` ======
        // multiThrow(0) ok
        // caught IOException: io trouble
        // caught SQLException: sql trouble
        // caught InterruptedException: interrupted
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
