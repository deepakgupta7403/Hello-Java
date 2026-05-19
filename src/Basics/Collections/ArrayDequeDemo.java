package Basics.Collections;

import java.util.ArrayDeque;
import java.util.List;

/**
 * java.util.ArrayDeque - The Modern Stack-and-Queue Champion
 * ----------------------------------------------------------
 * ArrayDeque is a RESIZABLE CIRCULAR ARRAY implementation of Deque. It can
 * be used as either a QUEUE, a STACK, or a true DEQUE. In most cases it is
 * the right answer:
 *
 *      Stack       prefer ArrayDeque over java.util.Stack
 *      Queue       prefer ArrayDeque over LinkedList
 *      Deque       prefer ArrayDeque over LinkedList
 *
 *
 * Why It Exists
 * -------------
 *   - java.util.Stack extends Vector, which is synchronized and slow.
 *   - LinkedList works but is heavy: each node is a separate object, two
 *     pointers, and worse cache locality.
 *   - ArrayDeque packs elements in one contiguous array - smaller memory,
 *     better cache behaviour, no synchronisation tax.
 *
 *
 * Constructors
 * ------------
 *   new ArrayDeque&lt;&gt;()                 capacity 16
 *   new ArrayDeque&lt;&gt;(int numElements)  initial capacity
 *   new ArrayDeque&lt;&gt;(Collection)        copy of another collection
 *
 *
 * Methods - Three Faces of the Same Object
 * ----------------------------------------
 *
 *   FIFO (Queue)              LIFO (Stack)           Deque (both ends)
 *   ----------------------    --------------------   --------------------
 *   offer/offerLast (e)       push (e)                addFirst/addLast (e)
 *   poll/pollFirst ()          pop ()                  pollFirst/pollLast ()
 *   peek/peekFirst ()          peek ()                 peekFirst/peekLast ()
 *
 * Throw-on-failure variants:
 *   add(e)/addLast(e)/addFirst(e)
 *   remove() / removeFirst() / removeLast()
 *   element() / getFirst() / getLast()
 *
 *
 * Restrictions
 * ------------
 *   - NULL elements are NOT allowed (intentional - null is reserved as the
 *     "queue is empty" sentinel for poll/peek). Use LinkedList if you need
 *     to store nulls (rare).
 *   - NOT thread-safe. For concurrent access use a ConcurrentLinkedDeque or
 *     a BlockingDeque.
 *
 *
 * Big-O
 * -----
 *   add / remove / peek at either end                O(1) amortised
 *   contains / remove(Object)                        O(n)
 *
 *
 * Iteration
 * ---------
 *   iterator()              first-to-last
 *   descendingIterator()    last-to-first
 */

public class ArrayDequeDemo {

    public static void main(String[] args) {

        section("1) FIFO queue usage");
        ArrayDeque<String> q = new ArrayDeque<>();
        q.offer("first");
        q.offer("second");
        q.offer("third");
        System.out.println("queue        = " + q);
        System.out.println("peek         = " + q.peek());
        System.out.println("poll         = " + q.poll() + "  -> " + q);

        section("2) LIFO stack usage");
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("stack (top first) = " + stack);
        System.out.println("peek = " + stack.peek());
        System.out.println("pop  = " + stack.pop() + "  -> " + stack);

        section("3) Deque - work at BOTH ends");
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addFirst(2);
        d.addFirst(1);
        d.addLast(3);
        d.addLast(4);
        System.out.println("deque = " + d);
        System.out.println("peekFirst   = " + d.peekFirst());
        System.out.println("peekLast    = " + d.peekLast());
        System.out.println("removeFirst -> " + d.removeFirst() + "  deque=" + d);
        System.out.println("removeLast  -> " + d.removeLast()  + "  deque=" + d);

        section("4) Throws vs returns-special");
        ArrayDeque<Integer> e = new ArrayDeque<>();
        System.out.println("pollFirst on empty = " + e.pollFirst());   // null
        System.out.println("peekFirst on empty = " + e.peekFirst());   // null
        try { e.getFirst(); }
        catch (java.util.NoSuchElementException ex) {
            System.out.println("getFirst on empty -> NoSuchElementException");
        }

        section("5) null rejection - deliberate safety");
        try { new ArrayDeque<>().offer(null); }
        catch (NullPointerException ex) {
            System.out.println("offer(null) -> NullPointerException");
        }

        section("6) Iteration direction");
        ArrayDeque<Integer> walk = new ArrayDeque<>(List.of(1, 2, 3, 4, 5));
        System.out.print("forward : "); walk.forEach(n -> System.out.print(n + " "));
        System.out.println();
        System.out.print("backward: ");
        for (java.util.Iterator<Integer> it = walk.descendingIterator(); it.hasNext(); ) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        section("7) Reverse a string using a stack");
        String text = "hello";
        ArrayDeque<Character> chars = new ArrayDeque<>();
        for (char c : text.toCharArray()) chars.push(c);
        StringBuilder reversed = new StringBuilder();
        while (!chars.isEmpty()) reversed.append(chars.pop());
        System.out.println("reverse(\"" + text + "\") = " + reversed);

        section("8) Sliding-window maximum (classic deque problem)");
        int[] arr = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        ArrayDeque<Integer> idx = new ArrayDeque<>();    // holds INDICES
        StringBuilder maxes = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            // drop indices that fell out of the window
            while (!idx.isEmpty() && idx.peekFirst() <= i - k) idx.pollFirst();
            // maintain a decreasing deque
            while (!idx.isEmpty() && arr[idx.peekLast()] <= arr[i]) idx.pollLast();
            idx.offerLast(i);
            if (i >= k - 1) maxes.append(arr[idx.peekFirst()]).append(' ');
        }
        System.out.println("input  = " + java.util.Arrays.toString(arr));
        System.out.println("max of every " + k + " = " + maxes.toString().trim());

        section("9) Performance teaser - ArrayDeque vs LinkedList");
        final int N = 1_000_000;
        long t = System.nanoTime();
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < N; i++) a.push(i);
        while (!a.isEmpty()) a.pop();
        long aMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        java.util.LinkedList<Integer> l = new java.util.LinkedList<>();
        for (int i = 0; i < N; i++) l.push(i);
        while (!l.isEmpty()) l.pop();
        long lMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("ArrayDeque push+pop x " + N + ": " + aMs + " ms");
        System.out.println("LinkedList push+pop x " + N + ": " + lMs + " ms");

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
