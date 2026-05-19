package Basics.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex Quantifiers
 * -----------------
 * A QUANTIFIER controls how many times the immediately preceding element
 * may repeat. Java supports three FLAVOURS for every quantifier - greedy,
 * reluctant (a.k.a. lazy), and possessive.
 *
 *
 * Counts
 * ------
 *   X?         zero or one     (optional)
 *   X*         zero or more
 *   X+         one  or more
 *   X{n}       exactly n times
 *   X{n,}      n or more
 *   X{n,m}     between n and m (inclusive)
 *
 *
 * Three Flavours
 * --------------
 *   GREEDY      Default. Matches as MUCH as possible, then backtracks if the
 *               rest of the pattern fails.        X*  X+  X?  X{n,m}
 *
 *   RELUCTANT   Add `?` after. Matches as LITTLE as possible, then expands
 *               only when the rest of the pattern needs more characters.
 *                                                  X*?  X+?  X??  X{n,m}?
 *
 *   POSSESSIVE  Add `+` after. Like greedy but NEVER gives back what it
 *               matched - no backtracking. Useful for performance and to
 *               prevent catastrophic backtracking.
 *                                                  X*+  X++  X?+  X{n,m}+
 *
 *
 * The Classic Example (greedy vs reluctant)
 * -----------------------------------------
 *      regex  : <.+>
 *      input  : "<a><b><c>"
 *
 *      greedy    .+   matches everything between the FIRST '<' and the LAST '>'
 *                     -> single match  "<a><b><c>"
 *      reluctant .+?  matches the minimum
 *                     -> three matches "<a>", "<b>", "<c>"
 *
 *
 * Catastrophic Backtracking (use possessive or atomic groups)
 * -----------------------------------------------------------
 *      regex  : (a+)+b
 *      input  : "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac"
 *
 *      Greedy regexes can take EXPONENTIAL time on inputs that almost match.
 *      Possessive quantifiers prevent the backtracking that causes the
 *      blow-up:
 *          (a++)+b           // possessive inner - linear time
 *
 *
 * Common Patterns
 * ---------------
 *      \d{3}          three digits
 *      \d{4,}         four or more digits
 *      \d{2,4}        between two and four digits
 *      \s*            optional surrounding whitespace
 *      [^,]+          a CSV field
 */

public class Quantifiers {

    public static void main(String[] args) {

        section("1) ? * +   - the everyday three");
        match("colou?r",       "color");           // optional u
        match("colou?r",       "colour");
        match("ab*c",          "ac");              // zero or more b
        match("ab*c",          "abc");
        match("ab*c",          "abbbbc");
        match("ab+c",          "ac");              // one or more b - FAILS
        match("ab+c",          "abc");
        match("ab+c",          "abbbbc");

        section("2) {n}, {n,}, {n,m}");
        match("\\d{4}",        "2026");            // exactly 4 digits
        match("\\d{4}",        "26");              // fails - too few
        match("\\d{3,}",       "1234567");         // 3 or more
        match("\\d{2,4}",      "12345");           // 2..4 - matches but not full

        section("3) Greedy vs reluctant - the classic example");
        find("<.+>",  "<a><b><c>");                // greedy   -> "<a><b><c>"
        find("<.+?>", "<a><b><c>");                // reluctant -> "<a>" three times

        section("4) Greedy backtracking demo");
        // .* gobbles everything, then backs off until the "world" tail fits.
        find(".*world", "hello world hello world");   // greedy matches as much as possible
        find(".*?world", "hello world hello world");  // reluctant stops at the first "world"

        section("5) Possessive - no backtracking");
        // \d++ is identical to \d+ when the match succeeds; the difference
        // appears when failure forces backtracking.
        Pattern greedy      = Pattern.compile("\\d+\\.");           // digits then a literal dot
        Pattern possessive  = Pattern.compile("\\d++\\.");
        // input has no dot - greedy will backtrack trying to make the . match;
        // possessive will not.
        long t = nanos();
        for (int i = 0; i < 100; i++) greedy.matcher("12345678901234567890").matches();
        long ms1 = (nanos() - t) / 1_000_000;
        t = nanos();
        for (int i = 0; i < 100; i++) possessive.matcher("12345678901234567890").matches();
        long ms2 = (nanos() - t) / 1_000_000;
        System.out.println("greedy fail loop      : " + ms1 + " ms");
        System.out.println("possessive fail loop  : " + ms2 + " ms (typically faster)");

        section("6) Catastrophic backtracking - DO NOT run with very long inputs");
        // For "ok" inputs both succeed quickly:
        time("(a+)+b",  "aaaaaaaab");
        time("(a++)+b", "aaaaaaaab");
        // The dangerous case ALMOST matches but fails: try with many a's
        // followed by 'c' (no trailing b). Greedy blows up; possessive does not.
        // Kept short here so we don't hang the demo.
        time("(a++)+b", "aaaaaaaaaaaaaaaaaaaac");

        // OUTPUT (timings vary; trust the relative numbers)
        // (matches inline comments above)
    }

    // --- small helpers ---

    private static void match(String regex, String input) {
        boolean ok = Pattern.compile(regex).matcher(input).matches();
        System.out.printf("matches %-12s %-12s -> %s%n", regex, "\"" + input + "\"", ok);
    }

    private static void find(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        java.util.List<String> hits = new java.util.ArrayList<>();
        while (m.find()) hits.add(m.group());
        System.out.printf("find    %-12s %-25s -> %s%n", regex, "\"" + input + "\"", hits);
    }

    private static void time(String regex, String input) {
        long start = nanos();
        Pattern.compile(regex).matcher(input).matches();
        long ms = (nanos() - start) / 1_000_000;
        System.out.printf("time    %-12s %-30s -> %d ms%n", regex, "\"" + input + "\"", ms);
    }

    private static long nanos() { return System.nanoTime(); }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
