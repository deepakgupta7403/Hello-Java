package Phase5_CollectionsLambdasStreams.LambdaAndStreams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * java.util.stream.Collectors - The Collect-Toolbox
 * -------------------------------------------------
 * The Collectors class is a giant library of reusable collectors for use
 * with `stream.collect(...)`. Almost every "build a structured result"
 * task in stream code uses one.
 *
 *
 * The Cheatsheet
 * --------------
 *
 *   Building collections
 *      toList()                          mutable List (Java 8) - prefer Stream.toList() for unmodifiable
 *      toSet()
 *      toUnmodifiableList / Set / Map    immutable (Java 10+)
 *      toCollection(Supplier)            specific collection type
 *
 *   Maps
 *      toMap(keyFn, valueFn)
 *      toMap(keyFn, valueFn, mergeFn)              resolve key conflicts
 *      toMap(keyFn, valueFn, mergeFn, mapFactory)  pick the Map impl
 *      toConcurrentMap(...)
 *
 *   Grouping
 *      groupingBy(classifier)
 *      groupingBy(classifier, downstreamCollector)
 *      groupingBy(classifier, mapFactory, downstreamCollector)
 *      partitioningBy(predicate)                   true/false split
 *      partitioningBy(predicate, downstreamCollector)
 *      groupingByConcurrent(...)
 *
 *   Counting + summarising
 *      counting()
 *      summingInt / summingLong / summingDouble
 *      averagingInt / averagingLong / averagingDouble
 *      summarizingInt / summarizingLong / summarizingDouble
 *      maxBy(Comparator), minBy(Comparator)
 *      reducing(...)
 *
 *   String building
 *      joining()
 *      joining(separator)
 *      joining(separator, prefix, suffix)
 *
 *   Composing
 *      mapping(mapper, downstream)
 *      collectingAndThen(downstream, finisher)
 *      filtering(predicate, downstream)            (Java 9+)
 *      flatMapping(mapper, downstream)             (Java 9+)
 *      teeing(c1, c2, combiner)                    (Java 12+, very powerful)
 *
 *
 * The "downstream" Pattern
 * ------------------------
 * Many collectors take a "downstream" collector that processes the values
 * inside each group. That is how you compose interesting analytics:
 *
 *      groupingBy(department, summingDouble(Employee::salary))
 *      groupingBy(department, mapping(Employee::name, toList()))
 */

public class CollectorsClass {

    record Person(String name, String dept, double salary) {}

    public static void main(String[] args) {

        List<Person> staff = List.of(
                new Person("Alice", "ENG", 90_000),
                new Person("Bob",   "ENG", 85_000),
                new Person("Carol", "PM",  110_000),
                new Person("Dave",  "ENG", 95_000),
                new Person("Eve",   "PM",  105_000),
                new Person("Fran",  "HR",  80_000)
        );

        section("1) toList / toSet / toMap");
        List<String> names = staff.stream().map(Person::name).collect(Collectors.toList());
        java.util.Set<String> depts = staff.stream().map(Person::dept).collect(Collectors.toSet());
        Map<String, Double> salaryByName = staff.stream()
                .collect(Collectors.toMap(Person::name, Person::salary));
        System.out.println("names = " + names);
        System.out.println("depts = " + depts);
        System.out.println("salaryByName = " + salaryByName);

        section("2) toMap with a MERGE function (resolves duplicate keys)");
        // Dummy duplicate-key scenario - sum all salaries per name (if dupes existed).
        Map<String, Double> bySumOnDup = staff.stream()
                .collect(Collectors.toMap(Person::name, Person::salary, Double::sum));
        System.out.println("merge-form toMap = " + bySumOnDup);

        section("3) toMap with a map FACTORY - choose TreeMap for sorted keys");
        Map<String, Double> sortedByName = staff.stream()
                .collect(Collectors.toMap(Person::name, Person::salary, Double::sum, TreeMap::new));
        System.out.println("sorted = " + sortedByName);

        section("4) groupingBy - the workhorse");
        Map<String, List<Person>> byDept = staff.stream()
                .collect(Collectors.groupingBy(Person::dept));
        byDept.forEach((d, list) -> System.out.println(d + " -> " + list));

        section("5) groupingBy + DOWNSTREAM collector");
        // headcount per department
        Map<String, Long> headcount = staff.stream()
                .collect(Collectors.groupingBy(Person::dept, Collectors.counting()));
        System.out.println("headcount = " + headcount);

        // total salary per department
        Map<String, Double> totalSalary = staff.stream()
                .collect(Collectors.groupingBy(
                        Person::dept,
                        Collectors.summingDouble(Person::salary)));
        System.out.println("totalSalary = " + totalSalary);

        // names per department - mapping(...)
        Map<String, List<String>> namesByDept = staff.stream()
                .collect(Collectors.groupingBy(
                        Person::dept,
                        Collectors.mapping(Person::name, Collectors.toList())));
        System.out.println("namesByDept = " + namesByDept);

        section("6) partitioningBy - true/false split");
        Map<Boolean, List<Person>> highEarner = staff.stream()
                .collect(Collectors.partitioningBy(p -> p.salary() >= 100_000));
        System.out.println("highEarner=true : " + highEarner.get(true));
        System.out.println("highEarner=false: " + highEarner.get(false));

        section("7) summarizingDouble - count/sum/min/max/avg in one pass");
        java.util.DoubleSummaryStatistics stats = staff.stream()
                .collect(Collectors.summarizingDouble(Person::salary));
        System.out.println("stats = " + stats);

        section("8) joining - string builder for streams");
        String roster = staff.stream().map(Person::name).collect(Collectors.joining(", ", "[", "]"));
        System.out.println("roster = " + roster);

        section("9) collectingAndThen - finisher transformation");
        // Make a sorted, IMMUTABLE list in one collect call.
        List<String> sortedImmutable = staff.stream()
                .map(Person::name)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> { java.util.Collections.sort(list); return List.copyOf(list); }
                ));
        System.out.println("sortedImmutable = " + sortedImmutable);

        section("10) filtering / flatMapping (Java 9+) - downstream filters");
        Map<String, Long> engPlusPmCount = staff.stream()
                .collect(Collectors.groupingBy(
                        Person::dept,
                        Collectors.filtering(p -> p.salary() >= 90_000, Collectors.counting())
                ));
        System.out.println("engPlusPmCount (>=90k) = " + engPlusPmCount);

        section("11) teeing (Java 12+) - split into TWO collectors, combine");
        // Compute average AND max salary in one pass and combine the result.
        record Stats(double avg, double max) {}
        Stats result = staff.stream().collect(Collectors.teeing(
                Collectors.averagingDouble(Person::salary),
                Collectors.maxBy(java.util.Comparator.comparingDouble(Person::salary)),
                (avg, maxOpt) -> new Stats(avg, maxOpt.map(Person::salary).orElse(0d))
        ));
        System.out.println("teeing -> " + result);

        section("12) toUnmodifiable* / toConcurrentMap");
        List<String> immutableNames = staff.stream().map(Person::name)
                .collect(Collectors.toUnmodifiableList());
        ConcurrentMap<String, Long> conMap = staff.stream()
                .collect(Collectors.groupingByConcurrent(Person::dept, Collectors.counting()));
        System.out.println("immutableNames = " + immutableNames);
        System.out.println("conMap         = " + conMap);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused")
    private static Stream<?> keepImport;
    @SuppressWarnings("unused")
    private static Optional<?> keepImport2;
}
