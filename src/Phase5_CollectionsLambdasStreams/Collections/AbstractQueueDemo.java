package Phase5_CollectionsLambdasStreams.Collections;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * java.util.AbstractQueue - Skeletal Queue Implementation
 * -------------------------------------------------------
 * AbstractQueue is to Queue what AbstractList is to List - it does the
 * boilerplate so you only have to implement a few primitive operations.
 *
 *
 * What You Must Provide
 * ---------------------
 *   E peek()                 - look at the head; return null if empty
 *   E poll()                 - remove and return the head; null if empty
 *   boolean offer(E e)       - try to add; return true if successful
 *   Iterator&lt;E&gt; iterator()   - inherited via AbstractCollection
 *   int size()               - inherited via AbstractCollection
 *
 * AbstractQueue then derives:
 *   add(e)         -> calls offer(e); throws IllegalStateException on false
 *   remove()       -> calls poll(); throws NoSuchElementException on null
 *   element()      -> calls peek(); throws NoSuchElementException on null
 *   addAll(c)      -> calls add(e) for each
 *
 *
 * Why You'd Subclass It
 * ---------------------
 *   - You are building a CUSTOM queue with weird semantics (random-access
 *     priority, bounded queue, side-effecting offer ...).
 *   - You want the throws-on-failure variants for free.
 *   - You want your queue to follow the same conventions as the JDK's.
 *
 *
 * Real-World JDK Subclasses
 * -------------------------
 *   PriorityQueue                          - heap
 *   ArrayBlockingQueue                     - bounded
 *   LinkedBlockingQueue                    - linked, optionally bounded
 *   ConcurrentLinkedQueue                  - lock-free
 *   DelayQueue                             - delayed elements
 *
 * They all build on AbstractQueue rather than re-implementing the Queue
 * methods from scratch.
 *
 *
 * The Demo Below
 * --------------
 * We build a tiny BOUNDED FIFO queue on top of a plain ArrayList and let
 * AbstractQueue fill in the rest of the Queue API.
 */

public class AbstractQueueDemo {

    /**
     * BoundedQueue - capped FIFO queue.
     *
     * We only implement peek / poll / offer / size / iterator. AbstractQueue
     * supplies add / remove / element / addAll automatically with the
     * correct exception-on-failure behaviour.
     */
    static class BoundedQueue<E> extends AbstractQueue<E> {
        private final List<E> data = new ArrayList<>();
        private final int capacity;

        BoundedQueue(int capacity) { this.capacity = capacity; }

        @Override public boolean offer(E e) {
            if (e == null) throw new NullPointerException("null not permitted");
            if (data.size() >= capacity) return false;
            return data.add(e);
        }

        @Override public E poll() {
            return data.isEmpty() ? null : data.remove(0);
        }

        @Override public E peek() {
            return data.isEmpty() ? null : data.get(0);
        }

        @Override public Iterator<E> iterator() { return data.iterator(); }
        @Override public int size()              { return data.size(); }
    }

    public static void main(String[] args) {

        section("1) BoundedQueue extending AbstractQueue - what you implement");
        BoundedQueue<Integer> q = new BoundedQueue<>(3);
        System.out.println("offer(1) = " + q.offer(1));
        System.out.println("offer(2) = " + q.offer(2));
        System.out.println("offer(3) = " + q.offer(3));
        System.out.println("offer(4) = " + q.offer(4) + "  (capacity 3, refused)");
        System.out.println("queue    = " + q);

        section("2) FREE methods inherited from AbstractQueue");
        BoundedQueue<Integer> bag = new BoundedQueue<>(2);
        bag.offer(10);
        bag.offer(20);
        try {
            bag.add(30);                      // inherited - throws when offer returns false
        } catch (IllegalStateException e) {
            System.out.println("add() on full -> IllegalStateException");
        }

        try {
            new BoundedQueue<Integer>(0).remove();   // inherited - throws when empty
        } catch (java.util.NoSuchElementException e) {
            System.out.println("remove() on empty -> NoSuchElementException");
        }

        try {
            new BoundedQueue<Integer>(0).element();  // inherited - throws when empty
        } catch (java.util.NoSuchElementException e) {
            System.out.println("element() on empty -> NoSuchElementException");
        }

        section("3) addAll - also inherited");
        BoundedQueue<Integer> bulk = new BoundedQueue<>(10);
        bulk.addAll(List.of(1, 2, 3, 4, 5));     // uses add internally
        System.out.println("after addAll = " + bulk);

        section("4) Iteration via the iterator you provided");
        System.out.print("iter: ");
        for (int n : bulk) System.out.print(n + " ");
        System.out.println();

        section("5) JDK queues are all AbstractQueue subclasses");
        System.out.println("PriorityQueue is AbstractQueue? "
                + (new java.util.PriorityQueue<Integer>() instanceof AbstractQueue));
        System.out.println("ConcurrentLinkedQueue is AbstractQueue? "
                + (new java.util.concurrent.ConcurrentLinkedQueue<Integer>() instanceof AbstractQueue));
        System.out.println("ArrayBlockingQueue is AbstractQueue? "
                + (new java.util.concurrent.ArrayBlockingQueue<Integer>(1) instanceof AbstractQueue));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
