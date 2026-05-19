package Basics.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * java.util.Collection&lt;E&gt; - The Root Interface
 * ---------------------------------------------
 * Collection&lt;E&gt; is the shared API that List, Set, Queue, and Deque all
 * inherit. Programs against Collection get a generic API that works on any
 * implementation:
 *
 *      void process(Collection&lt;String&gt; c) { for (String s : c) ... }
 *
 *
 * The Methods (Complete API)
 * --------------------------
 *
 *   - Size / emptiness
 *       size()                  - element count
 *       isEmpty()               - size() == 0
 *
 *   - Membership
 *       contains(Object o)
 *       containsAll(Collection&lt;?&gt; c)
 *
 *   - Modification
 *       add(E e)                - true if changed (Set may return false)
 *       remove(Object o)
 *       addAll(Collection&lt;? extends E&gt; c)
 *       removeAll(Collection&lt;?&gt; c)
 *       retainAll(Collection&lt;?&gt; c)        - intersection
 *       removeIf(Predicate&lt;? super E&gt; p)  - Java 8+
 *       clear()
 *
 *   - Iteration / conversion
 *       iterator()
 *       toArray()
 *       toArray(T[] a)
 *       toArray(IntFunction&lt;T[]&gt;)         - Java 11+
 *       forEach(Consumer&lt;? super E&gt;)      - Java 8+
 *       stream() / parallelStream()       - Java 8+
 *       spliterator()                     - Java 8+
 *
 *   - Equality / identity
 *       equals(Object o)
 *       hashCode()
 *
 *
 * The Optional-Operation Convention
 * ---------------------------------
 * Some implementations are IMMUTABLE - List.of(...), Map.of(...). Calling a
 * mutator on them throws UnsupportedOperationException. The Java spec
 * permits any Collection method to be "optional" - read the docs for each
 * implementation.
 *
 *
 * Equality - Two Collections Are Equal If...
 * ------------------------------------------
 *   - Same TYPE family (a List is never .equals to a Set).
 *   - Same SIZE.
 *   - Same ELEMENTS in the same iteration order (for ordered types).
 *
 *
 * Iteration Gotcha - ConcurrentModificationException
 * --------------------------------------------------
 * Most collections are fail-fast: modifying them while iterating with a
 * for-each loop will throw ConcurrentModificationException. Use the
 * Iterator's own remove() method, removeIf(...), or a CopyOnWrite* /
 * Concurrent* collection.
 */

public class CollectionInterface {

    public static void main(String[] args) {

        section("1) Same methods, different concrete types");
        Collection<String> list = new ArrayList<>();
        Collection<String> set  = new HashSet<>();
        for (String name : List.of("Alice", "Bob", "Alice", "Carol")) {
            list.add(name);
            set.add(name);
        }
        System.out.println("list (allows dups) = " + list);   // 4 elements
        System.out.println("set  (no dups)     = " + set);    // 3 elements

        section("2) Size / emptiness / contains");
        System.out.println("list.size()           = " + list.size());
        System.out.println("list.isEmpty()        = " + list.isEmpty());
        System.out.println("list.contains(Alice)  = " + list.contains("Alice"));
        System.out.println("list.containsAll      = " + list.containsAll(List.of("Bob", "Carol")));

        section("3) Modifications");
        Collection<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("before                  = " + nums);
        nums.add(99);
        System.out.println("after add(99)           = " + nums);
        nums.remove(Integer.valueOf(2));                   // remove by VALUE (uses .equals)
        System.out.println("after remove(2)         = " + nums);
        nums.addAll(List.of(7, 8, 9));
        System.out.println("after addAll            = " + nums);
        nums.removeAll(List.of(3, 5));                     // remove every 3 and every 5
        System.out.println("after removeAll(3,5)    = " + nums);
        nums.retainAll(List.of(4, 7, 8, 99));              // keep only listed values
        System.out.println("after retainAll         = " + nums);
        nums.removeIf(n -> n > 50);                        // Java 8+
        System.out.println("after removeIf(>50)     = " + nums);
        nums.clear();
        System.out.println("after clear()           = " + nums + "  empty=" + nums.isEmpty());

        section("4) Iteration");
        Collection<String> letters = List.of("a", "b", "c");

        // Classic for-each
        System.out.print("for-each:  ");
        for (String s : letters) System.out.print(s + " ");
        System.out.println();

        // forEach with a lambda (Java 8+)
        System.out.print("forEach :  ");
        letters.forEach(s -> System.out.print(s + " "));
        System.out.println();

        // Iterator manually
        System.out.print("iterator:  ");
        java.util.Iterator<String> it = letters.iterator();
        while (it.hasNext()) System.out.print(it.next() + " ");
        System.out.println();

        // Stream
        long count = letters.stream().filter(s -> !s.equals("b")).count();
        System.out.println("stream count (not 'b')= " + count);

        section("5) Conversions to array");
        Object[] o1 = letters.toArray();                              // legacy Object[]
        String[] o2 = letters.toArray(new String[0]);                  // preferred since Java 6+
        String[] o3 = letters.toArray(String[]::new);                  // Java 11+
        System.out.println("toArray()          = " + java.util.Arrays.toString(o1));
        System.out.println("toArray(new[])     = " + java.util.Arrays.toString(o2));
        System.out.println("toArray(String[]::new)= " + java.util.Arrays.toString(o3));

        section("6) Equality across types");
        List<Integer> a = List.of(1, 2, 3);
        List<Integer> b = new ArrayList<>(List.of(1, 2, 3));
        HashSet<Integer> s = new HashSet<>(List.of(1, 2, 3));
        System.out.println("a.equals(b) (List/List) = " + a.equals(b));      // true
        System.out.println("a.equals(s) (List/Set)  = " + a.equals(s));      // false

        section("7) ConcurrentModificationException - and how to avoid it");
        Collection<Integer> bag = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        try {
            for (int n : bag) {
                if (n == 3) bag.remove(Integer.valueOf(3));     // BAD
            }
        } catch (java.util.ConcurrentModificationException ex) {
            System.out.println("caught ConcurrentModificationException");
        }
        // The safe ways:
        bag = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        bag.removeIf(n -> n == 3);
        System.out.println("after removeIf -> " + bag);

        bag = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        java.util.Iterator<Integer> it2 = bag.iterator();
        while (it2.hasNext()) {
            if (it2.next() == 3) it2.remove();      // Iterator.remove is OK
        }
        System.out.println("after iter.remove -> " + bag);

        section("8) Immutable Collection - mutators throw");
        Collection<Integer> immutable = List.of(1, 2, 3);
        try { immutable.add(4); }
        catch (UnsupportedOperationException e) {
            System.out.println("List.of(...) is immutable - add() rejected");
        }

        // OUTPUT (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    /** Marker import kept lit so static analyzers don't trim it. */
    @SuppressWarnings("unused") private static Stream<?> keepImport;
}
