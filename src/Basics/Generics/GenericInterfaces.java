package Basics.Generics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Generic Interfaces
 * ------------------
 * Just like classes, an INTERFACE can declare type parameters. Generic
 * interfaces are the foundation of almost every modern Java API:
 *
 *      interface Comparable&lt;T&gt; { int compareTo(T o); }
 *      interface Comparator&lt;T&gt; { int compare(T a, T b); }
 *      interface Iterable&lt;T&gt;   { Iterator&lt;T&gt; iterator(); }
 *      interface Function&lt;T, R&gt; { R apply(T t); }
 *      interface Map.Entry&lt;K, V&gt; { K getKey(); V getValue(); }
 *
 *
 * Two Ways To Implement A Generic Interface
 * -----------------------------------------
 *
 *   1. FIX the type parameter:
 *
 *         class Customer implements Comparable&lt;Customer&gt; {
 *             public int compareTo(Customer o) { ... }
 *         }
 *
 *   2. PASS THROUGH a type parameter the implementing class also declares:
 *
 *         class Stack&lt;E&gt; implements Iterable&lt;E&gt; {
 *             public Iterator&lt;E&gt; iterator() { ... }
 *         }
 *
 *
 * Multiple Generic Interfaces
 * ---------------------------
 * A class can implement more than one generic interface as long as their
 * parameter bindings are consistent:
 *
 *      class Customer implements Comparable&lt;Customer&gt;, Serializable { ... }
 *
 * You CANNOT implement the same generic interface with two different
 * type arguments:
 *
 *      class Bad implements Comparable&lt;Integer&gt;, Comparable&lt;String&gt; { ... }
 *      // ^^^ COMPILE ERROR
 *
 *
 * Functional Generic Interfaces - The Lambdas Bridge
 * --------------------------------------------------
 * Java's standard functional interfaces are all generic:
 *
 *      Function&lt;T, R&gt;        R apply(T)
 *      BiFunction&lt;T, U, R&gt;   R apply(T, U)
 *      Predicate&lt;T&gt;          boolean test(T)
 *      Consumer&lt;T&gt;           void accept(T)
 *      Supplier&lt;T&gt;           T get()
 *      Comparator&lt;T&gt;         int compare(T, T)
 *
 *
 * Implementations Below
 * ---------------------
 *   Container&lt;E&gt;          - our own generic interface
 *   ArrayContainer&lt;E&gt;     - implements with pass-through param
 *   IntContainer          - implements with FIXED param
 *   Repository&lt;K, V&gt;      - multi-param generic interface
 *   InMemoryRepo&lt;K, V&gt;    - pass-through implementor
 *   Cache                 - implements Function&lt;String, String&gt; (fix R = String)
 */

public class GenericInterfaces {

    // ============================================================
    // 1) Our own generic interface
    // ============================================================
    interface Container<E> {
        void add(E item);
        E    get(int index);
        int  size();
        default boolean isEmpty() { return size() == 0; }
    }

    /** Pass-through: ArrayContainer is still generic. */
    static class ArrayContainer<E> implements Container<E> {
        private final Object[] data;
        private int n;
        ArrayContainer(int capacity) { this.data = new Object[capacity]; }
        @Override public void add(E item) { data[n++] = item; }
        @SuppressWarnings("unchecked")
        @Override public E get(int index) { return (E) data[index]; }
        @Override public int size() { return n; }
    }

    /** Fixed parameter: IntContainer is NOT generic - it nails E to Integer. */
    static class IntContainer implements Container<Integer> {
        private int[] data;
        private int n;
        IntContainer(int capacity) { this.data = new int[capacity]; }
        @Override public void add(Integer item) { data[n++] = item; }
        @Override public Integer get(int index) { return data[index]; }
        @Override public int size() { return n; }
    }

    // ============================================================
    // 2) Multi-parameter generic interface
    // ============================================================
    interface Repository<K, V> {
        void put(K key, V value);
        V    get(K key);
        boolean contains(K key);
    }

    static class InMemoryRepo<K, V> implements Repository<K, V> {
        private final java.util.Map<K, V> map = new java.util.HashMap<>();
        @Override public void put(K key, V value) { map.put(key, value); }
        @Override public V    get(K key)          { return map.get(key); }
        @Override public boolean contains(K key)  { return map.containsKey(key); }
    }

    // ============================================================
    // 3) Implementing the JDK's generic interfaces - Comparable / Comparator
    // ============================================================
    record Person(String name, int age) implements Comparable<Person> {
        @Override public int compareTo(Person other) {
            return Integer.compare(age, other.age);    // natural order: by age
        }
    }

    // ============================================================
    // 4) Implementing a FUNCTIONAL generic interface
    // ============================================================
    /** A "function" String -&gt; String. Equivalent to a lambda of type Function&lt;String,String&gt;. */
    static class Cache implements Function<String, String> {
        private final java.util.Map<String, String> store = new java.util.HashMap<>();
        @Override public String apply(String key) {
            return store.computeIfAbsent(key, k -> "value-for-" + k);
        }
    }

    public static void main(String[] args) {

        section("1) Pass-through implementation - ArrayContainer<E>");
        Container<String> sc = new ArrayContainer<>(4);
        sc.add("alpha"); sc.add("beta"); sc.add("gamma");
        System.out.println("size = " + sc.size() + ", get(1) = " + sc.get(1));

        section("2) Fixed-parameter implementation - IntContainer");
        Container<Integer> ic = new IntContainer(4);
        ic.add(10); ic.add(20); ic.add(30);
        System.out.println("size = " + ic.size() + ", sum = " +
                (ic.get(0) + ic.get(1) + ic.get(2)));

        section("3) Multi-parameter repository");
        Repository<String, Integer> ages = new InMemoryRepo<>();
        ages.put("alice", 30); ages.put("bob", 25);
        System.out.println("ages[alice] = " + ages.get("alice"));
        System.out.println("contains(eve)? " + ages.contains("eve"));

        section("4) Comparable<Person> - natural order by age");
        List<Person> people = Arrays.asList(
                new Person("Alice", 30),
                new Person("Bob",   25),
                new Person("Carol", 28)
        );
        people = new java.util.ArrayList<>(people);
        java.util.Collections.sort(people);            // uses Person.compareTo
        people.forEach(p -> System.out.println("  " + p));

        section("5) Comparator<Person> - an EXTERNAL ordering");
        Comparator<Person> byName = Comparator.comparing(Person::name);
        people.sort(byName);
        people.forEach(p -> System.out.println("  " + p));

        section("6) Implementing Function<String, String>");
        Cache cache = new Cache();
        System.out.println("cache(\"first\")  = " + cache.apply("first"));
        System.out.println("cache(\"first\")  = " + cache.apply("first"));
        System.out.println("cache(\"second\") = " + cache.apply("second"));

        // Same shape as the lambda equivalent:
        Function<String, String> lambdaCache = k -> "lambda-" + k;
        System.out.println("lambdaCache(x) = " + lambdaCache.apply("x"));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
