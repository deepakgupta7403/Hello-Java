package OOPSConcepts.BankingApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Bank - aggregate root that owns many Accounts.
 *
 * Demonstrates:
 *   - ENCAPSULATION  - accounts list is hidden; callers go through openAccount,
 *                      find, applyMonthlyCycle.
 *   - POLYMORPHISM   - applyMonthlyCycle iterates over Account references;
 *                      each subclass's overridden calculateInterest runs.
 *   - SINGLE RESPONSIBILITY - the Bank schedules; each Account owns its rules.
 */
public class Bank {

    private final String name;
    private final List<Account> accounts = new ArrayList<>();

    public Bank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("bank name required");
        }
        this.name = name;
    }

    public String name() { return name; }

    /** Add an already-constructed account to this bank. */
    public <A extends Account> A openAccount(A account) {
        accounts.add(account);
        return account;
    }

    /** Look up by account number. */
    public Optional<Account> find(String accountNumber) {
        for (Account a : accounts) {
            if (a.accountNumber().equals(accountNumber)) return Optional.of(a);
        }
        return Optional.empty();
    }

    /** Read-only view of all accounts. */
    public List<Account> accounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Runs the monthly cycle for every account. The runtime dispatches to
     * each subclass's calculateInterest() - the textbook example of runtime
     * polymorphism.
     */
    public void applyMonthlyCycle() {
        for (Account a : accounts) {
            a.applyInterest();
        }
    }

    /** Print a per-account summary - useful for a quick demo. */
    public void printStatement() {
        System.out.println("\n=== " + name + " - Account Statement ===");
        for (Account a : accounts) {
            System.out.println(a);
            for (Transaction t : a.history()) {
                System.out.printf("    %-9s %8.2f  -> balance %8.2f%n",
                        t.type(), t.amount(), t.balanceAfter());
            }
        }
    }
}
