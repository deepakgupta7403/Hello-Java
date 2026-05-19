package Basics.Collections;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * java.util.Enumeration&lt;E&gt; - The Original (Java 1.0) Iterator
 * -----------------------------------------------------------
 * Enumeration is the LEGACY ancestor of Iterator. It is the iteration
 * contract used by the original Java 1.0 collection classes:
 *
 *      Vector / Stack / Hashtable
 *      StringTokenizer
 *      ZipFile / NetworkInterface (some IO classes still expose Enumerations)
 *
 *
 * Methods
 * -------
 *      boolean hasMoreElements()
 *      E       nextElement()
 *      default Iterator&lt;E&gt; asIterator()   (Java 9+, useful bridge)
 *
 *
 * Enumeration vs Iterator
 * -----------------------
 *   Enumeration            Iterator (modern)
 *   --------------------   --------------------
 *   Java 1.0                Java 1.2
 *   hasMoreElements()       hasNext()
 *   nextElement()           next()
 *   - no remove             remove() (optional)
 *   - no forEach (until 9)  forEachRemaining()
 *
 * Iterator superseded Enumeration in Java 1.2. The Collections Framework
 * uses Iterator everywhere. Enumeration is kept for backwards compatibility
 * with very old APIs.
 *
 *
 * When You Still See It
 * ---------------------
 *   - servlet APIs: `HttpServletRequest.getHeaderNames()` returns an
 *     Enumeration&lt;String&gt; (kept for compatibility).
 *   - some classloader APIs: `getResources(name)` returns Enumeration&lt;URL&gt;.
 *   - StringTokenizer (also legacy - prefer String.split or Scanner).
 *   - any code targeting JDK 1.1 or older libraries.
 *
 *
 * Bridging Enumeration &lt;-&gt; Iterator
 * ---------------------------------
 *   Enumeration.asIterator()                    Java 9+
 *   Collections.enumeration(Collection)         legacy direction
 *   Collections.list(Enumeration)               drain to a List
 */

public class EnumerationDemo {

    public static void main(String[] args) {

        section("1) Vector returns BOTH Enumeration and Iterator");
        Vector<String> v = new Vector<>(List.of("a", "b", "c"));

        Enumeration<String> en = v.elements();       // 1.0 style
        Iterator<String>    it = v.iterator();        // modern

        System.out.print("Enumeration: "); while (en.hasMoreElements()) System.out.print(en.nextElement() + " ");
        System.out.println();
        System.out.print("Iterator   : "); while (it.hasNext())          System.out.print(it.next()        + " ");
        System.out.println();

        section("2) Hashtable - Enumeration over keys / values");
        Hashtable<String, Integer> ht = new Hashtable<>();
        ht.put("alpha", 1); ht.put("beta", 2); ht.put("gamma", 3);
        System.out.print("keys()    : "); for (Enumeration<String>  e = ht.keys();    e.hasMoreElements(); ) System.out.print(e.nextElement() + " ");
        System.out.println();
        System.out.print("elements(): "); for (Enumeration<Integer> e = ht.elements(); e.hasMoreElements(); ) System.out.print(e.nextElement() + " ");
        System.out.println();

        section("3) StringTokenizer - another Enumeration source");
        StringTokenizer st = new StringTokenizer("Hello world from Java");
        while (st.hasMoreElements()) {
            System.out.println("  token: " + st.nextToken());
        }
        // Modern equivalent of the loop above:
        //   for (String tok : "Hello world from Java".split("\\s+")) ...

        section("4) Bridge - Enumeration.asIterator (Java 9+)");
        Enumeration<String> en2 = v.elements();
        Iterator<String>    it2 = en2.asIterator();
        it2.forEachRemaining(s -> System.out.print(s + " "));
        System.out.println();

        section("5) Bridge - Collections.enumeration(Collection) and Collections.list");
        Enumeration<Integer> fromList = Collections.enumeration(List.of(1, 2, 3));
        System.out.print("converted to enum: ");
        while (fromList.hasMoreElements()) System.out.print(fromList.nextElement() + " ");
        System.out.println();

        Enumeration<String> srcEnum = v.elements();
        List<String> drainedList = Collections.list(srcEnum);
        System.out.println("drained to list = " + drainedList);

        section("6) Real-world Enumeration - ClassLoader resources");
        try {
            Enumeration<java.net.URL> urls =
                    Thread.currentThread().getContextClassLoader().getResources("");
            int count = 0;
            while (urls.hasMoreElements() && count++ < 5) {
                System.out.println("  resource: " + urls.nextElement());
            }
            if (count == 0) System.out.println("  (no resources in this run)");
        } catch (java.io.IOException e) {
            System.out.println("  (IO failure: " + e.getMessage() + ")");
        }

        section("7) Don't use Enumeration in new code");
        // Use the for-each loop directly. The compiler turns this into
        // Iterator-based iteration:
        for (String s : v) System.out.print(s + " ");
        System.out.println();

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
