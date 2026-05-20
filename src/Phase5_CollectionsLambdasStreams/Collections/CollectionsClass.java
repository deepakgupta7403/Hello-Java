package Phase5_CollectionsLambdasStreams.Collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * java.util.Collections - The Utility Class
 * -----------------------------------------
 * Easy to confuse with Collection (interface).
 *      java.util.Collection&lt;E&gt;   - the INTERFACE every collection implements.
 *      java.util.Collections     - a CLASS of static helpers that work on
 *                                  those collections.
 *
 * It is the rough equivalent of java.util.Arrays but for collections.
 *
 *
 * Methods Grouped By Purpose
 * --------------------------
 *
 *   Sorting & ordering
 *      sort(List)
 *      sort(List, Comparator)
 *      reverse(List)
 *      shuffle(List)
 *      shuffle(List, Random)
 *      rotate(List, int distance)
 *      swap(List, int i, int j)
 *
 *   Searching
 *      binarySearch(List, key)
 *      binarySearch(List, key, Comparator)
 *      min(Collection)
 *      max(Collection)
 *      min/max(Collection, Comparator)
 *      frequency(Collection, Object)
 *      disjoint(Collection, Collection)
 *
 *   Population / fill
 *      fill(List, element)
 *      nCopies(int, element)             - immutable list of N copies
 *      replaceAll(List, oldVal, newVal)
 *      copy(List dest, List src)
 *
 *   Wrappers (return VIEWS that wrap the original)
 *      unmodifiableList / Set / Map / Collection / SortedSet / SortedMap
 *      synchronizedList / Set / Map / Collection / SortedSet / SortedMap
 *      checkedList / Set / Map ...       - dynamic type-check wrapper
 *      emptyList / emptySet / emptyMap   - return shared immutable empties
 *      singletonList / singleton / singletonMap
 *
 *   Conversion / decoration
 *      addAll(Collection, T...)
 *      list(Enumeration)                 - bridge from legacy Enumeration
 *      enumeration(Collection)           - bridge to legacy Enumeration
 *      newSetFromMap(Map)                - back a Set with any Map
 *      asLifoQueue(Deque)                - stack-style view of a Deque
 *
 *
 * Modern Alternatives (Java 9+)
 * -----------------------------
 *   - List.of(...), Set.of(...), Map.of(...) - immutable, no nulls allowed.
 *   - List.copyOf, Set.copyOf, Map.copyOf    - immutable snapshots.
 * These are smaller and clearer than Collections.unmodifiable*.
 */

public class CollectionsClass {

    public static void main(String[] args) {

        section("1) sort / reverse / shuffle / rotate / swap");
        List<Integer> a = new ArrayList<>(List.of(5, 3, 9, 1, 7, 2, 8));
        Collections.sort(a);
        System.out.println("sort         = " + a);
        Collections.reverse(a);
        System.out.println("reverse      = " + a);
        Collections.shuffle(a, new java.util.Random(42));   // seeded for reproducibility
        System.out.println("shuffle      = " + a);
        Collections.rotate(a, 2);
        System.out.println("rotate(+2)   = " + a);
        Collections.swap(a, 0, 1);
        System.out.println("swap(0,1)    = " + a);

        section("2) Custom comparator via sort(List, Comparator)");
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "bob", "Dave"));
        Collections.sort(names, Comparator.comparingInt(String::length).thenComparing(String::compareTo));
        System.out.println("by length, then alpha = " + names);

        section("3) Searching - binarySearch, min, max, frequency");
        List<Integer> sorted = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        System.out.println("binarySearch(5)   = " + Collections.binarySearch(sorted, 5));     // 4
        System.out.println("binarySearch(10)  = " + Collections.binarySearch(sorted, 10));    // negative
        System.out.println("min  = " + Collections.min(sorted));
        System.out.println("max  = " + Collections.max(sorted));
        List<Integer> dupes = List.of(1, 2, 2, 3, 2, 4);
        System.out.println("frequency(2)      = " + Collections.frequency(dupes, 2));
        System.out.println("disjoint(1..3, 5..6)= "
                + Collections.disjoint(List.of(1, 2, 3), List.of(5, 6)));

        section("4) Population - fill / nCopies / replaceAll / copy");
        List<String> blanks = new ArrayList<>(Arrays.asList(new String[5]));
        Collections.fill(blanks, "-");
        System.out.println("fill          = " + blanks);

        List<String> three = Collections.nCopies(3, "hi");
        System.out.println("nCopies(3, hi)= " + three);
        try { three.add("bye"); }
        catch (UnsupportedOperationException e) {
            System.out.println("  nCopies result is IMMUTABLE");
        }

        List<Integer> r = new ArrayList<>(List.of(1, 2, 1, 3, 1));
        Collections.replaceAll(r, 1, 99);
        System.out.println("replaceAll(1->99) = " + r);

        List<Integer> src  = List.of(7, 8, 9);
        List<Integer> dest = new ArrayList<>(List.of(0, 0, 0, 0, 0));
        Collections.copy(dest, src);
        System.out.println("copy(dest,src)    = " + dest);

        section("5) Wrappers");
        // Unmodifiable view - read-only
        List<Integer> mutable    = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> readOnly   = Collections.unmodifiableList(mutable);
        try { readOnly.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("unmodifiableList - add() rejected");
        }
        // BUT the wrapper is a VIEW - changes to `mutable` show through.
        mutable.add(4);
        System.out.println("readOnly sees backing change = " + readOnly);

        // Synchronized wrapper - thread-safe coarse-grained
        List<Integer> sync = Collections.synchronizedList(new ArrayList<>(List.of(1, 2, 3)));
        synchronized (sync) {     // still need to sync for iteration!
            for (int n : sync) System.out.print(n + " ");
            System.out.println();
        }

        // Singletons + empties
        System.out.println("emptyList()      = " + Collections.emptyList());
        System.out.println("singletonList(99)= " + Collections.singletonList(99));

        section("6) Modern equivalents - List.of, Set.of, Map.of");
        List<Integer> imm = List.of(1, 2, 3);
        System.out.println("List.of      = " + imm);
        System.out.println("Set.of       = " + java.util.Set.of(1, 2, 3));
        System.out.println("Map.of       = " + java.util.Map.of("a", 1, "b", 2));

        section("7) addAll varargs - shorthand");
        List<Integer> dst = new ArrayList<>();
        Collections.addAll(dst, 1, 2, 3, 4, 5);
        System.out.println("addAll varargs -> " + dst);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
