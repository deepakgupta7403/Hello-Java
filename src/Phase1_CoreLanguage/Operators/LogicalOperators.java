package Phase1_CoreLanguage.Operators;

/**
 * Logical (Boolean) Operators
 * ---------------------------
 *      &&    short-circuit AND   (right side evaluated only if left is true)
 *      ||    short-circuit OR    (right side evaluated only if left is false)
 *      !     logical NOT
 *      &     non-short-circuit AND  (rare - evaluates BOTH sides always)
 *      |     non-short-circuit OR   (rare - evaluates BOTH sides always)
 *      ^     XOR (exclusive or)
 * <p>
 *
 * Short-Circuit vs Non-Short-Circuit
 * ----------------------------------
 * - && and || stop evaluating as soon as the result is known. This is the SAFE
 *   way to combine null checks with method calls:
 * <p>
 *
 *      if (s != null && s.length() > 0) { ... }   // safe - no NPE
 *      if (s != null & s.length() > 0)  { ... }   // dangerous - both sides evaluate
 * <p>
 *
 * - & and | are used when the right side has SIDE EFFECTS you want to keep
 *   regardless. Almost always you want && and ||.
 * <p>
 *
 * Truth Tables
 * ------------
 *      A     B    | A&&B   A||B   A^B   !A
 *      ----------+----------------------------
 *      true  true | true   true   false false
 *      true  false| false  true   true  false
 *      false true | false  true   true  true
 *      false false| false  false  false true
 */

public class LogicalOperators {

    public static void main(String[] args) {
        boolean t = true;
        boolean f = false;

        System.out.println("t && f = " + (t && f));    // false
        System.out.println("t || f = " + (t || f));    // true
        System.out.println("!t     = " + (!t));        // false
        System.out.println("t ^ f  = " + (t ^ f));     // true
        System.out.println("t ^ t  = " + (t ^ t));     // false

        // --- Short-circuit demo ---
        // sideEffect() returns false but also prints. With && / ||  it should
        // never be called when the result is already known.

        System.out.println("--- short-circuit && ---");
        if (false && sideEffect("right of &&")) {   // sideEffect NOT called
            System.out.println("never reached");
        }
        System.out.println("--- short-circuit || ---");
        if (true || sideEffect("right of ||")) {    // sideEffect NOT called
            System.out.println("entered the if");
        }

        // --- Non-short-circuit demo ---
        System.out.println("--- non-short-circuit & ---");
        if (false & sideEffect("right of &")) {     // sideEffect IS called
            System.out.println("never reached");
        }

        // --- Practical example - safe null check with && ---
        String s = null;
        if (s != null && s.length() > 0) {           // safe: short-circuited
            System.out.println("non-empty");
        } else {
            System.out.println("null or empty");
        }

        // The unsafe version would throw NullPointerException:
        // if (s != null & s.length() > 0) { ... }   // NPE!

        // OUTPUT
        // t && f = false
        // t || f = true
        // !t     = false
        // t ^ f  = true
        // t ^ t  = false
        // --- short-circuit && ---
        // --- short-circuit || ---
        // entered the if
        // --- non-short-circuit & ---
        // sideEffect called with: right of &
        // null or empty
    }

    private static boolean sideEffect(String tag) {
        System.out.println("sideEffect called with: " + tag);
        return false;
    }
}
