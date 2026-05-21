package Phase2_MethodsArraysStrings.Arrays;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * java.util.Arrays - The Array Utility Class
 * ------------------------------------------
 * `Arrays` is a final class of STATIC HELPERS for working with Java arrays:
 * print, sort, search, copy, fill, compare, convert to a List or Stream.
 * It is one of the most useful classes in the JDK - learn it well.
 * <p>
 *
 * Methods Covered Here (most-used selection)
 * ------------------------------------------
 *      Printing :    Arrays.toString(arr)          single dim
 *                    Arrays.deepToString(grid)     multi dim
 * <p>
 *
 *      Sorting  :    Arrays.sort(arr)              full sort
 *                    Arrays.sort(arr, from, to)    partial sort
 *                    Arrays.sort(arr, comparator)  custom order (objects)
 *                    Arrays.parallelSort(arr)      multi-threaded sort
 * <p>
 *
 *      Searching:    Arrays.binarySearch(arr, key)  array MUST be sorted
 * <p>
 *
 *      Copying  :    Arrays.copyOf(arr, newLen)
 *                    Arrays.copyOfRange(arr, from, to)
 *                    System.arraycopy(...)         lower-level, faster
 *                    arr.clone()                   single-dim shallow copy
 * <p>
 *
 *      Filling  :    Arrays.fill(arr, value)
 *                    Arrays.fill(arr, from, to, value)
 *                    Arrays.setAll(arr, i -> ...)        (Java 8+)
 * <p>
 *
 *      Equality :    Arrays.equals(a, b)
 *                    Arrays.deepEquals(a, b)        for nested arrays
 *                    Arrays.compare(a, b)           Java 9+, lex order
 *                    Arrays.mismatch(a, b)          Java 9+, first diff index
 *                    Arrays.hashCode(arr)
 *                    Arrays.deepHashCode(grid)
 * <p>
 *
 *      Bridges  :    Arrays.asList(...)             FIXED-SIZE list view
 *                    Arrays.stream(arr)             open a Stream over arr
 * <p>
 *
 * Common Gotchas
 * --------------
 *  - `arr.toString()` does NOT print contents; use Arrays.toString(arr).
 *  - `Arrays.asList(arr)` of a PRIMITIVE array does the wrong thing - it
 *    returns a List with ONE element (the array itself). Use Integer[] or
 *    convert via IntStream / boxed().
 *  - The list returned by `Arrays.asList(...)` is FIXED-SIZE - you cannot
 *    add or remove elements. To get a growable copy use `new ArrayList<>(...)`.
 *  - `binarySearch` returns a negative insertion point if the key is missing.
 */

public class ArraysClass {

