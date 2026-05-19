package Basics.MemoryAllocation;

/**
 * How Java Objects Are Stored in Memory
 * -------------------------------------
 * Every Java object lives on the HEAP, even tiny wrappers like Integer. The
 * exact byte layout is JVM-specific, but on HotSpot / OpenJDK every object
 * is built from three pieces:
 *
 *      +-----------------+ <- start of object  (8-byte aligned address)
 *      |  Object Header  |  12 bytes typical (with compressed oops)
 *      +-----------------+
 *      |   Instance      |  fields packed in declaration order,
 *      |   Fields        |  longs/doubles first if alignment helps
 *      +-----------------+
 *      |   Padding       |  to make total size a multiple of 8 bytes
 *      +-----------------+
 *
 *
 * Header Contents (HotSpot, simplified)
 * -------------------------------------
 *   - Mark Word (8 bytes on 64-bit): hash code, lock info, GC age bits, ...
 *   - Klass Pointer (4 bytes with -XX:+UseCompressedClassPointers, else 8):
 *     points to the class metadata in the Method Area.
 *   - Arrays add a 4-byte length field.
 *
 * That gives a typical 12-byte header for ordinary objects and 16 bytes for
 * arrays (plus alignment padding).
 *
 *
 * "Reference" - what does an Object variable hold?
 * ------------------------------------------------
 * A reference is essentially a POINTER. On 64-bit JVMs HotSpot uses
 * COMPRESSED OOPS (Ordinary Object Pointers) - 32-bit references that the
 * JVM scales back to 64-bit addresses transparently - to save memory.
 *
 *
 * Where Different Things Live
 * ---------------------------
 *
 *      Customer c = new Customer("Alice", 30);
 *
 *      Stack frame:
 *          c  ----+----+   (4 bytes - compressed oop)
 *                 |    |
 *                 v
 *      Heap:
 *      +---------+
 *      | header  |  12 bytes
 *      +---------+
 *      | name    |--> [String object on heap]
 *      +---------+
 *      | age     |  4 bytes (int)
 *      +---------+
 *      | padding |
 *      +---------+
 *
 *   - The variable `c` is on the STACK and holds a reference.
 *   - The Customer instance is on the HEAP.
 *   - The String "Alice" is ALSO on the heap (a separate object).
 *
 *
 * String Pool
 * -----------
 * Compile-time string literals are interned in a special table inside the
 * heap (since Java 7). Two identical literals share the same object. The
 * intern() method can move a runtime-built string into that pool. See
 * Basics/Strings/StringIntroduction.java for the demo.
 *
 *
 * Boxed Wrappers Allocate Too
 * ---------------------------
 * Autoboxing creates a new Integer / Double / etc. on the heap unless the
 * value falls into the Integer cache range (-128..127 by default). That
 * is why tight loops with `Long sum = 0L; sum += i` are slow - each += is
 * an allocation. Use primitives in hot paths.
 *
 *
 * Sizing Tools
 * ------------
 *   - The JOL library (`org.openjdk.jol`) can print exact object layouts.
 *   - `java.lang.instrument.Instrumentation.getObjectSize` gives the size
 *     of one object - but excludes the objects it references.
 *
 * This file uses neither; we just demonstrate the EFFECTS of allocation
 * with the Runtime API.
 */

public class ObjectsInMemory {

    // ============================================================
    // Two small classes whose layouts we will allocate
    // ============================================================
    static class Customer {
        String name;
        int    age;
        Customer(String name, int age) { this.name = name; this.age = age; }
    }

    static class Big {
        long a, b, c, d, e, f, g, h;            // 64 bytes of longs
        Customer owner;
        int    counter;
        Big(Customer owner) { this.owner = owner; }
    }

    public static void main(String[] args) {

        section("1) Local variables on the stack, objects on the heap");
        Customer c = new Customer("Alice", 30);
        // `c` lives in the current stack frame as a reference.
        // The Customer instance and the String "Alice" live on the heap.
        System.out.println("c is a reference to: " + c.getClass().getName() + " @ " + System.identityHashCode(c));
        System.out.println("c.name is a reference to a separate String: \"" + c.name + "\"");

        section("2) Multiple variables can point at the same object");
        Customer alias = c;                       // copies the reference, not the object
        System.out.println("c == alias        : " + (c == alias));
        System.out.println("identityHashCode  : " + System.identityHashCode(c) + " == "
                                                  + System.identityHashCode(alias));

        section("3) Allocating many objects - watch heap usage rise");
        long mb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long before = rt.totalMemory() - rt.freeMemory();
        Big[] bag = new Big[200_000];
        for (int i = 0; i < bag.length; i++) {
            bag[i] = new Big(new Customer("u" + i, i % 100));
        }
        long after = rt.totalMemory() - rt.freeMemory();
        System.out.printf("heap before: %5d MB%n", before / mb);
        System.out.printf("heap after : %5d MB   for %d Big objects + %d Customers%n",
                after / mb, bag.length, bag.length);

        // Hand-wavy estimate: each Big ~ 24-byte header + 64-byte longs + 2 refs
        // + Customer (~ 24 bytes header + 1 ref + 1 int + padding) + String header
        // + char[] for the name. The number you observe is a few times that
        // because of allocation rounding and the eden-space generation.

        section("4) Boxing creates new objects (outside the Integer cache)");
        // Integer.valueOf caches -128..127 -> shared objects, == is true.
        Integer a = 100, b = 100;
        Integer x = 200, y = 200;
        System.out.println("100 == 100 ? " + (a == b));      // true (cached)
        System.out.println("200 == 200 ? " + (x == y));      // false (separate objects)

        section("5) Identity hash code is stable BUT may shift between collectors");
        System.out.println("identityHashCode(c)   = " + System.identityHashCode(c));
        System.out.println("c.hashCode()          = " + c.hashCode());
        // identityHashCode is what Object.hashCode() WOULD return if it were not
        // overridden. Useful for diagnostic logs that need to distinguish
        // two objects with the same contents.

        // KEEP the reference alive so the GC doesn't free bag[] too early.
        System.out.println("\nfirst owner = " + bag[0].owner.name);

        // OUTPUT (sizes vary; the relative shape is the point)
        // (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
