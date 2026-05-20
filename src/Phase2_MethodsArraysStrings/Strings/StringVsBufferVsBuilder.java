package Phase2_MethodsArraysStrings.Strings;

/**
 * String vs StringBuffer vs StringBuilder
 * ---------------------------------------
 * Java offers THREE classes for textual data. They look similar but have very
 * different mutation, thread-safety, and performance characteristics. Picking
 * the right one is a recurring interview question and a real performance lever.
 *
 *
 * Side-by-Side Comparison
 * -----------------------
 *
 *  Aspect             | String                | StringBuffer        | StringBuilder
 *  -------------------+-----------------------+---------------------+---------------------
 *  Mutability         | Immutable             | Mutable             | Mutable
 *  Thread safety      | Implicit (immutable)  | Synchronized        | NOT synchronized
 *  Performance        | Fast for read,        | Slowest (locking)   | Fastest
 *                     | slow for build        |                     |
 *  Pool / interning   | Yes (string pool)     | No                  | No
 *  Hashcode caching   | Yes                   | No                  | No
 *  Methods overloaded | toString, valueOf,    | append, insert,     | append, insert,
 *                     | format, etc.          | replace, delete,    | replace, delete,
 *                     |                       | reverse, ...        | reverse, ...
 *  Use as map key     | Excellent             | Avoid (mutable +    | Avoid (mutable +
 *                     |                       |  no good equals)    |  no good equals)
 *  equals()           | Content equality      | Reference equality  | Reference equality
 *                     |                       | (inherits Object!)  | (inherits Object!)
 *  Class introduced   | Java 1.0              | Java 1.0            | Java 5
 *  Internal storage   | private final byte[]  | char[] / byte[]     | char[] / byte[]
 *
 *
 * Important Detail - equals() on StringBuffer/StringBuilder
 * --------------------------------------------------------
 * Neither StringBuffer nor StringBuilder override equals(). Two builders with
 * IDENTICAL CONTENT are NOT equal by .equals(). Convert to String first:
 *
 *      sb1.toString().equals(sb2.toString());
 *
 *
 * When To Use Which
 * -----------------
 *  - STRING:        for textual VALUES (storage, parameters, return types, keys).
 *  - STRINGBUILDER: for BUILDING up a String in single-threaded code (the
 *                    default for most "compose a string" needs).
 *  - STRINGBUFFER:  for the rare case of MULTIPLE THREADS appending to the
 *                    same buffer concurrently. Otherwise prefer StringBuilder.
 *
 *
 * Performance Benchmark (in main())
 * ---------------------------------
 * Three loops of N = 100_000 appends:
 *   1. String   "+=" inside a loop
 *   2. StringBuffer.append
 *   3. StringBuilder.append
 * The numbers vary by JVM but the relative ordering is always:
 *      Slowest:  String += loop (O(N^2))
 *      Mid    :  StringBuffer (synchronized overhead)
 *      Fastest:  StringBuilder
 */

public class StringVsBufferVsBuilder {

    public static void main(String[] args) {
        final int N = 50_000;

        // ============================================================
        // 1. String concatenation in a loop - the slow way
        // ============================================================
        long t1 = System.nanoTime();
        String s = "";
        for (int i = 0; i < N; i++) {
            s += "x";
        }
        long ms1 = (System.nanoTime() - t1) / 1_000_000;
        System.out.println("String  += loop : " + ms1 + " ms   length=" + s.length());

        // ============================================================
        // 2. StringBuffer.append - thread-safe, synchronized
        // ============================================================
        long t2 = System.nanoTime();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append('x');
        }
        long ms2 = (System.nanoTime() - t2) / 1_000_000;
        System.out.println("StringBuffer    : " + ms2 + " ms   length=" + sb.length());

        // ============================================================
        // 3. StringBuilder.append - the fastest
        // ============================================================
        long t3 = System.nanoTime();
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < N; i++) {
            bld.append('x');
        }
        long ms3 = (System.nanoTime() - t3) / 1_000_000;
        System.out.println("StringBuilder   : " + ms3 + " ms   length=" + bld.length());

        // ============================================================
        // 4. The equals() trap
        // ============================================================
        StringBuilder b1 = new StringBuilder("hello");
        StringBuilder b2 = new StringBuilder("hello");
        System.out.println("\nb1.equals(b2)              = " + b1.equals(b2));  // false!
        System.out.println("b1.toString().equals(b2.toString()) = "
                                                + b1.toString().equals(b2.toString()));  // true

        // ============================================================
        // 5. Thread-safety contrast
        // ============================================================
        // StringBuffer is safe to share across threads.
        StringBuffer safeBuf = new StringBuffer();
        Runnable add1000 = () -> {
            for (int i = 0; i < 1000; i++) safeBuf.append('x');
        };
        Thread sa = new Thread(add1000);
        Thread sb2 = new Thread(add1000);
        sa.start(); sb2.start();
        try { sa.join(); sb2.join(); } catch (InterruptedException e) { /* ignore */ }
        System.out.println("\nStringBuffer length after 2 threads x 1000 appends = "
                + safeBuf.length() + " (expected 2000)");

        // StringBuilder is NOT safe to share. Two threads racing will lose
        // data and may even throw ArrayIndexOutOfBoundsException. NEVER share
        // a StringBuilder across threads without external synchronisation.
        // (We do not demonstrate that here because the failure is intermittent.)

        // ============================================================
        // 6. Mutating helper - what NOT to do for a "constant"
        // ============================================================
        StringBuilder shared = new StringBuilder("hello");
        polluteCaller(shared);
        System.out.println("\nshared after method = " + shared);   // mutated!

        String safe = "hello";
        ignoreCaller(safe);
        System.out.println("safe   after method = " + safe);       // unchanged

        // SAMPLE OUTPUT (timings vary; ordering is constant)
        // String  += loop : 380 ms   length=50000
        // StringBuffer    :  3 ms    length=50000
        // StringBuilder   :  2 ms    length=50000
        //
        // b1.equals(b2)              = false
        // b1.toString().equals(b2.toString()) = true
        //
        // StringBuffer length after 2 threads x 1000 appends = 2000 (expected 2000)
        //
        // shared after method = hello!!!
        // safe   after method = hello
    }

    /** A method can mutate the builder a caller passes in. */
    static void polluteCaller(StringBuilder sb) {
        sb.append("!!!");
    }

    /** A method receiving a String cannot mutate the caller's value. */
    static void ignoreCaller(String s) {
        s = s + "!!!";   // local reassignment only
    }
}
