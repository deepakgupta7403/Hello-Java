package Phase6_RuntimeMemoryRegexReflection.MemoryAllocation;

/**
 * Stack vs Heap Memory Allocation
 * -------------------------------
 * One of the most-asked Java interview questions and one of the most useful
 * mental models for reasoning about your code.
 *
 *
 * Side-By-Side
 * ------------
 *
 *  Aspect           | Stack                          | Heap
 *  -----------------+--------------------------------+--------------------------------
 *  Scope            | PER-THREAD                     | SHARED across all threads
 *  Holds            | METHOD FRAMES with locals,     | ALL OBJECTS and arrays
 *                   |   arguments, return address    |
 *  Allocation       | push when method is entered    | `new` requests an allocation
 *                   |   pop when method returns      |   from the GC
 *  Deallocation     | AUTOMATIC at frame pop         | by the GARBAGE COLLECTOR
 *                   |                                |   when the object becomes
 *                   |                                |   unreachable
 *  Speed            | very fast (just a pointer bump)| slower (object header, GC)
 *  Size             | small per thread (~ 512 KB)    | large (often gigabytes)
 *  Lifetime         | tied to the method call        | until no reference remains
 *  Failure mode     | StackOverflowError             | OutOfMemoryError: Java heap
 *                   |   (too-deep recursion)         |   space
 *  Tuning           | -Xss (per-thread stack)        | -Xms, -Xmx, GC choice
 *
 *
 * What Goes Where, Exactly
 * ------------------------
 *
 *      void foo() {
 *          int x = 5;                   // x is on the STACK (primitive local)
 *          String s = "hi";              // 's' is on the STACK; the String
 *                                        //   object itself lives on the HEAP
 *                                        //   (and is interned)
 *          Customer c = new Customer(); // 'c' is on the STACK (a reference);
 *                                        //   the Customer instance is on the HEAP
 *      }
 *
 * When foo() returns:
 *   - The frame is popped - x, s, c disappear instantly.
 *   - The Customer object remains on the heap until the GC reclaims it.
 *
 *
 * Escape Analysis (HotSpot)
 * -------------------------
 * The JIT can prove that an object "does not escape" the method that
 * created it - no reference leaves through return values, threads or fields
 * - and may then place it on the stack (or in registers) instead of the
 * heap. This is invisible to your code; you only see the speedup.
 *
 *
 * Pass-By-Value Reminder
 * ----------------------
 * Java is always pass-by-value. For primitives a copy of the VALUE is
 * pushed onto the called method's frame. For references a copy of the
 * REFERENCE (the pointer) is pushed - both frames point at the SAME heap
 * object.
 *
 *
 * Common Misconceptions
 * ---------------------
 *   "primitives are always on the stack"  - WRONG. A primitive FIELD of an
 *                                            object lives inside the object
 *                                            on the heap.
 *
 *   "the heap is slow"                     - Not really; modern JVMs allocate
 *                                            faster than malloc/free in C.
 *
 *   "stack memory is freed by the GC"      - No - stack frames are popped
 *                                            automatically when the method
 *                                            returns.
 *
 *   "smaller heap = faster GC"             - Often the opposite. Tiny heaps
 *                                            mean MORE GC cycles.
 */

public class StackVsHeap {

    // ============================================================
    // A small object so we can demonstrate references
    // ============================================================
    static class Box {
        int value;
        Box(int v) { this.value = v; }
    }

    public static void main(String[] args) {

        section("1) Primitive on the stack vs object on the heap");
        int a = 42;                                  // 'a' is on the stack frame of main
        Box b = new Box(99);                          // 'b' is on the stack; the Box is on the heap
        System.out.println("a (stack value)        = " + a);
        System.out.println("b (stack reference)    = points to Box on heap");
        System.out.println("b.value (heap field)   = " + b.value);

        section("2) Reassigning a reference does NOT change the heap object");
        Box other = b;                                // 'other' is a SECOND stack reference to the SAME heap Box
        other.value = 100;
        System.out.println("after other.value=100, b.value = " + b.value);
        // Both `b` and `other` saw the heap object change.

        section("3) Pass-by-value semantics");
        int  pv = 1;
        Box  rv = new Box(1);
        tryToChangePrimitive(pv);
        tryToChangeReference(rv);
        tryToReassignReference(rv);
        System.out.println("pv after method call          = " + pv);          // 1     (primitive copy)
        System.out.println("rv.value after method call    = " + rv.value);    // 99    (object mutated)
        // The reassignment inside tryToReassignReference only changed the
        // METHOD'S LOCAL copy of the reference, not main's `rv`.

        section("4) Stack memory is per-thread - one frame stack per Thread");
        // The recursion happens on this thread's stack - independent of any
        // other thread.
        try {
            recurse(0);
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError caught at depth ~" + lastDepth);
        }

        section("5) Heap allocations show up in Runtime.totalMemory()");
        long MB = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long before = rt.totalMemory() - rt.freeMemory();
        Box[] many = new Box[300_000];
        for (int i = 0; i < many.length; i++) many[i] = new Box(i);
        long after = rt.totalMemory() - rt.freeMemory();
        System.out.printf("heap used before: %5d MB%n", before / MB);
        System.out.printf("heap used after : %5d MB   for %d Boxes%n", after / MB, many.length);
        System.out.println("first / last Box value: " + many[0].value + " / " + many[many.length - 1].value);

        // OUTPUT (numbers vary)
    }

    static void tryToChangePrimitive(int n)    { n = 999; }                   // local copy only
    static void tryToChangeReference(Box b)    { b.value = 99; }              // mutates the shared heap object
    static void tryToReassignReference(Box b)  { b = new Box(-1); }            // changes only the local reference

    private static int lastDepth;
    static void recurse(int n) {
        lastDepth = n;
        recurse(n + 1);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
