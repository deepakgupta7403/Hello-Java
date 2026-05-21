package Phase3_ObjectOrientation.NestedClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Inner (non-static member) Classes
 * ---------------------------------
 * An inner class is a NON-STATIC nested class. Every instance carries a
 * HIDDEN REFERENCE to an instance of the enclosing outer class. That
 * lets it access outer fields and methods directly.
 * <p>
 *
 *      public class Outer {
 *          private int n;
 *          public class Inner {
 *              int twice() { return n * 2; }  // 'n' belongs to the outer
 *          }
 *      }
 * <p>
 *
 *      Outer o = new Outer();
 *      Inner i = o.new Inner();                 // note the prefix
 * <p>
 *
 * The hidden reference
 * --------------------
 * The inner instance keeps the outer instance ALIVE for GC purposes.
 * That's why event listeners declared as inner classes can leak the
 * Activity / Fragment / Frame they live in. PREFER static nested for
 * helpers that don't need outer state.
 * <p>
 *
 * `Outer.this`
 * ------------
 * Inside the inner class, `this` refers to the inner instance and
 * `Outer.this` refers to the enclosing instance. Useful when names
 * shadow each other.
 * <p>
 *
 * Use cases (legitimate)
 * ----------------------
 *   - Iterator implementations that need access to the collection.
 *   - Adapter classes tied to an instance.
 *   - The Observer pattern where the listener really IS part of the
 *     observed object's identity.
 */

public class InnerClass {

    /** Linked-list-like container that exposes an iterator over its data. */
    public static class Bag<T> implements Iterable<T> {
        private final List<T> items = new ArrayList<>();

        public void add(T t) { items.add(t); }

        // BagIterator is an inner class — it needs to read `items`.
        @Override public Iterator<T> iterator() { return new BagIterator(); }

        private class BagIterator implements Iterator<T> {
            private int i = 0;
            @Override public boolean hasNext() { return i < items.size(); }
            @Override public T next() {
                if (i >= items.size()) throw new NoSuchElementException();
                return items.get(i++);
            }
        }
    }

    /** Demonstrating Outer.this. */
    public static class Outer {
        private int n = 1;
        public class Inner {
            private int n = 99;
            void show() {
                System.out.println("inner n  = " + this.n);
                System.out.println("outer n  = " + Outer.this.n);
            }
        }
    }

    public static void main(String[] args) {

        section("1) Inner iterator");
        Bag<String> bag = new Bag<>();
        bag.add("alpha"); bag.add("beta"); bag.add("gamma");
        for (String s : bag) System.out.println("  " + s);

        section("2) Outer.this");
        Outer o = new Outer();
        Outer.Inner i = o.new Inner();
        i.show();

        section("3) Construction syntax");
        // SHORT FORM when you're inside the outer:
        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();
        System.out.println("inner = " + inner);

        section("4) The leak risk");
        // If you give 'inner' to a long-lived listener registry, 'outer' is
        // kept alive too. To break that, make the inner class STATIC and
        // store an explicit reference if needed.

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
