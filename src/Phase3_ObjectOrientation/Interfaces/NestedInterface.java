package Phase3_ObjectOrientation.Interfaces;

import java.util.Iterator;

/**
 * Nested Interfaces
 * -----------------
 * An interface declared INSIDE another type (a class or another interface) is
 * a NESTED INTERFACE. It is a way to scope a contract to the type that owns
 * it - the interface "belongs" to the outer type both conceptually and in
 * its fully qualified name.
 *
 *      class List {
 *          interface Iterator { ... }       // nested
 *      }
 *      // fully qualified name: List.Iterator
 *
 *
 * Two Flavours
 * ------------
 *   1. Nested inside a CLASS
 *        - Implicitly STATIC (every nested type inside an interface is also
 *          implicitly static; inside a class, the `static` keyword is allowed
 *          for clarity but is the default for nested interfaces too).
 *        - Can be private / protected / package-private / public (controls
 *          who outside the file can see it).
 *
 *   2. Nested inside an INTERFACE
 *        - Implicitly PUBLIC and STATIC.
 *
 *
 * Why Nest an Interface?
 * ----------------------
 *   - LOGICAL GROUPING - the interface is meaningful only in the context of
 *     the outer type (Map.Entry, ChronoLocalDate.Era, Stream.Builder).
 *   - NAMESPACE HYGIENE - prevents top-level name pollution.
 *   - ACCESS CONTROL - a `private` nested interface is invisible to other
 *     files, useful for internal abstractions inside a class.
 *
 *
 * JDK Examples
 * ------------
 *   Map.Entry<K,V>                - per-entry view of a Map
 *   Iterable.Iterator<T>          - (Iterator is now a top-level interface,
 *                                    but historically it was nested)
 *   AbstractMap.SimpleEntry<K,V>  - a class implementing Map.Entry
 *   Builder patterns              - inner Builder interface owned by the type
 *
 *
 * Rules To Remember
 * -----------------
 *   - You CANNOT instantiate an interface directly - nested or not.
 *   - Use the qualified name to refer to it from outside:
 *
 *         Outer.NestedInterface nested = new Outer.NestedInterface() { ... };
 *
 *   - A nested interface can extend other interfaces, just like a top-level one.
 *   - Inside the outer type's body you refer to it by its simple name.
 */

public class NestedInterface {

    // ============================================================
    // 1) Interface nested inside a CLASS
    // ============================================================
    static class SimpleList<E> {

        /**
         * A read-only cursor over a SimpleList.
         * It is meaningful only in the context of SimpleList, so we nest it.
         */
        public interface Cursor<E> {
            boolean hasNext();
            E next();
        }

        private final Object[] data;
        public SimpleList(Object[] data) { this.data = data; }

        /** Returns a cursor implementing the nested interface. */
        @SuppressWarnings("unchecked")
        public Cursor<E> cursor() {
            return new Cursor<E>() {
                int i = 0;
                @Override public boolean hasNext() { return i < data.length; }
                @Override public E       next()    { return (E) data[i++]; }
            };
        }
    }

    // ============================================================
    // 2) Interface nested inside another INTERFACE
    // ============================================================
    interface Repository<T> {

        /**
         * A SAVE callback contract. Nested inside Repository because it only
         * makes sense in that context. Implicitly public + static.
         */
        interface SaveListener<T> {
            void saved(T value);
        }

        void save(T value, SaveListener<T> listener);
    }

    static class InMemoryRepo<T> implements Repository<T> {
        @Override
        public void save(T value, SaveListener<T> listener) {
            // ... persist value somewhere ...
            listener.saved(value);
        }
    }

    // ============================================================
    // 3) PRIVATE nested interface - hidden from the outside world
    // ============================================================
    static class Cache {

        /**
         * `private` nested interface - usable only by Cache itself. Other
         * classes cannot even mention the type Cache.Eviction.
         */
        private interface Eviction {
            String pick();
        }

        private final Eviction policy;
        public Cache() {
            // Anonymous implementation of the private nested interface.
            this.policy = () -> "random-key";
        }
        public String evict() { return policy.pick(); }
    }

    public static void main(String[] args) {

        section("1) Interface nested in a class - SimpleList.Cursor");
        SimpleList<String> list = new SimpleList<>(new Object[]{"a", "b", "c"});
        SimpleList.Cursor<String> c = list.cursor();
        while (c.hasNext()) System.out.print(c.next() + " ");
        System.out.println();

        section("2) Interface nested in an interface - Repository.SaveListener");
        Repository<String> repo = new InMemoryRepo<>();
        repo.save("hello", v -> System.out.println("saved: " + v));     // lambda

        section("3) Private nested interface - invisible from outside");
        Cache cache = new Cache();
        System.out.println("evict() = " + cache.evict());
        // Cache.Eviction e = ...  // ERROR - Eviction has private access

        section("4) Real-world example - Map.Entry");
        java.util.Map<String, Integer> m = java.util.Map.of("a", 1, "b", 2);
        for (java.util.Map.Entry<String, Integer> e : m.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
        // Map.Entry is the nested interface; getKey()/getValue() come from it.

        // OUTPUT
        // ====== 1) Interface nested in a class - SimpleList.Cursor ======
        // a b c
        // ====== 2) Interface nested in an interface - Repository.SaveListener ======
        // saved: hello
        // ====== 3) Private nested interface - invisible from outside ======
        // evict() = random-key
        // ====== 4) Real-world example - Map.Entry ======
        // a -> 1
        // b -> 2
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    // A small unused import-style note: `Iterator` referenced here only to
    // anchor the import - real JDK example is in main().
    @SuppressWarnings("unused")
    private static Iterator<String> unused;
}
