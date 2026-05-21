package Phase5_CollectionsLambdasStreams.Collections;

import java.util.*;

/**
 * java.util.ArrayList - Resizing-Array Implementation of List
 * -----------------------------------------------------------
 * ArrayList is the DEFAULT List implementation in Java. Internally it stores
 * elements in an Object[] and grows the array (typically by 1.5x) when it
 * gets full.
 * <p>
 *
 * Why It Exists
 * -------------
 * Plain arrays are FIXED SIZE and have minimal operations. ArrayList wraps
 * one to give you:
 *   - growth on demand,
 *   - the full List API,
 *   - bidirectional iteration,
 *   - integration with the rest of java.util.
 * <p>
 *
 * When To Use It
 * --------------
 *   - You need an ORDERED collection with INDEX ACCESS.
 *   - Most modifications are at the END (append).
 *   - Random get() is frequent.
 *   - Single-threaded or read-mostly.
 * <p>
 *
 * When NOT To Use It
 * ------------------
 *   - Frequent inserts/removes in the MIDDLE (every shift is O(n)).
 *     -> consider LinkedList (rarely) or a different structure entirely.
 *   - You need uniqueness ->  HashSet.
 *   - You need concurrent writers -> CopyOnWriteArrayList or external sync.
 * <p>
 *
 * Big-O
 * -----
 *   get(i), set(i), size(), isEmpty()                O(1)
 *   add at end                                       O(1) amortised
 *   add/remove at index i                            O(n - i)
 *   contains, indexOf, lastIndexOf                   O(n)
 *   iterator/iteration                               O(n)
 * <p>
 *
 * Constructors
 * ------------
 *   new ArrayList&lt;&gt;()                          // initial capacity 10
 *   new ArrayList&lt;&gt;(int initialCapacity)        // pre-size for known load
 *   new ArrayList&lt;&gt;(Collection&lt;? extends E&gt; c)  // copy of another
 * <p>
 *
 * Capacity vs Size
 * ----------------
 *   - capacity: how big the internal array currently is.
 *   - size:     how many elements you have actually added.
 * ensureCapacity(int) grows ahead of time; trimToSize() shrinks to size().
 * <p>
 *
 * Methods (Complete API, in addition to those inherited from List/Collection)
 * --------------------------------------------------------------------------
 *   ensureCapacity(int)
 *   trimToSize()
 *   clone()                  - returns Object; cast to ArrayList if needed
 */

public class ArrayListDemo {

    public static void main(String[] args) {

        section("1) Constructors");
        ArrayList<String> empty   = new ArrayList<>();
        ArrayList<String> sized   = new ArrayList<>(100);
        ArrayList<String> seeded  = new ArrayList<>(List.of("a", "b", "c"));
        System.out.println("empty   = " + empty);
        System.out.println("sized   = " + sized + "  (size=0 even though capacity=100)");
        System.out.println("seeded  = " + seeded);

        section("2) add / addAll / set / get");
        ArrayList<Integer> a = new ArrayList<>();
        a.add(10);
        a.add(20);
        a.add(30);
        a.addAll(List.of(40, 50));
        a.add(1, 99);                                  // insert at index 1
        System.out.println("a               = " + a);
        System.out.println("get(0)          = " + a.get(0));
        a.set(0, 0);
        System.out.println("after set(0,0)  = " + a);

        section("3) remove - two flavours");
        // remove(int)  -> by INDEX, returns the removed element
        // remove(Object) -> by VALUE, returns boolean
        a.remove(2);                                   // index 2
        System.out.println("after remove(2)   = " + a);
        a.remove(Integer.valueOf(50));                 // value 50
        System.out.println("after remove(50)  = " + a);

        section("4) Iterator + ListIterator");
        ArrayList<String> tokens = new ArrayList<>(List.of("alpha", "beta", "gamma"));

        Iterator<String> it = tokens.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.equals("beta")) it.remove();          // Iterator.remove is safe
        }
        System.out.println("after iter.remove = " + tokens);

        ListIterator<String> lit = tokens.listIterator();
        while (lit.hasNext()) {
            String s = lit.next();
            lit.set(s.toUpperCase());                   // replace in place
        }
        lit.add("DELTA");                                // append at the cursor
        System.out.println("after listIterator= " + tokens);

        section("5) contains / indexOf / isEmpty / size / clear");
        ArrayList<Integer> bag = new ArrayList<>(List.of(1, 2, 3, 2));
        System.out.println("contains(2)  = " + bag.contains(2));
        System.out.println("indexOf(2)   = " + bag.indexOf(2));
        System.out.println("lastIndexOf(2)= " + bag.lastIndexOf(2));
        System.out.println("size()       = " + bag.size());
        System.out.println("isEmpty()    = " + bag.isEmpty());
        bag.clear();
        System.out.println("after clear  = " + bag);

        section("6) Capacity hints - ensureCapacity / trimToSize");
        ArrayList<Integer> nums = new ArrayList<>();
        nums.ensureCapacity(1_000);                      // pre-size to avoid resizes
        for (int i = 0; i < 1_000; i++) nums.add(i);
        nums.trimToSize();                                // back down to fit
        System.out.println("size after fill+trim = " + nums.size());

        section("7) replaceAll / removeIf / sort");
        ArrayList<Integer> r = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));
        r.replaceAll(n -> n * 2);
        r.removeIf(n -> n > 10);
        r.sort(java.util.Comparator.naturalOrder());
        System.out.println("after replaceAll/removeIf/sort = " + r);

        section("8) toArray / Stream");
        Object[] o1 = r.toArray();
        Integer[] o2 = r.toArray(new Integer[0]);
        Integer[] o3 = r.toArray(Integer[]::new);       // Java 11+
        System.out.println("toArray sizes = " + o1.length + " / " + o2.length + " / " + o3.length);

        long even = r.stream().filter(n -> n % 2 == 0).count();
        System.out.println("even count via stream = " + even);

        section("9) subList - view, not a copy");
        ArrayList<Integer> big = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5));
        List<Integer> mid = big.subList(2, 5);
        mid.set(0, -99);
        System.out.println("big after subList edit = " + big);

        section("10) clone()");
        ArrayList<Integer> copy = (ArrayList<Integer>) big.clone();
        copy.add(999);
        System.out.println("big  = " + big);
        System.out.println("copy = " + copy);

        section("11) Sort with Collections.sort vs List.sort");
        ArrayList<Integer> s = new ArrayList<>(List.of(5, 1, 4, 2, 3));
        Collections.sort(s);                              // legacy way
        System.out.println("Collections.sort -> " + s);
        s.sort(java.util.Comparator.reverseOrder());      // modern, on the List itself
        System.out.println("s.sort(reverse)  -> " + s);

        // OUTPUT (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
