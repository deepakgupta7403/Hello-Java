package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lookahead and Lookbehind ("Lookaround")
 * ---------------------------------------
 * LOOKAROUND ASSERTIONS check WHAT IS AROUND a position WITHOUT consuming
 * any characters. They are zero-width assertions, like anchors.
 * <p>
 *
 *      (?=X)        positive lookahead   - assert "X follows here"
 *      (?!X)        negative lookahead   - assert "X does NOT follow here"
 *      (?<=X)       positive lookbehind  - assert "X precedes here"
 *      (?<!X)       negative lookbehind  - assert "X does NOT precede here"
 * <p>
 *
 * Why "Zero-Width" Matters
 * ------------------------
 * The check happens at a POSITION; the regex cursor does not advance.
 * That lets you compose constraints without consuming input:
 * <p>
 *
 *      \d+(?=USD)             a number FOLLOWED BY "USD" (USD is not part of the match)
 *      (?<=\$)\d+             a number PRECEDED BY "$"   ($ is not part of the match)
 *      \b(?!the\b)\w+         any word that is NOT "the"
 *      (?<![A-Z])[A-Z]\w+     a capitalised word NOT preceded by another upper-case letter
 * <p>
 *
 * Lookbehind Limits (in Java)
 * ---------------------------
 * Historically Java required lookbehind to be FIXED LENGTH. Since Java 9+
 * variable-length lookbehind is supported, but inside the lookbehind the
 * pattern still must have a finite maximum length. Truly unbounded
 * lookbehind (e.g. `(?<=a*)`) will be rejected by the compiler.
 * <p>
 *
 * Common Real-World Uses
 * ----------------------
 *   - Extract a value adjacent to a marker (with the marker not in the match).
 *   - "Match only when SOMETHING ELSE is/isn't true here" - useful for
 *     password rules ("must contain at least one digit", etc.).
 *   - Insert text BETWEEN two specific characters.
 *   - Split without consuming the delimiter.
 * <p>
 *
 * Password rule example (compact)
 * -------------------------------
 *      ^(?=.*[a-z])         must contain a lowercase
 *       (?=.*[A-Z])         must contain an uppercase
 *       (?=.*\d)            must contain a digit
 *       (?=.*[@$!%*?&])     must contain a special char
 *       [A-Za-z\d@$!%*?&]{8,}$
 */

public class LookaroundAssertions {

    public static void main(String[] args) {

        section("1) Positive lookahead (?=X) - take the number BEFORE 'USD'");
        find("\\d+(?=USD)", "10USD 20EUR 30USD 40JPY");        // -> 10, 30

        section("2) Negative lookahead (?!X) - any word NOT 'the'");
        find("\\b(?!the\\b)\\w+\\b", "the cat sat on the mat");

        section("3) Positive lookbehind (?<=X) - take number AFTER '$'");
        find("(?<=\\$)\\d+", "price $20 and $35, no 50");      // -> 20, 35

        section("4) Negative lookbehind (?<!X) - capitalised word NOT after another upper case");
        find("(?<![A-Z])[A-Z][a-z]+", "USA, India, NATO Member Countries");
        // -> India, Member, Countries  (skips USA, NATO because previous letter is upper)

        section("5) Compose multiple lookaheads - password validation");
        Pattern strong = Pattern.compile(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        );
        for (String pw : new String[]{"weak", "Strong123", "Strong@123", "noDigit@!"}) {
            System.out.printf("%-12s -> %s%n", pw, strong.matcher(pw).matches());
        }

        section("6) Lookaround does NOT consume - 'split' between two chars");
        // Insert a comma between digit and letter without consuming either.
        String joined = "12a34b56c".replaceAll("(?<=\\d)(?=[a-z])", ",");
        System.out.println("inserted -> " + joined);   // 12,a34,b56,c

        section("7) Variable-length lookbehind (Java 9+, bounded)");
        // Match a number preceded by "USD" or "USDD" (2 lengths).
        Pattern p7 = Pattern.compile("(?<=USDD?)\\d+");
        Matcher m7 = p7.matcher("USD10 USDD20 USD30");
        while (m7.find()) System.out.println("  " + m7.group());

        section("8) `find()` step-by-step in replaceAll - the lookaround stays out of the match");
        // Mask card numbers but only when prefixed by "card:".
        String masked = Pattern.compile("(?<=card:)\\d{4}")
                               .matcher("card:1234, card:5678, code:9999")
                               .replaceAll("****");
        System.out.println("masked -> " + masked);

        // OUTPUT (representative)
        // ====== 1) Positive lookahead (?=X) - take the number BEFORE 'USD' ======
        // find \d+(?=USD) in "10USD 20EUR 30USD 40JPY" -> [10, 30]
        // ====== 2) Negative lookahead (?!X) - any word NOT 'the' ======
        // find \b(?!the\b)\w+\b in "the cat sat on the mat" -> [cat, sat, on, mat]
        // ====== 3) Positive lookbehind (?<=X) - take number AFTER '$' ======
        // find (?<=\$)\d+ in "price $20 and $35, no 50" -> [20, 35]
        // ====== 4) Negative lookbehind (?<!X) - capitalised word NOT after another upper case ======
        // find (?<![A-Z])[A-Z][a-z]+ in "USA, India, NATO Member Countries" -> [India, Member, Countries]
        // ====== 5) Compose multiple lookaheads - password validation ======
        // weak         -> false
        // Strong123    -> false
        // Strong@123   -> true
        // noDigit@!    -> false
        // ====== 6) Lookaround does NOT consume - 'split' between two chars ======
        // inserted -> 12,a34,b56,c
        // ====== 7) Variable-length lookbehind (Java 9+, bounded) ======
        //   10
        //   20
        //   30
        // ====== 8) `find()` step-by-step in replaceAll - the lookaround stays out of the match ======
        // masked -> card:****, card:****, code:9999
    }

    private static void find(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        java.util.List<String> hits = new java.util.ArrayList<>();
        while (m.find()) hits.add(m.group());
        System.out.printf("find %-30s in %-35s -> %s%n", regex, "\"" + input + "\"", hits);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
