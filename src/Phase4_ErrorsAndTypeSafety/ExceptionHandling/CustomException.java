package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

/**
 * Custom Exceptions
 * -----------------
 * Sometimes the standard exceptions are too generic. A domain-specific
 * exception class lets callers catch your particular failure with a precise
 * `catch` and lets you carry extra data (account id, retry-after seconds,
 * error code) along with the message.
 * <p>
 *
 * Two Flavours
 * ------------
 *   - CHECKED   : extend Exception                  (forced to handle)
 *   - UNCHECKED : extend RuntimeException           (NOT forced to handle)
 * <p>
 *
 * Pick CHECKED for failures the CALLER is expected to recover from
 * (FileNotFoundException - try a different file). Pick UNCHECKED for
 * programmer errors and invariant violations (IllegalStateException) -
 * forcing every caller to handle them just clutters the code.
 * <p>
 *
 * Recommended Constructors (the four-constructor convention)
 * ----------------------------------------------------------
 * Mirror the standard ones from the parent. The compiler will pick the
 * right overload at the call site:
 * <p>
 *
 *      public MyException()                                 { super(); }
 *      public MyException(String message)                   { super(message); }
 *      public MyException(String message, Throwable cause)  { super(message, cause); }
 *      public MyException(Throwable cause)                  { super(cause); }
 * <p>
 *
 * Naming Convention
 * -----------------
 * Class names should end with the word "Exception" - `InsufficientFundsException`,
 * `PaymentDeclinedException`, etc.
 * <p>
 *
 * Carrying Extra Data
 * -------------------
 * You can add fields and constructor parameters to capture context. Common
 * extras: an error code, the offending value, a "retry after" timestamp.
 * <p>
 *
 *      public class RateLimitException extends RuntimeException {
 *          private final long retryAfterMs;
 *          public RateLimitException(String message, long retryAfterMs) {
 *              super(message);
 *              this.retryAfterMs = retryAfterMs;
 *          }
 *          public long getRetryAfterMs() { return retryAfterMs; }
 *      }
 * <p>
 *
 * Tip
 * ---
 * Prefer the JDK's standard exception types when they fit (NullPointerException,
 * IllegalArgumentException, IllegalStateException). Reach for a custom class
 * when callers benefit from a more specific type.
 */

public class CustomException {

    // ============================================================
    // 1) A CHECKED custom exception
    // ============================================================
    static class InsufficientFundsException extends Exception {
        private final double shortfall;             // extra context

        public InsufficientFundsException(double shortfall) {
            super("insufficient funds, short by " + shortfall);
            this.shortfall = shortfall;
        }

        public InsufficientFundsException(String message, Throwable cause) {
            super(message, cause);
            this.shortfall = 0;
        }

        public double getShortfall() { return shortfall; }
    }

    // ============================================================
    // 2) An UNCHECKED custom exception
    // ============================================================
    static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String email) {
            super("invalid email format: " + email);
        }
    }

    // ============================================================
    // Demo classes that THROW the custom exceptions
    // ============================================================
    static class Account {
        private double balance = 100;

        // Method declares the CHECKED exception via `throws`.
        public void withdraw(double amount) throws InsufficientFundsException {
            if (amount > balance) {
                throw new InsufficientFundsException(amount - balance);
            }
            balance -= amount;
        }

        public double balance() { return balance; }
    }

    static class UserService {
        // Unchecked - no `throws` required; the JVM still propagates it.
        public void register(String email) {
            if (email == null || !email.contains("@")) {
                throw new InvalidEmailException(email);
            }
            System.out.println("registered " + email);
        }
    }

    public static void main(String[] args) {

        section("1) Checked custom exception");
        Account acc = new Account();
        try {
            acc.withdraw(50);                       // OK
            acc.withdraw(120);                      // throws
        } catch (InsufficientFundsException e) {
            System.out.println("caught: " + e.getMessage());
            System.out.println("short by: " + e.getShortfall());
        }
        System.out.println("balance: " + acc.balance());

        section("2) Unchecked custom exception");
        UserService svc = new UserService();
        try {
            svc.register("ok@example.com");
            svc.register("bad-email");              // throws
        } catch (InvalidEmailException e) {
            System.out.println("caught: " + e.getMessage());
        }

        section("3) Wrap a low-level cause inside a custom exception");
        try {
            loadProfile("bogus");
        } catch (ProfileLoadException e) {
            System.out.println("caught: " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }

        // OUTPUT
        // ====== 1) Checked custom exception ======
        // caught: insufficient funds, short by 70.0
        // short by: 70.0
        // balance: 50.0
        // ====== 2) Unchecked custom exception ======
        // registered ok@example.com
        // caught: invalid email format: bad-email
        // ====== 3) Wrap a low-level cause inside a custom exception ======
        // caught: failed to load profile for user 'bogus'
        // cause : java.lang.NumberFormatException: For input string: "bogus"
    }

    /**
     * Demonstrates the (message, cause) constructor on a custom exception.
     * We translate a low-level NumberFormatException into a domain-level
     * ProfileLoadException while keeping the original as the cause.
     */
    static void loadProfile(String userIdAsString) throws ProfileLoadException {
        try {
            // Pretend the user id has to be numeric and we look it up by int.
            int id = Integer.parseInt(userIdAsString);
            System.out.println("loaded profile for id " + id);
        } catch (NumberFormatException e) {
            throw new ProfileLoadException(
                    "failed to load profile for user '" + userIdAsString + "'",
                    e
            );
        }
    }

    /** A more typical "in its own file" style custom exception. */
    static class ProfileLoadException extends Exception {
        public ProfileLoadException(String message)                   { super(message); }
        public ProfileLoadException(String message, Throwable cause)  { super(message, cause); }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
