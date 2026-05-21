package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex Flags
 * -----------
 * Flags change HOW the engine matches a pattern. You can pass them to
 * Pattern.compile as the second argument, or set them inline inside the
 * pattern via `(?flags)` so the regex itself is self-describing.
 * <p>
 *
 *      Pattern.compile("hello", Pattern.CASE_INSENSITIVE);
 *      Pattern.compile("(?i)hello");                            // inline
 *      Pattern.compile("(?i:HEL)lo");                            // scoped inline
 * <p>
 *
 * The Flags You Will Actually Use
 * -------------------------------
 * <p>
 *
 *   Pattern.CASE_INSENSITIVE   (?i)   - "hello" matches "HELLO" too
 *   Pattern.MULTILINE          (?m)   - ^ and $ match per-line, not just at the
 *                                       very start/end of the input
 *   Pattern.DOTALL             (?s)   - "." matches newlines too
 *   Pattern.COMMENTS           (?x)   - whitespace and # comments inside the
 *                                       pattern are ignored, letting you space
 *                                       out a complex pattern
 *   Pattern.UNICODE_CASE       (?u)   - case-insensitive matching also covers
 *                                       non-ASCII letters
 *   Pattern.UNICODE_CHARACTER_CLASS  (?U)
 *                                     - \w, \d, \s use Unicode-aware definitions
 *   Pattern.UNIX_LINES         (?d)   - "." treats only `\n` as a line terminator
 *   Pattern.LITERAL            (no inline form) - treats the pattern as a
 *                                       literal string (no metacharacters)
 *   Pattern.CANON_EQ           (no inline form) - canonical equivalence of
 *                                       Unicode sequences (rarely needed)
 * <p>
 *
 * Combining Flags
 * ---------------
 * Bitwise OR them:
 * <p>
 *
 *      Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
 * <p>
 *
 * Or chain inline:
 * <p>
 *
 *      Pattern.compile("(?im)^hello");           // case-insensitive + multiline
 * <p>
 *
 * Scoped Inline Flags - (?flags:X)
 * --------------------------------
 * Apply flags ONLY to a sub-expression:
 * <p>
 *
 *      Pattern.compile("(?i:hel)lo");
 * <p>
 *
 * Matches "Hello", "HELlo", "HELLo" but NOT "Hellol" or "Hellol".
 * (Outside the (?i:...) the matching is case-sensitive again.)
 * <p>
 *
 * Flag Quick Demos Below
 * ----------------------
 * Each section toggles ONE flag so you can see the difference.
 */

public class RegexFlags {

    public static void main(String[] args) {

        section("1) CASE_INSENSITIVE  (?i)");
        Pattern sensitive   = Pattern.compile("java");
        Pattern insensitive = Pattern.compile("java", Pattern.CASE_INSENSITIVE);
        Pattern inline      = Pattern.compile("(?i)java");
        for (String s : new String[]{"java", "JAVA", "Java"}) {
            System.out.printf("%-6s : sens=%-5s ins=%-5s inline=%s%n",
                    s,
                    sensitive.matcher(s).matches(),
                    insensitive.matcher(s).matches(),
                    inline.matcher(s).matches());
        }

        section("2) MULTILINE  (?m)  - ^ and $ work per line");
        String multi = "alpha\nbeta\ngamma";
        // Without MULTILINE, ^ only matches at the very start.
        find(Pattern.compile("^\\w+"),                     multi);   // [alpha]
        // With MULTILINE, ^ matches at the start of every line.
        find(Pattern.compile("^\\w+", Pattern.MULTILINE), multi);   // [alpha, beta, gamma]
        find(Pattern.compile("(?m)\\w+$"),                multi);   // [alpha, beta, gamma]

        section("3) DOTALL  (?s)  - . matches newlines");
        String html = "<a>\nstuff\n</a>";
        find(Pattern.compile("<a>(.*)</a>"),                html);  // [] - . doesn't match \n
        find(Pattern.compile("(?s)<a>(.*)</a>"),            html);  // captures across lines

        section("4) COMMENTS  (?x)  - whitespace + # ignored");
        Pattern phone = Pattern.compile(
                """
                \\(?\\d{3}\\)?     # area code, optionally in parens
                [\\s.-]?          # optional separator
                \\d{3}             # exchange
                [\\s.-]?          # optional separator
                \\d{4}             # subscriber
                """,
                Pattern.COMMENTS
        );
        for (String n : new String[]{"(555) 867-5309", "555.867.5309", "5558675309", "no number"}) {
            System.out.printf("%-15s -> %s%n", n, phone.matcher(n).matches());
        }

        section("5) UNICODE_CHARACTER_CLASS  (?U)  - \\w / \\d / \\s become Unicode-aware");
        String s = "naïve café 2026 αβγ";
        find(Pattern.compile("\\w+"),                          s); // ASCII only -> [naïve... but only ASCII subset]
        find(Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS), s); // catches Unicode letters

        section("6) LITERAL  - regex metacharacters lose meaning");
        Pattern literal = Pattern.compile("a.b", Pattern.LITERAL);
        System.out.println("\"a.b\".matches(literal a.b) = " + literal.matcher("a.b").matches());
        System.out.println("\"axb\".matches(literal a.b) = " + literal.matcher("axb").matches());

        section("7) Combining flags");
        Pattern combo = Pattern.compile("^hello", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        find(combo, "Hello world\nHELLO again\nhi there");

        section("8) SCOPED inline flags  (?i:...)");
        Pattern scoped = Pattern.compile("(?i:hel)lo");
        for (String w : new String[]{"hello", "HELLO", "HELlo", "Hellol"}) {
            System.out.printf("%-7s -> matches=%s%n", w, scoped.matcher(w).matches());
        }

        // OUTPUT (representative)
    }

    private static void find(Pattern p, String input) {
        Matcher m = p.matcher(input);
        java.util.List<String> hits = new java.util.ArrayList<>();
        while (m.find()) hits.add(m.group());
        System.out.printf("  %-50s -> %s%n", "/" + p.pattern().replace("\n", "\\n") + "/", hits);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
