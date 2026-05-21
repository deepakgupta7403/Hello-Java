package Phase3_ObjectOrientation.BankingApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Account - Abstract Base Class for the Banking Project
 * -----------------------------------------------------
 * Demonstrates several OOP pillars in one place:
 * <p>
 *
 *   - ABSTRACTION   - declares the SHAPE of an account but leaves the
 *                     interest calculation to each subclass (abstract method).
 *   - ENCAPSULATION - balance is `private`, mutated only through deposit /
 *                     withdraw / transfer; each method validates input.
 *   - INHERITANCE   - SavingsAccount and CheckingAccount extend this class
 *                     and reuse the shared behaviour.
 *   - POLYMORPHISM  - the Bank deals with an Account reference and the JVM
 *                     dispatches to the subclass's overridden method at runtime.
 * <p>
 *
 * Design Notes
 * ------------
 *   - We track each transaction in an internal history list. The getter
 *     returns an UNMODIFIABLE view to keep the encapsulation tight.
 *   - The account number is FINAL - set in the constructor and never changes.
 *   - All money is represented as `double` for simplicity. In real banking
 *     code you would use BigDecimal to avoid floating-point rounding errors.
 */
public abstract class Account {

    private static int nextAccountNumber = 1000;     // package-private factory counter

    private final  String accountNumber;
    private final  String holderName;
    private        double balance;
    private final  List<Transaction> history = new ArrayList<>();

    protected Account(String holderName, double openingBalance) {
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("holderName must be provided");
        }
        if (openingBalance < 0) {
            throw new IllegalArgumentException("opening balance must be >= 0");
        }
        this.accountNumber = "ACC-" + (++nextAccountNumber);
        this.holderName    = holderName;
        this.balance       = openingBalance;
        if (openingBalance > 0) {
            history.add(new Transaction(Transaction.Type.OPEN, openingBalance, balance));
        }
    }

    // ----- read-only accessors (encapsulation) -----
    public String accountNumber() { return accountNumber; }
    public String holderName()    { return holderName; }
    public double balance()       { return balance; }
    public List<Transaction> history() { return Collections.unmodifiableList(history); }

    // ----- controlled mutators -----
    public void deposit(double amount) {
        validatePositive(amount, "deposit");
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
    }

    public void withdraw(double amount) {
        validatePositive(amount, "withdraw");
        if (amount > balance) {
            throw new IllegalStateException("insufficient funds");
        }
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAW, amount, balance));
    }

    /** Transfer money to another account atomically (within this single thread). */
    public void transferTo(Account other, double amount) {
        if (other == this) throw new IllegalArgumentException("cannot transfer to self");
        this.withdraw(amount);          // throws if insufficient
        other.deposit(amount);
    }

    // ----- the abstract HOOK each subclass must fill in -----
    /** Returns the interest accrued for the current period. */
    public abstract double calculateInterest();

    /** Common interest-application step that uses the abstract hook. */
    public final void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            balance += interest;
            history.add(new Transaction(Transaction.Type.INTEREST, interest, balance));
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s %s  balance=%.2f",
                accountNumber, holderName,
                getClass().getSimpleName(), balance);
    }

    private static void validatePositive(double v, String name) {
        if (v <= 0) {
            throw new IllegalArgumentException(name + " amount must be > 0 (was " + v + ")");
        }
    }
}
