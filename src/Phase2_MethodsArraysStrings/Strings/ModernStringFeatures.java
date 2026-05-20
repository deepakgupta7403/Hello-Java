package Phase2_MethodsArraysStrings.Strings;

/**
 * Modern String Features (Java 11 -> 21)
 * --------------------------------------
 * Java keeps adding small but useful String methods. This file is a quick
 * tour of everything Basics-level that landed in 11, 12, 15, and beyond -
 * all available on Java 21.
 *
 *
 * Java 11
 * -------
 *      isBlank()                       - true if empty OR only whitespace
 *      lines()                         - Stream<String> of lines (no separators)
 *      strip() / stripLeading() /      - Unicode-aware trim (smarter than trim())
 *          stripTrailing()
 *      repeat(int count)               - concat the string with itself N times
 *      chars() / codePoints()          - IntStream over the characters
 *
 * Java 12
 * -------
 *      indent(int n)                   - add (or remove) leading spaces per line
 *      transform(Function<String, R>)  - apply any function, fluently
 *
 * Java 15
 * -------
 *      Text Blocks  """ ... """        - multi-line string literals
 *      formatted(args)                 - instance form of String.format
 *
 * Java 21
 * -------
 *      Pattern matching for switch     - match on `String` values with `when` guards
 *      String templates (preview only) - NOT covered here, the feature was
 *                                        retracted from later releases.
 *
 *
 * Why These Matter
 * ----------------
 *  - `strip()` handles Unicode whitespace - `trim()` only strips ASCII < 0x20.
 *  - `repeat()` is much faster than a loop with `+=`.
 *  - Text blocks make JSON, SQL, HTML in code dramatically easier to read.
 *  - `transform()` and `lines()` enable a fluent, functional style.
 */

public class ModernStringFeatures {

    public static void main(String[] args) {

        section("Java 11 - isBlank() vs isEmpty()");
        System.out.println("\"\".isEmpty()         = " + "".isEmpty());        // true
        System.out.println("\"\".isBlank()         = " + "".isBlank());        // true
        System.out.println("\"   \".isEmpty()      = " + "   ".isEmpty());     // false
        System.out.println("\"   \".isBlank()      = " + "   ".isBlank());     // true
        System.out.println("\" \\t\\n\".isBlank()  = " + " \t\n".isBlank());   // true

        section("Java 11 - strip() vs trim()");
        // Unicode "FULL WIDTH SPACE" (U+3000) - NOT stripped by trim()
        String tricky = "　  hello 　";
        System.out.println("trim().length()   = " + tricky.trim().length());   // still has
        System.out.println("strip().length()  = " + tricky.strip().length());  // truly stripped

        System.out.println("stripLeading()    = '" + "   hi".stripLeading() + "'");
        System.out.println("stripTrailing()   = '" + "hi   ".stripTrailing() + "'");

        section("Java 11 - repeat()");
        System.out.println("\"ab\".repeat(3)   = " + "ab".repeat(3));          // ababab
        System.out.println("\"-\".repeat(10)   = " + "-".repeat(10));          // ----------
        System.out.println("\"x\".repeat(0)    = '" + "x".repeat(0) + "'");    // empty

        // A common use - boxed banners
        String title = " SECTION ";
        String bar   = "=".repeat(title.length());
        System.out.println(bar);
        System.out.println(title);
        System.out.println(bar);

        section("Java 11 - lines() : Stream<String>");
        String multi = "alpha\nbeta\ngamma";
        multi.lines().forEach(System.out::println);
        long count = multi.lines().count();
        System.out.println("lines count       = " + count);

        section("Java 11 - chars() / codePoints()");
        long vowels = "Hello, World".chars()
                                    .filter(ch -> "aeiouAEIOU".indexOf(ch) >= 0)
                                    .count();
        System.out.println("vowel count       = " + vowels);

        section("Java 12 - indent()");
        // indent(n>0) adds n spaces to every line and appends a newline
        // indent(n<0) removes up to |n| leading whitespace characters
        String code = "if (x) {\n    doSomething();\n}";
        System.out.print(code.indent(4));   // shifted right by 4 spaces

        section("Java 12 - transform()");
        String quoted = "java".transform(s -> "<" + s + ">");
        System.out.println("transform         = " + quoted);

        Integer wordCount = "the quick brown fox".transform(s -> s.split(" ").length);
        System.out.println("transform to int  = " + wordCount);

        section("Java 15 - Text Blocks");
        String json = """
                {
                  "name": "Deepak",
                  "skills": ["Java", "Spring"]
                }
                """;
        System.out.println(json);

        // Common indentation is stripped automatically - look at the result:
        String sql = """
                SELECT id, name
                FROM   users
                WHERE  active = true
                """;
        System.out.println(sql);

        section("Java 15 - String#formatted (instance form of String.format)");
        String msg = "Hello %s, you have %d new messages.".formatted("Deepak", 7);
        System.out.println(msg);

        section("Java 21 - Pattern matching for switch on String");
        for (String input : new String[]{"yes", "no", "maybe", null}) {
            String reply = switch (input) {
                case null         -> "missing";
                case "yes", "y"   -> "affirmative";
                case "no", "n"    -> "negative";
                case String s when s.startsWith("m") -> "uncertain (" + s + ")";
                default           -> "unknown";
            };
            System.out.println("input=" + input + "  ->  " + reply);
        }

        // SAMPLE OUTPUT
        // (... matches the inline comments above ...)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
