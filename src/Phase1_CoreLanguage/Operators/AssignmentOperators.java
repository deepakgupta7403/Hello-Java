package Phase1_CoreLanguage.Operators;

/**
 * Assignment Operators
 * --------------------
 * The basic assignment operator is = and the COMPOUND assignment operators
 * combine an arithmetic / bitwise operator with assignment:
 * <p>
 *
 *      =      simple assignment
 *      +=     a += b   -> a = a + b
 *      -=     a -= b   -> a = a - b
 *      *=     a *= b   -> a = a * b
 *      /=     a /= b   -> a = a / b
 *      %=     a %= b   -> a = a % b
 *      &=     a &= b   -> a = a & b
 *      |=     a |= b   -> a = a | b
 *      ^=     a ^= b   -> a = a ^ b
 *      <<=    a <<= b  -> a = a << b
 *      >>=    a >>= b  -> a = a >> b
 *      >>>=   a >>>= b -> a = a >>> b
 * <p>
 *
 * The Hidden Cast in Compound Assignments
 * ---------------------------------------
 * Compound assignment operators perform an IMPLICIT cast back to the variable's
 * type. The plain '=' does not.
 * <p>
 *
 *      short s = 10;
 *      s = s + 1;       // ERROR - s + 1 is int, can't assign int to short
 *      s += 1;          // OK    - += quietly casts back to short
 * <p>
 *
 * This is convenient but can MASK BUGS - the cast can lose data without warning.
 * <p>
 *
 * Right-to-Left Associativity / Chained Assignment
 * ------------------------------------------------
 *      int x, y, z;
 *      x = y = z = 5;     // assigns 5 to z, then y, then x
 * <p>
 *
 * Avoid in real code - too easy to misread.
 */

public class AssignmentOperators {

    public static void main(String[] args) {
        int n = 10;

        n += 5;   System.out.println("after += 5  : " + n);   // 15
        n -= 3;   System.out.println("after -= 3  : " + n);   // 12
        n *= 2;   System.out.println("after *= 2  : " + n);   // 24
        n /= 4;   System.out.println("after /= 4  : " + n);   // 6
        n %= 4;   System.out.println("after %= 4  : " + n);   // 2
        n <<= 3;  System.out.println("after <<=3  : " + n);   // 16
        n |= 1;   System.out.println("after |= 1  : " + n);   // 17
        n &= 5;   System.out.println("after &= 5  : " + n);   // 1
        n ^= 7;   System.out.println("after ^= 7  : " + n);   // 6

        // --- The hidden cast: compound assignment quietly narrows ---
        short s = 10;
        // s = s + 1;       // compile error
        s += 1;             // legal - compiler inserts (short) cast
        System.out.println("short s after += 1 : " + s);   // 11

        // The hidden cast can lose data silently:
        s = 100;
        s += 50_000;        // 100 + 50000 = 50100 -> doesn't fit in short -> wraps
        System.out.println("short s after += 50_000 : " + s);  // some unexpected value

        // --- Chained assignment ---
        int x, y, z;
        x = y = z = 42;
        System.out.println("x=" + x + ", y=" + y + ", z=" + z);   // 42, 42, 42

        // OUTPUT
        // after += 5  : 15
        // after -= 3  : 12
        // after *= 2  : 24
        // after /= 4  : 6
        // after %= 4  : 2
        // after <<=3  : 16
        // after |= 1  : 17
        // after &= 5  : 1
        // after ^= 7  : 6
        // short s after += 1 : 11
        // short s after += 50_000 : -15436     (overflow/wrap behaviour)
        // x=42, y=42, z=42
    }
}
