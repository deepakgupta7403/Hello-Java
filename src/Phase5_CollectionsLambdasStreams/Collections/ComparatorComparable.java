package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;

/**
 * Comparable vs Comparator
 * ------------------------
 * Two interfaces in the JDK control how objects get ordered. They look
 * similar but have different jobs.
 * <p>
 *
 * Comparable&lt;T&gt;  -  "I know how I compare to other Ts"
 * --------------------------------------------------
 *   - Defined ON the class whose objects need an order.
 *   - One abstract method:  int compareTo(T other)
 *   - Used as the NATURAL ORDERING by Collections.sort, TreeSet, TreeMap.
 * <p>
 *
 *      class Money implements Comparable&lt;Money&gt; {
 *          public int compareTo(Money o) { ... }
 *      }
 * <p>
 *
 * Comparator&lt;T&gt;  -  "External rule for ordering Ts"
 * -------------------------------------------------
 *   - Defined OUTSIDE the class - any number of comparators per type.
 *   - One abstract method:  int compare(T a, T b)
 *   - Lets the SAME class be sorted multiple different ways.
 * <p>
 *
 *      Comparator&lt;Person&gt; byName    = Comparator.comparing(Person::name);
 *      Comparator&lt;Person&gt; byAgeDesc = Comparator.comparingInt(Person::age).reversed();
 * <p>
 *
 * compareTo / compare Return Value Contract
 * -----------------------------------------
 *   negative -> this object is LESS THAN the other
 *   zero      -> equal in the ordering
 *   positive  -> this object is GREATER THAN the other
 * <p>
 *
 * Implementations MUST be:
 *   - REFLEXIVE:    a.compareTo(a) == 0
 *   - ANTI-SYMMETRIC: a.compareTo(b) and b.compareTo(a) have opposite signs
 *   - TRANSITIVE:   if a &lt; b and b &lt; c then a &lt; c
 *   - CONSISTENT with equals(): strongly recommended but not required
 * <p>
 *
 * Comparator Combinators (Java 8+)
 * --------------------------------
 *      Comparator.comparing(keyExtractor)
 *      Comparator.comparingInt / comparingLong / comparingDouble
 *      cmp.reversed()
 *      cmp.thenComparing(otherCmp)
 *      cmp.thenComparing(keyExtractor)
 *      Comparator.naturalOrder() / reverseOrder()
 *      Comparator.nullsFirst(cmp) / nullsLast(cmp)
 * <p>
 *
 * When To Use Which
 * -----------------
 *   - You own the class AND there is ONE natural ordering    ->  Comparable.
 *   - You don't own the class, or you need MULTIPLE orderings ->  Comparator.
 *   - You have BOTH                                          ->  perfectly fine.
 * <p>
 *
 * Common Pitfall - subtraction in compare()
 * -----------------------------------------
 *      return a.age - b.age;       // can OVERFLOW for very large ints
 *      return Integer.compare(a.age, b.age);     // safe and clearer
 */

public class ComparatorComparable {

    // ============================================================
    // 1) A class with NATURAL ordering via Comparable
    // ============================================================
    static class Person implements Comparable<Person> {
        final String name;
        final int    age;
        Person(String name, int age) { this.name = name; this.age = age; }

        /** Natural order: by name, alphabetical. */
        @Override public int compareTo(Person o) {
            return this.name.compareTo(o.name);
        }
        @Override public String toString() { return name + "(" + age + ")"; }
    }

    public static void main(String[] args) {

        section("1) Comparable - the natural ordering");
        List<Person> people = new ArrayList<>(List.of(
                new Person("Charlie", 30),
                new Person("Alice",   28),
                new Person("Bob",     34)
        ));
        java.util.Collections.sort(people);
        System.out.println("by natural order = " + people);

        section("2) Comparator - external rules");
        Comparator<Person> byAge      = Comparator.comparingInt(p -> p.age);
        Comparator<Person> byAgeDesc  = byAge.reversed();
        Comparator<Person> byNameLen  = Comparator.comparingInt(p -> p.name.length());

        people.sort(byAge);
        System.out.println("by age           = " + people);
        people.sort(byAgeDesc);
        System.out.println("by age desc      = " + people);
        people.sort(byNameLen.thenComparing(p -> p.name));
        System.out.println("by name length   = " + people);

        section("3) Chaining + null-safety");
        List<String> withNulls = Arrays.asList("banana", null, "apple", null, "cherry");
        withNulls.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("nullsFirst sort = " + withNulls);

        withNulls.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        System.out.println("nullsLast  sort = " + withNulls);

        section("4) Method reference + naturalOrder / reverseOrder");
        List<Integer> nums = new ArrayList<>(List.of(5, 1, 4, 2, 3));
        nums.sort(Comparator.naturalOrder());
        System.out.println("asc  = " + nums);
        nums.sort(Comparator.reverseOrder());
        System.out.println("desc = " + nums);

        section("5) TreeSet picks up Comparable automatically");
        // Without a Comparator, TreeSet uses Person.compareTo (i.e. by name).
        TreeSet<Person> sortedByName = new TreeSet<>(people);
        sortedByName.forEach(p -> System.out.println("  " + p));

        section("6) TreeSet overriding the natural order with a Comparator");
        TreeSet<Person> sortedByAge = new TreeSet<>(byAge);
        sortedByAge.addAll(people);
        sortedByAge.forEach(p -> System.out.println("  " + p));

        section("7) The classic interview trap - subtraction overflow");
        int big = Integer.MAX_VALUE;
        int small = -10;
        // Naive subtraction:
        System.out.println("big - small =       " + (big - small));            // overflow
        // Safe:
        System.out.println("Integer.compare(big, small) = " + Integer.compare(big, small));

        section("8) Stream.sorted with a Comparator");
        people.stream()
              .sorted(Comparator.comparingInt(p -> p.age))
              .forEach(p -> System.out.println("  " + p));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
