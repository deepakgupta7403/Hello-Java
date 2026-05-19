package Basics.LambdaAndStreams.Examples;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Real-World Example 1 - Filtering Employees by Salary
 *
 * Demonstrates: filter, sorted, map, mapToDouble, groupingBy, averaging,
 * Collectors.partitioningBy, toList. The classic "play with a list of
 * Employee records" workout.
 */
public class EmployeeSalaryExample {

    record Employee(String name, String dept, double salary, int yearsOfService) {}

    public static void main(String[] args) {

        List<Employee> staff = List.of(
                new Employee("Alice",  "ENG", 92_000,  4),
                new Employee("Bob",    "ENG", 85_000,  2),
                new Employee("Carol",  "PM",  110_000, 5),
                new Employee("Dave",   "ENG", 75_000,  1),
                new Employee("Eve",    "PM",  120_000, 8),
                new Employee("Fran",   "HR",  68_000,  3),
                new Employee("Greg",   "ENG", 102_000, 6),
                new Employee("Hank",   "PM",   95_000, 4),
                new Employee("Ivy",    "HR",   72_000, 2)
        );

        section("1) Employees earning more than 90k, sorted DESC by salary");
        staff.stream()
             .filter(e -> e.salary() > 90_000)
             .sorted(Comparator.comparingDouble(Employee::salary).reversed())
             .forEach(e -> System.out.printf("  %-7s %-3s  $%,.0f%n",
                     e.name(), e.dept(), e.salary()));

        section("2) Average salary per department");
        Map<String, Double> avgByDept = staff.stream()
                .collect(Collectors.groupingBy(
                        Employee::dept,
                        Collectors.averagingDouble(Employee::salary)));
        avgByDept.forEach((d, a) -> System.out.printf("  %-3s  avg = $%,.0f%n", d, a));

        section("3) Top earner in each department");
        Map<String, Employee> topByDept = staff.stream()
                .collect(Collectors.toMap(
                        Employee::dept,
                        e -> e,
                        (a, b) -> a.salary() >= b.salary() ? a : b));
        topByDept.forEach((d, e) -> System.out.println("  " + d + " -> " + e));

        section("4) Above- vs below-average overall");
        double avg = staff.stream().mapToDouble(Employee::salary).average().orElse(0);
        Map<Boolean, List<Employee>> parts = staff.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() >= avg));
        System.out.printf("  overall avg = $%,.0f%n", avg);
        System.out.println("  above avg = " + parts.get(true).stream().map(Employee::name).toList());
        System.out.println("  below avg = " + parts.get(false).stream().map(Employee::name).toList());

        section("5) Total payroll");
        double total = staff.stream().mapToDouble(Employee::salary).sum();
        System.out.printf("  total = $%,.0f%n", total);

        section("6) Engineers sorted by years of service");
        staff.stream()
             .filter(e -> e.dept().equals("ENG"))
             .sorted(Comparator.comparingInt(Employee::yearsOfService).reversed())
             .forEach(e -> System.out.printf("  %s (%d yrs)%n", e.name(), e.yearsOfService()));

        section("7) Top-3 highest-paid - using limit");
        staff.stream()
             .sorted(Comparator.comparingDouble(Employee::salary).reversed())
             .limit(3)
             .forEach(e -> System.out.printf("  %s  $%,.0f%n", e.name(), e.salary()));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
