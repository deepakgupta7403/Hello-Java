package Phase2_MethodsArraysStrings.Arrays;

/**
 * Arrays in Java - Introduction
 * -----------------------------
 * An ARRAY is a CONTAINER OBJECT that holds a FIXED NUMBER of values of a
 * SINGLE TYPE. The length is established when the array is created and never
 * changes after that.
 *
 *      +---+---+---+---+---+
 *      |10 |20 |30 |40 |50 |     int[] nums = new int[5];
 *      +---+---+---+---+---+
 *        0   1   2   3   4       indices (always start at 0)
 *
 *
 * Key Properties
 * --------------
 *  - HOMOGENEOUS - every element has the same declared type.
 *  - FIXED SIZE  - once created the length cannot grow or shrink. If you need
 *                  a growable container, use ArrayList instead.
 *  - INDEXED     - elements are accessed via an integer index from 0 to
 *                  length-1. Out-of-range access throws ArrayIndexOutOfBoundsException.
 *  - OBJECTS     - even an int[] is itself an Object that lives on the heap.
 *                  arr.length is a FIELD, not a method (no parentheses).
 *  - DEFAULTS    - new int[3] -> {0, 0, 0}; new String[3] -> {null, null, null}.
 *
 *
 * Three Ways to Create an Array
 * -----------------------------
 *  1. Declare-then-allocate with default values:
 *
 *         int[] nums = new int[5];           // {0, 0, 0, 0, 0}
 *
 *  2. Allocate AND initialise inline using an array literal:
 *
 *         int[] nums = {10, 20, 30};         // shorthand, length inferred
 *
 *  3. Use `new` with an initialiser (needed when not at declaration site):
 *
 *         int[] nums;
 *         nums = new int[]{10, 20, 30};      // explicit `new int[]`
 *
 *
 * Stack vs Heap
 * -------------
 *      int[] nums = new int[3];
 *                ^
 *                'nums' lives on the STACK as a reference. The actual 3-element
 *                array object lives on the HEAP and is garbage-collected when
 *                no references remain.
 *
 *
 * Iterating Over an Array
 * -----------------------
 *      for (int i = 0; i < arr.length; i++) { ... }     // index-based
 *      for (int x : arr)                  { ... }       // enhanced for-each
 *      Arrays.stream(arr).forEach(...);                 // Stream API (Java 8+)
 *
 *
 * Common Pitfalls
 * ---------------
 *  - arr.length is the FIELD - no parentheses (unlike String.length()).
 *  - The first index is 0 and the last is arr.length-1.
 *  - Default for object arrays is null - dereferencing an empty slot
 *    causes NullPointerException.
 *  - Arrays.toString(arr) prints the contents; raw `arr` prints something
 *    cryptic like [I@1540e19d (a JVM-internal identifier).
 */

public class ArrayIntroduction {

    public static void main(String[] args) {

        // --- 1) Create + use a default-initialised int array ---
        int[] scores = new int[5];                // all zeros
        scores[0] = 90;
        scores[1] = 75;
        scores[2] = 60;
        // scores[3] and scores[4] remain 0

        System.out.println("scores.length         = " + scores.length);
        System.out.println("scores[2]             = " + scores[2]);
        System.out.println("scores (raw)          = " + scores);                 // [I@...
        System.out.println("scores (toString)     = " + java.util.Arrays.toString(scores));

        // --- 2) Array literal - shorthand for declaration + initialisation ---
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        System.out.println("days[0] = " + days[0] + ", days[6] = " + days[6]);

        // --- 3) `new` with an initialiser (needed when separated from the
        //        declaration) ---
        int[] nums;
        nums = new int[]{2, 3, 5, 7, 11};
        System.out.println("nums                   = " + java.util.Arrays.toString(nums));

        // --- 4) Iteration - three styles ---
        System.out.println("\nIndex-based loop:");
        for (int i = 0; i < days.length; i++) {
            System.out.println("  days[" + i + "] = " + days[i]);
        }

        System.out.println("\nEnhanced for-each loop:");
        for (String d : days) {
            System.out.println("  " + d);
        }

        System.out.println("\nStream API (Java 8+):");
        java.util.Arrays.stream(nums).forEach(n -> System.out.print(n + " "));
        System.out.println();

        // --- 5) Default values ---
        int[]     ints    = new int[3];           // {0, 0, 0}
        double[]  doubles = new double[3];        // {0.0, 0.0, 0.0}
        boolean[] bools   = new boolean[3];       // {false, false, false}
        String[]  strs    = new String[3];        // {null, null, null}

        System.out.println("\nDefault values");
        System.out.println("int[]     = " + java.util.Arrays.toString(ints));
        System.out.println("double[]  = " + java.util.Arrays.toString(doubles));
        System.out.println("boolean[] = " + java.util.Arrays.toString(bools));
        System.out.println("String[]  = " + java.util.Arrays.toString(strs));

        // --- 6) Pitfall - going out of bounds ---
        try {
            int bad = scores[10];
            System.out.println(bad);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\nOut-of-bounds caught: " + e.getMessage());
        }

        // --- 7) Pitfall - NullPointerException on a default-null slot ---
        try {
            int len = strs[0].length();   // strs[0] is null
            System.out.println(len);
        } catch (NullPointerException e) {
            System.out.println("NPE on strs[0]: element is null by default");
        }

        // OUTPUT
        // scores.length         = 5
        // scores[2]             = 60
        // scores (raw)          = [I@1540e19d   (the JVM signature varies)
        // scores (toString)     = [90, 75, 60, 0, 0]
        // days[0] = Mon, days[6] = Sun
        // nums                   = [2, 3, 5, 7, 11]
        //
        // Index-based loop:
        //   days[0] = Mon
        //   days[1] = Tue
        //   ... (all seven days)
        //
        // Enhanced for-each loop:
        //   Mon
        //   ... (all seven days)
        //
        // Stream API (Java 8+):
        // 2 3 5 7 11
        //
        // Default values
        // int[]     = [0, 0, 0]
        // double[]  = [0.0, 0.0, 0.0]
        // boolean[] = [false, false, false]
        // String[]  = [null, null, null]
        //
        // Out-of-bounds caught: Index 10 out of bounds for length 5
        // NPE on strs[0]: element is null by default
    }
}
