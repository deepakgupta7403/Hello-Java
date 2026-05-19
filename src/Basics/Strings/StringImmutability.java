package Basics.Strings;

/**
 * Why Strings Are Immutable
 * -------------------------
 * In Java a String object cannot be CHANGED after creation. Methods like
 * `toUpperCase()`, `substring(...)`, `concat(...)` always RETURN A NEW STRING
 * instead of mutating the receiver. The original object is untouched.
 *
 *      String s = "java";
 *      s.toUpperCase();              // returns "JAVA" but ignored
 *      System.out.println(s);        // still prints "java"
 *
 *      s = s.toUpperCase();          // now s points to the NEW "JAVA"
 *
 *
 * Reasons Java's Designers Chose Immutability
 * -------------------------------------------
 *
 *  1. STRING POOL / Memory Sharing
 *     - If a literal could change, two variables pointing to the same pool
 *       object would surprise each other. Immutability lets the JVM safely
 *       cache and share literals.
 *
 *  2. THREAD SAFETY
 *     - Immutable objects are inherently thread-safe; they need no locks.
 *       Strings can be passed between threads with no risk of mutation.
 *
 *  3. SECURITY
 *     - Many security-critical APIs accept Strings: class names, file paths,
 *       URLs, network endpoints, SQL fragments, login credentials. If they
 *       were mutable, attackers could change the value AFTER the security
 *       check but BEFORE the use ("time-of-check vs time-of-use" attacks).
 *
 *  4. HASHCODE CACHING
 *     - String hashCode is computed once and stored. HashMap / HashSet lookups
 *       stay fast and correct because the hash never changes.
 *
 *  5. CLASS LOADING
 *     - Class names in Java are Strings. Immutability is the simplest way to
 *       guarantee they never change between load and use.
 *
 *  6. SAFE TO PASS AROUND
 *     - No defensive copies needed when accepting or returning a String. The
 *       receiver cannot break the sender's invariants.
 *
 *
 * What "Immutable" Actually Means
 * -------------------------------
 *  - The byte[] backing field is `private final`.
 *  - All "mutator" methods return a NEW String; none modify in place.
 *  - You CAN reassign a String variable - that just rebinds the name to
 *    another (possibly new) String.
 *  - Reflection could technically reach in and overwrite the final array,
 *    but doing so violates the JVM specification and breaks the language
 *    guarantees - don't do it.
 *
 *
 * Demonstration
 * -------------
 * The main() below shows:
 *  - mutator methods do NOT change the original
 *  - pool sharing is safe because of immutability
 *  - hash code stays consistent for a given content
 *  - passing a String to a method cannot mutate the caller's value
 */

public class StringImmutability {

    public static void main(String[] args) {

        // ============================================================
        // 1. "Mutator" methods return new objects
        // ============================================================
        String s = "java";

        s.toUpperCase();                              // result thrown away!
        System.out.println("s after toUpperCase() ignored = " + s);   // "java"

        String upper = s.toUpperCase();               // capture the new String
        System.out.println("s     = " + s);           // "java"
        System.out.println("upper = " + upper);       // "JAVA"
        System.out.println("same object ? " + (s == upper));   // false

        // ============================================================
        // 2. Pool sharing is safe because Strings cannot change
        // ============================================================
        String a = "Hello";
        String b = "Hello";
        System.out.println("\na == b ? " + (a == b));     // true - shared
        // If `a` could be mutated, this sharing would be unsafe.

        // ============================================================
        // 3. Hash code is cached; for the same content it never changes
        // ============================================================
        String key = "compute-once";
        int h1 = key.hashCode();
        int h2 = key.hashCode();
        System.out.println("hashCode stable ? " + (h1 == h2));   // true
        System.out.println("hashCode value   = " + h1);

        // ============================================================
        // 4. Methods cannot mutate the caller's String
        // ============================================================
        String name = "deepak";
        tryToMutate(name);
        System.out.println("\nname after tryToMutate = " + name); // still "deepak"

        // ============================================================
        // 5. The "loop concatenation" trap is a consequence of immutability
        // ============================================================
        // Every iteration creates a NEW String. For long loops this is O(n^2)
        // - use StringBuilder instead (see StringConcatenation.java).
        String acc = "";
        for (int i = 0; i < 5; i++) {
            acc += i;          // builds "0", "01", "012", ...
        }
        System.out.println("\nacc after loop = " + acc);

        // ============================================================
        // 6. final reference vs final contents - same idea applies
        // ============================================================
        final String fixed = "I cannot change";
        // fixed = "...";        // ERROR - cannot reassign
        // fixed.setCharAt(0,'X')// no such method exists
        System.out.println("fixed = " + fixed);

        // OUTPUT
        // s after toUpperCase() ignored = java
        // s     = java
        // upper = JAVA
        // same object ? false
        //
        // a == b ? true
        // hashCode stable ? true
        // hashCode value   = 1357883693
        //
        // name after tryToMutate = deepak
        //
        // acc after loop = 01234
        // fixed = I cannot change
    }

    /**
     * Demonstrates that a method CANNOT alter the caller's String. The
     * parameter `s` is a local variable holding a copy of the reference;
     * reassigning it has no effect on the caller.
     */
    static void tryToMutate(String s) {
        s = s.toUpperCase();    // changes only the local reference
        s = s + "!!!";
    }
}
