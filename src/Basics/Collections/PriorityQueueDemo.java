package Basics.Collections;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * java.util.PriorityQueue&lt;E&gt; - Heap-Backed Priority Queue
 * --------------------------------------------------------
 * A PriorityQueue is a Queue where elements come out in PRIORITY order, not
 * the order they were added. Internally it uses a BINARY HEAP stored in an
 * array, so the "top" element (smallest by natural order, by default) is
 * always at index 0.
 *
 *
 * Why It Exists
 * -------------
 * Many real-world scheduling problems need "give me the most important
 * thing next" - earliest deadline first, shortest path next, highest
 * priority task next, top-K elements. A PriorityQueue does that in
 * O(log n) per add/remove.
 *
 *
 * When To Use It
 * --------------
 *   - Dijkstra / A* (shortest path).
 *   - Task schedulers ranked by priority or deadline.
 *   - Top-K problems (keep the K smallest / largest seen so far).
 *   - Event simulators that fire events in time order.
 *
 *
 * Big-O
 * -----
 *   offer / add / poll / remove (head)     O(log n)
 *   peek / element                          O(1)
 *   contains                                O(n)
 *   remove(Object o) (not head)             O(n)
 *
 *
 * Ordering
 * --------
 * By default a PriorityQueue is a MIN-HEAP using natural ordering. You
 * change the order by supplying a Comparator in the constructor.
 *
 *      new PriorityQueue<>()                                  // min-heap
 *      new PriorityQueue<>(Comparator.reverseOrder())          // max-heap
 *      new PriorityQueue<>(Comparator.comparing(Task::deadline))
 *
 *
 * Iteration is NOT Ordered
 * ------------------------
 * The Iterator and toString walk the underlying array, NOT in priority
 * order. To see elements in order, repeatedly poll().
 *
 *
 * Constructors
 * ------------
 *   new PriorityQueue&lt;&gt;()
 *   new PriorityQueue&lt;&gt;(int initialCapacity)
 *   new PriorityQueue&lt;&gt;(Comparator&lt;? super E&gt;)
 *   new PriorityQueue&lt;&gt;(int initialCapacity, Comparator&lt;? super E&gt;)
 *   new PriorityQueue&lt;&gt;(Collection&lt;? extends E&gt;)
 */

public class PriorityQueueDemo {

    record Task(String name, int priority, long deadlineMs) {}

    public static void main(String[] args) {

        section("1) Min-heap by natural order (the default)");
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int n : new int[]{5, 1, 4, 2, 3}) pq.offer(n);
        System.out.println("internal array (toString):  " + pq);
        System.out.print("drained in order: ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println();

        section("2) Max-heap via Comparator.reverseOrder()");
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int n : new int[]{5, 1, 4, 2, 3}) maxHeap.offer(n);
        System.out.print("drained largest first: ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " ");
        System.out.println();

        section("3) Custom Comparator on a record");
        PriorityQueue<Task> byPriority = new PriorityQueue<>(
                Comparator.comparingInt(Task::priority)
        );
        byPriority.offer(new Task("Pay rent",        3, 1));
        byPriority.offer(new Task("Fix prod bug",    1, 2));
        byPriority.offer(new Task("Send invoice",    2, 3));
        byPriority.offer(new Task("Buy milk",        5, 4));

        while (!byPriority.isEmpty()) {
            System.out.println("  next -> " + byPriority.poll());
        }

        section("4) Top-K via a bounded min-heap (classic interview trick)");
        // Keep the K LARGEST seen so far using a MIN-heap of size K.
        // The smallest of the K is always at the top - cheap to evict.
        int[] stream = {7, 1, 9, 3, 6, 4, 8, 2, 5};
        int k = 3;
        PriorityQueue<Integer> topK = new PriorityQueue<>(k);   // min-heap
        for (int n : stream) {
            if (topK.size() < k) topK.offer(n);
            else if (n > topK.peek()) {
                topK.poll();
                topK.offer(n);
            }
        }
        // Drain to read top-K in ascending order
        while (!topK.isEmpty()) System.out.print(topK.poll() + " ");
        System.out.println("(top-" + k + " of stream)");

        section("5) Beware - iteration is NOT priority order");
        PriorityQueue<Integer> p = new PriorityQueue<>(List.of(7, 1, 5, 3, 9));
        System.out.print("iterator says: ");
        for (int n : p) System.out.print(n + " ");
        System.out.println();
        System.out.print("polling says : ");
        while (!p.isEmpty()) System.out.print(p.poll() + " ");
        System.out.println();

        section("6) remove(Object) - O(n), but valid");
        PriorityQueue<Integer> q = new PriorityQueue<>(List.of(7, 1, 5, 3, 9));
        q.remove(5);
        System.out.println("after remove(5): " + q);

        section("7) Constructor from Collection - heapified in O(n)");
        PriorityQueue<Integer> bulk = new PriorityQueue<>(List.of(9, 3, 7, 1, 5, 8, 2, 4, 6));
        System.out.print("drained: ");
        while (!bulk.isEmpty()) System.out.print(bulk.poll() + " ");
        System.out.println();

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
