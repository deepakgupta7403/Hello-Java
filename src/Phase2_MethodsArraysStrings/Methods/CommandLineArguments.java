package Phase2_MethodsArraysStrings.Methods;

/**
 * Command-Line Arguments
 * ----------------------
 * When you launch a Java program, anything you type after the class name on the
 * command line is passed to `main` as a String array:
 * <p>
 *
 *      public static void main(String[] args)
 * <p>
 *
 * Each whitespace-separated token becomes one element of `args`. Arguments are
 * ALWAYS Strings - you must parse them yourself if you need numbers.
 * <p>
 *
 * Running This File
 * -----------------
 * Compile first:
 *      javac src/Basics/Methods/CommandLineArguments.java
 * <p>
 *
 * Then run (from the src directory) with whatever arguments you want:
 *      cd src
 *      java Basics.Methods.CommandLineArguments alpha beta 42
 * <p>
 *
 * Or, single-file mode (Java 11+):
 *      java src/Basics/Methods/CommandLineArguments.java alpha beta 42
 * <p>
 *
 * In IntelliJ - right-click -> "Modify Run Configuration..." -> "Program
 * arguments" field. List tokens separated by spaces. Quote tokens that contain
 * spaces: "Hello World".
 * <p>
 *
 * Things To Know
 * --------------
 *  - args is NEVER null. If no arguments are passed, args.length == 0.
 *  - The PROGRAM NAME (the class) is NOT in args - that is a C/C++ convention.
 *    args[0] is the FIRST user argument.
 *  - Quote any argument that contains spaces:   "Hello World".
 *  - Standard input is separate: command-line args are not the same as Scanner.
 * <p>
 *
 * Demo
 * ----
 * This program echoes each argument, then sums any that parse as integers.
 */

public class CommandLineArguments {

    public static void main(String[] args) {

        System.out.println("You passed " + args.length + " argument(s).");

        // --- 1) Echo each argument with its index ---
        for (int i = 0; i < args.length; i++) {
            System.out.println("  args[" + i + "] = \"" + args[i] + "\"");
        }

        // --- 2) Sum any arguments that parse as integers; ignore the rest ---
        int total = 0;
        int counted = 0;
        for (String token : args) {
            try {
                total += Integer.parseInt(token);
                counted++;
            } catch (NumberFormatException ignore) {
                // not an integer - skip
            }
        }
        System.out.println("Sum of " + counted + " integer argument(s) = " + total);

        // --- 3) Simple "flag" parsing pattern ---
        boolean verbose = false;
        String name     = "world";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-v", "--verbose" -> verbose = true;
                case "--name"          -> {
                    if (i + 1 < args.length) {
                        name = args[++i];          // consume the next token
                    } else {
                        System.err.println("--name requires a value");
                    }
                }
                default -> { /* ignored in this simple example */ }
            }
        }
        System.out.println("verbose = " + verbose + ", name = " + name);
        if (verbose) {
            System.out.println("(verbose mode is on)");
        }

        // SAMPLE RUNS
        //
        // > java Basics.Methods.CommandLineArguments
        // You passed 0 argument(s).
        // Sum of 0 integer argument(s) = 0
        // verbose = false, name = world
        //
        // > java Basics.Methods.CommandLineArguments alpha 10 20 beta
        // You passed 4 argument(s).
        //   args[0] = "alpha"
        //   args[1] = "10"
        //   args[2] = "20"
        //   args[3] = "beta"
        // Sum of 2 integer argument(s) = 30
        // verbose = false, name = world
        //
        // > java Basics.Methods.CommandLineArguments --verbose --name Deepak
        // You passed 3 argument(s).
        //   args[0] = "--verbose"
        //   args[1] = "--name"
        //   args[2] = "Deepak"
        // Sum of 0 integer argument(s) = 0
        // verbose = true, name = Deepak
        // (verbose mode is on)
    }
}
