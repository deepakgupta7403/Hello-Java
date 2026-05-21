package Phase5_CollectionsLambdasStreams.Collections;

import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * Vector and Stack - the Legacy List Classes
 * ------------------------------------------
 * Vector and Stack were part of Java 1.0, predating the Collections Framework
 * (which arrived in 1.2). They were RETROFITTED into the framework when it
 * shipped:
 * <p>
 *
 *      Vector implements List         (and is essentially a synchronized ArrayList)
 *      Stack   extends Vector         (LIFO operations on top of Vector)
 * <p>
 *
 * Both are STILL in the JDK for backwards compatibility, but modern code
 * avoids them. The JDK Javadoc explicitly recommends using:
 * <p>
 *
 *      Vector  ->  ArrayList            (if no synchronization needed)
 *                  Collections.synchronizedList(new ArrayList<>())
 *                                       (if you really want coarse-grained sync)
 *                  CopyOnWriteArrayList (read-mostly concurrent use)
 * <p>
 *
 *      Stack   ->  ArrayDeque           (push / pop / peek - faster, not sync)
 * <p>
 *
 * Why Are They Legacy?
 * --------------------
 *   1. EVERY method on Vector is synchronized - a tax you pay even in
 *      single-threaded code.
 *   2. Stack extends Vector, which is widely considered a misuse of
 *      inheritance ("is-a" was the wrong relationship). The result:
 *      Stack inherits index-based add/remove from List, which lets you
 *      poke holes in the middle of what should be a stack.
 *   3. Both expose Enumeration alongside Iterator - two ways to iterate.
 * <p>
 *
 * Vector-Specific Methods (beyond List)
 * -------------------------------------
 *   capacity()                      current backing-array length
 *   ensureCapacity(int)             grow ahead of time
 *   trimToSize()                    shrink to size()
 *   firstElement() / lastElement()  shortcut accessors
 *   addElement / insertElementAt / removeElement* / setElementAt
 *                                    legacy method names
 *   elements()                      returns an Enumeration
 * <p>
 *
 * Stack-Specific Methods
 * ----------------------
 *   push(e)               adds on top, returns the element
 *   pop()                 removes and returns the top, throws if empty
 *   peek()                returns the top without removing
 *   empty()               isEmpty() alias
 *   search(o)             1-based position from the top, -1 if absent
 * <p>
 *
 * This File Demonstrates Both - and Then The Modern Replacement.
 */

public class VectorAndStack {

    public static void main(String[] args) {

        section("1) Vector - synchronized ArrayList, basically");
        Vector<String> v = new Vector<>();
        v.add("alpha");
        v.add("beta");
        v.addElement("gamma");                      // legacy method name
        v.insertElementAt("inserted", 1);
        System.out.println("vector       = " + v);
        System.out.println("get(2)       = " + v.get(2));
        System.out.println("firstElement = " + v.firstElement());
        System.out.println("lastElement  = " + v.lastElement());
        System.out.println("capacity()   = " + v.capacity());
        System.out.println("size()       = " + v.size());
        v.removeElement("inserted");
        System.out.println("after removeElement = " + v);

        section("2) Vector + Enumeration (legacy iteration)");
        Enumeration<String> en = v.elements();
        while (en.hasMoreElements()) {
            System.out.println("  " + en.nextElement());
        }
        // Modern equivalent: a regular for-each (Iterator under the hood).

        section("3) Stack - LIFO using push / pop / peek / search");
        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        s.push(3);
        System.out.println("stack (top last) = " + s);
        System.out.println("peek()   = " + s.peek());           // 3
        System.out.println("search(1)= " + s.search(1));        // 3 (1-based from top)
        System.out.println("pop()    = " + s.pop() + "  stack=" + s);
        System.out.println("empty()  = " + s.empty());

        section("4) Stack's bad inheritance - you can mutate the middle");
        // Because Stack extends Vector, it has List methods on the back-end:
        Stack<String> raw = new Stack<>();
        raw.push("a"); raw.push("b"); raw.push("c");
        raw.add(1, "MIDDLE INSERT");                // legal but breaks stack semantics
        System.out.println("Stack abused = " + raw);

        section("5) Modern replacement - ArrayDeque");
        java.util.ArrayDeque<Integer> stack = new java.util.ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("ArrayDeque stack (top first) = " + stack);
        System.out.println("peek()  = " + stack.peek());
        System.out.println("pop()   = " + stack.pop());

        section("6) Performance - sync tax matters in tight loops");
        final int N = 200_000;
        long t = System.nanoTime();
        Vector<Integer> vp = new Vector<>();
        for (int i = 0; i < N; i++) vp.add(i);
        long vMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        java.util.ArrayList<Integer> alp = new java.util.ArrayList<>();
        for (int i = 0; i < N; i++) alp.add(i);
        long aMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("Vector    add(" + N + "): " + vMs + " ms");
        System.out.println("ArrayList add(" + N + "): " + aMs + " ms  (faster, no sync overhead)");

        section("7) When sync IS desired - prefer the explicit wrapper or concurrent types");
        // Coarse-grained sync, but at least the SYNCHRONIZED is visible at the call site.
        List<Integer> syncList = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
        syncList.addAll(List.of(1, 2, 3));
        synchronized (syncList) {     // still need a lock for iteration
            for (int n : syncList) System.out.print(n + " ");
        }
        System.out.println();
        // For real concurrency, look at CopyOnWriteArrayList or
        // ConcurrentLinkedQueue (see those dedicated files).

        // OUTPUT (representative; timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
