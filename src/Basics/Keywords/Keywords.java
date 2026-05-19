package Basics.Keywords;

/**
 * Java Keywords (Reserved Words)
 * ------------------------------
 * Keywords are words RESERVED by the Java language. You cannot use them as
 * identifiers (variable, class, method names). Java has 50+ reserved words,
 * grouped here by purpose.
 *
 *
 * 1) Primitive Types (8)
 * ----------------------
 *      boolean, byte, char, short, int, long, float, double
 *
 *
 * 2) Flow Control (11)
 * --------------------
 *      if, else, switch, case, default,
 *      for, while, do,
 *      break, continue, return
 *
 *
 * 3) Class / Object Related (10)
 * ------------------------------
 *      class, interface, enum, extends, implements,
 *      new, this, super, instanceof, package
 *
 *
 * 4) Access Modifiers (3)
 * -----------------------
 *      public, private, protected
 *      (the fourth - "default" / package-private - has NO keyword)
 *
 *
 * 5) Non-Access Modifiers (7)
 * ---------------------------
 *      static, final, abstract, synchronized,
 *      transient, volatile, native, strictfp
 *      (strictfp became implicit in Java 17, but it is still a reserved word)
 *
 *
 * 6) Exception Handling (5)
 * -------------------------
 *      try, catch, finally, throw, throws
 *
 *
 * 7) Miscellaneous (4)
 * --------------------
 *      void          - method returns nothing
 *      import        - bring another class/package into scope
 *      assert        - debug-time invariant check (Java 1.4+)
 *      const, goto   - RESERVED but UNUSED, kept reserved so you cannot use them
 *                      as identifiers (Java does NOT actually support goto/const).
 *
 *
 * 8) Reserved Literals (3) - technically literals, not keywords
 * -------------------------------------------------------------
 *      true, false, null
 *
 *
 * 9) Contextual Keywords (Restricted - act as keywords ONLY in certain contexts)
 * -----------------------------------------------------------------------------
 *      var       (Java 10+) - local variable type inference
 *      yield     (Java 14+) - switch expression result
 *      record    (Java 16+) - immutable data class
 *      sealed, non-sealed, permits (Java 17+) - sealed class hierarchies
 *      _         (Java 9 deprecated, Java 21+ reserved as unnamed pattern variable)
 *
 * These are NOT fully reserved - you CAN still name a variable "var" or "yield"
 * if you really want to (though it is confusing). They are only treated as
 * keywords in the specific syntactic positions where they apply.
 *
 *
 * Why "const" and "goto" are Reserved but Unused
 * -----------------------------------------------
 * They were left reserved on purpose, so that the compiler can produce a
 * helpful error message ("goto is not supported") instead of treating them as
 * regular identifiers. The Java designers chose final + labeled break/continue
 * over const + goto for safer, more readable code.
 *
 *
 * This class only demonstrates that you CANNOT use keywords as identifiers.
 * The illegal lines are commented - uncomment any of them to see the compile
 * error from the javac compiler.
 */

public class Keywords {

    // --- Legal use of keywords in their proper place ---
    public static final int MAX = 100;          // public + static + final
    private volatile boolean running = true;    // private + volatile
    protected transient String cached;          // protected + transient

    // --- Illegal: keywords as identifiers (uncomment to see errors) ---
    // int class    = 10;     // ERROR: 'class' is a keyword
    // int new      = 10;     // ERROR: 'new'   is a keyword
    // int return   = 10;     // ERROR: 'return' is a keyword
    // int goto     = 10;     // ERROR: 'goto'  is reserved (unused but reserved)
    // int const    = 10;     // ERROR: 'const' is reserved (unused but reserved)
    // int true     = 10;     // ERROR: 'true'  is a literal

    // --- Contextual keywords - actually allowed as identifiers (but confusing) ---
    static int var = 5;       // legal because "var" is contextual, not reserved
    static int yield = 7;     // legal for the same reason

    public static void main(String[] args) {
        // demonstrate flow-control keywords
        for (int i = 0; i < 3; i++) {
            if (i == 1) continue;          // 'continue' keyword
            if (i == 2) break;             // 'break' keyword
            System.out.println("i = " + i);
        }

        // exception handling keywords
        try {
            throw new RuntimeException("boom");   // 'throw' keyword
        } catch (RuntimeException e) {            // 'catch' keyword
            System.out.println("caught: " + e.getMessage());
        } finally {                               // 'finally' keyword
            System.out.println("always runs");
        }

        // OUTPUT
        // i = 0
        // caught: boom
        // always runs
    }
}
