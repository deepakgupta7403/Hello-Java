package Phase0_SetupAndFirstPrograms.Output;

/**
 * Printing Output to the Console
 * ------------------------------
 * Java exposes the standard output stream as the static field System.out, which
 * is an instance of java.io.PrintStream. PrintStream offers three families of
 * methods for writing text to the console:
 * <p>
 *
 *      System.out.print(...)    -> writes the argument, does NOT add a newline
 *      System.out.println(...)  -> writes the argument and then a newline
 *      System.out.printf(...)   -> C-style formatted output (no trailing newline)
 *      System.out.format(...)   -> same as printf - alias
 * <p>
 *
 * There is also System.err - a separate PrintStream used for error messages.
 * Conceptually identical to System.out but typically rendered in red in IDEs and
 * goes to a different OS file descriptor (stderr).
 * <p>
 *
 * Performance Note
 * ----------------
 * System.out is "auto-flush" so each call performs an I/O operation. For tight
 * loops printing millions of lines, wrap it in a BufferedWriter for ~10x speedup:
 *     PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
 * <p>
 *
 * String Concatenation vs printf
 * ------------------------------
 * - println("x = " + x) is simple and fast for a few values.
 * - printf("x = %d%n", x) is preferred when formatting numbers (width, precision,
 *   padding, locale-aware decimals, etc.).
 */

public class PrintMethods {

    public static void main(String[] args) {
        // --- 1) print() vs println() ---
        System.out.print("Hello, ");
        System.out.print("World");      // No newline -> next print continues on same line
        System.out.println("!");        // Adds the newline
        System.out.println("Next line"); // Starts on a fresh line

        // --- 2) Printing different data types - println is overloaded ---
        System.out.println(42);                 // int
        System.out.println(3.14159);            // double
        System.out.println(true);               // boolean
        System.out.println('A');                // char
        System.out.println("Java");             // String
        System.out.println(new int[]{1, 2, 3}); // arrays print like [I@1540e19d - need Arrays.toString()

        // --- 3) Concatenation with + ---
        String name = "Deepak";
        int age = 25;
        System.out.println("Name: " + name + ", Age: " + age);

        // --- 4) printf with format specifiers ---
        // %d  - decimal integer
        // %f  - floating point
        // %s  - string
        // %c  - character
        // %b  - boolean
        // %n  - platform-specific newline (use this instead of \n)
        // %%  - literal percent sign
        System.out.printf("Name=%s, Age=%d, Height=%.2f%n", name, age, 1.78);
        System.out.printf("Progress: %d%%%n", 75);

        // --- 5) Width and alignment ---
        // %5d   -> right-aligned in a field of width 5
        // %-5d  -> left-aligned in a field of width 5
        System.out.printf("[%5d]%n",  42);    // [   42]
        System.out.printf("[%-5d]%n", 42);    // [42   ]
        System.out.printf("[%05d]%n", 42);    // [00042]   (zero-padded)

        // --- 6) Precision ---
        System.out.printf("Pi = %.4f%n", Math.PI);   // 4 decimal places
        System.out.printf("Pi = %10.4f%n", Math.PI); // width 10, precision 4

        // --- 7) System.err for errors ---
        System.err.println("This is an error message.");

        // OUTPUT
        // Hello, World!
        // Next line
        // 42
        // 3.14159
        // true
        // A
        // Java
        // [I@1540e19d
        // Name: Deepak, Age: 25
        // Name=Deepak, Age=25, Height=1.78
        // Progress: 75%
        // [   42]
        // [42   ]
        // [00042]
        // Pi = 3.1416
        // Pi =     3.1416
        // This is an error message.
    }
}
