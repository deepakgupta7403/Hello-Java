package Phase2_MethodsArraysStrings.Methods;

/**
 * Methods in Java - Introduction
 * ------------------------------
 * A METHOD is a reusable block of code that performs a specific task. It is the
 * primary unit of program organisation in Java: classes are built out of fields
 * (state) plus methods (behaviour).
 *
 *
 * Why Methods?
 * ------------
 *  - REUSABILITY    - write once, call many times.
 *  - READABILITY    - give a chunk of logic a descriptive name.
 *  - MAINTAINABILITY- change behaviour in ONE place.
 *  - TESTABILITY    - small methods are easy to test in isolation.
 *  - ABSTRACTION    - callers know WHAT a method does, not HOW.
 *
 *
 * Anatomy of a Method
 * -------------------
 *      [modifiers] [returnType] methodName ( [parameters] ) [throws ExceptionList] {
 *          // method body
 *          [return expression;]   // required unless returnType is void
 *      }
 *
 *      public          static     int     add  ( int a, int b )    {
 *      |__modifier_|   |_static_| |type|  |name||___parameters___|
 *
 *          return a + b;
 *      }
 *
 *
 * The Pieces in Detail
 * --------------------
 *  1. Modifiers          - access (public/private/...) + others (static, final,
 *                          abstract, synchronized, native, strictfp).
 *  2. Return type        - any Java type, or `void` for "returns nothing".
 *  3. Method name        - identifier; convention is camelCase, verb-first.
 *  4. Parameter list     - zero or more typed parameters.
 *  5. throws clause      - declares checked exceptions this method can throw.
 *  6. Body               - the code that runs; may include `return` statements.
 *
 *
 * Method Signature
 * ----------------
 * The SIGNATURE is the method name PLUS the parameter types (in order).
 * The return type and modifiers are NOT part of the signature.
 *
 *      add(int, int)            // signature
 *      add(double, double)      // different signature -> overloading allowed
 *
 *
 * Method Overloading
 * ------------------
 * Two methods can share a name as long as their PARAMETER LISTS differ. The
 * compiler picks the best match at call time based on argument types.
 *
 *      int    add(int a, int b)       { ... }
 *      double add(double a, double b) { ... }      // overload - OK
 *
 * Differing ONLY by return type is NOT enough - it is a compile error.
 *
 *
 * Parameter Passing - "Pass by Value"
 * -----------------------------------
 * Java is ALWAYS pass-by-value:
 *  - For primitives, a COPY of the value is passed.
 *  - For objects, a COPY of the REFERENCE is passed (so the called method can
 *    mutate the object's state but cannot make the caller's variable point to
 *    a different object).
 *
 *
 * Return Statements
 * -----------------
 *  - For non-void methods, EVERY code path must return a value (compiler check).
 *  - For void methods, `return;` (alone) optionally exits early.
 */

public class MethodIntroduction {

    public static void main(String[] args) {

        // --- 1) Call a method with two int parameters and an int return ---
        int sum = add(3, 4);
        System.out.println("add(3,4)            = " + sum);

        // --- 2) Overloaded methods - compiler picks by argument types ---
        System.out.println("add(2.5, 4.5)       = " + add(2.5, 4.5));
        System.out.println("add(\"Hi \", \"there\") = " + add("Hi ", "there"));

        // --- 3) void method - performs an action, no value back ---
        greet("Deepak");
        greet(null);   // demonstrates early return

        // --- 4) Pass-by-value with a primitive ---
        int x = 10;
        tryToChange(x);
        System.out.println("x after tryToChange(x) = " + x);   // unchanged - still 10

        // --- 5) Pass-by-value with an object (reference value is copied) ---
        StringBuilder sb = new StringBuilder("hello");
        mutate(sb);
        System.out.println("sb after mutate(sb)    = " + sb);  // changed - "hello!"

        reassign(sb);
        System.out.println("sb after reassign(sb)  = " + sb);  // still "hello!" - caller variable unaffected

        // --- 6) Recursion: method calling itself ---
        System.out.println("factorial(5)        = " + factorial(5));   // 120

        // OUTPUT
        // add(3,4)            = 7
        // add(2.5, 4.5)       = 7.0
        // add("Hi ", "there") = Hi there
        // Hello, Deepak!
        // (no greeting - null name)
        // x after tryToChange(x) = 10
        // sb after mutate(sb)    = hello!
        // sb after reassign(sb)  = hello!
        // factorial(5)        = 120
    }

    // ---------- Overloaded add methods ----------

    /** Adds two ints. */
    static int add(int a, int b) {
        return a + b;
    }

    /** Adds two doubles - different signature, same name. */
    static double add(double a, double b) {
        return a + b;
    }

    /** Concatenates two strings - also an overload. */
    static String add(String a, String b) {
        return a + b;
    }

    // ---------- A void method with an early-return guard ----------

    /** Prints a greeting; does nothing if name is null. */
    static void greet(String name) {
        if (name == null) {
            System.out.println("(no greeting - null name)");
            return;                              // void early-return
        }
        System.out.println("Hello, " + name + "!");
    }

    // ---------- Pass-by-value demos ----------

    /** Tries to "change" a primitive parameter - cannot, the caller is unaffected. */
    static void tryToChange(int n) {
        n = 999;                                 // only the local copy changes
    }

    /** Mutates the OBJECT referenced by the parameter - visible to the caller. */
    static void mutate(StringBuilder s) {
        s.append("!");
    }

    /** Reassigns the local reference - has NO effect on the caller's variable. */
    static void reassign(StringBuilder s) {
        s = new StringBuilder("REPLACED");
    }

    // ---------- Recursion ----------

    /** Computes n! recursively. Base case is required to terminate. */
    static long factorial(int n) {
        if (n <= 1) return 1;                    // base case
        return n * factorial(n - 1);             // recursive case
    }
}
