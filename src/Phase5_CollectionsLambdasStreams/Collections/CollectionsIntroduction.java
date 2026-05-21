package Phase5_CollectionsLambdasStreams.Collections;

/**
 * Java Collections Framework - Introduction
 * -----------------------------------------
 * The COLLECTIONS FRAMEWORK is the standard library for storing and
 * manipulating groups of objects. It is one of the most important pieces of
 * the JDK; almost every non-trivial Java program uses it.
 * <p>
 *
 * Why a Framework, Not Just Arrays?
 * ---------------------------------
 * Arrays are simple but limited:
 *   - FIXED SIZE.
 *   - One ELEMENT TYPE only.
 *   - Few BUILT-IN OPERATIONS (no search, sort, remove-by-value).
 *   - No POLYMORPHISM across "list-like things".
 * <p>
 *
 * The Collections Framework gives you:
 *   - GROWABLE containers that resize automatically.
 *   - Many SHAPES (List, Set, Queue, Map, Deque) for different access patterns.
 *   - A RICH METHOD SET (add, remove, contains, sort, filter, ...).
 *   - INTERFACES so you can swap implementations without changing callers.
 *   - INTEROPERABILITY with streams, lambdas, parallel processing, and the
 *     rest of java.util.
 * <p>
 *
 * The Hierarchy in One Picture
 * ----------------------------
 * <p>
 *
 *      Iterable<E>
 *          |
 *          v
 *      Collection<E> ---------------------------------+
 *          |                                          |
 *          +---- List<E>                              +---- Map<K,V>      (NOT a Collection - separate root)
 *          |       |-- ArrayList                              |
 *          |       |-- LinkedList                             |-- HashMap
 *          |       |-- Vector  (legacy)                       |-- LinkedHashMap
 *          |       |-- CopyOnWriteArrayList                   |-- TreeMap
 *          |                                                  |-- ConcurrentHashMap
 *          +---- Set<E>                                       |-- Hashtable  (legacy)
 *          |       |-- HashSet
 *          |       |     `-- LinkedHashSet
 *          |       |-- TreeSet     (also SortedSet, NavigableSet)
 *          |       |-- EnumSet
 *          |       |-- CopyOnWriteArraySet
 *          |
 *          +---- Queue<E>
 *          |       |-- PriorityQueue
 *          |       |-- ArrayBlockingQueue, LinkedBlockingQueue (concurrent)
 *          |       `-- Deque<E>
 *          |             |-- ArrayDeque
 *          |             |-- LinkedList (yes, also a List!)
 *          |             `-- BlockingDeque (concurrent)
 * <p>
 *
 * NOTE: Map is NOT a sub-type of Collection. A Map is a separate root
 * because its operations work on KEY/VALUE PAIRS, not on single elements.
 * <p>
 *
 * Three "Buckets" of Implementations
 * ----------------------------------
 *   1. ORDER-PRESERVING (List, Queue, Deque) - keep insertion order or a
 *      meaningful "first / last".
 *   2. UNIQUENESS (Set) - no duplicates, no positional access by default.
 *   3. KEY -> VALUE (Map) - look up a value by a key.
 * <p>
 *
 * Picking The Right Implementation (90% Of Real-World Use)
 * --------------------------------------------------------
 *   List of stuff, mostly read           -> ArrayList
 *   Frequent insert/remove in the middle -> LinkedList   (rare in practice)
 *   Need uniqueness                      -> HashSet
 *   Uniqueness + sorted order            -> TreeSet
 *   Uniqueness + insertion order         -> LinkedHashSet
 *   Key/value lookup                     -> HashMap
 *   Key/value + insertion order          -> LinkedHashMap
 *   Key/value + sorted by key            -> TreeMap
 *   FIFO queue                           -> ArrayDeque
 *   Priority queue                       -> PriorityQueue
 *   Stack                                -> ArrayDeque (push / pop / peek)
 * <p>
 *
 * What This Folder Covers
 * -----------------------
 *   CollectionsIntroduction.java         (this file)
 *   CollectionInterface.java             - methods on the root Collection<E>
 *   CollectionsClass.java                - java.util.Collections static helpers
 *   ListInterface.java + ArrayListDemo / LinkedListDemo
 *   SetInterface.java  + HashSet / LinkedHashSet / TreeSet
 *   QueueInterface.java + PriorityQueueDemo
 *   DequeInterface.java
 *   MapInterface.java  + HashMap / LinkedHashMap / TreeMap
 *   IteratorDemo.java
 *   ComparatorComparable.java
 *   ModernCollections.java               - Java 8-21 (factory methods,
 *                                          Stream collectors, Sequenced
 *                                          collections)
 *   FaceDetectionApp/                    - end-to-end project using many
 *                                          collection types together.
 */

public class CollectionsIntroduction {

    public static void main(String[] args) {

        section("1) Polymorphism through the interface hierarchy");
        // ALL of these implement the Collection<E> contract, so we can use
        // the same code path on any of them.
        java.util.Collection<String> list  = new java.util.ArrayList<>(java.util.List.of("a", "b", "c"));
        java.util.Collection<String> set   = new java.util.HashSet<>(java.util.List.of("a", "b", "c", "a"));
        java.util.Collection<String> queue = new java.util.ArrayDeque<>(java.util.List.of("a", "b", "c"));

        printSize("ArrayList ", list);
        printSize("HashSet   ", set);     // duplicates collapsed
        printSize("ArrayDeque", queue);

        section("2) The Map root - separate API for key/value");
        java.util.Map<String, Integer> ages = new java.util.HashMap<>();
        ages.put("Alice", 30);
        ages.put("Bob",   25);
        System.out.println("ages = " + ages);
        System.out.println("ages.get(\"Alice\") = " + ages.get("Alice"));

        section("3) Same data, different collection type");
        java.util.List<Integer> input = java.util.List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        System.out.println("input              = " + input);
        System.out.println("as HashSet          = " + new java.util.HashSet<>(input));
        System.out.println("as LinkedHashSet    = " + new java.util.LinkedHashSet<>(input));
        System.out.println("as TreeSet (sorted) = " + new java.util.TreeSet<>(input));

        section("4) Big-O cheatsheet (memorise once)");
        System.out.println("""
                Operation       ArrayList    LinkedList    HashSet     TreeSet     HashMap
                add at end       O(1)*        O(1)         O(1)*       O(log n)    O(1)*
                add at index     O(n)         O(n)          n/a         n/a         n/a
                get(index)       O(1)         O(n)          n/a         n/a         n/a (get(key))
                contains(x)      O(n)         O(n)         O(1)*       O(log n)    n/a (containsKey)
                remove(x)        O(n)         O(n)         O(1)*       O(log n)    O(1)*
                * amortised, assumes good hash distribution where applicable
                """);

        // OUTPUT (representative)
    }

    private static void printSize(String label, java.util.Collection<?> c) {
        System.out.println(label + " size=" + c.size() + "  contents=" + c);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
