package OOPSConcepts.EmployeeApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * EmployeeRepository - the aggregate that owns the in-memory employee table.
 *
 * Programs against the Employee INTERFACE rather than any specific record,
 * which is the whole point of interface-driven design: the repository works
 * unchanged no matter how many new permitted variants we add.
 *
 * Demonstrates:
 *   - Programming to an interface (Employee).
 *   - Functional interfaces used as method parameters (EmployeeFilter).
 *   - Generic helpers constrained by capability markers (Auditable, Promotable).
 */
public class EmployeeRepository {

    private final List<Employee> employees = new ArrayList<>();

    /** Add an employee. Returns the same reference so callers can chain. */
    public <E extends Employee> E add(E employee) {
        employees.add(employee);
        return employee;
    }

    /** Read-only view of all employees. */
    public List<Employee> all() {
        return Collections.unmodifiableList(employees);
    }

    /** Find the first employee matching a filter. */
    public Optional<Employee> findFirst(EmployeeFilter filter) {
        for (Employee e : employees) {
            if (filter.accept(e)) return Optional.of(e);
        }
        return Optional.empty();
    }

    /** Find every employee matching a filter. */
    public List<Employee> findAll(EmployeeFilter filter) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (filter.accept(e)) result.add(e);
        }
        return result;
    }

    /** Sum the monthly payroll across every employee. */
    public double totalMonthlyPay() {
        double sum = 0;
        for (Employee e : employees) sum += e.monthlyPay();
        return sum;
    }

    /**
     * Audit only the employees that opted IN by implementing Auditable.
     * Demonstrates how a marker interface lets us filter polymorphically.
     */
    public void runAudit() {
        System.out.println("\n--- audit log ---");
        for (Employee e : employees) {
            if (e instanceof Auditable) {
                System.out.println("AUDIT " + e.summary());
            }
        }
    }

    /**
     * Promote every employee that is Promotable, returning a NEW list of
     * Employees with the raise applied. Records are immutable, so we model
     * the promotion as a fresh object rather than mutating in place.
     */
    public List<Employee> promoteEveryone(double percent) {
        List<Employee> updated = new ArrayList<>();
        for (Employee e : employees) {
            if (e instanceof Promotable p) {
                updated.add(p.promote(percent));
            } else {
                updated.add(e);
            }
        }
        return updated;
    }

    /** Print a tidy table of all employees, sorted by annual salary. */
    public void printRoster() {
        System.out.println("\n--- roster (sorted by annual salary, desc) ---");
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(Comparator.comparingDouble(Employee::annualSalary).reversed());
        for (Employee e : sorted) System.out.println(e.summary());
    }
}
