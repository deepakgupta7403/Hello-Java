package Phase3_ObjectOrientation.BankingApp;

import java.time.LocalDateTime;

/**
 * Transaction - immutable log entry for a single account event.
 * <p>
 *
 * Implemented as a Java RECORD (Java 16+):
 *   - All fields are final.
 *   - equals / hashCode / toString are generated.
 *   - The compiler also synthesises a canonical constructor and accessor
 *     methods type(), amount(), balanceAfter(), timestamp().
 * <p>
 *
 * The nested `Type` enum makes the kind of event explicit and lets us
 * use exhaustive `switch` statements in any caller code.
 */
public record Transaction(
        Transaction.Type type,
        double           amount,
        double           balanceAfter,
        LocalDateTime    timestamp
) {

    /** Compact constructor - validates and supplies a default timestamp. */
    public Transaction {
        if (amount < 0) {
            throw new IllegalArgumentException("transaction amount must be >= 0");
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    /** Convenience constructor - uses "now" for the timestamp. */
    public Transaction(Type type, double amount, double balanceAfter) {
        this(type, amount, balanceAfter, LocalDateTime.now());
    }

    public enum Type {
        OPEN, DEPOSIT, WITHDRAW, INTEREST, FEE
    }
}
