package Phase3_ObjectOrientation.BankingApp;

/**
 * SavingsAccount - "is-a" Account with an interest rate.
 *
 * Demonstrates:
 *   - INHERITANCE   - extends Account, reuses its encapsulated balance API.
 *   - POLYMORPHISM  - overrides calculateInterest() so applyInterest() (a
 *                     template method on Account) does the right thing per
 *                     subclass at runtime.
 */
public class SavingsAccount extends Account {

    private final double annualRate;       // e.g. 0.04 for 4% per year

    public SavingsAccount(String holderName, double openingBalance, double annualRate) {
        super(holderName, openingBalance);
        if (annualRate < 0 || annualRate > 0.50) {
            throw new IllegalArgumentException("rate out of sensible range");
        }
        this.annualRate = annualRate;
    }

    public double annualRate() { return annualRate; }

    /** Simple monthly interest: balance * rate / 12. */
    @Override
    public double calculateInterest() {
        return balance() * annualRate / 12.0;
    }
}
