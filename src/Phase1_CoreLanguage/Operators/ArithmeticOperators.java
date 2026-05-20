package Phase1_CoreLanguage.Operators;

/**
 * Arithmetic Operators
 * --------------------
 * Java provides the standard five arithmetic operators:
 *
 *      +     addition         (and string concatenation when an operand is String)
 *      -     subtraction
 *      *     multiplication
 *      /     division         (integer division if both operands are integers)
 *      %     modulus          (remainder of integer division)
 *
 *
 * Integer Division vs Floating-Point Division
 * -------------------------------------------
 * If BOTH operands are integers, '/' performs INTEGER division and discards the
 * fractional part. If EITHER operand is a floating-point value, '/' performs
 * floating-point division.
 *
 *      7 / 2     -> 3        (integer division)
 *      7 / 2.0   -> 3.5      (one operand is double - promoted to floating-point)
 *
 *
 * Overflow
 * --------
 * Integer arithmetic in Java WRAPS AROUND silently on overflow - there is no
 * exception. If you need overflow detection use Math.addExact, multiplyExact,
 * etc. (they throw ArithmeticException).
 *
 *      Integer.MAX_VALUE + 1   -> -2147483648  (silent overflow)
 *      Math.addExact(Integer.MAX_VALUE, 1)     -> throws ArithmeticException
 *
 *
 * Division by Zero
 * ----------------
 * - Integer division by zero  -> throws ArithmeticException ("/ by zero")
 * - Floating-point divide by zero -> NO exception, produces:
 *      positive / 0.0  ->  Infinity
 *      negative / 0.0  -> -Infinity
 *      0.0 / 0.0       ->  NaN
 *
 *
 * Operator Precedence (highest first, partial list)
 * -------------------------------------------------
 *      unary +, -, ++, --
 *      *, /, %
 *      +, -
 *
 * Use parentheses for clarity rather than relying on memory.
 */

public class ArithmeticOperators {

    public static void main(String[] args) {
        int a = 17;
        int b = 5;

        System.out.println("a + b = " + (a + b));    // 22
        System.out.println("a - b = " + (a - b));    // 12
        System.out.println("a * b = " + (a * b));    // 85
        System.out.println("a / b = " + (a / b));    // 3   (integer division)
        System.out.println("a % b = " + (a % b));    // 2   (remainder)

        // Floating-point division
        double fa = 17, fb = 5;
        System.out.println("fa / fb = " + (fa / fb));     // 3.4

        // Mixed-type division - int promoted to double
        System.out.println("a  / fb = " + (a / fb));      // 3.4

        // String concatenation with +
        String name = "Java";
        int version = 21;
        System.out.println(name + " " + version);         // "Java 21"

        // Watch out - the left-to-right evaluation of +
        System.out.println("Sum = " + 1 + 2);             // "Sum = 12"   (treats as strings)
        System.out.println("Sum = " + (1 + 2));           // "Sum = 3"    (parens fix it)

        // Overflow (silent)
        int maxInt = Integer.MAX_VALUE;
        System.out.println("maxInt + 1 = " + (maxInt + 1));   // -2147483648

        // Overflow (detected)
        try {
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException e) {
            System.out.println("addExact overflow: " + e.getMessage());
        }

        // Division by zero
        try {
            int crash = a / 0;
            System.out.println(crash);
        } catch (ArithmeticException e) {
            System.out.println("int / 0 -> " + e.getMessage());
        }
        System.out.println("1.0 / 0  = " + (1.0 / 0));    // Infinity
        System.out.println("0.0 / 0  = " + (0.0 / 0));    // NaN

        // OUTPUT
        // a + b = 22
        // a - b = 12
        // a * b = 85
        // a / b = 3
        // a % b = 2
        // fa / fb = 3.4
        // a  / fb = 3.4
        // Java 21
        // Sum = 12
        // Sum = 3
        // maxInt + 1 = -2147483648
        // addExact overflow: integer overflow
        // int / 0 -> / by zero
        // 1.0 / 0  = Infinity
        // 0.0 / 0  = NaN
    }
}
