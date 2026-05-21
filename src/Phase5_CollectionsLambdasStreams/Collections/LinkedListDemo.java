package Phase5_CollectionsLambdasStreams.Collections;

import java.util.LinkedList;
import java.util.List;

/**
 * java.util.LinkedList - Doubly-Linked List Implementation
 * --------------------------------------------------------
 * LinkedList implements BOTH List AND Deque. Internally it is a chain of
 * nodes; each node holds the value plus a previous/next pointer:
 * <p>
 *
 *      head -> [ A ] &lt;-> [ B ] &lt;-> [ C ] &lt;- tail
 * <p>
 *
 * Why It Exists
 * -------------
 * Constant-time INSERT/REMOVE at either end and at a known cursor (via
 * ListIterator). That makes it convenient for queue/stack/deque use, and
 * historically for "lots of inserts in the middle" workloads.
 * <p>
 *
 * When To Use It
 * --------------
 *   - You need a DEQUE - add/remove at BOTH ends in O(1).
 *   - You build up a list at the FRONT (Linked.addFirst is O(1); ArrayList
 *     would shift on each prepend).
 * <p>
 *
 * Honest reality check: ArrayList wins MOST benchmarks because of cache
 * friendliness. Reach for LinkedList only when you specifically need its
 * deque characteristics; otherwise prefer ArrayDeque or ArrayList.
 * <p>
 *
 * Big-O
 * -----
 *   addFirst / addLast / removeFirst / removeLast        O(1)
 *   get(i) / set(i) / add(i, e) / remove(i)              O(n)  (walk the chain)
 *   contains, indexOf, lastIndexOf                       O(n)
 * <p>
 *
 * The List-and-Deque Dual Personality
 * -----------------------------------
 * Because LinkedList implements Deque too, the SAME object exposes both:
 * <p>
 *
 *   List API:  add, get, set, indexOf, subList ...
 *   Deque API: addFirst, peekLast, pollFirst, push, pop ...
 * <p>
 *
 * In modern code, prefer ArrayDeque for deque/stack/queue duties - it is
 * smaller, faster and has no random-access methods at all.
 */

public class LinkedListDemo {

    public static void main(String[] args) {

        section("1) Constructors + basic List API");
        LinkedList<String> a = new LinkedList<>();
        a.add("alpha");
        a.add("beta");
        a.add("gamma");
        a.add(1, "inserted");
        System.out.println("list           = " + a);
        System.out.println("get(2)         = " + a.get(2));
        System.out.println("indexOf(beta)  = " + a.indexOf("beta"));

        section("2) Deque-style add/peek/poll at both ends (constant time)");
        LinkedList<Integer> d = new LinkedList<>(List.of(2, 3, 4));
        d.addFirst(1);
        d.addLast(5);
        System.out.println("after addFirst/addLast = " + d);
        System.out.println("peekFirst() = " + d.peekFirst());
        System.out.println("peekLast()  = " + d.peekLast());
        System.out.println("pollFirst() = " + d.pollFirst() + "  list=" + d);
        System.out.println("pollLast()  = " + d.pollLast()  + "  list=" + d);

        section("3) Stack-style usage - push / pop / peek");
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("stack (top first) = " + stack);
        System.out.println("peek()             = " + stack.peek());
        System.out.println("pop()              = " + stack.pop() + "  stack=" + stack);

        section("4) Queue-style usage - offer / poll");
        LinkedList<String> q = new LinkedList<>();
        q.offer("first");
        q.offer("second");
        q.offer("third");
        System.out.println("queue = " + q);
        System.out.println("poll  = " + q.poll() + "  queue=" + q);

        section("5) Iteration - ListIterator (bidirectional)");
        LinkedList<Integer> ll = new LinkedList<>(List.of(10, 20, 30, 40));
        java.util.ListIterator<Integer> it = ll.listIterator();
        while (it.hasNext()) {
            int v = it.next();
            if (v == 20) it.set(200);              // replace in place
            if (v == 30) it.add(35);               // insert after 30
        }
        System.out.println("after listIterator edits = " + ll);

        section("6) descendingIterator - walk from tail to head");
        for (java.util.Iterator<Integer> rit = ll.descendingIterator(); rit.hasNext(); ) {
            System.out.print(rit.next() + " ");
        }
        System.out.println();

        section("7) Comparison - LinkedList vs ArrayList for FRONT inserts");
        final int N = 50_000;
        long t = System.nanoTime();
        java.util.ArrayList<Integer> al = new java.util.ArrayList<>();
        for (int i = 0; i < N; i++) al.add(0, i);           // worst case
        long alMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        java.util.LinkedList<Integer> ll2 = new java.util.LinkedList<>();
        for (int i = 0; i < N; i++) ll2.addFirst(i);
        long llMs = (System.nanoTime() - t) / 1_000_000;
        System.out.println("ArrayList.add(0,e) loop  : " + alMs + " ms");
        System.out.println("LinkedList.addFirst loop : " + llMs + " ms  (much faster for this pattern)");

        section("8) Comparison - LinkedList vs ArrayList for RANDOM get");
        java.util.ArrayList<Integer> al2 = new java.util.ArrayList<>();
        java.util.LinkedList<Integer> ll3 = new java.util.LinkedList<>();
        for (int i = 0; i < N; i++) { al2.add(i); ll3.add(i); }

        t = System.nanoTime();
        long sumAl = 0; for (int i = 0; i < 5_000; i++) sumAl += al2.get(i * 5);
        long alGetMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        long sumLl = 0; for (int i = 0; i < 5_000; i++) sumLl += ll3.get(i * 5);
        long llGetMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("ArrayList.get(i)         : " + alGetMs + " ms  (cache-friendly)");
        System.out.println("LinkedList.get(i)        : " + llGetMs + " ms  (walks the chain - slow)");
        System.out.println("sums match: " + (sumAl == sumLl));

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
