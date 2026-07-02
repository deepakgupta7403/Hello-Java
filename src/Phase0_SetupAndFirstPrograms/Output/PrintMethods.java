package Phase0_SetupAndFirstPrograms.Output;

/**
 * <h1>Printing Output to the Console</h1>
 *
 * <p>Java exposes the standard output stream as the static field <code>System.out</code>, which
 * is an instance of <code>java.io.PrintStream</code>. PrintStream offers three families of
 * methods for writing text to the console:</p>
 *
 * <pre>
 *      System.out.print(...)    -&gt; writes the argument, does NOT add a newline
 *      System.out.println(...)  -&gt; writes the argument and then a newline
 *      System.out.printf(...)   -&gt; C-style formatted output (no trailing newline)
 *      System.out.format(...)   -&gt; same as printf - alias
 * </pre>
 *
 * <p>There is also <code>System.err</code> &mdash; a separate PrintStream used for error messages.
 * Conceptually identical to <code>System.out</code> but typically rendered in red in IDEs and
 * goes to a different OS file descriptor (stderr).</p>
 *
 * <h2>Performance Note</h2>
 * <p><code>System.out</code> is "auto-flush" so each call performs an I/O operation. For tight
 * loops printing millions of lines, wrap it in a <code>BufferedWriter</code> for ~10x speedup:</p>
 * <pre>
 *     PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
 * </pre>
 *
 * <h2>String Concatenation vs printf</h2>
 * <ul>
 *   <li><code>println("x = " + x)</code> is simple and fast for a few values.</li>
 *   <li><code>printf("x = %d%n", x)</code> is preferred when formatting numbers (width, precision,
 *   padding, locale-aware decimals, etc.).</li>
 * </ul>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
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
