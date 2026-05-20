package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * java.util.Set&lt;E&gt; - No Duplicates Allowed
 * ----------------------------------------
 * Set extends Collection but ADDS NO NEW METHODS. The contract is:
 *
 *      "this collection contains NO duplicate elements"
 *
 * What "duplicate" means is defined by .equals(...) - two elements are
 * considered duplicates if a.equals(b). This means YOUR custom classes
 * need a correct equals/hashCode for HashSet to work right.
 *
 *
 * The Set Family
 * --------------
 *   HashSet         - hash-table backed.  ORDER NOT GUARANTEED.  O(1) ops.
 *   LinkedHashSet   - hash table + linked list. INSERTION ORDER.  O(1) ops.
 *   TreeSet         - red-black tree. SORTED ORDER.  O(log n) ops.
 *
 *   Set.of(...)     - immutable, no nulls (Java 9+).
 *   EnumSet         - super-fast Set for enum keys; bit-vector internally.
 *   CopyOnWriteArraySet - concurrent; backed by COW array.
 *
 *
 * SortedSet + NavigableSet (extra interfaces)
 * -------------------------------------------
 *   SortedSet methods:    first(), last(), headSet, tailSet, subSet
 *   NavigableSet adds:    floor(e), ceiling(e), lower(e), higher(e),
 *                         pollFirst(), pollLast(), descendingSet()
 *
 *
 * When To Use Which
 * -----------------
 *   - No order needed, just uniqueness            -> HashSet (default).
 *   - Need to iterate in insertion order          -> LinkedHashSet.
 *   - Need elements always sorted                 -> TreeSet.
 *   - All elements are from one enum              -> EnumSet (fastest).
 *
 *
 * Important Contract - equals/hashCode
 * ------------------------------------
 * If you put your own classes in a HashSet, you MUST override hashCode and
 * equals consistently. If two equal objects have different hash codes, they
 * end up in different buckets and the set will hold both - the "duplicates"
 * the Set promised to prevent.
 */

public class SetInterface {

    record Person(String name) {}

    public static void main(String[] args) {

        section("1) Three implementations - same data, different behaviour");
        var input = java.util.List.of("banana", "apple", "cherry", "apple", "banana");
        Set<String> hash    = new HashSet<>(input);
        Set<String> linked  = new LinkedHashSet<>(input);
        Set<String> tree    = new TreeSet<>(input);
        System.out.println("HashSet       (no order)         = " + hash);
        System.out.println("LinkedHashSet (insertion order)  = " + linked);
        System.out.println("TreeSet       (sorted)           = " + tree);

        section("2) add returns true/false depending on duplicate");
        Set<Integer> s = new HashSet<>();
        System.out.println("add 1 = " + s.add(1));      // true
        System.out.println("add 1 = " + s.add(1));      // false - duplicate
        System.out.println("set   = " + s);

        section("3) Set algebra - union / intersection / difference");
        Set<Integer> a = new HashSet<>(java.util.List.of(1, 2, 3, 4));
        Set<Integer> b = new HashSet<>(java.util.List.of(3, 4, 5, 6));

        Set<Integer> union        = new HashSet<>(a); union.addAll(b);
        Set<Integer> intersection = new HashSet<>(a); intersection.retainAll(b);
        Set<Integer> difference   = new HashSet<>(a); difference.removeAll(b);

        System.out.println("a u b = " + union);          // {1,2,3,4,5,6}
        System.out.println("a n b = " + intersection);   // {3,4}
        System.out.println("a \\ b = " + difference);    // {1,2}

        section("4) SortedSet / NavigableSet methods");
        NavigableSet<Integer> ns = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));
        System.out.println("first()       = " + ns.first());
        System.out.println("last()        = " + ns.last());
        System.out.println("headSet(30)   = " + ns.headSet(30));        // strictly less
        System.out.println("tailSet(30)   = " + ns.tailSet(30));        // 30 included
        System.out.println("subSet(20,40) = " + ns.subSet(20, 40));
        System.out.println("floor(25)     = " + ns.floor(25));          // 20
        System.out.println("ceiling(25)   = " + ns.ceiling(25));        // 30
        System.out.println("lower(30)     = " + ns.lower(30));          // 20
        System.out.println("higher(30)    = " + ns.higher(30));         // 40
        System.out.println("descending    = " + ns.descendingSet());

        section("5) equals/hashCode contract demo (records do it for you)");
        Set<Person> people = new HashSet<>();
        people.add(new Person("Alice"));
        people.add(new Person("Alice"));         // record equals/hashCode collapses duplicate
        people.add(new Person("Bob"));
        System.out.println("people = " + people + "  (size=" + people.size() + ")");

        section("6) Immutable Set.of(...)");
        Set<Integer> imm = Set.of(1, 2, 3);
        try { imm.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("Set.of(...) is immutable - add() rejected");
        }

        section("7) For-each iteration order is implementation-specific");
        SortedSet<Integer> sorted = new TreeSet<>(java.util.List.of(3, 1, 4, 1, 5, 9, 2, 6));
        System.out.print("TreeSet iteration = ");
        for (int n : sorted) System.out.print(n + " ");
        System.out.println();

        // OUTPUT (representative; HashSet ordering may differ on your run)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
