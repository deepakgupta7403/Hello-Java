package Phase5_CollectionsLambdasStreams.Collections;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * java.util.IdentityHashMap&lt;K, V&gt; - Map That Compares With `==`
 * -------------------------------------------------------------
 * IdentityHashMap is just like HashMap except that it uses REFERENCE
 * EQUALITY for keys (the `==` operator) and System.identityHashCode for
 * hashing instead of equals/hashCode.
 * <p>
 *
 *      HashMap            : `a.equals(b)` means same key
 *      IdentityHashMap    : `a == b`      means same key
 * <p>
 *
 * Why It Exists
 * -------------
 * The standard Map contract says "use equals", but sometimes you really
 * want OBJECT IDENTITY. Two main use cases:
 * <p>
 *
 *   1. Graph traversal where each NODE OBJECT must be visited exactly
 *      once, even if two nodes happen to be equals().
 *      e.g. detecting cycles when serialising an object graph.
 * <p>
 *
 *   2. Storing PER-OBJECT metadata where you specifically care that two
 *      different instances have separate entries even if they have the
 *      same contents.
 * <p>
 *
 * When To Use It
 * --------------
 *   - Implementing equals on a recursive data structure - track visited
 *     nodes by IDENTITY to avoid infinite loops on equal-but-distinct
 *     objects.
 *   - Building serialisers that must remember which instances they have
 *     already written, regardless of contents.
 *   - Caches keyed by the precise instance you were given.
 * <p>
 *
 * Why You Should Normally Avoid It
 * --------------------------------
 * The vast majority of map use cases want VALUE equality. Using
 * IdentityHashMap when you really wanted HashMap silently produces wrong
 * lookups - two strings with the same content but different identities
 * will be treated as different keys.
 * <p>
 *
 * Other Notes
 * -----------
 *   - Initial capacity refers to MAX EXPECTED ENTRIES, not buckets.
 *     IdentityHashMap uses linear probing in a flat array.
 *   - Iteration order is NOT defined.
 *   - Implements Map but DELIBERATELY violates the Map general contract
 *     for equals/hashCode - the Javadoc says so up front.
 *   - Not thread-safe.
 */

public class IdentityHashMapDemo {

    public static void main(String[] args) {

        section("1) HashMap vs IdentityHashMap - the headline difference");
        String s1 = new String("hello");      // two equal but DIFFERENT objects
        String s2 = new String("hello");
        System.out.println("s1 == s2     = " + (s1 == s2));        // false - distinct
        System.out.println("s1.equals(s2)= " + s1.equals(s2));     // true  - same content

        Map<String, Integer> normal   = new HashMap<>();
        Map<String, Integer> identity = new IdentityHashMap<>();

        normal.put(s1, 1);
        normal.put(s2, 2);                   // overrides - "equals" the same key
        System.out.println("HashMap size         = " + normal.size());   // 1

        identity.put(s1, 1);
        identity.put(s2, 2);                 // distinct instances - both kept
        System.out.println("IdentityHashMap size = " + identity.size()); // 2

        section("2) Same identity instance overrides as expected");
        identity.put(s1, 99);                // SAME instance again
        System.out.println("after put(s1, 99) -> get(s1) = " + identity.get(s1));
        System.out.println("                  -> get(s2) = " + identity.get(s2));

        section("3) Use case - graph cycle detection");
        // We walk a small graph and skip nodes we've already seen.
        // Two Node objects with the same fields would be `equals` in many
        // implementations - we use IDENTITY to avoid skipping legitimately
        // distinct nodes.
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        a.next = b; b.next = c; c.next = a;            // CYCLE!

        Map<Node, Boolean> visited = new IdentityHashMap<>();
        Node cur = a;
        int steps = 0;
        while (cur != null && !visited.containsKey(cur)) {
            visited.put(cur, true);
            System.out.println("visited " + cur.label);
            cur = cur.next;
            steps++;
        }
        System.out.println("stopped after " + steps + " steps, " +
                visited.size() + " unique nodes");

        section("4) Identity-based serialiser dedup");
        // Imagine writing a structure to JSON; record EACH unique instance
        // by identity so we can emit $ref={id} for repeats.
        IdentityHashMap<Object, Integer> ids = new IdentityHashMap<>();
        Object o1 = new Object();
        Object o2 = new Object();
        ids.put(o1, 1);
        ids.put(o2, 2);
        // Even if a third object would .equals() o1, it would get its own id:
        Object o1Twin = new Object();
        ids.put(o1Twin, 3);
        System.out.println("ids size = " + ids.size());

        section("5) Watch out - many map operations behave by IDENTITY here");
        // containsKey, get, remove all use ==
        System.out.println("contains s1 (instance)  = " + identity.containsKey(s1));
        System.out.println("contains \"hello\" lit  = " + identity.containsKey("hello"));
        // The literal "hello" is a third, distinct object - and identity-equality fails.

        // OUTPUT (representative; HashMap ordering may vary)
    }

    static class Node {
        final String label;
        Node next;
        Node(String l) { this.label = l; }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
