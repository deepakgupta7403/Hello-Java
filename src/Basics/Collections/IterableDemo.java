package Basics.Collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;

/**
 * java.lang.Iterable&lt;T&gt; - The "for-each" Contract
 * -----------------------------------------------
 * Any class that implements Iterable can be used in Java's enhanced
 * for-each loop:
 *
 *      for (Item i : container) { ... }
 *
 * The compiler rewrites that loop to call iterator() on `container` and
 * then drive it. Implementing Iterable is therefore the simplest way to
 * make your own class loopable.
 *
 *
 * The Iterable Contract (Java 8+)
 * -------------------------------
 *      Iterator&lt;T&gt; iterator()                    [required]
 *      default void forEach(Consumer&lt;? super T&gt;) [added in Java 8]
 *      default Spliterator&lt;T&gt; spliterator()      [added in Java 8]
 *
 * The two defaults give every Iterable a forEach(...) and stream()
 * (indirectly through spliterator) for free.
 *
 *
 * Relationship to Other Types
 * ---------------------------
 *      Iterable<T>                              <-- root
 *         |
 *         v
 *      Collection<T>     - List, Set, Queue, Deque
 *      Path, FileChannel.LinesStream, ...       - various other types
 *
 *
 * Why Implement Iterable?
 * -----------------------
 *   - Domain objects with collection-like behaviour ("matrix rows",
 *     "events in a session") become loopable.
 *   - Stream support follows automatically with
 *     StreamSupport.stream(it.spliterator(), false).
 *   - Any user of your type can stop caring whether you store data in an
 *     ArrayList, a database cursor, or a generator.
 *
 *
 * Two Custom Iterables Below
 * --------------------------
 *   IntRange       - generates 0..n-1 lazily; no backing storage.
 *   LinkedChain    - tiny linked list, with its own Iterator implementation.
 */

public class IterableDemo {

    // ============================================================
    // 1) IntRange - "for (int i : new IntRange(5))"
    // ============================================================
    static class IntRange implements Iterable<Integer> {
        private final int fromIncl, toExcl;

        IntRange(int fromIncl, int toExcl) {
            if (fromIncl > toExcl) throw new IllegalArgumentException("from > to");
            this.fromIncl = fromIncl;
            this.toExcl   = toExcl;
        }

        @Override public Iterator<Integer> iterator() {
            return new Iterator<>() {
                int cursor = fromIncl;
                @Override public boolean hasNext() { return cursor < toExcl; }
                @Override public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return cursor++;
                }
            };
        }
    }

    // ============================================================
    // 2) LinkedChain - a minimal linked list that is Iterable
    // ============================================================
    static class LinkedChain<E> implements Iterable<E> {
        private Node<E> head;

        public LinkedChain<E> add(E v) {
            if (head == null) { head = new Node<>(v); }
            else {
                Node<E> cur = head;
                while (cur.next != null) cur = cur.next;
                cur.next = new Node<>(v);
            }
            return this;
        }

        @Override public Iterator<E> iterator() {
            return new Iterator<>() {
                Node<E> cur = head;
                @Override public boolean hasNext() { return cur != null; }
                @Override public E next() {
                    if (cur == null) throw new NoSuchElementException();
                    E v = cur.value;
                    cur = cur.next;
                    return v;
                }
            };
        }

        private static class Node<E> {
            E value; Node<E> next;
            Node(E v) { this.value = v; }
        }
    }

    public static void main(String[] args) {

        section("1) Use the IntRange in a for-each loop");
        for (int n : new IntRange(0, 5)) System.out.print(n + " ");
        System.out.println();

        section("2) Use the IntRange with forEach (default method - free)");
        new IntRange(10, 14).forEach(n -> System.out.println("  forEach " + n));

        section("3) Iterable.spliterator gives you Stream support for free");
        long evens = java.util.stream.StreamSupport
                .stream(new IntRange(0, 10).spliterator(), /*parallel*/ false)
                .filter(n -> n % 2 == 0)
                .count();
        System.out.println("evens in 0..9 = " + evens);

        section("4) LinkedChain - your own linked list, fully loopable");
        LinkedChain<String> chain = new LinkedChain<>();
        chain.add("alpha").add("beta").add("gamma");
        for (String s : chain) System.out.println("  " + s);
        // forEach + spliterator work here too because Iterable supplies them.

        section("5) Iterable is the bridge - everything in java.util uses it");
        Iterable<Integer> list = List.of(1, 2, 3);          // List implements Iterable
        Iterable<Integer> set  = new java.util.HashSet<>(List.of(4, 5, 6));
        Iterable<Integer> q    = new java.util.ArrayDeque<>(List.of(7, 8, 9));
        for (Iterable<Integer> it : List.of(list, set, q)) {
            System.out.print("iter: "); it.forEach(n -> System.out.print(n + " "));
            System.out.println();
        }

        section("6) Spliterator characteristics (informational)");
        Spliterator<Integer> sp = new IntRange(0, 4).spliterator();
        System.out.println("estimateSize() = " + sp.estimateSize());
        // Our quick implementation uses the default Iterable.spliterator, which
        // does NOT know an exact size; it returns Long.MAX_VALUE for that.

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
