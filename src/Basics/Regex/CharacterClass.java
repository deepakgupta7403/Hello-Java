package Basics.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Character Classes
 * -----------------
 * A CHARACTER CLASS is a regex construct that matches ONE CHARACTER drawn
 * from a defined set. Three kinds:
 *
 *   1. CUSTOM SETS                 [abc], [a-z], [a-zA-Z0-9_], [^abc]
 *   2. PREDEFINED CLASSES          \d  \D  \w  \W  \s  \S  .
 *   3. POSIX / UNICODE CLASSES     \p{Alpha}, \p{Digit}, \p{IsAlphabetic}
 *
 *
 * 1) Custom Character Sets
 * ------------------------
 *   [abc]      a, b, OR c
 *   [^abc]     any char EXCEPT a, b, c
 *   [a-z]      any lowercase letter (range)
 *   [a-zA-Z]   union of ranges
 *   [a-z&&[^aeiou]]  intersection - "lowercase consonant"
 *
 * Inside [ ] most metacharacters lose their special meaning. The exceptions
 * are:  ]  \  ^ (at the start)  - (in the middle)
 *
 *
 * 2) Predefined Classes (shortcuts)
 * ---------------------------------
 *   .       any character EXCEPT a newline   (unless DOTALL flag)
 *   \d      a digit            == [0-9]
 *   \D      NOT a digit        == [^0-9]
 *   \w      "word" char        == [a-zA-Z0-9_]
 *   \W      NOT a word char
 *   \s      whitespace         == [ \t\n\x0B\f\r]
 *   \S      NOT whitespace
 *   \h \H   horizontal whitespace / not (Java 8+)
 *   \v \V   vertical whitespace / not   (Java 8+)
 *
 *
 * 3) POSIX & Unicode Classes
 * --------------------------
 *   \p{Lower}     [a-z]
 *   \p{Upper}     [A-Z]
 *   \p{Alpha}     letters
 *   \p{Digit}     digits
 *   \p{Alnum}     letters or digits
 *   \p{Space}     whitespace
 *   \p{Punct}     punctuation
 *   \p{ASCII}     ASCII range 0..127
 *
 *   Unicode aware (require the UNICODE_CHARACTER_CLASS flag, or use \p{IsX}):
 *   \p{IsAlphabetic}      letters from any script
 *   \p{IsLatin}           Latin script
 *   \p{IsGreek}           Greek script
 *   \p{InCyrillic}        Unicode "block" (where the code point lives)
 *   \p{N}                 Number (general category)
 *
 *
 * Negation
 * --------
 *   \P{Alpha}     opposite of \p{Alpha}
 *   [^abc]        negated custom set
 *
 *
 * Why care?
 * ---------
 * Character classes are the WORKHORSE of regex. Almost every real-world
 * pattern is some quantified character class:
 *      \d{3}-\d{4}                 phone number
 *      [a-zA-Z0-9._%+-]+@...        email local-part
 *      [^,]+                       any field in a CSV line
 */

public class CharacterClass {

    public static void main(String[] args) {

        section("1) Custom sets - [abc], ranges, negation");
        showMatches("[abc]",          "cat in a basket");          // a, a, a
        showMatches("[A-Za-z]",       "JavaScript-26");            // letters only
        showMatches("[^aeiou]",       "abracadabra");              // non-vowels
        showMatches("[a-z&&[^aeiou]]","abracadabra");              // lowercase consonants

        section("2) Predefined classes - \\d, \\w, \\s, .");
        showMatches("\\d", "a1b22c333");        // 1 2 2 3 3 3
        showMatches("\\D", "a1b22c333");        // letters
        showMatches("\\w", "a1 b@2");           // a 1 b 2
        showMatches("\\W", "a1 b@2");           //   @
        showMatches("\\s", "a b\tc\nd");        // space tab newline
        showMatches(".",   "a b\tc\nd");        // every char except newline

        section("3) POSIX classes - \\p{Alpha}, \\p{Digit}, etc.");
        showMatches("\\p{Alpha}", "abc123XYZ-456");
        showMatches("\\p{Digit}", "abc123XYZ-456");
        showMatches("\\p{Alnum}", "Hello, World!");
        showMatches("\\p{Punct}", "Hello, World!");

        section("4) Unicode classes - works across scripts");
        showMatches("\\p{IsAlphabetic}", "नमस्ते Hello Γειά");          // letters of any script
        showMatches("\\p{IsLatin}",      "नमस्ते Hello Γειά");           // Latin only
        showMatches("\\p{IsGreek}",      "नमस्ते Hello Γειά");           // Greek only

        section("5) Negation - \\P{X} flips a Unicode class");
        showMatches("\\P{Alpha}", "abc 123-xyz");      // non-letters

        section("6) Common real-world uses");
        // CSV field - non-comma chars
        String[] fields = "alpha,beta,,gamma".split(",");
        System.out.println("CSV fields    = " + java.util.Arrays.toString(fields));

        // Strip all non-digits
        String digits = "Call (555) 867-5309".replaceAll("\\D", "");
        System.out.println("digits only   = " + digits);

        // Keep only word chars
        String slug = "Hello, World! 2026".replaceAll("\\W+", "-").toLowerCase();
        System.out.println("slug          = " + slug);

        // OUTPUT (representative)
        // ====== 1) Custom sets - [abc], ranges, negation ======
        // [abc] in "cat in a basket"           -> [a, a, a]
        // [A-Za-z] in "JavaScript-26"          -> [J, a, v, a, S, c, r, i, p, t]
        // [^aeiou] in "abracadabra"            -> [b, r, c, d, b, r]
        // [a-z&&[^aeiou]] in "abracadabra"     -> [b, r, c, d, b, r]
        // ...
    }

    /** Find every match of `regex` in `text` and print them as a list. */
    private static void showMatches(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        java.util.List<String> hits = new java.util.ArrayList<>();
        while (m.find()) hits.add(m.group());
        System.out.printf("%-20s in %-25s -> %s%n", regex, "\"" + text + "\"", hits);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
