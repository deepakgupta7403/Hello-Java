package Phase0_SetupAndFirstPrograms.Input;

import java.util.Scanner;

/**
 * Taking Input - java.util.Scanner
 * --------------------------------
 * Scanner (since Java 5) is the easiest way to read input from the user.
 * It wraps an InputStream (System.in for the console, but it can also read from
 * files, strings, etc.) and provides typed convenience methods like nextInt(),
 * nextDouble(), nextLine().
 *
 *
 * Typical Methods
 * ---------------
 *   nextByte()    - reads next token as byte
 *   nextShort()   - reads next token as short
 *   nextInt()     - reads next token as int
 *   nextLong()    - reads next token as long
 *   nextFloat()   - reads next token as float
 *   nextDouble()  - reads next token as double
 *   nextBoolean() - reads next token as boolean
 *   next()        - reads next WORD (whitespace-delimited)
 *   nextLine()    - reads the rest of the CURRENT LINE (including spaces)
 *
 *
 * The classic "Scanner Pitfall"
 * -----------------------------
 * After calling nextInt() / nextDouble() etc., a newline character ('\n') is
 * left in the buffer. A subsequent nextLine() will read that empty line instead
 * of waiting for new input. The fix is to call an extra sc.nextLine() to consume
 * the leftover newline. The example below demonstrates both the bug and the fix.
 *
 *
 * Closing the Scanner
 * -------------------
 * Scanner implements Closeable. Closing the Scanner that wraps System.in also
 * closes System.in for the rest of the JVM - so be careful. For console apps it
 * is fine, but if multiple parts of your program read input, leave it open.
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
