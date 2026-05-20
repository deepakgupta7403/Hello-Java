package Phase1_CoreLanguage.Operators;

/**
 * Unary Operators
 * ---------------
 * Unary operators act on a SINGLE operand.
 *
 *      +x      unary plus  (rarely useful; promotes byte/short to int)
 *      -x      unary minus (negation)
 *      !b      logical NOT (boolean only)
 *      ~x      bitwise NOT (one's complement, integer only)
 *      ++x     pre-increment  (increment, then use)
 *      x++     post-increment (use, then increment)
 *      --x     pre-decrement
 *      x--     post-decrement
 *
 *
 * Pre vs Post Increment
 * ---------------------
 * The difference matters only when the expression is USED:
 *
 *      int i = 5;
 *      int a = ++i;     // i becomes 6, then a = 6  -> a=6, i=6
 *
 *      int j = 5;
 *      int b = j++;     // b = 5 (old j), then j becomes 6 -> b=5, j=6
 *
 *
 * Classic Interview Trap
 * ----------------------
 *      int i = 5;
 *      i = i++;        //  i stays 5!
 * Reason: the right side captures 5 (the OLD value), then i++ increments to 6,
 * then the assignment overwrites i back with the captured 5.
 */

public class UnaryOperators {

    public static void main(String[] args) {
        int x = 5;

        // Unary plus / minus
        System.out.println("+x   = " + (+x));     // 5
        System.out.println("-x   = " + (-x));     // -5

        // Logical NOT
        boolean t = true;
        System.out.println("!t   = " + (!t));     // false

        // Bitwise NOT
        System.out.println("~5   = " + (~5));     // -6 (one's complement)

        // Pre vs post increment
        int i = 5;
        int a = ++i;   // i becomes 6, a = 6
        System.out.println("after a = ++i : a=" + a + ", i=" + i);   // 6, 6

        int j = 5;
        int b = j++;   // b = 5, then j becomes 6
        System.out.println("after b = j++ : b=" + b + ", j=" + j);   // 5, 6

        // Pre vs post decrement
        int k = 5;
        System.out.println("--k   = " + (--k));   // 4
        System.out.println("k--   = " + (k--));   // 4 (k becomes 3 AFTER print)
        System.out.println("k now = " + k);       // 3

        // The classic trap
        int trap = 5;
        trap = trap++;        // stays 5
        System.out.println("trap after  trap = trap++ : " + trap);   // 5

        int correct = 5;
        correct = ++correct;  // becomes 6
        System.out.println("correct after correct = ++correct : " + correct); // 6

        // Unary plus does ONE useful thing - it promotes byte/short to int
        byte sb = 10;
        // var sum = sb + 1; // arithmetic auto-promotes too, so + is rarely needed
        int promoted = +sb;
        System.out.println("promoted = " + promoted + " (class " + ((Object)promoted).getClass().getSimpleName() + ")");

        // OUTPUT
        // +x   = 5
        // -x   = -5
        // !t   = false
        // ~5   = -6
        // after a = ++i : a=6, i=6
        // after b = j++ : b=5, j=6
        // --k   = 4
        // k--   = 4
        // k now = 3
        // trap after  trap = trap++ : 5
        // correct after correct = ++correct : 6
        // promoted = 10 (class Integer)
    }
}
