package Phase3_ObjectOrientation.EmployeeApp;

/**
 * Intern - stipend-based employee. Not Promotable, but is Auditable so HR
 * tracks them in compliance reports.
 */
public record Intern(
        int    id,
        String name,
        double stipend
) implements Employee, Auditable {

    public Intern {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (stipend < 0) {
            throw new IllegalArgumentException("stipend must be >= 0");
        }
    }

    @Override public double monthlyPay() { return stipend; }
}
