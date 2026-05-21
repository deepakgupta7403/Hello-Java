package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

/**
 * Exception Handling in Java - Introduction
 * -----------------------------------------
 * An EXCEPTION is an abnormal event that disrupts the normal flow of a
 * program. When an exception is THROWN, the JVM looks up the call stack for
 * a matching handler; if it finds none, the program terminates and the JVM
 * prints the stack trace.
 * <p>
 *
 * Java's exception system has three jobs:
 *   1. SEPARATE error handling from regular code (no error codes mixed in).
 *   2. PROPAGATE failures up the stack until something handles them.
 *   3. ENFORCE certain kinds of failure to be acknowledged (checked exceptions).
 * <p>
 *
 * The Throwable Hierarchy
 * -----------------------
 * <p>
 *
 *      Throwable                                         (top of the chain)
 *      |
 *      |-- Error           (serious JVM-level problems - DO NOT catch)
 *      |     |-- OutOfMemoryError
 *      |     |-- StackOverflowError
 *      |     |-- VirtualMachineError
 *      |
 *      |-- Exception       (problems your code might recover from)
 *            |-- IOException, SQLException, ...           CHECKED
 *            |-- ClassNotFoundException                   CHECKED
 *            |
 *            |-- RuntimeException                         UNCHECKED
 *                  |-- NullPointerException
 *                  |-- ArrayIndexOutOfBoundsException
 *                  |-- ClassCastException
 *                  |-- IllegalArgumentException
 *                  |-- ArithmeticException
 *                  |-- NumberFormatException
 *                  |-- ConcurrentModificationException
 * <p>
 *
 * Checked vs Unchecked - Quick Rule
 * ---------------------------------
 *   CHECKED   - Any Exception that is NOT a RuntimeException. The compiler
 *               FORCES you to either catch them or declare them with `throws`.
 *               Used for failures the caller is expected to plan for
 *               (a file might be missing, the DB might be down).
 * <p>
 *
 *   UNCHECKED - RuntimeException and its descendants, plus all Errors. The
 *               compiler does NOT force you to acknowledge them. Used for
 *               programming bugs (NPE, IOOBE) and unrecoverable conditions.
 * <p>
 *
 * Anatomy of a Stack Trace
 * ------------------------
 *      Exception in thread "main" java.lang.NumberFormatException: For input string: "abc"
 *          at java.base/java.lang.NumberFormatException.forInputString(...)
 *          at java.base/java.lang.Integer.parseInt(...)
 *          at MyApp.run(MyApp.java:17)            <- your code, your line
 *          at MyApp.main(MyApp.java:9)
 * <p>
 *
 * Read it top to bottom: the EXCEPTION type, its message, then the call
 * chain (newest first). The first `at MyApp.X(...)` line is usually the bug.
 * <p>
 *
 * Five Keywords That Make Exception Handling Work
 * -----------------------------------------------
 *      try        - protect a region of code
 *      catch      - handle a thrown exception by type
 *      finally    - run cleanup no matter what
 *      throw      - raise an exception explicitly
 *      throws     - declare that a method may propagate certain checked
 *                   exceptions to its caller
 * <p>
 *
 * What This Folder Covers
 * -----------------------
 *   ExceptionIntroduction.java     (this file)
 *   TryCatchBlock.java             - try/catch / multi-catch / nested try
 *   FinalFinallyFinalize.java      - three different "final*" keywords
 *   ThrowAndThrows.java            - throw vs throws + propagation
 *   CustomException.java           - your own checked + unchecked types
 *   ChainedException.java          - wrap a cause without losing it
 *   NullPointerExceptions.java     - NPE, helpful messages, Optional
 *   ExceptionInOverriding.java     - the throws rules for overriders
 *   TryWithResources.java          - auto-close cleanup (Java 7+, enhanced 9+)
 *   ExceptionBestPractices.java    - dos and donts
 */

public class ExceptionIntroduction {

    public static void main(String[] args) {

        section("1) An UNCHECKED exception (compiler does not force handling)");
        try {
            int x = 10 / 0;                          // ArithmeticException
            System.out.println(x);
        } catch (ArithmeticException ex) {
            System.out.println("caught " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }

        section("2) A CHECKED exception (compiler forces handling)");
        try {
            mightThrowChecked(true);
        } catch (java.io.IOException e) {
            System.out.println("caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        section("3) Walking the type hierarchy with instanceof");
        Throwable t = new IllegalArgumentException("bad input");
        System.out.println("instanceof RuntimeException = " + (t instanceof RuntimeException));
        System.out.println("instanceof Exception        = " + (t instanceof Exception));
        System.out.println("instanceof Throwable        = " + (t instanceof Throwable));

        section("4) Reading a stack trace - we trigger a small one on purpose");
        try {
            level1();
        } catch (RuntimeException e) {
            // Just print the bottom frames so the output stays short.
            e.printStackTrace(System.out);
        }

        section("5) Common RuntimeException quick reference");
        // Each of these is in the JDK and very frequent. They are commented
        // so this main() does not bail out - uncomment one to see it fly.
        // String s = null; s.length();                   // NullPointerException
        // int[] a = new int[3]; int v = a[5];            // ArrayIndexOutOfBoundsException
        // Object o = "x"; Integer i = (Integer) o;       // ClassCastException
        // Integer.parseInt("not a number");              // NumberFormatException
        // throw new IllegalStateException("bad state");  // IllegalStateException

        // OUTPUT
        // ====== 1) An UNCHECKED exception (compiler does not force handling) ======
        // caught ArithmeticException: / by zero
        // ====== 2) A CHECKED exception (compiler forces handling) ======
        // caught IOException: simulated I/O failure
        // ====== 3) Walking the type hierarchy with instanceof ======
        // instanceof RuntimeException = true
        // instanceof Exception        = true
        // instanceof Throwable        = true
        // ====== 4) Reading a stack trace - we trigger a small one on purpose ======
        // java.lang.IllegalStateException: triggered from level3
        //     at Basics.ExceptionHandling.ExceptionIntroduction.level3(...)
        //     at Basics.ExceptionHandling.ExceptionIntroduction.level2(...)
        //     at Basics.ExceptionHandling.ExceptionIntroduction.level1(...)
        //     at Basics.ExceptionHandling.ExceptionIntroduction.main(...)
        // ====== 5) Common RuntimeException quick reference ======
    }

    /** Demonstrates a CHECKED exception - must declare `throws IOException`. */
    static void mightThrowChecked(boolean fail) throws java.io.IOException {
        if (fail) throw new java.io.IOException("simulated I/O failure");
    }

    // --- A tiny call chain so we get a multi-frame stack trace ---
    static void level1() { level2(); }
    static void level2() { level3(); }
    static void level3() { throw new IllegalStateException("triggered from level3"); }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
