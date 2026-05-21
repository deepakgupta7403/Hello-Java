package Phase3_ObjectOrientation.EmployeeApp;

/**
 * EmployeeFilter - a NESTED + FUNCTIONAL interface.
 * <p>
 *
 * Used by the repository's `find(...)` method to express arbitrary search
 * criteria as a lambda:
 * <p>
 *
 *      repo.find(EmployeeFilter.byMinSalary(10_000)
 *                              .and(EmployeeFilter.byType(Intern.class)));
 * <p>
 *
 * Demonstrates:
 *   - @FunctionalInterface annotation enforcing the SAM rule.
 *   - default `and` / `or` / `negate` composition methods (like Predicate).
 *   - static FACTORIES on the interface itself for common filters.
 */
@FunctionalInterface
public interface EmployeeFilter {

    boolean accept(Employee e);

    // ----- composition - same idiom as java.util.function.Predicate -----
    default EmployeeFilter and(EmployeeFilter other) {
        return e -> this.accept(e) && other.accept(e);
    }
    default EmployeeFilter or(EmployeeFilter other) {
        return e -> this.accept(e) || other.accept(e);
    }
    default EmployeeFilter negate() {
        return e -> !this.accept(e);
    }

    // ----- static factories for common cases -----
    static EmployeeFilter all() {
        return e -> true;
    }
    static EmployeeFilter byMinSalary(double min) {
        return e -> e.monthlyPay() >= min;
    }
    static EmployeeFilter byType(Class<? extends Employee> type) {
        return type::isInstance;
    }
    static EmployeeFilter nameStartsWith(String prefix) {
        return e -> e.name() != null && e.name().startsWith(prefix);
    }
}
