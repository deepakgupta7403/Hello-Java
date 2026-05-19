package Basics.ModernJava;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;

/**
 * Sequenced Collections (Java 21)
 * -------------------------------
 * Before Java 21, getting the FIRST or LAST element of a List, LinkedHashSet,
 * or LinkedHashMap was awkward: list.get(0), list.get(list.size()-1),
 * iterator().next(), etc.
 *
 * Java 21 introduces three new interfaces under java.util:
 *
 *      SequencedCollection<E>      (parent of List, Deque, LinkedHashSet)
 *      SequencedSet<E>             (extends SequencedCollection)
 *      SequencedMap<K, V>          (LinkedHashMap, TreeMap)
 *
 * They provide a UNIFORM API for ordered collections:
 *
 *      addFirst(e)        addLast(e)
 *      getFirst()         getLast()
 *      removeFirst()      removeLast()
 *      reversed()                              // a view in reverse order
 *
 * SequencedMap adds:
 *      firstEntry()       lastEntry()
 *      putFirst(k, v)     putLast(k, v)
 *      pollFirstEntry()   pollLastEntry()
 *      sequencedKeySet()  sequencedValues()  sequencedEntrySet()
 *
 *
 * Why It Matters
 * --------------
 *  - One mental model across List / Deque / LinkedHashSet / LinkedHashMap.
 *  - No more list.get(list.size() - 1) and the off-by-one bugs around it.
 *  - reversed() returns a VIEW - cheap, no copy.
 */

public class SequencedCollections {

    public static void main(String[] args) {

        // --- 1) ArrayList - already a SequencedCollection in 21 ---
        SequencedCollection<String> list = new ArrayList<>(List.of("b", "c", "d"));
        list.addFirst("a");
        list.addLast("e");
        System.out.println("list                 = " + list);          // [a, b, c, d, e]
        System.out.println("list.getFirst()      = " + list.getFirst());// a
        System.out.println("list.getLast()       = " + list.getLast()); // e
        System.out.println("list.reversed()      = " + list.reversed()); // [e, d, c, b, a]

        // reversed() is a VIEW - mutations propagate back to the original.
        list.reversed().removeFirst();        // removes 'e' from the back of list
        System.out.println("after reverse-pop    = " + list);          // [a, b, c, d]

        // --- 2) LinkedHashSet is now a SequencedSet ---
        SequencedSet<Integer> set = new LinkedHashSet<>(List.of(2, 3, 4));
        set.addFirst(1);
        set.addLast(5);
        System.out.println("set                  = " + set);           // [1, 2, 3, 4, 5]
        System.out.println("set.reversed()       = " + set.reversed());// [5, 4, 3, 2, 1]

        // --- 3) LinkedHashMap is now a SequencedMap ---
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("two",   2);
        map.put("three", 3);
        map.putFirst("one", 1);
        map.putLast("four",  4);
        System.out.println("map                  = " + map);                  // {one=1, two=2, three=3, four=4}
        System.out.println("map.firstEntry()     = " + map.firstEntry());     // one=1
        System.out.println("map.lastEntry()      = " + map.lastEntry());      // four=4
        System.out.println("map.reversed()       = " + map.reversed());       // {four=4, three=3, two=2, one=1}
        System.out.println("map.pollFirstEntry() = " + map.pollFirstEntry()); // one=1, removed
        System.out.println("map after poll       = " + map);                  // {two=2, three=3, four=4}

        // OUTPUT (matches the inline comments above)
        // list                 = [a, b, c, d, e]
        // list.getFirst()      = a
        // list.getLast()       = e
        // list.reversed()      = [e, d, c, b, a]
        // after reverse-pop    = [a, b, c, d]
        // set                  = [1, 2, 3, 4, 5]
        // set.reversed()       = [5, 4, 3, 2, 1]
        // map                  = {one=1, two=2, three=3, four=4}
        // map.firstEntry()     = one=1
        // map.lastEntry()      = four=4
        // map.reversed()       = {four=4, three=3, two=2, one=1}
        // map.pollFirstEntry() = one=1
        // map after poll       = {two=2, three=3, four=4}
    }
}
