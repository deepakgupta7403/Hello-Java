package Phase3_ObjectOrientation.EmployeeApp;

/**
 * Auditable - a MARKER INTERFACE.
 * <p>
 *
 * Implementing this interface means "this object should be logged by the
 * audit framework". The marker has no methods; downstream code checks
 * `instanceof Auditable` and acts accordingly.
 * <p>
 *
 * We could have used an annotation instead (see MarkerInterface.java for the
 * comparison). Using an interface here lets us write generic helpers with an
 * upper bound, e.g.:
 * <p>
 *
 *      static <T extends Auditable> void auditAll(Collection<T> items) { ... }
 */
public interface Auditable {
}
