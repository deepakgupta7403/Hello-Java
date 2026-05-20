package Phase5_CollectionsLambdasStreams.Collections;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * java.util.WeakHashMap&lt;K, V&gt; - Map With Weakly-Referenced Keys
 * -------------------------------------------------------------
 * WeakHashMap stores its KEYS using WeakReference. When the JVM's garbage
 * collector decides that no other strong reference points to a key, the
 * entry vanishes from the map automatically.
 *
 *      Map<MyObj, ResourceState> states = new WeakHashMap<>();
 *      ...
 *      // when MyObj goes out of scope and the GC runs, the corresponding
 *      // (key, value) entry disappears.
 *
 *
 * Why It Exists
 * -------------
 *   - To attach metadata to an object WITHOUT preventing it from being
 *     garbage-collected.
 *   - To build caches keyed by an external object's lifetime.
 *
 *
 * Classic Use Case - "Attach data to a foreign object"
 * ----------------------------------------------------
 * You have a third-party class you cannot modify but you want to remember
 * something per instance. Put it in a WeakHashMap keyed by the instance.
 * When the instance is collected, your associated entry disappears too -
 * no leak.
 *
 *
 * Important Pitfall - VALUES Are Held STRONGLY
 * --------------------------------------------
 * If your VALUE references back to the key (directly or transitively),
 * the key can never be collected and the entry never disappears. Break
 * the cycle with a WeakReference value or by storing only "key-free" data.
 *
 *
 * Other Notes
 * -----------
 *   - Entries can disappear AT ANY TIME between two calls. Treat the
 *     map as having a stochastic size.
 *   - Iteration is fail-fast and BUSY: it must filter out cleared entries.
 *   - Not thread-safe. Wrap with Collections.synchronizedMap if needed.
 *
 *
 * Comparison To Other "Special" Maps
 * ----------------------------------
 *   WeakHashMap        keys collected when no strong refs elsewhere
 *   IdentityHashMap    keys compared with == (not equals)
 *   ConcurrentHashMap  thread-safe, no weak references
 *   LinkedHashMap      ordered, optional LRU eviction
 */

public class WeakHashMapDemo {

    public static void main(String[] args) throws Exception {

        section("1) Basic API - identical to HashMap");
        WeakHashMap<String, Integer> m = new WeakHashMap<>();
        // CAREFUL - String literals are interned, never collected. We use
        // `new String(...)` here so the strong reference can really go away.
        String keyA = new String("alpha");
        String keyB = new String("beta");
        m.put(keyA, 1);
        m.put(keyB, 2);
        System.out.println("size before = " + m.size());

        section("2) Drop the strong reference, encourage GC, watch entry vanish");
        keyA = null;                  // no other strong reference to "alpha"
        encourageGc();
        System.out.println("size after  = " + m.size() + "   (entry for 'alpha' was reclaimed)");
        System.out.println("remaining   = " + m);

        section("3) Caveat - String LITERALS live in the pool, never collected");
        WeakHashMap<String, Integer> literalKeys = new WeakHashMap<>();
        literalKeys.put("interned", 42);
        encourageGc();
        System.out.println("size after gc = " + literalKeys.size() +
                "   (\"interned\" is in the String pool - kept alive forever)");

        section("4) Value referencing key prevents collection");
        // Demo a value that holds a STRONG reference back to its key.
        WeakHashMap<Object, Holder> bad = new WeakHashMap<>();
        Object refKey = new Object();
        Holder h = new Holder(refKey);     // strong ref to refKey via the value
        bad.put(refKey, h);
        refKey = null;
        encourageGc();
        System.out.println("size despite null'd key = " + bad.size() +
                "   (value still references key - leak)");

        section("5) Real-world use - attach lifecycle data to a borrowed object");
        WeakHashMap<Object, String> nameOf = new WeakHashMap<>();
        Object obj = new Object();
        nameOf.put(obj, "the-name");
        System.out.println("name(obj) = " + nameOf.get(obj));
        obj = null;
        encourageGc();
        System.out.println("after letting obj go: map size = " + nameOf.size());

        section("6) Iteration filters out cleared entries automatically");
        WeakHashMap<Object, String> live = new WeakHashMap<>();
        Object alive = new Object();
        live.put(new Object(), "transient");        // immediately unreachable
        live.put(alive, "kept");
        encourageGc();
        for (Map.Entry<Object, String> e : live.entrySet()) {
            System.out.println("  key=" + e.getKey() + " value=" + e.getValue());
        }
        // The "transient" entry should already have been pruned.

        // OUTPUT
        // (representative; "size after gc" results depend on the JVM's mood)
    }

    /** A small holder whose value strongly references its key - the leak demo. */
    static class Holder {
        final Object backref;
        Holder(Object backref) { this.backref = backref; }
    }

    /** Hint the GC. Real production code never depends on this. */
    private static void encourageGc() {
        for (int i = 0; i < 3; i++) {
            System.gc();
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
