package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * java.util.HashSet&lt;E&gt; - Hash-Table-Backed Set
 * --------------------------------------------
 * HashSet is the default Set implementation. Internally it is a wrapper
 * around a HashMap whose keys are your elements and whose values are a
 * single shared dummy object.
 * <p>
 *
 *      hashSet.add(x)  ===  hashMap.put(x, PRESENT)
 *      hashSet.contains(x) === hashMap.containsKey(x)
 * <p>
 *
 * Why It Exists
 * -------------
 * You frequently need "do I already have this thing?" with O(1) lookup,
 * and you do not care about order. Lists scan linearly - O(n) per lookup;
 * HashSet collapses that to constant time on average.
 * <p>
 *
 * When To Use It
 * --------------
 *   - Deduplicate a collection.
 *   - Track "seen" items (visited URLs, sent IDs, ...).
 *   - Fast membership tests in a stream-style pipeline.
 * <p>
 *
 * The equals/hashCode Contract (Critical)
 * ---------------------------------------
 * If you store your own classes in a HashSet you MUST override BOTH
 * equals and hashCode. The contract says:
 * <p>
 *
 *      if a.equals(b)  then  a.hashCode() == b.hashCode()
 * <p>
 *
 * Violating it does not throw - it silently produces a "broken" set with
 * duplicates and missing lookups.
 * <p>
 *
 * Records (Java 16+) generate correct equals/hashCode for you.
 * <p>
 *
 * Big-O (Expected, Assuming a Good Hash)
 * --------------------------------------
 *   add / remove / contains / size       O(1)
 *   iteration                            O(capacity + size)
 * <p>
 *
 * Worst case (every element hashes to the same bucket) is O(n) per
 * operation, but modern HashSet/HashMap balance with a tree once a bucket
 * gets too long.
 * <p>
 *
 * Constructors
 * ------------
 *   new HashSet&lt;&gt;()
 *   new HashSet&lt;&gt;(int initialCapacity)
 *   new HashSet&lt;&gt;(int initialCapacity, float loadFactor)
 *   new HashSet&lt;&gt;(Collection&lt;? extends E&gt; c)
 */

public class HashSetDemo {

    /** Custom class with proper equals/hashCode - works in a HashSet. */
    static class Good {
        final String name;
        final int    age;
        Good(String name, int age) { this.name = name; this.age = age; }

        @Override public boolean equals(Object o) {
            return o instanceof Good g && g.age == age && Objects.equals(g.name, name);
        }
        @Override public int hashCode() { return Objects.hash(name, age); }
        @Override public String toString() { return name + "(" + age + ")"; }
    }

    /** "Forgot" hashCode - HashSet silently breaks. */
    static class Bad {
        final String name;
        Bad(String name) { this.name = name; }
        @Override public boolean equals(Object o) {
            return o instanceof Bad b && Objects.equals(b.name, name);
        }
        // No hashCode override - inherits Object's, which is identity-based.
        @Override public String toString() { return "Bad(" + name + ")"; }
    }

    public static void main(String[] args) {

        section("1) Basics - add / contains / remove / size / clear");
        HashSet<String> s = new HashSet<>();
        s.add("alpha");
        s.add("beta");
        s.add("alpha");                       // duplicate ignored
        System.out.println("set         = " + s);
        System.out.println("contains a  = " + s.contains("alpha"));
        System.out.println("size        = " + s.size());
        s.remove("alpha");
        System.out.println("after remove= " + s);
        s.clear();
        System.out.println("after clear = " + s);

        section("2) Deduplicate a list");
        List<Integer> input = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);
        java.util.Set<Integer> unique = new HashSet<>(input);
        System.out.println("input  = " + input);
        System.out.println("unique = " + unique + "  (order may vary)");

        section("3) Bulk ops - addAll / removeAll / retainAll");
        HashSet<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
        HashSet<Integer> b = new HashSet<>(List.of(3, 4, 5, 6));
        HashSet<Integer> u = new HashSet<>(a); u.addAll(b);
        HashSet<Integer> i = new HashSet<>(a); i.retainAll(b);
        HashSet<Integer> d = new HashSet<>(a); d.removeAll(b);
        System.out.println("union        = " + u);
        System.out.println("intersection = " + i);
        System.out.println("difference   = " + d);

        section("4) Custom class - WORKS when equals + hashCode are overridden");
        HashSet<Good> good = new HashSet<>();
        good.add(new Good("Alice", 30));
        good.add(new Good("Alice", 30));        // collapsed
        good.add(new Good("Alice", 31));        // different age - new entry
        System.out.println("good = " + good + "  size=" + good.size());

        section("5) Custom class - BROKEN when hashCode is missing");
        HashSet<Bad> bad = new HashSet<>();
        bad.add(new Bad("Alice"));
        bad.add(new Bad("Alice"));              // equal but different hash -> kept both!
        System.out.println("bad = " + bad + "  size=" + bad.size());
        System.out.println("  ^ note: two 'equal' objects but the Set holds both - bug.");

        section("6) Hash collisions - watch the bucket count rise with capacity hint");
        // A modest capacity hint reduces rehashing as the set grows.
        HashSet<Integer> bigger = new HashSet<>(1024);          // pre-sized
        for (int n = 0; n < 1000; n++) bigger.add(n);
        System.out.println("bigger.size() = " + bigger.size());

        section("7) Iteration is UNORDERED");
        HashSet<Integer> rnd = new HashSet<>();
        for (int n : new int[]{10, 1, 5, 2, 8, 3}) rnd.add(n);
        System.out.print("iteration: ");
        for (int n : rnd) System.out.print(n + " ");
        System.out.println("  (do NOT rely on this order)");

        section("8) Stream + Collectors.toSet()");
        var fromStream = java.util.stream.Stream.of("a", "b", "c", "b", "a")
                .collect(java.util.stream.Collectors.toSet());
        System.out.println("fromStream = " + fromStream);

        // OUTPUT (representative; HashSet ordering may differ)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
