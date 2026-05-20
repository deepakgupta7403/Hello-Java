package Phase1_CoreLanguage.WrapperClasses;

/**
 * Wrapper Classes in Java
 * -----------------------
 * Every primitive in Java has a corresponding WRAPPER class that lives in the
 * java.lang package. A wrapper is an OBJECT that holds a single primitive value.
 *
 *      primitive | wrapper       | size of primitive
 *      ----------+---------------+-------------------
 *      byte      | Byte          | 8 bits
 *      short     | Short         | 16 bits
 *      int       | Integer       | 32 bits  (note - NOT "Int")
 *      long      | Long          | 64 bits
 *      float     | Float         | 32 bits
 *      double    | Double        | 64 bits
 *      char      | Character     | 16 bits  (note - NOT "Char")
 *      boolean   | Boolean       | JVM-dep.
 *
 *
 * Why Do Wrapper Classes Exist?
 * -----------------------------
 *  1. Collections only hold OBJECTS - List<int> is illegal but List<Integer> works.
 *  2. Wrappers can be NULL (a primitive cannot) - useful to represent "missing"
 *     values in DB rows / JSON / DTOs.
 *  3. They expose USEFUL STATIC HELPERS like parseInt(), toBinaryString(),
 *     compare(), min(), max(), MAX_VALUE / MIN_VALUE constants.
 *  4. They participate in generics, reflection, and serialization frameworks.
 *  5. They are IMMUTABLE - safe to share across threads.
 *
 *
 * Boxing and Unboxing
 * -------------------
 * - Boxing      = converting a primitive into the matching wrapper object.
 *                 int -> Integer.
 * - Unboxing    = the reverse: wrapper -> primitive.
 * - Autoboxing  = the compiler does this for you automatically (since Java 5).
 *
 *      Integer boxed   = 42;        // autoboxing  -> Integer.valueOf(42)
 *      int     primit  = boxed;     // auto-unbox  -> boxed.intValue()
 *
 *
 * The Famous Integer Cache (Performance Trap)
 * -------------------------------------------
 * Integer.valueOf(int) caches the integers in the range -128..127. Two boxed
 * Integers in that range share the SAME object - so == happens to return true.
 * Outside that range, two boxed Integers are different objects and == is false.
 *
 *      Integer a = 100, b = 100;   //   a == b   -> true   (cached)
 *      Integer c = 200, d = 200;   //   c == d   -> false  (NOT cached)
 *
 * Lesson: ALWAYS compare wrappers with .equals(), never with ==.
 *
 *
 * Useful Helpers
 * --------------
 *   Integer.parseInt("42")        -> 42
 *   Integer.valueOf("42")         -> Integer(42)
 *   Integer.toBinaryString(10)    -> "1010"
 *   Integer.toHexString(255)      -> "ff"
 *   Integer.bitCount(7)           -> 3   (number of set bits)
 *   Integer.compare(a, b)         -> -1 / 0 / 1
 *   Integer.MAX_VALUE             -> 2147483647
 *   Boolean.parseBoolean("true")  -> true
 *   Character.isDigit('5')        -> true
 *   Character.toUpperCase('a')    -> 'A'
 *
 *
 * See AutoboxingUnboxing.java in this package for the boxing/unboxing demo.
 */

public class WrapperClassesIntro {

    public static void main(String[] args) {
        // --- 1) Creating wrappers (preferred way - valueOf for the cache) ---
        Integer i = Integer.valueOf(42);     // boxed
        Double  d = Double.valueOf(3.14);
        Boolean b = Boolean.valueOf(true);
        Character c = Character.valueOf('A');

        System.out.println("i = " + i + ", d = " + d + ", b = " + b + ", c = " + c);

        // --- 2) Parsing strings into primitives / wrappers ---
        int parsed   = Integer.parseInt("123");   // returns primitive int
        Integer wrap = Integer.valueOf("456");    // returns Integer (boxed)
        System.out.println("parseInt(\"123\") = " + parsed);
        System.out.println("valueOf(\"456\") = " + wrap);

        // --- 3) Useful constants ---
        System.out.println("Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println("Double.MAX_VALUE  = " + Double.MAX_VALUE);

        // --- 4) Base conversions on Integer ---
        System.out.println("10  in binary = " + Integer.toBinaryString(10));   // 1010
        System.out.println("255 in hex    = " + Integer.toHexString(255));     // ff
        System.out.println("8   in octal  = " + Integer.toOctalString(8));     // 10

        // --- 5) The Integer cache pitfall ---
        Integer x = 100, y = 100;
        System.out.println("100 == 100 ? " + (x == y));         // true  (cached)
        Integer p = 200, q = 200;
        System.out.println("200 == 200 ? " + (p == q));         // false (NOT cached)
        System.out.println("200 .equals 200 ? " + p.equals(q)); // true  (correct way)

        // --- 6) Wrappers can be null - primitives cannot ---
        Integer maybeMissing = null;
        System.out.println("missing = " + maybeMissing);
        // int dangerous = maybeMissing;   // NullPointerException at auto-unbox!

        // --- 7) Character helpers ---
        System.out.println("isDigit('5') ? " + Character.isDigit('5'));
        System.out.println("isLetter('A')? " + Character.isLetter('A'));
        System.out.println("toUpper('j') = " + Character.toUpperCase('j'));

        // OUTPUT
        // i = 42, d = 3.14, b = true, c = A
        // parseInt("123") = 123
        // valueOf("456") = 456
        // Integer.MIN_VALUE = -2147483648
        // Integer.MAX_VALUE = 2147483647
        // Double.MAX_VALUE  = 1.7976931348623157E308
        // 10  in binary = 1010
        // 255 in hex    = ff
        // 8   in octal  = 10
        // 100 == 100 ? true
        // 200 == 200 ? false
        // 200 .equals 200 ? true
        // missing = null
        // isDigit('5') ? true
        // isLetter('A')? true
        // toUpper('j') = J
    }
}