    public static void main(String[] args) {

        int[] data = {5, 3, 9, 1, 7, 2, 8, 6, 4};

        // --- 1) Printing ---
        System.out.println("toString          : " + Arrays.toString(data));

        // --- 2) Sorting (in place) ---
        int[] sorted = data.clone();             // clone so original stays untouched
        Arrays.sort(sorted);
        System.out.println("sorted            : " + Arrays.toString(sorted));

        // Partial sort - only indices [2, 6)
        int[] partial = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        Arrays.sort(partial, 2, 6);              // sorts {7,6,5,4} in place
        System.out.println("partial sort      : " + Arrays.toString(partial));

        // parallelSort - same result but uses Fork/Join under the hood
        int[] big = IntStream.range(0, 16).map(i -> 15 - i).toArray();
        Arrays.parallelSort(big);
        System.out.println("parallelSort      : " + Arrays.toString(big));

        // Custom-order sort needs an object array (no primitive overload)
        Integer[] objects = {3, 1, 4, 1, 5, 9, 2, 6};
        Arrays.sort(objects, Collections.reverseOrder());
        System.out.println("descending sort   : " + Arrays.toString(objects));

        // --- 3) Binary search (input MUST be sorted) ---
        int idxFound   = Arrays.binarySearch(sorted, 7);
        int idxMissing = Arrays.binarySearch(sorted, 10);
        System.out.println("binarySearch 7    : index " + idxFound);
        System.out.println("binarySearch 10   : " + idxMissing +
                " (negative means insertion point = " + (-idxMissing - 1) + ")");

        // --- 4) Copying ---
        int[] copy        = Arrays.copyOf(data, data.length);          // same length
        int[] grow        = Arrays.copyOf(data, data.length + 3);      // zero-padded
        int[] shrink      = Arrays.copyOf(data, 4);                    // truncated
        int[] middleSlice = Arrays.copyOfRange(data, 2, 7);            // indices 2..6
        System.out.println("copy              : " + Arrays.toString(copy));
        System.out.println("grow              : " + Arrays.toString(grow));
        System.out.println("shrink            : " + Arrays.toString(shrink));
        System.out.println("range[2,7)        : " + Arrays.toString(middleSlice));

        // System.arraycopy - the low-level building block of Arrays.copyOf
        int[] dest = new int[10];
        System.arraycopy(data, 0, dest, 2, 5);  // copy 5 ints starting at dest[2]
        System.out.println("System.arraycopy  : " + Arrays.toString(dest));

        // --- 5) Filling ---
        int[] full = new int[6];
        Arrays.fill(full, 7);
        System.out.println("fill 7            : " + Arrays.toString(full));

        Arrays.fill(full, 1, 4, 0);             // [from, to)
        System.out.println("fill range to 0   : " + Arrays.toString(full));

        // setAll - compute each element from its index
        int[] squares = new int[6];
        Arrays.setAll(squares, i -> i * i);
        System.out.println("setAll squares    : " + Arrays.toString(squares));

        // --- 6) Equality ---
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        int[] c = {1, 2, 4};
        System.out.println("a == b            : " + (a == b));        // false - different objects
        System.out.println("Arrays.equals(a,b): " + Arrays.equals(a, b)); // true
        System.out.println("Arrays.compare(a,c): " + Arrays.compare(a, c)); // negative - a < c
        System.out.println("Arrays.mismatch(a,c): " + Arrays.mismatch(a, c)); // 2 (first diff index)

        // --- 7) Bridges to List and Stream ---
        Integer[] words = {1, 2, 3, 4, 5};
        List<Integer> list = Arrays.asList(words);    // FIXED-SIZE view
        System.out.println("asList            : " + list);
        try {
            list.add(6);                              // unsupported
        } catch (UnsupportedOperationException e) {
            System.out.println("asList is fixed-size: add() not supported");
        }

        int sum = Arrays.stream(data).sum();
        double avg = Arrays.stream(data).average().orElse(0);
        int max = Arrays.stream(data).max().orElseThrow();
        System.out.println("stream sum/avg/max: " + sum + " / " + avg + " / " + max);

        // The asList primitive trap - this is List<int[]> of size 1, NOT List<Integer> of size 5
        List<int[]> trap = Arrays.asList(data);
        System.out.println("asList(int[]) size: " + trap.size() + " (NOT what most people expect)");

        // Correct way to make a List<Integer> from int[]
        List<Integer> boxed = Arrays.stream(data).boxed().toList();
        System.out.println("boxed list        : " + boxed);

        // OUTPUT (sample)
        // toString          : [5, 3, 9, 1, 7, 2, 8, 6, 4]
        // sorted            : [1, 2, 3, 4, 5, 6, 7, 8, 9]
        // partial sort      : [9, 8, 4, 5, 6, 7, 3, 2, 1]
        // parallelSort      : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
        // descending sort   : [9, 6, 5, 4, 3, 2, 1, 1]
        // binarySearch 7    : index 6
        // binarySearch 10   : -10 (negative means insertion point = 9)
        // copy              : [5, 3, 9, 1, 7, 2, 8, 6, 4]
        // grow              : [5, 3, 9, 1, 7, 2, 8, 6, 4, 0, 0, 0]
        // shrink            : [5, 3, 9, 1]
        // range[2,7)        : [9, 1, 7, 2, 8]
        // System.arraycopy  : [0, 0, 5, 3, 9, 1, 7, 0, 0, 0]
        // fill 7            : [7, 7, 7, 7, 7, 7]
        // fill range to 0   : [7, 0, 0, 0, 7, 7]
        // setAll squares    : [0, 1, 4, 9, 16, 25]
        // a == b            : false
        // Arrays.equals(a,b): true
        // Arrays.compare(a,c): -1
        // Arrays.mismatch(a,c): 2
        // asList            : [1, 2, 3, 4, 5]
        // asList is fixed-size: add() not supported
        // stream sum/avg/max: 45 / 5.0 / 9
        // asList(int[]) size: 1 (NOT what most people expect)
        // boxed list        : [5, 3, 9, 1, 7, 2, 8, 6, 4]
    }
}
