package Phase1_CoreLanguage.Operators;

/**
 * Relational (Comparison) Operators
 * ---------------------------------
 * Relational operators compare two values and produce a boolean result.
 *
 *      ==    equal to
 *      !=    not equal to
 *      >     greater than
 *      <     less than
 *      >=    greater than or equal
 *      <=    less than or equal
 *
 *
 * IMPORTANT - == on Objects vs Primitives
 * ---------------------------------------
 * - On PRIMITIVES, == compares VALUES.
 * - On REFERENCE TYPES, == compares REFERENCES (do both variables point to the
 *   same object on the heap?). To compare logical equality of objects, use the
 *   .equals(...) method.
 *
 *      String a = new String("hi");
 *      String b = new String("hi");
 *      a == b          -> false   (different objects)
 *      a.equals(b)     -> true    (same content)
 *
 *
 * NaN Comparisons
 * ---------------
 * NaN (Not-a-Number) is unique - it is NOT equal to itself:
 *      Double.NaN == Double.NaN     -> false
 *      Double.NaN != Double.NaN     -> true
 *      Double.NaN >  0              -> false
 *      Double.NaN <  0              -> false
 * Use Double.isNaN(value) to test for NaN safely.
 */

public class RelationalOperators {

    public static void main(String[] args) {
        int a = 10;
        int b = 20;

        System.out.println("a == b ? " + (a == b));   // false
        System.out.println("a != b ? " + (a != b));   // true
        System.out.println("a >  b ? " + (a >  b));   // false
        System.out.println("a <  b ? " + (a <  b));   // true
        System.out.println("a >= 10? " + (a >= 10));  // true
        System.out.println("b <= 20? " + (b <= 20));  // true

        // Reference vs content comparison for objects
        String s1 = new String("hello");
        String s2 = new String("hello");
        String s3 = s1;                     // alias - same reference

        System.out.println("s1 == s2        ? " + (s1 == s2));          // false
        System.out.println("s1 == s3        ? " + (s1 == s3));          // true
        System.out.println("s1.equals(s2)   ? " + s1.equals(s2));       // true

        // String literal pool - literals share the same reference
        String x = "java";
        String y = "java";
        System.out.println("\"java\"==\"java\"? " + (x == y));          // true (pooled)

        // NaN comparisons
        double nan = Double.NaN;
        System.out.println("nan == nan      ? " + (nan == nan));        // false (!)
        System.out.println("Double.isNaN(nan)? " + Double.isNaN(nan));  // true

        // OUTPUT
        // a == b ? false
        // a != b ? true
        // a >  b ? false
        // a <  b ? true
        // a >= 10? true
        // b <= 20? true
        // s1 == s2        ? false
        // s1 == s3        ? true
        // s1.equals(s2)   ? true
        // "java"=="java"? true
        // nan == nan      ? false
        // Double.isNaN(nan)? true
    }
}
