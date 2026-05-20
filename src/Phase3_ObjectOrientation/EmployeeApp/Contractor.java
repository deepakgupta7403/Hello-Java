package Phase3_ObjectOrientation.EmployeeApp;

/**
 * Contractor - external worker paid a flat monthly retainer.
 *
 * No auto-promotion, no marker. Demonstrates an Employee variant that opts
 * INTO the smallest possible surface - only the Employee contract itself.
 */
public record Contractor(
        int    id,
        String name,
        double monthlyRetainer
) implements Employee {

    public Contractor {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (monthlyRetainer < 0) {
            throw new IllegalArgumentException("retainer must be >= 0");
        }
    }

    @Override public double monthlyPay() { return monthlyRetainer; }
}
