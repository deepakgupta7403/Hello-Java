package Phase2_MethodsArraysStrings.Arrays;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Final Arrays - "final" only protects the REFERENCE, not the CONTENTS
 * --------------------------------------------------------------------
 * Marking an array variable `final` is a frequent source of confusion. It only
 * means the variable CANNOT BE REASSIGNED to point to a different array. The
 * elements INSIDE the array remain mutable.
 *
 *      final int[] nums = {1, 2, 3};
 *      nums[0] = 99;            // OK    - element is reassigned, not the variable
 *      nums = new int[]{4,5,6}; // ERROR - cannot reassign `nums`
 *
 *
 * Java Has No Built-In Immutable Array
 * ------------------------------------
 * Unlike Kotlin's `IntArray` + `val` distinction, Java arrays are mutable. To
 * publish data that callers cannot tamper with, you must work AROUND this.
 * Patterns to achieve real immutability:
 *
 *  1. Wrap in `List.of(...)`        - true immutable List (since Java 9).
 *     Best choice for new code.
 *
 *  2. Return a defensive COPY       - getter returns arr.clone() so callers
 *     see a snapshot and cannot mutate the original.
 *
 *  3. Wrap in `Collections.unmodifiableList(Arrays.asList(arr))` -
 *     unmodifiable VIEW over an array (still backed by the array - if you
 *     hold onto the original, you can still mutate via that reference).
 *
 *
 * Compile-Time Constants
 * ----------------------
 * `static final int MAX = 10` is a compile-time constant - inlined into
 * callers. `static final int[] DATA = ...` is NOT a compile-time constant -
 * arrays cannot be inlined, only their REFERENCE is final. That is why
 * `public static final int[] PUBLIC_API = ...` is considered an antipattern
 * and IDEs warn about it - callers can still mutate the elements.
 *
 *
 * Constant Object Arrays
 * ----------------------
 * Even with `record` types or `String` elements, the ARRAY ITSELF is mutable
 * even though each element happens to be immutable. To freeze the structure,
 * use a List (preferred) or a defensive copy.
 */

public class FinalArrays {

    // A bad public "constant" - reference is final but elements are mutable.
    public  static final int[]    BAD_CONST  = {1, 2, 3};

    // A better alternative - a truly immutable List.
    public  static final List<Integer> GOOD_CONST = List.of(1, 2, 3);

    // Private + defensive getter pattern.
    private static final int[]    BACKING    = {10, 20, 30};

    /** Returns a copy so callers cannot mutate the backing array. */
    public static int[] safeCopy() {
        return BACKING.clone();
    }

    public static void main(String[] args) {

        // --- 1) final reference, mutable contents ---
        final int[] nums = {1, 2, 3};

        System.out.println("before mutation : " + Arrays.toString(nums));

        nums[0] = 99;                                     // legal
        nums[1] = nums[1] * 2;                            // legal
        System.out.println("after mutation  : " + Arrays.toString(nums));

        // nums = new int[]{4, 5, 6};                     // ERROR - cannot reassign

        // --- 2) The famous "public static final" array antipattern ---
        System.out.println("\nBAD_CONST before : " + Arrays.toString(BAD_CONST));
        BAD_CONST[0] = 999;                               // anyone can do this!
        System.out.println("BAD_CONST after  : " + Arrays.toString(BAD_CONST));

        // GOOD_CONST is a List.of(...) - mutation is rejected at runtime
        try {
            GOOD_CONST.set(0, 999);
        } catch (UnsupportedOperationException e) {
            System.out.println("GOOD_CONST is immutable - .set() throws");
        }

        // --- 3) Defensive copy via clone() ---
        int[] snapshot = safeCopy();
        snapshot[0] = -1;
        System.out.println("\nsnapshot after mut : " + Arrays.toString(snapshot));
        System.out.println("BACKING still      : " + Arrays.toString(safeCopy()));
        // The caller can mutate `snapshot` all day - the backing data is safe.

        // --- 4) Unmodifiable view over an array ---
        Integer[] arr = {1, 2, 3};
        List<Integer> view = Collections.unmodifiableList(Arrays.asList(arr));
        try {
            view.set(0, 999);                             // unsupported via the view
        } catch (UnsupportedOperationException e) {
            System.out.println("view rejects set(): " + e.getClass().getSimpleName());
        }

        // BUT - if you still hold the underlying array, you can mutate it:
        arr[0] = 999;                                     // bypasses the view
        System.out.println("view sees backing change: " + view);

        // --- 5) Cloning gotcha - clone() is SHALLOW for object arrays ---
        int[][] grid = { {1, 2}, {3, 4} };
        int[][] shallow = grid.clone();                   // top level cloned
        shallow[0][0] = 999;                              // mutates SAME row object
        System.out.println("\nshallow clone leaks: grid[0][0] = " + grid[0][0]);

        // Proper deep copy of a 2D int array:
        int[][] deep = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            deep[i] = grid[i].clone();
        }
        deep[0][0] = -1;
        System.out.println("after deep copy mut: grid[0][0] = " + grid[0][0]);

        // OUTPUT
        // before mutation : [1, 2, 3]
        // after mutation  : [99, 4, 3]
        //
        // BAD_CONST before : [1, 2, 3]
        // BAD_CONST after  : [999, 2, 3]
        // GOOD_CONST is immutable - .set() throws
        //
        // snapshot after mut : [-1, 20, 30]
        // BACKING still      : [10, 20, 30]
        //
        // view rejects set(): UnsupportedOperationException
        // view sees backing change: [999, 2, 3]
        //
        // shallow clone leaks: grid[0][0] = 999
        // after deep copy mut: grid[0][0] = 999
    }
}
