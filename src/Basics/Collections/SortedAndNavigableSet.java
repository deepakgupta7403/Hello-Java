package Basics.Collections;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * SortedSet and NavigableSet - Two Layered Interfaces on Top of Set
 * -----------------------------------------------------------------
 * The Set hierarchy actually has three levels:
 *
 *      Set            (no order at all)
 *      |
 *      v
 *      SortedSet      (elements come out in an order; range queries)
 *      |
 *      v
 *      NavigableSet   (SortedSet + neighbour queries + descending view)
 *
 * Both extra interfaces exist primarily to give SORTED set implementations
 * a richer contract. TreeSet implements NavigableSet (and therefore
 * SortedSet); ConcurrentSkipListSet does too.
 *
 *
 * SortedSet&lt;E&gt; - Methods Added Over Set
 * --------------------------------------
 *      Comparator&lt;? super E&gt; comparator()    - the order rule; null = natural
 *      E first()                              - smallest element (throws if empty)
 *      E last()                               - largest  element (throws if empty)
 *      SortedSet&lt;E&gt; headSet(E toEl)          - strictly less than toEl
 *      SortedSet&lt;E&gt; tailSet(E fromEl)        - fromEl inclusive
 *      SortedSet&lt;E&gt; subSet(E fromEl, E toEl) - fromEl inclusive, toEl exclusive
 *
 *
 * NavigableSet&lt;E&gt; - Methods Added Over SortedSet
 * ----------------------------------------------
 *   Neighbour queries (return null if not found):
 *      E lower(E e)        - greatest element STRICTLY LESS than e
 *      E floor(E e)        - greatest element &lt;= e
 *      E ceiling(E e)      - smallest element &gt;= e
 *      E higher(E e)       - smallest element STRICTLY GREATER than e
 *
 *   Polling (remove and return):
 *      E pollFirst()       - remove and return smallest, or null
 *      E pollLast()        - remove and return largest, or null
 *
 *   Reverse views:
 *      NavigableSet&lt;E&gt; descendingSet()
 *      Iterator&lt;E&gt; descendingIterator()
 *
 *   Inclusive-flag range views:
 *      NavigableSet&lt;E&gt; headSet(E toEl, boolean inclusive)
 *      NavigableSet&lt;E&gt; tailSet(E fromEl, boolean inclusive)
 *      NavigableSet&lt;E&gt; subSet(E from, boolean fromInclusive,
 *                              E to, boolean toInclusive)
 *
 *
 * Why Have Both?
 * --------------
 * SortedSet has been around since Java 1.2. NavigableSet (added in Java 6)
 * is the strict superset with the more useful "find a neighbour" API. New
 * code is encouraged to type variables as NavigableSet whenever feasible -
 * you get the full toolkit and can still pass to APIs that accept SortedSet
 * or plain Set.
 *
 *
 * Implementations
 * ---------------
 *   TreeSet                        - the everyday choice. Red-black tree.
 *   ConcurrentSkipListSet          - same operations, thread-safe.
 */

public class SortedAndNavigableSet {

    public static void main(String[] args) {

        section("1) SortedSet methods on a TreeSet");
        SortedSet<Integer> s = new TreeSet<>(java.util.List.of(50, 10, 30, 40, 20));
        System.out.println("set            = " + s);
        System.out.println("comparator()   = " + s.comparator());  // null -> natural order
        System.out.println("first()        = " + s.first());
        System.out.println("last()         = " + s.last());
        System.out.println("headSet(30)    = " + s.headSet(30));   // [10, 20]
        System.out.println("tailSet(30)    = " + s.tailSet(30));   // [30, 40, 50]
        System.out.println("subSet(20,40)  = " + s.subSet(20, 40));// [20, 30]

        section("2) Custom Comparator changes 'sorted' meaning");
        SortedSet<String> byLen = new TreeSet<>(Comparator.comparingInt(String::length));
        byLen.add("kiwi");
        byLen.add("fig");
        byLen.add("date");
        byLen.add("apple");
        System.out.println("byLen = " + byLen);
        System.out.println("comparator = " + byLen.comparator());

        section("3) NavigableSet - the neighbour queries");
        NavigableSet<Integer> ns = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));
        System.out.println("lower(30)      = " + ns.lower(30));    // 20  (strict)
        System.out.println("floor(30)      = " + ns.floor(30));    // 30  (inclusive)
        System.out.println("ceiling(30)    = " + ns.ceiling(30));  // 30  (inclusive)
        System.out.println("higher(30)     = " + ns.higher(30));   // 40  (strict)
        System.out.println("lower(0)       = " + ns.lower(0));     // null - none

        section("4) Polling - remove and return the extremes");
        System.out.println("pollFirst()    = " + ns.pollFirst() + "  set=" + ns);
        System.out.println("pollLast()     = " + ns.pollLast()  + "  set=" + ns);

        section("5) Inclusive-flag range views");
        NavigableSet<Integer> ns2 = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));
        System.out.println("headSet(30, true)        = " + ns2.headSet(30, true));      // [10,20,30]
        System.out.println("headSet(30, false)       = " + ns2.headSet(30, false));     // [10,20]
        System.out.println("subSet(20,true,40,true)  = " + ns2.subSet(20, true, 40, true));    // [20,30,40]
        System.out.println("subSet(20,false,40,true) = " + ns2.subSet(20, false, 40, true));   // [30,40]

        section("6) descendingSet - LIVE reverse view");
        NavigableSet<Integer> rev = ns2.descendingSet();
        System.out.println("ns2.descendingSet() = " + rev);
        rev.pollFirst();                             // removes the LARGEST in ns2
        System.out.println("ns2 after rev.pollFirst() = " + ns2);

        section("7) Practical use - quickest \"smallest score &gt;= 60\"");
        NavigableSet<Integer> scores = new TreeSet<>(java.util.List.of(42, 55, 67, 72, 88, 91));
        Integer pass = scores.ceiling(60);
        System.out.println("smallest passing score = " + pass);   // 67

        section("8) Reverse iteration with descendingIterator");
        java.util.Iterator<Integer> it = ns2.descendingIterator();
        System.out.print("desc iter: ");
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
