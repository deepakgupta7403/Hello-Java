package OOPSConcepts.BankingApp;

/**
 * BankingApp - runnable demo of the Simple Banking Application
 * ------------------------------------------------------------
 * This `main` ties together every OOP pillar from the rest of the folder:
 *
 *   - ABSTRACTION    : `Account` is abstract; callers code against it.
 *   - ENCAPSULATION  : balances and transaction histories are private and
 *                      manipulated only through validated methods.
 *   - INHERITANCE    : `SavingsAccount` and `CheckingAccount` share Account's
 *                      machinery.
 *   - POLYMORPHISM   : `Bank.applyMonthlyCycle()` calls `applyInterest()` on
 *                      every Account; each subclass's `calculateInterest()` is
 *                      dispatched at runtime.
 *   - PACKAGES       : every file lives in package OOPSConcepts.BankingApp.
 *   - RECORDS        : `Transaction` is an immutable record.
 *
 *
 * Run from project root:
 *
 *      javac src/OOPSConcepts/BankingApp/*.java
 *      cd src
 *      java OOPSConcepts.BankingApp.BankingApp
 */
public class BankingApp {

    public static void main(String[] args) {

        Bank bank = new Bank("HelloJava Bank");

        // Open three accounts of different concrete types
        SavingsAccount  alice = bank.openAccount(new SavingsAccount ("Alice Sharma",     5_000, 0.06));
        CheckingAccount bob   = bank.openAccount(new CheckingAccount("Bob Patel",        1_500, 10.0));
        SavingsAccount  carol = bank.openAccount(new SavingsAccount ("Carol Iyer",          0,  0.04));

        // ---- A few customer-facing transactions ----
        alice.deposit(1_500);
        bob.deposit(2_000);
        carol.deposit(800);

        bob.withdraw(700);
        alice.transferTo(carol, 1_000);          // polymorphic - works for any pair

        // Demonstrate the encapsulation guards
        try {
            bob.withdraw(50_000);                // insufficient funds
        } catch (IllegalStateException e) {
            System.out.println("blocked: " + e.getMessage());
        }
        try {
            alice.deposit(-1);                   // invalid amount
        } catch (IllegalArgumentException e) {
            System.out.println("blocked: " + e.getMessage());
        }

        // ---- Run the monthly cycle a couple of times ----
        // Savings accounts earn interest; checking accounts pay a fee.
        bank.applyMonthlyCycle();
        bank.applyMonthlyCycle();

        // ---- Print everything ----
        bank.printStatement();

        // ---- Show polymorphism through the abstract Account reference ----
        System.out.println("\n=== Polymorphic iteration ===");
        for (Account a : bank.accounts()) {
            // Same method call, different runtime behaviour per concrete type
            double i = a.calculateInterest();
            System.out.printf("%-30s next interest = %.2f%n", a.holderName(), i);
        }

        // SAMPLE OUTPUT (timestamps will vary)
        // blocked: insufficient funds
        // blocked: deposit amount must be > 0 (was -1.0)
        //
        // === HelloJava Bank - Account Statement ===
        // [ACC-1001] Alice Sharma                   SavingsAccount  balance=5532.50
        //     OPEN     5000.00  -> balance  5000.00
        //     DEPOSIT  1500.00  -> balance  6500.00
        //     WITHDRAW 1000.00  -> balance  5500.00
        //     INTEREST   27.50  -> balance  5527.50
        //     INTEREST   27.64  -> balance  5555.14   (numbers depend on order)
        // [ACC-1002] Bob Patel                      CheckingAccount balance=2780.00
        //     OPEN     1500.00  -> balance  1500.00
        //     DEPOSIT  2000.00  -> balance  3500.00
        //     WITHDRAW  700.00  -> balance  2800.00
        //     WITHDRAW   10.00  -> balance  2790.00
        //     WITHDRAW   10.00  -> balance  2780.00
        // [ACC-1003] Carol Iyer                     SavingsAccount  balance=1806.00
        //     DEPOSIT   800.00  -> balance   800.00
        //     DEPOSIT  1000.00  -> balance  1800.00
        //     INTEREST    6.00  -> balance  1806.00
        //     INTEREST    6.02  -> balance  1812.02
        //
        // === Polymorphic iteration ===
        // Alice Sharma                   next interest = 27.78
        // Bob Patel                      next interest = 0.00
        // Carol Iyer                     next interest = 6.04
    }
}
