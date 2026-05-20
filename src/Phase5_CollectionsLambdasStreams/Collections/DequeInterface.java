package Phase5_CollectionsLambdasStreams.Collections;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * java.util.Deque&lt;E&gt; - "Double-Ended Queue"
 * -----------------------------------------
 * A Deque (pronounced "deck") is a queue that supports insert and remove
 * at BOTH ENDS. It can be used as a queue, a stack, or both.
 *
 *      addFirst -> [A][B][C][D] <- addLast
 *      pollFirst <- [A][B][C][D] -> pollLast
 *
 *
 * The Three "Personalities" of a Deque
 * ------------------------------------
 *   QUEUE  (FIFO):    offer / poll        (same as Queue interface)
 *   STACK  (LIFO):    push  / pop         (push = addFirst, pop = removeFirst)
 *   DEQUE  (both):    addFirst / addLast / pollFirst / pollLast / peek...
 *
 * The same Deque object can do all three at the same time.
 *
 *
 * Method Cheatsheet
 * -----------------
 *
 *      Throws on failure          Returns special value
 *      ----------------------     ----------------------
 *      addFirst(e) / addLast(e)   offerFirst(e) / offerLast(e)
 *      removeFirst() / removeLast() pollFirst() / pollLast()
 *      getFirst() / getLast()     peekFirst() / peekLast()
 *
 *   Stack flavour:
 *      push(e)     -> addFirst(e)
 *      pop()       -> removeFirst()
 *      peek()      -> peekFirst()
 *
 *
 * Implementations
 * ---------------
 *   ArrayDeque  - resizing circular array. NO nulls. The DEFAULT.
 *                 Faster than LinkedList for queue and stack duties.
 *   LinkedList  - doubly-linked list. Allows nulls. Slower in benchmarks
 *                 but is also a List.
 *   ConcurrentLinkedDeque, LinkedBlockingDeque - concurrent variants.
 *
 *
 * Why You Should Prefer ArrayDeque For Stacks
 * -------------------------------------------
 * The legacy java.util.Stack class extends Vector (synchronized). It is
 * slow and discouraged. The modern recommendation in the JDK Javadoc:
 *
 *      "Deque interface and its implementations provide a more complete
 *       and consistent set of LIFO stack operations, which should be used
 *       in preference to this class."
 *
 *
 * The Big-O
 * ---------
 *   addFirst / addLast / removeFirst / removeLast / peekFirst / peekLast
 *                                                                 O(1)
 *   contains / remove(Object)                                     O(n)
 */

public class DequeInterface {

    public static void main(String[] args) {

        section("1) Queue (FIFO) usage");
        Deque<String> q = new ArrayDeque<>();
        q.offerLast("a");                 // queue tail
        q.offerLast("b");
        q.offerLast("c");
        System.out.println("queue   = " + q);
        System.out.println("poll    = " + q.pollFirst() + "  -> " + q);

        section("2) Stack (LIFO) usage");
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("stack (top first) = " + stack);    // [3, 2, 1]
        System.out.println("peek              = " + stack.peek());
        System.out.println("pop               = " + stack.pop() + "  -> " + stack);

        section("3) Deque (both ends)");
        Deque<Integer> d = new ArrayDeque<>();
        d.addFirst(2);
        d.addFirst(1);
        d.addLast(3);
        d.addLast(4);
        System.out.println("deque    = " + d);
        System.out.println("peekFirst = " + d.peekFirst());
        System.out.println("peekLast  = " + d.peekLast());
        System.out.println("removeFirst -> " + d.removeFirst() + "  deque=" + d);
        System.out.println("removeLast  -> " + d.removeLast()  + "  deque=" + d);

        section("4) Throws vs returns-special");
        Deque<Integer> empty = new ArrayDeque<>();
        System.out.println("pollFirst on empty = " + empty.pollFirst());        // null
        System.out.println("peekFirst on empty = " + empty.peekFirst());        // null
        try {
            empty.getFirst();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("getFirst on empty -> NoSuchElementException");
        }

        section("5) ArrayDeque vs LinkedList - same Deque API");
        Deque<Integer> ad = new ArrayDeque<>();
        Deque<Integer> ll = new LinkedList<>();
        for (int n : new int[]{1, 2, 3}) { ad.push(n); ll.push(n); }
        System.out.println("ArrayDeque stack  = " + ad);
        System.out.println("LinkedList stack  = " + ll);
        // ArrayDeque is recommended - smaller and faster, just doesn't allow null.

        section("6) ArrayDeque rejects null (a deliberate safety feature)");
        try { ad.offer(null); }
        catch (NullPointerException e) {
            System.out.println("ArrayDeque.offer(null) -> NullPointerException");
        }
        // LinkedList allows null - but that ambiguity is why ArrayDeque forbids it.

        section("7) Real-world example - undo / redo stacks");
        Deque<String> undo = new ArrayDeque<>();
        Deque<String> redo = new ArrayDeque<>();

        undo.push("typed 'h'");
        undo.push("typed 'i'");
        undo.push("typed '!'");
        System.out.println("history = " + undo);

        // user hits ctrl-z twice
        redo.push(undo.pop());
        redo.push(undo.pop());
        System.out.println("after 2 undos: history = " + undo + ", redo = " + redo);

        // user hits ctrl-y once
        undo.push(redo.pop());
        System.out.println("after 1 redo : history = " + undo + ", redo = " + redo);

        section("8) descendingIterator - walk tail to head");
        Deque<Integer> walk = new ArrayDeque<>();
        for (int n = 1; n <= 5; n++) walk.offerLast(n);
        System.out.print("forward : ");
        for (Integer n : walk) System.out.print(n + " ");
        System.out.println();
        System.out.print("backward: ");
        for (java.util.Iterator<Integer> it = walk.descendingIterator(); it.hasNext(); ) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
