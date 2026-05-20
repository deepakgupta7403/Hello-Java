package Phase3_ObjectOrientation.EmployeeApp;

/**
 * EmployeeApp - runnable demo of the Employee Management System.
 *
 * Wires together every kind of interface covered in this folder:
 *
 *   - SEALED INTERFACE        : Employee with permits-list of variants.
 *   - RECORDS implementing    : FullTimeEmployee, PartTimeEmployee,
 *     a sealed interface         Contractor, Intern.
 *   - CAPABILITY interfaces   : Promotable - only some variants implement it.
 *   - MARKER interface        : Auditable - "tag" with no methods.
 *   - FUNCTIONAL interface    : EmployeeFilter - used as a lambda predicate.
 *   - NESTED interface idea   : a `Promotable.promote` returns Employee, the
 *                                root interface.
 *   - DEFAULT, STATIC, PRIVATE methods inside Employee.
 *   - PATTERN MATCHING SWITCH on the sealed Employee type (Java 21).
 *
 * Run with:
 *
 *      javac src/OOPSConcepts/EmployeeApp/*.java
 *      cd src
 *      java OOPSConcepts.EmployeeApp.EmployeeApp
 */
public class EmployeeApp {

    public static void main(String[] args) {

        EmployeeRepository repo = new EmployeeRepository();

        // 1) Hire a mix of employee variants - id supplied by the static
        //    Employee.nextId() factory on the interface.
        repo.add(new FullTimeEmployee(Employee.nextId(), "Alice Sharma",     8_000.00));
        repo.add(new FullTimeEmployee(Employee.nextId(), "Bob Patel",        9_500.00));
        repo.add(new PartTimeEmployee(Employee.nextId(), "Carol Iyer",       50.00, 80.0));
        repo.add(new Contractor      (Employee.nextId(), "Dev Studio Inc.",  12_000.00));
        repo.add(new Intern          (Employee.nextId(), "Eve Roy",          1_200.00));

        // 2) Print the full roster
        repo.printRoster();

        // 3) Filter via the functional interface + composed predicates
        System.out.println("\nEmployees with monthly pay >= 5,000:");
        for (Employee e : repo.findAll(EmployeeFilter.byMinSalary(5_000))) {
            System.out.println("  " + e.summary());
        }

        System.out.println("\nFull-timers whose names start with 'A':");
        EmployeeFilter ftAndA = EmployeeFilter.byType(FullTimeEmployee.class)
                                              .and(EmployeeFilter.nameStartsWith("A"));
        repo.findAll(ftAndA).forEach(e -> System.out.println("  " + e.summary()));

        // 4) Marker interface: only Auditables show up
        repo.runAudit();

        // 5) Capability interface: Promotable employees get a 10% raise.
        //    promoteEveryone returns NEW employee records.
        System.out.println("\nApplying a 10% raise where promotion is supported...");
        var promoted = repo.promoteEveryone(10.0);
        for (Employee e : promoted) {
            System.out.println("  " + e.summary());
        }

        // 6) Pattern matching for switch on the sealed Employee interface.
        //    The compiler verifies that every permitted variant is handled.
        System.out.println("\nPay slip details (pattern matching switch):");
        for (Employee e : repo.all()) {
            String detail = switch (e) {
                case FullTimeEmployee ft  -> "fixed salary " + ft.baseMonthly();
                case PartTimeEmployee pt  -> "hours " + pt.hoursLogged()
                                          +  " x rate " + pt.hourlyRate();
                case Contractor       c   -> "retainer " + c.monthlyRetainer();
                case Intern           in  -> "stipend " + in.stipend();
            };
            System.out.println("  " + e.formattedId() + " (" + e.name() + ") -> " + detail);
        }

        // 7) Aggregate totals
        System.out.printf("%nMonthly payroll total: %.2f%n", repo.totalMonthlyPay());

        // SAMPLE OUTPUT
        // --- roster (sorted by annual salary, desc) ---
        // EMP-00004 | Dev Studio Inc.                | monthly= 12000.00 | annual=144000.00
        // EMP-00002 | Bob Patel                      | monthly=  9500.00 | annual=114000.00
        // EMP-00001 | Alice Sharma                   | monthly=  8000.00 | annual= 96000.00
        // EMP-00003 | Carol Iyer                     | monthly=  4000.00 | annual= 48000.00
        // EMP-00005 | Eve Roy                        | monthly=  1200.00 | annual= 14400.00
        //
        // Employees with monthly pay >= 5,000:
        //   EMP-00001 | Alice Sharma                   | monthly=  8000.00 | annual= 96000.00
        //   EMP-00002 | Bob Patel                      | monthly=  9500.00 | annual=114000.00
        //   EMP-00004 | Dev Studio Inc.                | monthly= 12000.00 | annual=144000.00
        //
        // Full-timers whose names start with 'A':
        //   EMP-00001 | Alice Sharma                   | monthly=  8000.00 | annual= 96000.00
        //
        // --- audit log ---
        // AUDIT EMP-00001 | Alice Sharma                   | monthly=  8000.00 | annual= 96000.00
        // AUDIT EMP-00002 | Bob Patel                      | monthly=  9500.00 | annual=114000.00
        // AUDIT EMP-00003 | Carol Iyer                     | monthly=  4000.00 | annual= 48000.00
        // AUDIT EMP-00005 | Eve Roy                        | monthly=  1200.00 | annual= 14400.00
        //
        // Applying a 10% raise where promotion is supported...
        //   EMP-00001 | Alice Sharma                   | monthly=  8800.00 | annual=105600.00
        //   EMP-00002 | Bob Patel                      | monthly= 10450.00 | annual=125400.00
        //   EMP-00003 | Carol Iyer                     | monthly=  4000.00 | annual= 48000.00     (unchanged)
        //   EMP-00004 | Dev Studio Inc.                | monthly= 12000.00 | annual=144000.00    (unchanged)
        //   EMP-00005 | Eve Roy                        | monthly=  1200.00 | annual= 14400.00    (unchanged)
        //
        // Pay slip details (pattern matching switch):
        //   EMP-00001 (Alice Sharma) -> fixed salary 8000.0
        //   EMP-00002 (Bob Patel) -> fixed salary 9500.0
        //   EMP-00003 (Carol Iyer) -> hours 80.0 x rate 50.0
        //   EMP-00004 (Dev Studio Inc.) -> retainer 12000.0
        //   EMP-00005 (Eve Roy) -> stipend 1200.0
        //
        // Monthly payroll total: 34700.00
    }
}
