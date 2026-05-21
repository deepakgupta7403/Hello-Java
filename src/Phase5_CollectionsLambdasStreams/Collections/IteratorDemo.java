package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;

/**
 * Iterator and Friends
 * --------------------
 * An ITERATOR is a cursor that walks through a collection one element at a
 * time. Java has three flavours, each more powerful than the previous:
 * <p>
 *
 *      Iterator&lt;E&gt;          - one-way, basic
 *      ListIterator&lt;E&gt;      - bidirectional, position-aware, mutating
 *      Spliterator&lt;E&gt;       - splittable, used by Stream parallelism
 * <p>
 *
 * Iterator&lt;E&gt; - The Original
 * --------------------------
 * Methods:
 *      hasNext()              - is there another element?
 *      next()                 - return the next element (throws NoSuchElement if none)
 *      remove()               - delete the element just returned by next()
 *                                (optional - some implementations refuse)
 *      forEachRemaining(c)    - apply a Consumer to every remaining element (Java 8+)
 * <p>
 *
 * The enhanced for loop is sugar over Iterator. These two are the same:
 * <p>
 *
 *      for (E e : list) { ... }
 * <p>
 *
 *      Iterator&lt;E&gt; it = list.iterator();
 *      while (it.hasNext()) {
 *          E e = it.next();
 *          ...
 *      }
 * <p>
 *
 * ListIterator&lt;E&gt; - Bidirectional, Position-Aware
 * -----------------------------------------------
 *      hasPrevious() / previous() / nextIndex() / previousIndex()
 *      add(e)                 - insert AT the cursor
 *      set(e)                 - replace the last element returned
 * Available only on List implementations.
 * <p>
 *
 * Spliterator&lt;E&gt; (Java 8+)
 * ------------------------
 * Iterates AND can SPLIT itself in half - that is what enables
 * `Collection.parallelStream()`. You almost never need to write one
 * yourself; you use it via streams.
 * <p>
 *
 * fail-fast vs fail-safe
 * ----------------------
 * Most JDK iterators are FAIL-FAST - if the underlying collection is
 * modified outside of the iterator while iterating, they throw
 * ConcurrentModificationException. CopyOnWrite* and Concurrent*
 * collections are FAIL-SAFE - they iterate over a snapshot taken at the
 * moment iterator() was called, so they never throw, but they may see
 * stale data.
 * <p>
 *
 * Removing While Iterating - the Two Right Ways
 * ---------------------------------------------
 *   - Iterator.remove() inside an explicit while loop.
 *   - Collection.removeIf(Predicate)  - Java 8+, preferred when applicable.
 * <p>
 *
 * NEVER call list.remove(x) from inside a for-each loop - that throws CME.
 */

public class IteratorDemo {

    public static void main(String[] args) {

        section("1) Iterator - hasNext / next / remove");
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Iterator<Integer> it = nums.iterator();
        while (it.hasNext()) {
            int v = it.next();
            if (v % 2 == 0) it.remove();          // remove all evens safely
        }
        System.out.println("after iter.remove evens = " + nums);

        section("2) forEachRemaining (Java 8+)");
        Iterator<Integer> it2 = List.of(10, 20, 30).iterator();
        it2.next();                                  // skip the first one
        it2.forEachRemaining(n -> System.out.println("  " + n));

        section("3) ListIterator - bidirectional, set, add, indexes");
        List<String> letters = new ArrayList<>(List.of("a", "b", "c", "d"));
        ListIterator<String> lit = letters.listIterator();
        while (lit.hasNext()) {
            int idx = lit.nextIndex();
            String s = lit.next();
            if (s.equals("b")) lit.set("B");        // replace in place
            if (s.equals("c")) lit.add("C+1");      // insert after current
            System.out.println("forward  idx=" + idx + " val=" + s);
        }
        System.out.println("after forward pass = " + letters);

        while (lit.hasPrevious()) {
            System.out.println("backward idx=" + lit.previousIndex() + " val=" + lit.previous());
        }

        section("4) fail-fast - external modification breaks iteration");
        List<Integer> bag = new ArrayList<>(List.of(1, 2, 3, 4));
        try {
            for (int n : bag) {
                if (n == 2) bag.remove(Integer.valueOf(2));      // BAD - external mutation
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("caught ConcurrentModificationException - fail-fast");
        }

        section("5) The two SAFE ways to remove while iterating");
        bag = new ArrayList<>(List.of(1, 2, 3, 4));
        bag.removeIf(n -> n == 2);                  // way 1 - removeIf
        System.out.println("removeIf -> " + bag);

        bag = new ArrayList<>(List.of(1, 2, 3, 4));
        Iterator<Integer> safe = bag.iterator();
        while (safe.hasNext()) {                     // way 2 - Iterator.remove
            if (safe.next() == 2) safe.remove();
        }
        System.out.println("iter.remove -> " + bag);

        section("6) fail-safe - CopyOnWriteArrayList iterates a snapshot");
        java.util.concurrent.CopyOnWriteArrayList<Integer> cow =
                new java.util.concurrent.CopyOnWriteArrayList<>(List.of(1, 2, 3, 4));
        for (int n : cow) {
            if (n == 2) cow.remove(Integer.valueOf(2));     // legal - but won't be seen
        }
        System.out.println("cow after loop = " + cow);

        section("7) Spliterator - the fuel of parallel streams");
        Spliterator<Integer> sp = List.of(1, 2, 3, 4, 5, 6, 7, 8).spliterator();
        Spliterator<Integer> half = sp.trySplit();
        System.out.print("half 1:  "); if (half != null) half.forEachRemaining(n -> System.out.print(n + " "));
        System.out.println();
        System.out.print("half 2:  "); sp.forEachRemaining(n -> System.out.print(n + " "));
        System.out.println();

        section("8) Iterable - any class that exposes iterator() can be in a for-each");
        var range = new IntRange(1, 5);
        for (int n : range) System.out.print(n + " ");
        System.out.println();

        // OUTPUT (representative)
    }

    /** A tiny custom Iterable so we can show how the contract works. */
    static class IntRange implements Iterable<Integer> {
        private final int from, toExclusive;
        IntRange(int from, int toExclusive) { this.from = from; this.toExclusive = toExclusive; }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                int next = from;
                @Override public boolean hasNext() { return next < toExclusive; }
                @Override public Integer next()    { return next++; }
            };
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
