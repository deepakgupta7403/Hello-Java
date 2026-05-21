package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;

/**
 * java.util.TreeSet&lt;E&gt; - Sorted Set Backed by a Red-Black Tree
 * ------------------------------------------------------------
 * TreeSet keeps its elements in SORTED ORDER, using either:
 *   - the natural ordering (E must implement Comparable&lt;E&gt;), or
 *   - a Comparator&lt;E&gt; you provide at construction.
 * <p>
 *
 * Why It Exists
 * -------------
 *   - Iteration is always in sorted order, no extra sort needed.
 *   - Range queries (subSet / headSet / tailSet) are O(log n).
 *   - "Nearest neighbour" lookups (floor / ceiling / lower / higher) are
 *     O(log n) and unique to TreeSet.
 * <p>
 *
 * When To Use It
 * --------------
 *   - You need elements in sorted order on every iteration.
 *   - You ask range / neighbour questions: "all events between 10 AM and
 *     noon", "smallest score >= 60", "largest price under $100".
 * <p>
 *
 *   For pure "is this in my set?" with no ordering needs, HashSet is
 *   faster: O(1) vs O(log n).
 * <p>
 *
 * Big-O
 * -----
 *   add / remove / contains                      O(log n)
 *   iteration (in order)                         O(n)
 *   first / last / floor / ceiling / lower / higher   O(log n)
 *   range views (sub / head / tail)              O(log n) to create, O(k) to iterate
 * <p>
 *
 * Constructors
 * ------------
 *   new TreeSet&lt;&gt;()
 *   new TreeSet&lt;&gt;(Comparator&lt;? super E&gt;)
 *   new TreeSet&lt;&gt;(Collection&lt;? extends E&gt;)
 *   new TreeSet&lt;&gt;(SortedSet&lt;E&gt;)
 * <p>
 *
 * Key Methods (Beyond the Inherited Set Contract)
 * -----------------------------------------------
 *   - SortedSet:
 *        first() / last()
 *        headSet(toE) - strictly less
 *        tailSet(fromE) - fromE inclusive
 *        subSet(fromE, toE) - fromE inclusive, toE exclusive
 *        comparator()
 * <p>
 *
 *   - NavigableSet:
 *        floor(e)   - greatest element &lt;= e
 *        ceiling(e) - smallest element &gt;= e
 *        lower(e)   - greatest element &lt; e
 *        higher(e)  - smallest element &gt; e
 *        pollFirst() / pollLast()
 *        descendingIterator() / descendingSet()
 *        navigableHeadSet / Tail / SubSet (with inclusive flags)
 */

public class TreeSetDemo {

    record Person(String name, int age) implements Comparable<Person> {
        @Override
        public int compareTo(Person o) {
            // Natural order: by age first, then by name.
            int c = Integer.compare(age, o.age);
            return c != 0 ? c : name.compareTo(o.name);
        }
    }

    public static void main(String[] args) {

        section("1) Natural-order sort of primitives");
        TreeSet<Integer> nums = new TreeSet<>(List.of(5, 1, 4, 2, 3, 1));
        System.out.println("set        = " + nums);                  // [1, 2, 3, 4, 5]
        System.out.println("first()    = " + nums.first());
        System.out.println("last()     = " + nums.last());
        System.out.println("size       = " + nums.size());

        section("2) Custom comparator at construction");
        TreeSet<String> byLen = new TreeSet<>(
                Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
        );
        byLen.add("date");
        byLen.add("fig");
        byLen.add("apple");
        byLen.add("kiwi");
        System.out.println("by length  = " + byLen);

        section("3) Sub-set views - LIVE views, share state with the parent");
        NavigableSet<Integer> ns = new TreeSet<>(List.of(10, 20, 30, 40, 50, 60));
        SortedSet<Integer> head = ns.headSet(30);          // [10, 20]
        SortedSet<Integer> tail = ns.tailSet(30);          // [30, 40, 50, 60]
        SortedSet<Integer> sub  = ns.subSet(20, 50);       // [20, 30, 40]
        System.out.println("headSet(30)= " + head);
        System.out.println("tailSet(30)= " + tail);
        System.out.println("subSet(20,50)= " + sub);
        sub.add(25);                                       // mutates ns too
        System.out.println("ns after sub.add(25) = " + ns);

        section("4) Neighbour queries on NavigableSet");
        System.out.println("floor(35)  = " + ns.floor(35));      // 30
        System.out.println("ceiling(35)= " + ns.ceiling(35));    // 40
        System.out.println("lower(30)  = " + ns.lower(30));      // 25
        System.out.println("higher(30) = " + ns.higher(30));     // 40

        section("5) pollFirst / pollLast - remove and return");
        System.out.println("pollFirst()= " + ns.pollFirst() + "  set=" + ns);
        System.out.println("pollLast() = " + ns.pollLast()  + "  set=" + ns);

        section("6) descendingSet - reverse view");
        TreeSet<Integer> small = new TreeSet<>(List.of(3, 1, 2));
        System.out.println("ascending  = " + small);
        System.out.println("descending = " + small.descendingSet());

        section("7) Custom objects via Comparable");
        TreeSet<Person> people = new TreeSet<>();
        people.add(new Person("Alice", 30));
        people.add(new Person("Bob",   25));
        people.add(new Person("Carol", 30));
        people.add(new Person("Dave",  22));
        for (Person p : people) System.out.println("  " + p);

        section("8) Custom objects via Comparator (override natural order)");
        TreeSet<Person> byNameDesc = new TreeSet<>(Comparator.comparing(Person::name).reversed());
        byNameDesc.addAll(people);
        byNameDesc.forEach(p -> System.out.println("  " + p));

        section("9) Range queries on objects");
        Person low  = new Person("",  25);
        Person high = new Person("z", 30);          // pick boundary values
        // Find everyone aged 25..29 inclusive
        SortedSet<Person> range = people.subSet(low, high);
        range.forEach(p -> System.out.println("  in range: " + p));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
