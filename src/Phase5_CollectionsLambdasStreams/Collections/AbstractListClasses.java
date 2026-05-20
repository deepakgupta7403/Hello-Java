package Phase5_CollectionsLambdasStreams.Collections;

import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * AbstractList and AbstractSequentialList - Skeletal Implementations
 * ------------------------------------------------------------------
 * The Collections Framework follows a common pattern: every public
 * INTERFACE has a companion ABSTRACT SKELETON CLASS that takes care of the
 * boilerplate so concrete subclasses only need to implement a handful of
 * primitive operations.
 *
 *      Collection         -> AbstractCollection
 *      List               -> AbstractList      -> AbstractSequentialList
 *      Set                -> AbstractSet
 *      Queue              -> AbstractQueue
 *      Map                -> AbstractMap
 *
 *
 * AbstractList&lt;E&gt; - For Random-Access Lists
 * -----------------------------------------
 * To build a List you only need to implement:
 *
 *      E get(int index)
 *      int size()
 *
 * That gives you a READ-ONLY list. To add mutation, override one or more of:
 *
 *      E set(int index, E element)         - replace
 *      void add(int index, E element)       - insert
 *      E remove(int index)                  - delete
 *
 * AbstractList provides correct implementations of iterator(), listIterator(),
 * indexOf, lastIndexOf, equals, hashCode, toString, sublist, and the fail-fast
 * modCount machinery - all on top of YOUR primitive overrides.
 *
 *
 * AbstractSequentialList&lt;E&gt; - For Linked Lists
 * --------------------------------------------
 * If random access (get(int)) is EXPENSIVE for your data structure, extend
 * AbstractSequentialList instead. You only need to implement:
 *
 *      ListIterator&lt;E&gt; listIterator(int index)
 *      int size()
 *
 * The class then derives get / set / add / remove from ListIterator
 * operations - typically O(n) but appropriate for chained nodes. java.util.LinkedList
 * uses this skeleton.
 *
 *
 * Why You'd Use Them
 * ------------------
 *   - Building a CUSTOM list-shaped data structure: a fixed-size view of an
 *     int[], a database-backed list, an "ints from a to b" range.
 *   - Implementing the List contract correctly without re-deriving all the
 *     bookkeeping (iterator state, hashCode order, equals semantics).
 *
 * In day-to-day code you reach for ArrayList / LinkedList - but knowing the
 * abstract skeletons exist is part of understanding how the framework is
 * put together.
 *
 *
 * Demo
 * ----
 * Two custom lists below:
 *   IntRangeList            - extends AbstractList; lazy "0..n-1" view, O(1) get.
 *   SingletonChain          - extends AbstractSequentialList; a doubly-linked
 *                              list with a hand-written ListIterator.
 */

public class AbstractListClasses {

    // ============================================================
    // 1) Custom AbstractList - immutable view of integers 0..n-1
    // ============================================================
    static class IntRangeList extends AbstractList<Integer> {
        private final int size;
        IntRangeList(int size) { this.size = size; }

