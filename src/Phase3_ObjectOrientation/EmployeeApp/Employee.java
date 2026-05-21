package Phase3_ObjectOrientation.EmployeeApp;

/**
 * Employee - the core CONTRACT every staff member must satisfy.
 * <p>
 *
 * Implemented as a SEALED INTERFACE (Java 17+) so the company knows exactly
 * which kinds of employees exist. Adding a new permitted record below will
 * cause every exhaustive `switch (employee) { ... }` elsewhere to fail to
 * compile until the new case is handled - a strong build-time safety net.
 * <p>
 *
 * Why an interface for the root type?
 * -----------------------------------
 *   - Employees vary widely (full-time, contractor, intern, ...) - we want a
 *     POLYMORPHIC type without forcing a deep class hierarchy.
 *   - Records make great variants: immutable, compact, equality for free.
 *   - The interface carries shared DEFAULT behaviour (annualSalary,
 *     formattedId) that every implementor inherits.
 * <p>
 *
 * Interface members used here
 * ---------------------------
 *   - abstract methods   : id, name, monthlyPay
 *   - default methods    : annualSalary, formattedId, summary
 *   - static methods     : nextId   - centralised id generator
 *   - private methods    : pad      - helper used by formattedId
 *   - constants          : MAX_PAY  - inherited public static final
 *   - permits clause     : restricts implementors to known types
 */
public sealed interface Employee permits FullTimeEmployee, PartTimeEmployee, Contractor, Intern {

    // ----- abstract contract every implementor must provide -----
    int    id();
    String name();
    double monthlyPay();

    // ----- shared constant (implicit public static final) -----
    double MAX_PAY = 1_000_000.0;

    // ----- shared default behaviour -----
    default double annualSalary() {
        return monthlyPay() * 12;
    }

    default String formattedId() {
        return "EMP-" + pad(id(), 5);
    }

    default String summary() {
        return String.format("%s | %-30s | monthly=%9.2f | annual=%9.2f",
                formattedId(), name(), monthlyPay(), annualSalary());
    }

    // ----- private helper (Java 9+) - hidden from outside callers -----
    private static String pad(int value, int width) {
        String s = String.valueOf(value);
        return "0".repeat(Math.max(0, width - s.length())) + s;
    }

    // ----- static factory utility -----
    static int nextId() {
        return Counter.next();
    }

    /** A tiny private holder for the id counter - hidden inside the interface. */
    final class Counter {
        private Counter() {}
        private static int n = 0;
        static int next() { return ++n; }
    }
}
