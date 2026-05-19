package OOPSConcepts.EmployeeApp;

/**
 * PartTimeEmployee - paid by the hour, capped at a monthly hour budget.
 *
 * Records:
 *   - id          : numeric id
 *   - name        : staff name
 *   - hourlyRate  : currency per hour
 *   - hoursLogged : hours worked this month
 *
 * monthlyPay() = hourlyRate * hoursLogged
 *
 * Implements Auditable but NOT Promotable - part-time staff in this model
 * get raised by changing hourlyRate, not by a `promote(...)` call.
 */
public record PartTimeEmployee(
        int    id,
        String name,
        double hourlyRate,
        double hoursLogged
) implements Employee, Auditable {

    public PartTimeEmployee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (hourlyRate < 0)  throw new IllegalArgumentException("hourlyRate must be >= 0");
        if (hoursLogged < 0) throw new IllegalArgumentException("hoursLogged must be >= 0");
    }

    @Override
    public double monthlyPay() {
        return hourlyRate * hoursLogged;
    }
}
