package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex in Java - Introduction
 * ----------------------------
 * A REGULAR EXPRESSION (regex) is a small language for describing
 * patterns of text. Java exposes it through the package java.util.regex,
 * which contains two main classes:
 *
 *      Pattern      - the compiled representation of a regex.
 *      Matcher      - the engine that runs a Pattern against an input.
 *
 *      Pattern p = Pattern.compile("\\d+");
 *      Matcher m = p.matcher("abc 123 xyz 45");
 *      m.find();              // true; matched "123"
 *      m.group();             // "123"
 *
 *
 * What Can A Regex Do?
 * --------------------
 *   - VALIDATE a string fits a shape (e.g. an email address).
 *   - SEARCH for occurrences inside a longer text.
 *   - EXTRACT pieces of a string into named groups.
 *   - REPLACE one substring (or set of substrings) with another.
 *   - SPLIT a string at every occurrence of a delimiter.
 *
 *
 * The 3 Ways to Use Regex in Java
 * -------------------------------
 *   1. Convenience methods on java.lang.String:
 *
 *         "abc 123".matches("\\w+ \\d+");
 *         "a,b,,c".split(",");
 *         "x1y2z3".replaceAll("\\d", "*");
 *         "x1y2z3".replaceFirst("\\d", "*");
 *
 *      These compile the regex EACH call. Fine for one-shots, wasteful
 *      inside loops.
 *
 *   2. Pattern + Matcher - precompile once, reuse many times:
 *
 *         Pattern p = Pattern.compile("\\d+");
 *         for (String s : huge) {
 *             Matcher m = p.matcher(s);
 *             ...
 *         }
 *
 *   3. The newer Stream/functional API (Java 8+):
 *
 *         p.matcher(text).results().map(MatchResult::group).forEach(...);
 *         p.splitAsStream(text);
 *
 *
 * Important: matches() vs find()
 * ------------------------------
 *      m.matches()  - returns TRUE only if the ENTIRE input matches.
 *      m.find()     - returns TRUE if a SUBSTRING anywhere matches.
 *      m.lookingAt()- returns TRUE if the input matches starting AT THE BEGINNING
 *                     (does not need to consume the whole input).
 *
 * `String.matches(regex)` calls `matches()` - so it requires a full match.
 * That is the #1 source of "my regex doesn't match" surprise.
 *
 *
 * Escaping In Java Strings
 * ------------------------
 * Regex uses `\` for many meta-characters (\d, \s, \w, \., \(, ...). Java
 * strings ALSO use `\` for escapes. That means a regex `\d` is written
 * `"\\d"` in a Java string literal. A literal backslash in regex is `\\`,
 * which is `"\\\\"` in a string literal.
 *
 *      Java 15+ TEXT BLOCKS spare you one level of escaping:
 *
 *          String r = """
 *                     \d+\s+\w+
 *                     """.strip();
 *
 *
 * Folder Map
 * ----------
 *   RegexIntroduction.java     (this file)
 *   MatcherClass.java          - everything Matcher can do
 *   CharacterClass.java        - [...], \d, \w, \s, POSIX, Unicode
 *   Quantifiers.java           - *, +, ?, {n,m} and greedy/reluctant/possessive
 *   MetacharactersAndAnchors.java
 *                              - ^, $, \b, \B, ., escapes
 *   GroupsAndBackreferences.java
 *                              - (...), (?:...), (?<name>...), \1
 *   LookaroundAssertions.java  - (?=...), (?!...), (?<=...), (?<!...)
 *   RegexFlags.java            - CASE_INSENSITIVE / MULTILINE / DOTALL / ...
 *   ModernRegexFeatures.java   - Stream-based regex (Java 8+)
 *   RegexExamples.java         - email / phone / url / password / etc.
 */

public class RegexIntroduction {

    public static void main(String[] args) {

        section("1) The shortcuts on java.lang.String");
        System.out.println("\"abc 123\".matches(\\\\w+ \\\\d+) = " + "abc 123".matches("\\w+ \\d+"));
        System.out.println("split(\",\")     = " + java.util.Arrays.toString("a,b,,c".split(",")));
        System.out.println("replaceAll(d->*) = " + "x1y2z3".replaceAll("\\d", "*"));
        System.out.println("replaceFirst     = " + "x1y2z3".replaceFirst("\\d", "*"));

        section("2) Pattern + Matcher - precompile once, reuse many times");
        Pattern wordPlusDigits = Pattern.compile("(\\w+)(\\d+)");
        String[] inputs = {"alpha42", "x99", "noDigits", "z00"};
        for (String in : inputs) {
            Matcher m = wordPlusDigits.matcher(in);
            if (m.matches()) {
                System.out.println(in + " -> letters='" + m.group(1) + "', digits='" + m.group(2) + "'");
            } else {
                System.out.println(in + " -> no full match");
            }
        }

        section("3) matches() vs find() vs lookingAt()");
        Pattern digits = Pattern.compile("\\d+");
        Matcher m = digits.matcher("abc 123 xyz 45");
        System.out.println("matches()      = " + m.matches());     // false - full match required
        m.reset();
        System.out.println("lookingAt()    = " + m.lookingAt());   // false - input does NOT start with digits
        m.reset();
        System.out.println("find()         = " + m.find());        // true  - "123"
        System.out.println("group()        = " + m.group());

        // .find() can be called repeatedly to walk through every match
        m.reset();
        System.out.print("all matches via find(): ");
        while (m.find()) System.out.print(m.group() + " ");
        System.out.println();

        section("4) Java escape vs regex escape - keep them straight");
        // To MATCH a literal dot, the regex needs `\.` ; the Java string needs `\\.`
        Pattern dot = Pattern.compile("\\.");
        System.out.println("\"3.14\".split(dot) = " + java.util.Arrays.toString(dot.split("3.14")));
        // versus
        System.out.println("\"3.14\".split(.)   = " + java.util.Arrays.toString("3.14".split(".")));   // splits EVERYTHING

        section("5) Compilation cost - reuse a Pattern when looping");
        long t0 = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            "x42".matches("\\w+\\d+");          // compiles every call
        }
        long slow = (System.nanoTime() - t0) / 1_000_000;

        Pattern compiled = Pattern.compile("\\w+\\d+");
        t0 = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            compiled.matcher("x42").matches();  // compiled once
        }
        long fast = (System.nanoTime() - t0) / 1_000_000;

        System.out.println("String.matches loop   : " + slow + " ms");
        System.out.println("Precompiled loop      : " + fast + " ms");

        // SAMPLE OUTPUT
        // ====== 1) The shortcuts on java.lang.String ======
        // "abc 123".matches(\\w+ \\d+) = true
        // split(",")     = [a, b, , c]
        // replaceAll(d->*) = x*y*z*
        // replaceFirst     = x*y2z3
        // ====== 2) Pattern + Matcher - precompile once, reuse many times ======
        // alpha42 -> letters='alpha', digits='42'
        // x99 -> letters='x', digits='99'
        // noDigits -> no full match
        // z00 -> letters='z', digits='00'
        // ====== 3) matches() vs find() vs lookingAt() ======
        // matches()      = false
        // lookingAt()    = false
        // find()         = true
        // group()        = 123
        // all matches via find(): 123 45
        // ====== 4) Java escape vs regex escape - keep them straight ======
        // "3.14".split(dot) = [3, 14]
        // "3.14".split(.)   = []         <- the . matches every char, leaving only empties
        // ====== 5) Compilation cost - reuse a Pattern when looping ======
        // String.matches loop   : 18 ms
        // Precompiled loop      : 3  ms
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
