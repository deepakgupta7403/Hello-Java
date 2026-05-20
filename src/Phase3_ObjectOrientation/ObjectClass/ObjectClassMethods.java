package Phase3_ObjectOrientation.ObjectClass;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * java.lang.Object - The Root of Every Class
 * ------------------------------------------
 * Every class in Java is a descendant of java.lang.Object. Even if you write
 * `class Foo {}` with no `extends` clause, the compiler inserts
 * `extends Object` automatically. That means EVERY OBJECT inherits ten methods
 * from Object - some you override constantly, some you should leave alone.
 *
 *
 * Methods Inherited from Object
 * -----------------------------
 *
 *      1. equals(Object o)              - logical equality test
 *      2. hashCode()                    - integer hash (used by HashMap / HashSet)
 *      3. toString()                    - human-readable representation
 *      4. getClass()                    - runtime Class<?> of the object
 *      5. clone()                       - protected; returns a field-by-field copy
 *      6. finalize()                    - DEPRECATED, JVM cleanup hook
 *      7. wait() / wait(timeout)        - thread coordination
 *      8. notify() / notifyAll()        - thread coordination
 *
 *
 * Methods You Almost Always OVERRIDE
 * ----------------------------------
 *      equals(Object)   - to define "two objects are logically the same"
 *      hashCode()       - MUST be overridden together with equals()
 *      toString()       - for human-readable logging / debugging
 *
 *
 * The equals/hashCode Contract
 * ----------------------------
 *  - If a.equals(b) is TRUE, then a.hashCode() == b.hashCode().
 *  - The reverse is NOT required (different objects may share a hash).
 *  - equals must be REFLEXIVE, SYMMETRIC, TRANSITIVE, CONSISTENT, and
 *    null-safe (a.equals(null) -> false).
 *  - Break this contract and HashMap / HashSet / HashTable / LinkedHashSet
 *    will silently misbehave.
 *
 *
 * What NOT to Override
 * --------------------
 *  - getClass()  is FINAL.
 *  - clone()     is brittle - prefer copy constructors or static factories.
 *  - finalize()  is DEPRECATED for removal; use try-with-resources or Cleaner.
 *  - wait/notify - use higher-level concurrency utilities (Lock, Semaphore,
 *                  CompletableFuture) instead of these primitives.
 *
 *
 * Modern Tip
 * ----------
 * The `record` keyword (Java 16+) auto-generates correct equals, hashCode and
 * toString from the components - free of charge. Records are the right choice
 * for simple data carriers.
 */

public class ObjectClassMethods {

    // ============================================================
    // A typical value-object with all three methods overridden
    // ============================================================
    static final class Point {
        private final int x, y;

        public Point(int x, int y) { this.x = x; this.y = y; }
        public int x() { return x; }
        public int y() { return y; }

        @Override
        public boolean equals(Object o) {
            // 1. identity short-cut
            if (this == o) return true;
            // 2. type + null check (using getClass keeps equals SYMMETRIC under subclassing)
            if (o == null || getClass() != o.getClass()) return false;
            // 3. cast + field comparison
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            // Objects.hash is convenient; Objects.hashCode is for one field.
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point(" + x + "," + y + ")";
        }
    }

    // ============================================================
    // A class that does NOT override equals / hashCode - see what happens
    // ============================================================
    static class Defaulted {
        int v;
        Defaulted(int v) { this.v = v; }
    }

    public static void main(String[] args) {

        section("equals / hashCode / toString");
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(5, 1);

        System.out.println("p1.toString()   = " + p1);              // overridden
        System.out.println("p1.equals(p2)   = " + p1.equals(p2));   // true
        System.out.println("p1.equals(p3)   = " + p1.equals(p3));   // false
        System.out.println("p1.hashCode()   = " + p1.hashCode());
        System.out.println("p2.hashCode()   = " + p2.hashCode());   // same as p1

        // Use in a HashSet - the override makes deduplication work
        Set<Point> set = new HashSet<>();
        set.add(p1);
        set.add(p2);   // duplicate (equal to p1) - ignored
        set.add(p3);
        System.out.println("set size        = " + set.size());      // 2

        section("Class without equals/hashCode - falls back to identity");
        Defaulted d1 = new Defaulted(7);
        Defaulted d2 = new Defaulted(7);
        System.out.println("d1.equals(d2)  = " + d1.equals(d2));    // false!
        System.out.println("d1.hashCode()  = " + d1.hashCode());
        System.out.println("d2.hashCode()  = " + d2.hashCode());    // different
        System.out.println("d1.toString()  = " + d1);               // ClassName@hexHash

        Set<Defaulted> dset = new HashSet<>();
        dset.add(d1); dset.add(d2);
        System.out.println("dset size       = " + dset.size());     // 2 - not deduplicated

        section("getClass() - runtime type information");
        Object obj = "hello";
        System.out.println("obj.getClass()        = " + obj.getClass());
        System.out.println("obj.getClass().name() = " + obj.getClass().getName());
        System.out.println("obj instanceof String = " + (obj instanceof String));

        section("clone() - shallow copy via Cloneable (kept for completeness)");
        ClonableCounter c = new ClonableCounter(5);
        try {
            ClonableCounter copy = (ClonableCounter) c.clone();
            System.out.println("original = " + c.value + ", copy = " + copy.value);
            copy.value = 99;
            System.out.println("after edit copy:   original=" + c.value + " copy=" + copy.value);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        section("Object identity hash code");
        // System.identityHashCode returns the hash Object.hashCode() WOULD have
        // returned, even if hashCode() has been overridden.
        System.out.println("identityHashCode(p1) = " + System.identityHashCode(p1));
        System.out.println("p1.hashCode()        = " + p1.hashCode());

        section("Record alternative (Java 16+) - automatic equals/hashCode/toString");
        Coord r1 = new Coord(3, 4);
        Coord r2 = new Coord(3, 4);
        System.out.println("r1 = " + r1);
        System.out.println("r1.equals(r2)        = " + r1.equals(r2));
        System.out.println("r1.hashCode()        = " + r1.hashCode());

        // OUTPUT
        // (matches the inline comments above)
    }

    /** A simple Cloneable example - prefer copy constructors in new code. */
    static class ClonableCounter implements Cloneable {
        int value;
        ClonableCounter(int v) { this.value = v; }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();           // shallow field-by-field copy
        }
    }

    /** Record - same as Point above but auto-generates equals/hashCode/toString. */
    record Coord(int x, int y) {}

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
