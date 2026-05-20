package Phase1_CoreLanguage.Operators;

/**
 * Ternary (Conditional) Operator
 * ------------------------------
 * The only TERNARY operator in Java - takes three operands - is the conditional
 * operator. It is a shorter alternative to a simple if-else when both branches
 * just compute a value.
 *
 *      condition ? valueIfTrue : valueIfFalse
 *
 *
 * Equivalent if-else
 * ------------------
 *      int max;
 *      if (a > b) max = a;
 *      else       max = b;
 *
 *      // is the same as:
 *      int max = (a > b) ? a : b;
 *
 *
 * When To Use It
 * --------------
 * - Good for short, expression-style assignments.
 * - Good for ternary chains that read well.
 *
 *
 * When To Avoid It
 * ----------------
 * - When either branch has SIDE EFFECTS - use if/else for clarity.
 * - When the expression spans more than one line - readability suffers.
 * - When it nests deeply - "ternary tower" is a known anti-pattern.
 *
 *
 * Type Rules
 * ----------
 * Both branches must produce COMPATIBLE types. The compiler computes a common
 * type:
 *      true ? 1 : 2.0     -> double (int widened to double)
 *      true ? 1 : "x"     -> compile error - no common type
 */

public class TernaryOperator {

    public static void main(String[] args) {

        // 1) Basic usage - max of two numbers
        int a = 7, b = 12;
        int max = (a > b) ? a : b;
        System.out.println("max(" + a + ", " + b + ") = " + max);

        // 2) Choose a label
        int score = 75;
        String grade = (score >= 60) ? "PASS" : "FAIL";
        System.out.println("Score " + score + " -> " + grade);

        // 3) Chained ternary (readable when the categories are mutually exclusive)
        int marks = 85;
        String letter = (marks >= 90) ? "A"
                       : (marks >= 75) ? "B"
                       : (marks >= 60) ? "C"
                       : (marks >= 40) ? "D"
                                       : "F";
        System.out.println("Marks " + marks + " -> grade " + letter);

        // 4) Avoid null arguments succinctly
        String input = null;
        String safe = (input != null) ? input : "default";
        System.out.println("safe = " + safe);

        // 5) Type promotion gotcha
        // Both branches must produce compatible types.
        // The result of (true ? 1 : 2.0) is a double because int is widened.
        Object result = (1 == 1) ? 1 : 2.0;
        System.out.println("result class = " + result.getClass().getSimpleName());   // Double

        // OUTPUT
        // max(7, 12) = 12
        // Score 75 -> PASS
        // Marks 85 -> grade B
        // safe = default
        // result class = Double
    }
}
