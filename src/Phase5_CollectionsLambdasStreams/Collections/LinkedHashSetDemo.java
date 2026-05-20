package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * java.util.LinkedHashSet&lt;E&gt; - "HashSet that Remembers Insertion Order"
 * --------------------------------------------------------------------
 * LinkedHashSet extends HashSet. Internally it keeps a doubly-linked list
 * threading through its entries in the order they were inserted. The
 * hash-table operations are still O(1); the linked list just costs you a
 * few extra pointers per element.
 *
 *
 * Why It Exists
 * -------------
 * Two problems with plain HashSet:
 *   - Iteration order is unpredictable.
 *   - Two runs of the same program may iterate in different orders.
 *
 * LinkedHashSet gives you the FAST membership of HashSet AND a STABLE,
 * insertion-based iteration order - good for logging, UI lists, and
 * deduplicating a stream while preserving the first-seen order.
 *
 *
 * Big-O
 * -----
 *   add / remove / contains / size              O(1)  (same as HashSet)
 *   iteration                                   O(size)
 *
 * (HashSet's iteration is O(capacity + size); LinkedHashSet's iteration
 * cost is proportional to the number of elements, not the bucket array
 * size - a tiny win in sparse sets.)
 *
 *
 * Constructors
 * ------------
 *   new LinkedHashSet&lt;&gt;()
 *   new LinkedHashSet&lt;&gt;(int initialCapacity)
 *   new LinkedHashSet&lt;&gt;(int initialCapacity, float loadFactor)
 *   new LinkedHashSet&lt;&gt;(Collection&lt;? extends E&gt;)
 *
 *
 * No New Methods
 * --------------
 * The API surface is the same as HashSet. The difference is purely the
 * iteration order it guarantees.
 *
 *
 * Java 21 - SequencedSet
 * ----------------------
 * Since Java 21, LinkedHashSet implements the new SequencedSet interface
 * with first/last access methods. See SequencedCollections.java in
 * Basics/ModernJava for the full demo.
 */

public class LinkedHashSetDemo {

    public static void main(String[] args) {

        section("1) Iteration order matches insertion order");
        LinkedHashSet<String> s = new LinkedHashSet<>();
        for (String w : List.of("delta", "alpha", "beta", "alpha", "gamma")) {
            s.add(w);                                   // duplicates collapsed; first seen wins
        }
        System.out.println("LinkedHashSet = " + s);     // [delta, alpha, beta, gamma]

        section("2) HashSet for contrast - unordered");
        HashSet<String> hs = new HashSet<>();
        for (String w : List.of("delta", "alpha", "beta", "alpha", "gamma")) hs.add(w);
        System.out.println("HashSet       = " + hs);    // some order, do not rely on it

        section("3) Deduplicate while keeping first-seen order");
        List<Integer> input = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        LinkedHashSet<Integer> unique = new LinkedHashSet<>(input);
        System.out.println("input  = " + input);
        System.out.println("unique = " + unique);       // [3, 1, 4, 5, 9, 2, 6]

        section("4) All HashSet methods still work");
        LinkedHashSet<Integer> a = new LinkedHashSet<>(List.of(1, 2, 3, 4));
        a.addAll(List.of(5, 6));
        a.removeAll(List.of(2, 4));
        System.out.println("after add+remove = " + a);

        section("5) Iteration is deterministic across runs");
        // Print twice to show the order is stable.
        LinkedHashSet<Integer> stable = new LinkedHashSet<>(List.of(7, 1, 9, 3, 5));
        System.out.print("pass 1: "); stable.forEach(n -> System.out.print(n + " ")); System.out.println();
        System.out.print("pass 2: "); stable.forEach(n -> System.out.print(n + " ")); System.out.println();

        section("6) Java 21 - SequencedSet methods (getFirst / getLast / reversed)");
        // Try / catch in case we're on a pre-21 JVM.
        try {
            java.lang.reflect.Method first = LinkedHashSet.class.getMethod("getFirst");
            java.lang.reflect.Method last  = LinkedHashSet.class.getMethod("getLast");
            java.lang.reflect.Method rev   = LinkedHashSet.class.getMethod("reversed");
            System.out.println("getFirst() = " + first.invoke(stable));
            System.out.println("getLast()  = " + last.invoke(stable));
            System.out.println("reversed() = " + rev.invoke(stable));
        } catch (NoSuchMethodException e) {
            System.out.println("(Java 21+ required for SequencedSet methods - skipped)");
        } catch (Exception e) {
            System.out.println("reflection error: " + e.getMessage());
        }

        section("7) Memory cost - links cost ~ 2 references per element");
        // Roughly: HashSet entry ~ 32 bytes, LinkedHashSet entry ~ 40 bytes.
        // Not a problem until you store millions of small objects.
        System.out.println("(use HashSet when memory is tight and order doesn't matter)");

        // OUTPUT (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
