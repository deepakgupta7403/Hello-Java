package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;

/**
 * java.util.List&lt;E&gt; - Ordered, Indexable, Duplicates Allowed
 * ----------------------------------------------------------
 * List extends Collection with POSITIONAL access - every element has an
 * INTEGER INDEX from 0 to size()-1.
 * <p>
 *
 *      +---+---+---+---+
 *      | A | B | C | D |     List of 4 elements
 *      +---+---+---+---+
 *        0   1   2   3
 * <p>
 *
 * What List Adds Over Collection
 * ------------------------------
 * <p>
 *
 *   Positional access
 *      get(int index)
 *      set(int index, E e)              - replace; returns the old value
 *      add(int index, E e)              - INSERT at index, shifting right
 *      remove(int index)                - REMOVE at index, shifting left
 * <p>
 *
 *   Position-based search
 *      indexOf(Object o)                - first index, or -1
 *      lastIndexOf(Object o)            - last index, or -1
 * <p>
 *
 *   Sub-list views
 *      subList(int from, int to)        - LIVE view, mutations propagate
 * <p>
 *
 *   Iteration
 *      listIterator()                   - bidirectional iterator
 *      listIterator(int index)
 * <p>
 *
 *   Bulk
 *      addAll(int index, Collection)    - insert another collection at a position
 *      replaceAll(UnaryOperator)        - Java 8+
 *      sort(Comparator)                 - Java 8+ (replaces Collections.sort)
 * <p>
 *
 * Implementations
 * ---------------
 *   ArrayList            - resizing array. DEFAULT choice.
 *   LinkedList           - doubly-linked list; also implements Deque.
 *   Vector / Stack       - LEGACY synchronized lists - avoid in new code.
 *   CopyOnWriteArrayList - concurrent, copy on every write. Reads cheap.
 *   List.of(...)         - immutable, no nulls (Java 9+).
 * <p>
 *
 * When to Pick Which
 * ------------------
 *   - Mostly add-at-the-end + random access      ->  ArrayList
 *   - Frequent add/remove in the MIDDLE          ->  LinkedList (rare)
 *   - "Mostly read, occasional write, many readers" -> CopyOnWriteArrayList
 *   - Constant data set at startup                ->  List.of(...)
 * <p>
 *
 * In practice, ArrayList wins almost every benchmark - it is the default.
 */

public class ListInterface {

    public static void main(String[] args) {

        section("1) Creation - five flavours");
        List<String> a1 = new ArrayList<>();                           // mutable empty
        List<String> a2 = new ArrayList<>(List.of("x", "y", "z"));     // mutable from another collection
        List<String> a3 = new LinkedList<>(List.of("x", "y"));         // a LinkedList counts as a List too
        List<String> a4 = Arrays.asList("a", "b", "c");                // FIXED-SIZE view of an array
        List<String> a5 = List.of("a", "b", "c");                       // IMMUTABLE
        System.out.println("ArrayList   = " + a2);
        System.out.println("LinkedList  = " + a3);
        System.out.println("asList      = " + a4 + "  (fixed size: cannot add/remove)");
        System.out.println("List.of     = " + a5 + "  (immutable: cannot mutate at all)");

        section("2) Positional access");
        List<String> letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        System.out.println("get(2)        = " + letters.get(2));
        letters.set(2, "C");
        System.out.println("after set(2,C)= " + letters);
        letters.add(1, "B-new");
        System.out.println("after add(1)  = " + letters);
        letters.remove(0);
        System.out.println("after remove(0)= " + letters);

        section("3) Search by value");
        List<Integer> nums = new ArrayList<>(List.of(10, 20, 30, 20, 40, 20));
        System.out.println("indexOf(20)     = " + nums.indexOf(20));     // 1
        System.out.println("lastIndexOf(20) = " + nums.lastIndexOf(20)); // 5

        section("4) subList - LIVE view, not a copy");
        List<Integer> big   = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> mid   = big.subList(3, 7);                   // [3, 4, 5, 6]
        System.out.println("subList     = " + mid);
        mid.set(0, 99);
        System.out.println("subList edit -> big = " + big);        // 99 appears at index 3

        section("5) listIterator - bidirectional iteration");
        ListIterator<Integer> it = new ArrayList<>(List.of(10, 20, 30, 40)).listIterator();
        while (it.hasNext()) {
            int idx = it.nextIndex();
            int val = it.next();
            System.out.println("  forward  idx=" + idx + " val=" + val);
        }
        // walk back
        while (it.hasPrevious()) {
            int idx = it.previousIndex();
            int val = it.previous();
            System.out.println("  backward idx=" + idx + " val=" + val);
        }

        section("6) replaceAll + sort (Java 8+)");
        List<Integer> r = new ArrayList<>(List.of(5, 1, 4, 2, 3));
        r.replaceAll(n -> n * 10);
        System.out.println("after replaceAll(*10) = " + r);
        r.sort(java.util.Comparator.reverseOrder());
        System.out.println("after sort(desc)      = " + r);

        section("7) Equality - List vs List");
        List<Integer> p = List.of(1, 2, 3);
        List<Integer> q = new ArrayList<>(List.of(1, 2, 3));
        System.out.println("p.equals(q) = " + p.equals(q));        // true (same elements in same order)

        section("8) Stream API on a List");
        int sumOfEven = List.of(1, 2, 3, 4, 5).stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("sum of even = " + sumOfEven);          // 6

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
