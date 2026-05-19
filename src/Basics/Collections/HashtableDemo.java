package Basics.Collections;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * java.util.Hashtable&lt;K, V&gt; - The Original Java 1.0 Map
 * -----------------------------------------------------
 * Hashtable is a LEGACY synchronized hash table that predates the
 * Collections Framework. It was retrofitted to implement Map when the
 * framework arrived in Java 1.2.
 *
 *
 * History
 * -------
 *   Java 1.0   : Hashtable shipped, every method synchronized.
 *   Java 1.2   : Map / HashMap added; Hashtable made to implement Map.
 *   Java 1.5   : ConcurrentHashMap arrived - far better concurrent map.
 *
 * Modern guidance: don't use Hashtable in NEW code. Choose:
 *   - HashMap                  - single-threaded.
 *   - ConcurrentHashMap        - concurrent, much faster than Hashtable.
 *   - Collections.synchronizedMap(new HashMap&lt;&gt;()) - if you really need
 *                                 a coarse one-lock map (rare).
 *
 *
 * Differences vs HashMap
 * ----------------------
 *
 *   Hashtable                       HashMap
 *   ---------                       -------
 *   ALL methods synchronized        Not synchronized
 *   FORBIDS null keys and values    Allows ONE null key, many null values
 *   Enumeration API (.elements,     Iterator API
 *      .keys) plus Iterator
 *   Internal "rehash" tunable       Internal "resize" with similar load factor
 *
 *
 * Why It Still Exists
 * -------------------
 * Backwards compatibility with very old code and frameworks (Servlet
 * sessions, AWT key bindings, JNDI). Direct use in new code is discouraged.
 *
 *
 * This File
 * ---------
 * Shows the Hashtable API, demonstrates its quirks, then points to the
 * modern replacements you should reach for instead.
 */

public class HashtableDemo {

    public static void main(String[] args) {

        section("1) Basic API - same as Map");
        Hashtable<String, Integer> ht = new Hashtable<>();
        ht.put("alice", 30);
        ht.put("bob",   25);
        ht.put("alice", 31);                       // overwrites; returns 30
        System.out.println("table          = " + ht);
        System.out.println("get(alice)     = " + ht.get("alice"));
        System.out.println("size           = " + ht.size());
        System.out.println("isEmpty        = " + ht.isEmpty());

        section("2) Nulls are REJECTED at runtime");
        try { ht.put(null, 1); }
        catch (NullPointerException e) {
            System.out.println("put(null, ...) -> NullPointerException");
        }
        try { ht.put("k", null); }
        catch (NullPointerException e) {
            System.out.println("put(..., null) -> NullPointerException");
        }

        section("3) Enumeration API - Hashtable's pre-Iterator way");
        Enumeration<String> keys   = ht.keys();
        Enumeration<Integer> values = ht.elements();
        System.out.print("keys via Enumeration:     "); while (keys.hasMoreElements())   System.out.print(keys.nextElement() + " ");
        System.out.println();
        System.out.print("values via Enumeration:   "); while (values.hasMoreElements()) System.out.print(values.nextElement() + " ");
        System.out.println();

        // Modern code uses the Map / Iterator equivalents:
        System.out.println("keySet()  = " + ht.keySet());
        System.out.println("values()  = " + ht.values());
        System.out.println("entrySet()= " + ht.entrySet());

        section("4) All methods are synchronized - performance compared with HashMap");
        final int N = 200_000;
        long t = System.nanoTime();
        Hashtable<Integer, Integer> htP = new Hashtable<>();
        for (int i = 0; i < N; i++) htP.put(i, i);
        long htMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        HashMap<Integer, Integer> hmP = new HashMap<>();
        for (int i = 0; i < N; i++) hmP.put(i, i);
        long hmMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("Hashtable put(" + N + "): " + htMs + " ms");
        System.out.println("HashMap   put(" + N + "): " + hmMs + " ms  (no sync overhead)");

        section("5) Modern concurrent replacement - ConcurrentHashMap");
        // ConcurrentHashMap is the right answer when you DO need a thread-safe map.
        // It uses lock striping + CAS, allowing many threads to update at once.
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("a", 1);
        chm.merge("a", 1, Integer::sum);          // atomic increment idiom
        chm.computeIfAbsent("b", k -> 99);         // atomic put-if-absent + compute
        System.out.println("ConcurrentHashMap = " + chm);

        section("6) When you still need Hashtable - JNDI / Servlet etc.");
        // Some legacy APIs INSIST on a Hashtable parameter. Build one with
        // a temporary HashMap if you like, then convert.
        Map<String, String> envBuilder = new HashMap<>();
        envBuilder.put("java.naming.factory.initial",
                       "com.sun.jndi.fscontext.RefFSContextFactory");
        envBuilder.put("java.naming.provider.url", "file:///tmp");
        Hashtable<String, String> jndi = new Hashtable<>(envBuilder);
        System.out.println("jndi-style table = " + jndi);

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