        @Override public Integer get(int index) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
            return index;
        }
        @Override public int size() { return size; }
    }

    // ============================================================
    // 2) Custom AbstractSequentialList - a tiny doubly-linked chain
    // ============================================================
    static class SingletonChain<E> extends AbstractSequentialList<E> {
        private final Node<E> head = new Node<>(null);   // sentinel; head.next is the first real node
        private final Node<E> tail = new Node<>(null);   // sentinel; tail.prev is the last real node
        private int size = 0;

        SingletonChain() {
            head.next = tail;
            tail.prev = head;
        }

        @Override public int size() { return size; }

        @Override
        public ListIterator<E> listIterator(int index) {
            if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
            return new ChainIterator(index);
        }

        private static class Node<E> {
            E value;
            Node<E> prev, next;
            Node(E v) { this.value = v; }
        }

        /** A minimal ListIterator over the chain. */
        private class ChainIterator implements ListIterator<E> {
            private Node<E> nextNode;
            private Node<E> lastReturned;
            private int     nextIndex;

            ChainIterator(int idx) {
                nextNode  = head.next;
                nextIndex = 0;
                while (nextIndex < idx) { nextNode = nextNode.next; nextIndex++; }
            }

            @Override public boolean hasNext()     { return nextNode != tail; }
            @Override public boolean hasPrevious() { return nextNode.prev != head; }
            @Override public int nextIndex()       { return nextIndex; }
            @Override public int previousIndex()   { return nextIndex - 1; }

            @Override public E next() {
                lastReturned = nextNode;
                E v = nextNode.value;
                nextNode = nextNode.next;
                nextIndex++;
                return v;
            }
            @Override public E previous() {
                lastReturned = nextNode = nextNode.prev;
                nextIndex--;
                return nextNode.value;
            }
            @Override public void set(E v) {
                if (lastReturned == null) throw new IllegalStateException();
                lastReturned.value = v;
            }
            @Override public void add(E v) {
                Node<E> node = new Node<>(v);
                node.prev = nextNode.prev;
                node.next = nextNode;
                nextNode.prev.next = node;
                nextNode.prev = node;
                size++; nextIndex++;
                lastReturned = null;
            }
            @Override public void remove() {
                if (lastReturned == null) throw new IllegalStateException();
                Node<E> p = lastReturned.prev, n = lastReturned.next;
                p.next = n; n.prev = p;
                if (lastReturned == nextNode) nextNode = n; else nextIndex--;
                size--;
                lastReturned = null;
            }
        }
    }

    public static void main(String[] args) {

        section("1) IntRangeList - your own immutable list in ~10 lines");
        List<Integer> r = new IntRangeList(5);
        System.out.println("range          = " + r);              // [0, 1, 2, 3, 4]
        System.out.println("get(3)         = " + r.get(3));
        System.out.println("size()         = " + r.size());
        System.out.println("indexOf(2)     = " + r.indexOf(2));   // inherited from AbstractList
        System.out.println("contains(4)    = " + r.contains(4));  // inherited
        for (int n : r) System.out.print(n + " ");                // iterator inherited
        System.out.println();

        section("2) IntRangeList toString / equals / hashCode are free");
        // AbstractList provides equals/hashCode that compare element-by-element
        // against any other List - including ArrayList.
        System.out.println("r.equals([0..4]) = " + r.equals(List.of(0, 1, 2, 3, 4)));

        section("3) SingletonChain - your own LinkedList via AbstractSequentialList");
        SingletonChain<String> chain = new SingletonChain<>();
        chain.add("alpha");
        chain.add("beta");
        chain.add("gamma");
        chain.add(1, "INSERTED");
        System.out.println("chain          = " + chain);          // [alpha, INSERTED, beta, gamma]
        System.out.println("size()         = " + chain.size());
        System.out.println("get(2)         = " + chain.get(2));   // derived from listIterator
        chain.remove(0);
        System.out.println("after remove(0)= " + chain);

        section("4) Iteration both ways via listIterator");
        Iterator<String> it = chain.iterator();
        System.out.print("forward : ");
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();
        ListIterator<String> back = chain.listIterator(chain.size());
        System.out.print("backward: ");
        while (back.hasPrevious()) System.out.print(back.previous() + " ");
        System.out.println();

        section("5) Why this matters - the JDK itself uses these skeletons");
        // java.util.ArrayList            extends AbstractList
        // java.util.LinkedList           extends AbstractSequentialList
        // java.util.Arrays.asList(...)   extends AbstractList (a small inner class)
        // java.util.Collections.nCopies(...) returns an AbstractList subclass
        System.out.println("Arrays.asList class       = " + java.util.Arrays.asList(1, 2).getClass().getName());
        System.out.println("Collections.nCopies class = " + java.util.Collections.nCopies(3, "x").getClass().getName());

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
