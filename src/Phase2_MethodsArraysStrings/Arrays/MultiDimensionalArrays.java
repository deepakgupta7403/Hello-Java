package Phase2_MethodsArraysStrings.Arrays;

import java.util.Arrays;

/**
 * Multi-Dimensional Arrays
 * ------------------------
 * Java does NOT have true multi-dimensional arrays the way Fortran or C do.
 * Instead, a "2D array" is really an ARRAY OF ARRAYS - each row is itself a
 * separate one-dimensional array on the heap, linked from a parent array.
 * <p>
 *
 *      int[][] grid = new int[3][4];
 * <p>
 *
 *      grid -> [ ref0 ][ ref1 ][ ref2 ]                  (3 row-references)
 *               |       |       |
 *               v       v       v
 *              [0,0,0,0][0,0,0,0][0,0,0,0]               (each row is 4 ints)
 * <p>
 *
 * Declaration Styles
 * ------------------
 *      int[][]  a;       // preferred
 *      int   a[][];      // legal but unusual
 *      int[] a[];        // legal but ugly
 * <p>
 *
 * Three Ways to Create a 2D Array
 * -------------------------------
 *  1. Allocate both dimensions, default-filled:
 * <p>
 *
 *         int[][] grid = new int[3][4];        // 3 rows, each of length 4
 * <p>
 *
 *  2. Allocate the outer array first, then each row separately:
 * <p>
 *
 *         int[][] grid = new int[3][];
 *         grid[0] = new int[]{1, 2, 3};
 *         grid[1] = new int[]{4, 5, 6};
 * <p>
 *
 *  3. Literal:
 * <p>
 *
 *         int[][] grid = { {1, 2, 3},
 *                          {4, 5, 6},
 *                          {7, 8, 9} };
 * <p>
 *
 * Iteration
 * ---------
 *      for (int r = 0; r < grid.length; r++)
 *          for (int c = 0; c < grid[r].length; c++)
 *              ... grid[r][c] ...
 * <p>
 *
 *      for (int[] row : grid)
 *          for (int v : row)
 *              ... v ...
 * <p>
 *
 * Useful Helpers
 * --------------
 *      Arrays.toString(intRow)        - print a single row
 *      Arrays.deepToString(grid)      - print the whole multi-dim array
 *      Arrays.deepEquals(a, b)        - element-by-element equality (any depth)
 * <p>
 *
 * Notes
 * -----
 *  - 3D, 4D ... are just arrays of 2D, 3D, ... arrays. Same idea.
 *  - Because each row is a separate object, rows can have DIFFERENT lengths -
 *    that variant is called a "jagged array" (see JaggedArrays.java).
 */

public class MultiDimensionalArrays {

    public static void main(String[] args) {

        // --- 1) new int[rows][cols] with default zeros ---
        int[][] zero = new int[3][4];
        System.out.println("Default 3x4 grid:");
        printGrid(zero);

        // --- 2) Array literal ---
        int[][] grid = {
                { 1,  2,  3,  4 },
                { 5,  6,  7,  8 },
                { 9, 10, 11, 12 }
        };
        System.out.println("\nLiteral grid:");
        printGrid(grid);

        System.out.println("grid[1][2] = " + grid[1][2]);     // 7
        System.out.println("rows       = " + grid.length);    // 3
        System.out.println("cols (row 0)= " + grid[0].length); // 4

        // --- 3) Per-row allocation (you can mix lengths - that's a jagged array) ---
        int[][] perRow = new int[3][];
        perRow[0] = new int[]{10};
        perRow[1] = new int[]{20, 30};
        perRow[2] = new int[]{40, 50, 60};
        System.out.println("\nPer-row alloc (jagged):");
        printGrid(perRow);

        // --- 4) Iteration styles ---
        System.out.println("\nIndex loops:");
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                System.out.print(grid[r][c] + "\t");
            }
            System.out.println();
        }

        System.out.println("\nEnhanced for-each:");
        for (int[] row : grid) {
            for (int v : row) {
                System.out.print(v + "\t");
            }
            System.out.println();
        }

        // --- 5) Use Arrays.deepToString to print the whole grid ---
        System.out.println("\ndeepToString: " + Arrays.deepToString(grid));

        // --- 6) Sum example - common task on a 2D array ---
        int total = 0;
        for (int[] row : grid) {
            for (int v : row) {
                total += v;
            }
        }
        System.out.println("Sum of all elements = " + total);

        // --- 7) A 3D array - just an array of 2D arrays ---
        int[][][] cube = new int[2][3][4];
        cube[0][1][2] = 999;
        System.out.println("\ncube dimensions: " + cube.length +
                "x" + cube[0].length + "x" + cube[0][0].length);
        System.out.println("cube[0][1][2]   = " + cube[0][1][2]);

        // --- 8) deepEquals vs equals ---
        int[][] copy = {
                { 1,  2,  3,  4 },
                { 5,  6,  7,  8 },
                { 9, 10, 11, 12 }
        };
        System.out.println("\nArrays.equals(grid, copy)     = " + Arrays.equals(grid, copy));
        System.out.println("Arrays.deepEquals(grid, copy) = " + Arrays.deepEquals(grid, copy));

        // OUTPUT
        // Default 3x4 grid:
        // 0  0  0  0
        // 0  0  0  0
        // 0  0  0  0
        //
        // Literal grid:
        // 1   2   3   4
        // 5   6   7   8
        // 9  10  11  12
        // grid[1][2] = 7
        // rows       = 3
        // cols (row 0)= 4
        //
        // Per-row alloc (jagged):
        // 10
        // 20  30
        // 40  50  60
        //
        // (... index/for-each output omitted - same as the grid above ...)
        //
        // deepToString: [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]]
        // Sum of all elements = 78
        //
        // cube dimensions: 2x3x4
        // cube[0][1][2]   = 999
        //
        // Arrays.equals(grid, copy)     = false
        // Arrays.deepEquals(grid, copy) = true
    }

    /** Print any 2D int array, row by row. */
    static void printGrid(int[][] g) {
        for (int[] row : g) {
            for (int v : row) System.out.print(v + "\t");
            System.out.println();
        }
    }
}
