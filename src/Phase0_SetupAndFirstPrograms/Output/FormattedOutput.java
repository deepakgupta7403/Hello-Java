package Phase0_SetupAndFirstPrograms.Output;

/**
 * Advanced Formatted Output
 * -------------------------
 * Beyond plain printf, Java offers two more ways to produce formatted text:
 * <p>
 *
 *   1. String.format(...)  -> returns the formatted string (does not print).
 *   2. java.util.Formatter -> the lower-level engine behind printf and format.
 *   3. Text Blocks (Java 15+) -> multi-line string literals using """ delimiters.
 * <p>
 *
 * Common Format Specifiers (Recap)
 * --------------------------------
 *      %d   - decimal integer
 *      %o   - octal integer
 *      %x   - hexadecimal integer (use %X for uppercase)
 *      %e   - scientific notation
 *      %f   - floating point
 *      %g   - general floating point (chooses %e or %f)
 *      %s   - string (calls toString() for objects)
 *      %c   - character
 *      %b   - boolean
 *      %t   - date/time (followed by a sub-specifier like %tY for year)
 *      %n   - platform-specific line separator
 * <p>
 *
 * Argument Index
 * --------------
 * You can reference the same argument multiple times using "1$", "2$", etc.
 *     "%1$s is %1$s years old"  -> reuses argument #1 twice
 */

public class FormattedOutput {

    public static void main(String[] args) {
        // --- 1) String.format - capture formatted text into a String ---
        String message = String.format("User %s scored %d/%d (%.1f%%)",
                                       "Deepak", 87, 100, 87.0);
        System.out.println(message);

        // --- 2) Integer in different bases ---
        int n = 255;
        System.out.printf("Decimal: %d%n", n);  // 255
        System.out.printf("Octal  : %o%n", n);  // 377
        System.out.printf("Hex    : %x%n", n);  // ff
        System.out.printf("HEX    : %X%n", n);  // FF

        // --- 3) Scientific notation ---
        double avogadro = 6.022e23;
        System.out.printf("%e%n", avogadro);   // 6.022000e+23
        System.out.printf("%.2e%n", avogadro); // 6.02e+23

        // --- 4) Argument indexing - reuse the same value ---
        System.out.printf("%1$s loves %2$s, and %2$s loves %1$s.%n", "Java", "you");

        // --- 5) Date / Time formatting ---
        java.util.Date now = new java.util.Date();
        System.out.printf("Year=%tY  Month=%tm  Day=%td%n", now, now, now);
        System.out.printf("Time=%tH:%tM:%tS%n", now, now, now);

        // --- 6) Text Blocks (Java 15+) - multi-line strings ---
        String json = """
                {
                    "name"  : "Deepak",
                    "age"   : 25,
                    "skills": ["Java", "Spring", "SQL"]
                }
                """;
        System.out.println(json);

        // --- 7) String.formatted (instance method, Java 15+) - same as String.format ---
        String greeting = "Hello %s, welcome to %s!".formatted("Deepak", "Java");
        System.out.println(greeting);

        // SAMPLE OUTPUT
        // User Deepak scored 87/100 (87.0%)
        // Decimal: 255
        // Octal  : 377
        // Hex    : ff
        // HEX    : FF
        // 6.022000e+23
        // 6.02e+23
        // Java loves you, and you loves Java.
        // Year=2026  Month=05  Day=19
        // Time=12:30:45
        // {
        //     "name"  : "Deepak",
        //     "age"   : 25,
        //     "skills": ["Java", "Spring", "SQL"]
        // }
        //
        // Hello Deepak, welcome to Java!
    }
}
