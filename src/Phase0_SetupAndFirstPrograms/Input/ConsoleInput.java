package Phase0_SetupAndFirstPrograms.Input;

import java.io.Console;
import java.util.Arrays;

/**
 * <h1>Taking Input &mdash; java.io.Console</h1>
 *
 * <p>The <code>Console</code> class (since Java 6) is the third common way of reading input.
 * Its key feature: it can read passwords WITHOUT echoing them on screen.</p>
 *
 * <h2>IMPORTANT - Console can be null</h2>
 *
 * <p><code>System.console()</code> returns null when:</p>
 * <ul>
 *   <li>The JVM is launched without an attached terminal (e.g. from inside an IDE
 *       like IntelliJ / Eclipse, or when stdin/stdout is redirected).</li>
 * </ul>
 *
 * <p>To test this class, run it from a real terminal:</p>
 * <pre>
 * javac ConsoleInput.java
 * java  ConsoleInput
 * </pre>
 *
 * <h2>Methods Used Here</h2>
 * <pre>
 * readLine(prompt, args...)     - reads a line, prompt supports printf-style format
 * readPassword(prompt, args...) - reads a line WITHOUT echo, returns char[]
 *                                 (returning char[] instead of String makes it
 *                                  easier to wipe the password from memory)
 * printf(format, args...)       - formatted print, returns the Console itself
 * </pre>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */

public class ConsoleInput {

    public static void main(String[] args) {
        Console console = System.console();

        if (console == null) {
            System.out.println("No console available - run this from a real terminal,");
            System.out.println("not from inside an IDE.");
            return;
        }

        String user = console.readLine("Username: ");
        char[] password = console.readPassword("Password: ");

        // Pretend we authenticate the user against some store
        console.printf("%nLogged in as: %s%n", user);
        console.printf("Password length: %d characters%n", password.length);

        // Best practice: clear the password from memory once it has been used.
        Arrays.fill(password, ' ');

        // SAMPLE RUN (from a terminal)
        // Username: deepak
        // Password:                <- typed characters do not appear on screen
        //
        // Logged in as: deepak
        // Password length: 8 characters
    }
}
