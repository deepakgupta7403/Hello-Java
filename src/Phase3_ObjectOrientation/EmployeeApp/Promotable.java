package Phase3_ObjectOrientation.EmployeeApp;

/**
 * Promotable - capability interface for employees who can receive a raise.
 * <p>
 *
 * NOT every Employee is promotable - a Contractor's compensation is set by
 * the contract, not by HR. By separating this CAPABILITY from the Employee
 * root, we follow the Interface Segregation Principle: classes implement
 * only the interfaces whose contract they actually fulfil.
 * <p>
 *
 * Pattern: a thin "trait" interface. A regular sealed root (Employee) plus
 * orthogonal capability interfaces (Promotable, Auditable, ...) keeps the
 * design flexible.
 */
public interface Promotable {
    /**
     * Returns a NEW Employee with the given percentage raise applied.
     * Implementors are encouraged to be immutable - return a fresh object
     * rather than mutating in place.
     */
    Employee promote(double percent);
}
