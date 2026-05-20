package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception Handling - Best Practices
 * -----------------------------------
 * A condensed list of the do's and don'ts that come up over and over in
 * code reviews, with quick code snippets you can read at a glance.
 *
 *
 * Top 10 Rules
 * ------------
 *
 *  1. THROW EARLY - validate inputs at the boundary and fail with a clear
 *                   message. Don't propagate a NullPointerException ten
 *                   stack frames deep.
 *
 *  2. CATCH LATE  - handle exceptions at the layer that can do something
 *                   meaningful (retry, user message, log+continue). Don't
 *                   catch deep inside a helper if you have nothing to do.
 *
 *  3. NEVER SWALLOW. Empty catch blocks are bugs. At a minimum log the
 *                   exception with full context.
 *
 *  4. DON'T CATCH Throwable / Error. Catching Throwable hides JVM-level
 *                   problems (OutOfMemoryError, StackOverflowError) that
 *                   you cannot meaningfully recover from anyway.
 *
 *  5. PREFER UNCHECKED for programmer bugs. Use checked exceptions for
 *                   conditions a reasonable caller is expected to handle.
 *
 *  6. INCLUDE CONTEXT in the message. Bad: "lookup failed".
 *                                       Good: "lookup failed for userId=42".
 *
 *  7. CHAIN CAUSES. When wrapping, always pass the original as the cause:
 *                   throw new HighLevel("...", lowLevel).
 *
 *  8. USE try-with-resources for anything implementing AutoCloseable.
 *
 *  9. AVOID using exceptions for FLOW CONTROL. Exceptions are
 *                  performance-expensive and obscure intent.
 *
 * 10. DOCUMENT exceptions with Javadoc @throws so callers know what to plan
 *                  for - both checked and the important unchecked ones.
 *
 *
 * Each rule has a tiny example below.
 */

public class ExceptionBestPractices {

    private static final Logger LOG = Logger.getLogger(ExceptionBestPractices.class.getName());

    public static void main(String[] args) {

        section("1) Throw early - validate at the boundary");
        try {
            register(null, 25);
        } catch (NullPointerException e) {
            System.out.println("clear: " + e.getMessage());
        }

        section("3) NEVER swallow - empty catch blocks are bugs");
        try {
            mightFail();
        } catch (RuntimeException ignored) {
            // BAD: this hides bugs forever.
            // Even a one-line log is infinitely better than nothing.
            LOG.log(Level.WARNING, "mightFail failed", ignored);
        }

        section("6) Include context in the message");
        try {
            loadConfig("missing.conf");
        } catch (IllegalStateException e) {
            System.out.println("good: " + e.getMessage());
        }

        section("7) Chain causes - never lose the underlying reason");
        try {
            translate("oops");
        } catch (IllegalArgumentException e) {
            System.out.println("top  : " + e.getMessage());
            System.out.println("cause: " + e.getCause());
        }

        section("9) Don't use exceptions for flow control");
        // Anti-pattern:
        boolean isInt = isParseableViaException("42");
        // Pattern:
        boolean isInt2 = isParseableViaCheck("42");
        System.out.println("isInt  (exception flow) = " + isInt);
        System.out.println("isInt2 (proper check)    = " + isInt2);

        section("Bonus - the friendliest two helpers in java.util.Objects");
        try {
            requirePositive(-3);
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }

        // OUTPUT
        // ====== 1) Throw early - validate at the boundary ======
        // clear: name must not be null
        // ====== 3) NEVER swallow - empty catch blocks are bugs ======
        // (logger output goes here)
        // ====== 6) Include context in the message ======
        // good: failed to load config from 'missing.conf'
        // ====== 7) Chain causes - never lose the underlying reason ======
        // top  : translate failed for input 'oops'
        // cause: java.lang.NumberFormatException: For input string: "oops"
        // ====== 9) Don't use exceptions for flow control ======
        // isInt  (exception flow) = true
        // isInt2 (proper check)    = true
        // ====== Bonus - the friendliest two helpers in java.util.Objects ======
        // caught: amount must be > 0 (was -3)
    }

    /**
     * Validate inputs at the boundary - fail fast with a precise message.
     * Notice the @throws Javadoc tag so callers know what to expect.
     *
     * @param name  must not be null
     * @param age   must be in [0, 150]
     * @throws NullPointerException     if name is null
     * @throws IllegalArgumentException if age is out of range
     */
    static void register(String name, int age) {
        Objects.requireNonNull(name, "name must not be null");
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("age out of range: " + age);
        }
        System.out.println("registered: " + name + " (" + age + ")");
    }

    static void mightFail() {
        throw new RuntimeException("simulated");
    }

    /** Include context (path) in the message so the log line is actionable. */
    static void loadConfig(String path) {
        // imagine some I/O work here that fails
        boolean missing = true;
        if (missing) {
            throw new IllegalStateException("failed to load config from '" + path + "'");
        }
    }

    /** Wrap-with-cause - keep the low-level reason. */
    static int translate(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("translate failed for input '" + raw + "'", nfe);
        }
    }

    // --- 9) Anti-pattern vs proper check ---

    static boolean isParseableViaException(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;            // exceptions for flow - slow + noisy
        }
    }

    static boolean isParseableViaCheck(String s) {
        if (s == null || s.isEmpty()) return false;
        int start = (s.charAt(0) == '-') ? 1 : 0;
        if (start == s.length()) return false;
        for (int i = start; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    /** Friendly helper using Objects + IllegalArgumentException - readable + fast. */
    static void requirePositive(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0 (was " + amount + ")");
        }
    }

    // A method that demonstrates the recommended "translate IOException to
    // a domain exception" pattern. Kept here for reference.
    @SuppressWarnings("unused")
    static String readUserOrFail(String userId) {
        try {
            // imagine some I/O
            throw new IOException("network down");
        } catch (IOException ioe) {
            // Don't leak IOException to higher layers - translate to a
            // domain-level exception with the cause attached.
            throw new IllegalStateException("could not load user '" + userId + "'", ioe);
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
