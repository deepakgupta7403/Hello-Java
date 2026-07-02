package Phase0_SetupAndFirstPrograms.Input;

import java.util.Scanner;

/**
 * <h1>Taking Input &mdash; java.util.Scanner</h1>
 *
 * <p>Scanner (since Java 5) is the easiest way to read input from the user.
 * It wraps an InputStream (<code>System.in</code> for the console, but it can also
 * read from files, strings, etc.) and provides typed convenience methods like
 * <code>nextInt()</code>, <code>nextDouble()</code>, <code>nextLine()</code>.</p>
 *
 * <h2>Typical Methods</h2>
 * <pre>
 *   nextByte()    - reads next token as byte
 *   nextShort()   - reads next token as short
 *   nextInt()     - reads next token as int
 *   nextLong()    - reads next token as long
 *   nextFloat()   - reads next token as float
 *   nextDouble()  - reads next token as double
 *   nextBoolean() - reads next token as boolean
 *   next()        - reads next WORD (whitespace-delimited)
 *   nextLine()    - reads the rest of the CURRENT LINE (including spaces)
 * </pre>
 *
 * <h2>The classic "Scanner Pitfall"</h2>
 * <p>After calling <code>nextInt()</code> / <code>nextDouble()</code> etc., a newline
 * character ('\n') is left in the buffer. A subsequent <code>nextLine()</code> will
 * read that empty line instead of waiting for new input. The fix is to call an extra
 * <code>sc.nextLine()</code> to consume the leftover newline. The example below
 * demonstrates both the bug and the fix.</p>
 *
 * <h2>Closing the Scanner</h2>
 * <p>Scanner implements Closeable. Closing the Scanner that wraps <code>System.in</code>
 * also closes <code>System.in</code> for the rest of the JVM &mdash; so be careful. For
 * console apps it is fine, but if multiple parts of your program read input, leave it
 * open.</p>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */

public class ScannerInput {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1) Read an int
        System.out.print("Enter your age   : ");
        int age = sc.nextInt();

        // 2) Read a double
        System.out.print("Enter your height: ");
        double height = sc.nextDouble();

        // 3) IMPORTANT - consume the leftover newline before calling nextLine()
        sc.nextLine();

        // 4) Read a full line (with spaces)
        System.out.print("Enter your name  : ");
        String name = sc.nextLine();

        System.out.println();
        System.out.println("Hello " + name + "!");
        System.out.println("You are " + age + " years old and " + height + "m tall.");

        sc.close();

        // SAMPLE RUN
        // Enter your age   : 25
        // Enter your height: 1.78
        // Enter your name  : Deepak Gupta
        //
        // Hello Deepak Gupta!
        // You are 25 years old and 1.78m tall.
    }
}
