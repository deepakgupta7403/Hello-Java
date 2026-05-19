package Basics.Input;

import java.io.Console;

/**
 * Taking Input - java.io.Console
 * ------------------------------
 * The Console class (since Java 6) is the third common way of reading input.
 * Its key feature: it can read passwords WITHOUT echoing them on screen.
 *
 *
 * IMPORTANT - Console can be null
 * -------------------------------
 * System.console() returns null when:
 *   - The JVM is launched without an attached terminal (e.g. from inside an IDE
 *     like IntelliJ / Eclipse, or when stdin/stdout is redirected).
 *
 * To test this class, run it from a real terminal:
 *     javac ConsoleInput.java
 *     java  ConsoleInput
 *
 *
 * Methods Used Here
 * -----------------
 *   readLine(prompt, args...)     - reads a line, prompt supports printf-style format
 *   readPassword(prompt, args...) - reads a line WITHOUT echo, returns char[]
 *                                   (returning char[] instead of String makes it
 *                                    easier to wipe the password from memory)
 *   printf(format, args...)       - formatted print, returns the Console itself
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
        java.util.Arrays.fill(password, ' ');

        // SAMPLE RUN (from a terminal)
        // Username: deepak
        // Password:                <- typed characters do not appear on screen
        //
        // Logged in as: deepak
        // Password length: 8 characters
    }
}
