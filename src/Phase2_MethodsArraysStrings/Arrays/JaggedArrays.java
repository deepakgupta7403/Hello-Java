package Phase2_MethodsArraysStrings.Arrays;

import java.util.Arrays;

/**
 * Jagged Arrays
 * -------------
 * A JAGGED ARRAY is a multi-dimensional array where each ROW can have a
 * different length. Because Java implements multi-dim arrays as
 * "arrays of arrays", every row is an independent object - so different rows
 * naturally support different lengths.
 * <p>
 *
 *      jagged -> [ ref0 ][ ref1 ][ ref2 ]
 *                  |       |       |
 *                  v       v       v
 *                 [1]    [1,2,3]  [1,2,3,4,5]              <- different lengths
 * <p>
 *
 * Why "Jagged"?
 * -------------
 * The visual silhouette of rows of different lengths looks like a jagged
 * (uneven) edge:
 * <p>
 *
 *      X
 *      X X X
 *      X X X X X
 * <p>
 *
 * How to Build One
 * ----------------
 *  1. Allocate the OUTER array with a known number of rows.
 *  2. Allocate EACH row separately with its own length.
 * <p>
 *
 *         int[][] jagged = new int[3][];                // 3 rows, no row arrays yet
 *         jagged[0] = new int[1];                       // length 1
 *         jagged[1] = new int[3];                       // length 3
 *         jagged[2] = new int[5];                       // length 5
 * <p>
 *
 *  Or with a literal:
 * <p>
 *
 *         int[][] jagged = {
 *             {1},
 *             {1, 2, 3},
 *             {1, 2, 3, 4, 5}
 *         };
 * <p>
 *
 * Iteration
 * ---------
 * Always use `row.length` (NOT a constant) when indexing each row:
 * <p>
 *
 *         for (int r = 0; r < jagged.length; r++)
 *             for (int c = 0; c < jagged[r].length; c++)   // PER-ROW length!
 *                 ...
 * <p>
 *
 * Where Jagged Arrays Are Useful
 * ------------------------------
 *  - Triangle / Pascal's triangle (each row is one longer than the previous).
 *  - Adjacency lists of a graph (each node may have a different number of
 *    neighbours).
 *  - Bucketed data where each bucket has a different population.
 *  - Pre-3D engines: per-row tile maps.
 */

public class JaggedArrays {

    public static void main(String[] args) {

        // ============================================================
        // 1. Manual construction
        // ============================================================
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{10};
        jagged[1] = new int[]{20, 30, 40};
        jagged[2] = new int[]{50, 60, 70, 80, 90};

        System.out.println("Manually built jagged array:");
        print(jagged);

        // ============================================================
        // 2. Literal construction
        // ============================================================
        String[][] words = {
                {"hi"},
                {"hello", "world"},
                {"how", "are", "you"}
        };

        System.out.println("\nString jagged array:");
        for (String[] row : words) {
            for (String w : row) System.out.print(w + " ");
            System.out.println();
        }

        // ============================================================
        // 3. Pascal's triangle - classic jagged-array use case
        // ============================================================
        int n = 7;
        int[][] pascal = new int[n][];
        for (int r = 0; r < n; r++) {
            pascal[r] = new int[r + 1];               // each row has r+1 entries
            pascal[r][0] = 1;
            pascal[r][r] = 1;
            for (int c = 1; c < r; c++) {
                pascal[r][c] = pascal[r - 1][c - 1] + pascal[r - 1][c];
            }
        }

        System.out.println("\nPascal's triangle (" + n + " rows):");
        for (int[] row : pascal) {
            // padding to visually centre
            for (int s = 0; s < n - row.length; s++) System.out.print("  ");
            for (int v : row) System.out.printf("%4d", v);
            System.out.println();
        }

        // ============================================================
        // 4. deepToString prints jagged arrays nicely
        // ============================================================
        System.out.println("\nArrays.deepToString(jagged) = " + Arrays.deepToString(jagged));

        // ============================================================
        // 5. Length per row - common bug if you treat it like a rectangle
        // ============================================================
        System.out.println("\nLengths per row:");
        for (int r = 0; r < jagged.length; r++) {
            System.out.println("  row " + r + " has length " + jagged[r].length);
        }

        // ============================================================
        // 6. Sum example
        // ============================================================
        int total = 0;
        for (int[] row : jagged) {
            for (int v : row) total += v;
        }
        System.out.println("\nTotal of all elements = " + total);

        // OUTPUT
        // Manually built jagged array:
        // [10]
        // [20, 30, 40]
        // [50, 60, 70, 80, 90]
        //
        // String jagged array:
        // hi
        // hello world
        // how are you
        //
        // Pascal's triangle (7 rows):
        //               1
        //             1   1
        //           1   2   1
        //         1   3   3   1
        //       1   4   6   4   1
        //     1   5  10  10   5   1
        //   1   6  15  20  15   6   1
        //
        // Arrays.deepToString(jagged) = [[10], [20, 30, 40], [50, 60, 70, 80, 90]]
        //
        // Lengths per row:
        //   row 0 has length 1
        //   row 1 has length 3
        //   row 2 has length 5
        //
        // Total of all elements = 450
    }

    /** Print a jagged int[][] one row per line using Arrays.toString. */
    static void print(int[][] g) {
        for (int[] row : g) {
            System.out.println(Arrays.toString(row));
        }
    }
}
