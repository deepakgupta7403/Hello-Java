package Phase2_MethodsArraysStrings.Strings;

/**
 * Strings in Java - Introduction
 * ------------------------------
 * A String is a sequence of characters. In Java it is NOT a primitive - it is
 * an OBJECT of the class java.lang.String. Internally a String stores its
 * characters in a private final array, which is one reason Strings are
 * immutable (see StringImmutability.java for the deep dive).
 * <p>
 *
 * Two Ways to Create a String
 * ---------------------------
 *  1. STRING LITERAL              "hello"
 *     - The compiler interns the literal into the JVM's STRING POOL.
 *     - Every literal with the same content shares ONE object.
 * <p>
 *
 *  2. EXPLICIT CONSTRUCTOR        new String("hello")
 *     - Always allocates a NEW object on the heap, NOT in the pool.
 *     - Verbose and almost always unnecessary - use literals.
 * <p>
 *
 * String Pool (a.k.a. String Constant Pool)
 * -----------------------------------------
 * A region inside the JVM heap (since Java 7) that caches String literals.
 * The point: identical literals share memory, which saves space and lets the
 * compiler treat constant expressions as compile-time constants.
 * <p>
 *
 *      String a = "java";
 *      String b = "java";
 *      String c = new String("java");
 *      String d = c.intern();         // returns the pooled instance
 * <p>
 *
 *      a == b      -> true   (same pool object)
 *      a == c      -> false  (c was forced into the heap)
 *      a == d      -> true   (intern returned the pool object)
 *      a.equals(c) -> true   (always compare content with .equals)
 * <p>
 *
 * String Backing Store
 * --------------------
 * Internally a String holds:
 *      private final byte[] value;
 *      private final byte    coder;     // LATIN1 (1 byte) or UTF16 (2 bytes)
 *      private int           hash;      // cached on first call to hashCode
 * <p>
 *
 * Since Java 9 - "Compact Strings" - if a string is ASCII / Latin-1, the JVM
 * stores 1 byte per character to save memory. UTF-16 is used only when
 * needed.
 * <p>
 *
 * Bridges Between char[] and String
 * ---------------------------------
 *      new String(char[] chars)          -> char[] to String
 *      str.toCharArray()                 -> String to char[]
 *      String.valueOf(anyType)           -> primitive/Object to String
 *      Integer.parseInt(str)             -> String to int (and similar)
 * <p>
 *
 * String vs char
 * --------------
 *      'a'        - a single CHAR (primitive, 16 bits, in single quotes)
 *      "a"        - a STRING of length 1 (Object, double quotes)
 * <p>
 *
 * Mixing them with `+` works because String overloads `+` for any operand.
 */

public class StringIntroduction {

    public static void main(String[] args) {

        // ============================================================
        // 1. Two ways to create a String
        // ============================================================
        String a = "hello";                    // literal - goes to the pool
        String b = "hello";                    // SAME pooled object as `a`
        String c = new String("hello");        // explicit heap allocation
        String d = c.intern();                 // returns the pooled "hello"

        System.out.println("a == b ?       " + (a == b));      // true  - pool
        System.out.println("a == c ?       " + (a == c));      // false - new
        System.out.println("a == d ?       " + (a == d));      // true  - intern
        System.out.println("a.equals(c) ?  " + a.equals(c));   // true  - always use .equals

        // ============================================================
        // 2. Basic queries
        // ============================================================
        String s = "Hello, Java!";
        System.out.println("length         = " + s.length());           // 12
        System.out.println("charAt(7)      = " + s.charAt(7));          // 'J'
        System.out.println("isEmpty        = " + s.isEmpty());          // false
        System.out.println("contains 'Java'= " + s.contains("Java"));   // true

        // ============================================================
        // 3. Bridges to and from char[]
        // ============================================================
        char[] arr = {'J', 'a', 'v', 'a'};
        String fromChars = new String(arr);
        char[] back      = fromChars.toCharArray();
        System.out.println("String from char[] = " + fromChars);
        System.out.println("back[0]            = " + back[0]);

        // ============================================================
        // 4. Bridges to and from numbers
        // ============================================================
        int    n     = 42;
        String numS  = String.valueOf(n);                    // "42"
        int    parsed = Integer.parseInt("100");             // 100
        System.out.println("numS               = " + numS);
        System.out.println("parsed             = " + parsed);

        // ============================================================
        // 5. char vs String
        // ============================================================
        char ch    = 'A';
        String chS = "A";
        System.out.println("ch + 1             = " + (ch + 1));   // 66 (int arithmetic)
        System.out.println("chS + 1            = " + (chS + 1));  // "A1" (string concat)

        // ============================================================
        // 6. Compact strings - just informational
        // ============================================================
        System.out.println("ASCII string length= " + "ASCII".length());
        System.out.println("Unicode string len = " + "नमस्ते".length()); // characters, not bytes

        // OUTPUT (sample)
        // a == b ?       true
        // a == c ?       false
        // a == d ?       true
        // a.equals(c) ?  true
        // length         = 12
        // charAt(7)      = J
        // isEmpty        = false
        // contains 'Java'= true
        // String from char[] = Java
        // back[0]            = J
        // numS               = 42
        // parsed             = 100
        // ch + 1             = 66
        // chS + 1            = A1
        // ASCII string length= 5
        // Unicode string len = 6
    }
}
