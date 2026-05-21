package Phase5_CollectionsLambdasStreams.Collections;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * java.util.Queue&lt;E&gt; - FIFO Linear Collection
 * -------------------------------------------
 * A Queue holds elements waiting to be processed. The classic model is
 * FIRST-IN-FIRST-OUT - the element you add first comes out first.
 * <p>
 *
 *      enqueue ->  [A][B][C][D]  -> dequeue
 * <p>
 *
 * The Queue API - Two Method Families
 * -----------------------------------
 * <p>
 *
 *   Throws on failure                Returns a special value
 *   --------------------------       ------------------------
 *   add(e)        - capacity full -> exception   |  offer(e)  -> returns false
 *   remove()      - empty queue   -> exception   |  poll()    -> returns null
 *   element()     - empty queue   -> exception   |  peek()    -> returns null
 * <p>
 *
 * Pick ONE style consistently in a code base. `offer` / `poll` / `peek` are
 * the safer default - exceptions tend to be unwanted control flow.
 * <p>
 *
 * Common Implementations
 * ----------------------
 *   LinkedList         - doubly-linked list, unbounded, allows nulls
 *   ArrayDeque         - resizing array, unbounded, NO nulls (preferred)
 *   PriorityQueue      - heap, ORDER BY PRIORITY (not insertion order)
 *   ArrayBlockingQueue - bounded, thread-safe (java.util.concurrent)
 *   LinkedBlockingQueue- optionally bounded, thread-safe
 *   SynchronousQueue   - hand-off queue with zero capacity
 *   ConcurrentLinkedQueue - lock-free unbounded queue
 * <p>
 *
 * When To Use Which
 * -----------------
 *   - Single-threaded FIFO              ->  ArrayDeque (fastest plain Queue).
 *   - Priority-ordered processing       ->  PriorityQueue.
 *   - Multi-producer / multi-consumer   ->  the concurrent queues.
 * <p>
 *
 * Deque vs Queue
 * --------------
 * Deque (double-ended queue) extends Queue. It adds operations at BOTH
 * ends - addFirst/addLast, peekFirst/peekLast - and is the recommended
 * tool for stacks too. See DequeInterface.java.
 */

public class QueueInterface {

    public static void main(String[] args) {

        section("1) Two method families - throws vs returns special");
        Queue<String> q = new ArrayDeque<>();

        // Return-style (preferred):
        System.out.println("offer(a) = " + q.offer("a"));   // true
        System.out.println("offer(b) = " + q.offer("b"));
        System.out.println("peek()   = " + q.peek());        // "a"
        System.out.println("poll()   = " + q.poll() + "  q=" + q);

        // What happens on an empty queue?
        Queue<String> empty = new ArrayDeque<>();
        System.out.println("empty.peek() = " + empty.peek());   // null
        System.out.println("empty.poll() = " + empty.poll());   // null

        try {
            empty.element();         // throws if empty
        } catch (java.util.NoSuchElementException e) {
            System.out.println("element() on empty -> NoSuchElementException");
        }
        try {
            empty.remove();          // throws if empty
        } catch (java.util.NoSuchElementException e) {
            System.out.println("remove()  on empty -> NoSuchElementException");
        }

        section("2) FIFO behaviour through any Queue");
        Queue<Integer> a = new ArrayDeque<>();
        for (int n : new int[]{1, 2, 3, 4, 5}) a.offer(n);
        System.out.print("drained: ");
        while (!a.isEmpty()) System.out.print(a.poll() + " ");
        System.out.println();

        section("3) PriorityQueue - sorted by priority, NOT insertion order");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int n : new int[]{5, 1, 4, 2, 3}) pq.offer(n);
        System.out.print("drained in PRIORITY order: ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println();

        section("4) LinkedList implements Queue too");
        Queue<String> ll = new LinkedList<>();
        ll.offer("first");
        ll.offer("second");
        ll.offer("third");
        System.out.println("LinkedList queue = " + ll);
        System.out.println("poll = " + ll.poll() + "  queue=" + ll);

        section("5) Don't try to put null into ArrayDeque");
        Queue<String> arrayDeque = new ArrayDeque<>();
        try { arrayDeque.offer(null); }
        catch (NullPointerException e) {
            System.out.println("ArrayDeque rejects null - good defaults");
        }
        // LinkedList ALLOWS null; use ArrayDeque if you want the safety net.

        section("6) The 'work queue' pattern - a tiny producer/consumer toy");
        Queue<String> work = new ArrayDeque<>();
        // producer
        for (int i = 0; i < 5; i++) work.offer("task-" + i);
        // consumer
        String task;
        while ((task = work.poll()) != null) {
            System.out.println("processing " + task);
        }

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
