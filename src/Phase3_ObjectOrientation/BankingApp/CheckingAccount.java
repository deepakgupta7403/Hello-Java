package Phase3_ObjectOrientation.BankingApp;

/**
 * CheckingAccount - "is-a" Account designed for day-to-day spending.
 * <p>
 *
 *   - Pays NO interest in this simple model (overrides return 0).
 *   - Charges a flat MONTHLY FEE each time applyInterest() runs (we treat
 *     `applyInterest` as the monthly cycle hook).
 * <p>
 *
 * Demonstrates:
 *   - INHERITANCE + POLYMORPHISM by overriding the hook differently from
 *     SavingsAccount.
 *   - METHOD OVERRIDING with a side effect (charging a fee).
 */
public class CheckingAccount extends Account {

    private final double monthlyFee;

    public CheckingAccount(String holderName, double openingBalance, double monthlyFee) {
        super(holderName, openingBalance);
        if (monthlyFee < 0) {
            throw new IllegalArgumentException("fee must be >= 0");
        }
        this.monthlyFee = monthlyFee;
    }

    public double monthlyFee() { return monthlyFee; }

    /**
     * Checking accounts pay no interest. We model the monthly fee here so that
     * the Bank's applyMonthlyCycle() loop also drives fee collection without
     * needing a separate API.
     */
    @Override
    public double calculateInterest() {
        if (monthlyFee > 0 && balance() >= monthlyFee) {
            // Withdraw the fee via the public API so it is logged and
            // validated like any other transaction.
            withdraw(monthlyFee);
        }
        return 0.0;
    }
}
