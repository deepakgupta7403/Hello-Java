package Phase3_ObjectOrientation.EmployeeApp;

/**
 * FullTimeEmployee - permanent staff paid a fixed monthly salary.
 * <p>
 *
 * Implemented as a RECORD, which:
 *   - is implicitly final (satisfies the sealed permits requirement),
 *   - generates equals/hashCode/toString from the components,
 *   - exposes a canonical constructor we can extend with a compact form
 *     for validation.
 * <p>
 *
 * Also implements two cross-cutting interfaces:
 *   - Promotable - this employee can be promoted (gets a raise).
 *   - Auditable  - marker interface; signals this object should be logged.
 */
public record FullTimeEmployee(
        int     id,
        String  name,
        double  baseMonthly
) implements Employee, Promotable, Auditable {

    /** Compact constructor - validates the inputs. */
    public FullTimeEmployee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (baseMonthly < 0 || baseMonthly > Employee.MAX_PAY) {
            throw new IllegalArgumentException("baseMonthly out of range");
        }
    }

    @Override public double monthlyPay() { return baseMonthly; }

    /**
     * Returns a NEW FullTimeEmployee with the salary increased by `percent`.
     * Records are immutable; we model a promotion as the production of a
     * fresh record.
     */
    @Override
    public FullTimeEmployee promote(double percent) {
        double next = baseMonthly * (1 + percent / 100.0);
        return new FullTimeEmployee(id, name, next);
    }
}
