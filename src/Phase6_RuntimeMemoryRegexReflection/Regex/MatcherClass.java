package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java.util.regex.Matcher - The Regex Engine
 * ------------------------------------------
 * A Matcher is the STATEFUL OBJECT that runs a Pattern against a particular
 * input. It tracks the position in the input, the last match's bounds, the
 * captured groups, and replacement state.
 * <p>
 *
 *      Pattern p = Pattern.compile("\\d+");
 *      Matcher m = p.matcher("abc 12 xy 345");
 * <p>
 *
 * Matchers are NOT thread-safe. Create one per thread (or per call).
 * <p>
 *
 * Matching Methods
 * ----------------
 *      matches()        - true if the ENTIRE input matches the pattern.
 *      lookingAt()      - true if the input matches starting from the beginning.
 *      find()           - finds the NEXT subsequence that matches; returns true if found.
 *      find(int start)  - starts the search at `start`.
 *      reset()          - sets the matcher back to the beginning of the input.
 *      reset(CharSequence input) - switches to a new input but keeps the pattern.
 * <p>
 *
 * Inspecting the Last Match
 * -------------------------
 *      group()          - the matched substring.
 *      group(int n)     - capturing group n (group 0 == whole match).
 *      group(String n)  - named capturing group "n".
 *      start() / end()  - indices in the input of the last match.
 *      start(n) / end(n)
 *      groupCount()     - number of CAPTURING groups in the pattern.
 *      results()        - Stream<MatchResult> of all matches (Java 9+).
 * <p>
 *
 * Replacement
 * -----------
 *      replaceAll(replacement)              - replaces every match.
 *      replaceFirst(replacement)            - replaces only the first match.
 *      replaceAll(Function<MatchResult,String> fn)   - per-match dynamic replacement (Java 9+).
 *      appendReplacement(sb, replacement)   - streaming custom replacement
 *      appendTail(sb)                       - copy the rest after the last match
 * <p>
 *
 * Inside the replacement string:
 *      $0, $1, $2, ...  - reference capturing groups by number.
 *      ${name}          - reference a named capturing group.
 *      \\$              - a literal $ in the replacement.
 *      \\\\             - a literal \ in the replacement.
 * <p>
 *
 * Use Matcher.quoteReplacement(s) to safely embed user-supplied text.
 * <p>
 *
 * Region & Anchors
 * ----------------
 *      region(start, end)        - limit matching to a sub-range
 *      regionStart() / regionEnd()
 *      hitEnd() / requireEnd()   - useful for incremental parsers
 */

public class MatcherClass {

    public static void main(String[] args) {
        Pattern WORD = Pattern.compile("\\w+");

        section("1) matches() / lookingAt() / find()");
        Matcher m = WORD.matcher("hello world");
        System.out.println("matches()        = " + m.matches());     // false ("hello world" contains space)
        m.reset();
        System.out.println("lookingAt()      = " + m.lookingAt());   // true (starts with a word)
        m.reset();
        while (m.find()) {
            System.out.printf("  match '%s' at [%d, %d)%n", m.group(), m.start(), m.end());
        }

        section("2) Capturing groups - by index");
        Pattern p2 = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher m2 = p2.matcher("today is 2026-05-19, tomorrow is 2026-05-20");
        while (m2.find()) {
            System.out.printf("  full=%s  year=%s  month=%s  day=%s%n",
                    m2.group(0), m2.group(1), m2.group(2), m2.group(3));
        }
        System.out.println("groupCount() = " + p2.matcher("").groupCount());

        section("3) Named groups - clearer in complex patterns");
        Pattern p3 = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher m3 = p3.matcher("2026-05-19");
        if (m3.matches()) {
            System.out.println("year  = " + m3.group("year"));
            System.out.println("month = " + m3.group("month"));
            System.out.println("day   = " + m3.group("day"));
        }

        section("4) replaceAll / replaceFirst");
        System.out.println(WORD.matcher("hello world").replaceAll("X"));     // "X X"
        System.out.println(WORD.matcher("hello world").replaceFirst("X"));   // "X world"

        section("5) Using $n in replacement");
        String swapped = Pattern.compile("(\\w+) (\\w+)")
                                .matcher("Deepak Gupta")
                                .replaceAll("$2 $1");
        System.out.println("swapped = " + swapped);

        section("6) replaceAll(Function) - dynamic replacement (Java 9+)");
        String shouted = Pattern.compile("\\b[a-z]+\\b")
                                .matcher("hello big WORLD java")
                                .replaceAll(r -> r.group().toUpperCase());
        System.out.println("shouted = " + shouted);

        section("7) appendReplacement + appendTail - the building blocks");
        Matcher mn = Pattern.compile("(\\d+)").matcher("year 2026, month 5, day 19");
        StringBuilder sb = new StringBuilder();
        while (mn.find()) {
            int n = Integer.parseInt(mn.group(1));
            mn.appendReplacement(sb, "[" + (n + 1) + "]");
        }
        mn.appendTail(sb);
        System.out.println("incremented = " + sb);

        section("8) Matcher.results() - stream of matches (Java 9+)");
        long count = Pattern.compile("\\d+")
                            .matcher("a 12 b 345 c 6 d")
                            .results()
                            .count();
        System.out.println("numeric tokens = " + count);

        section("9) reset() + reset(newInput)");
        Matcher mr = WORD.matcher("alpha beta");
        mr.find(); System.out.println("first      = " + mr.group());
        mr.reset();
        mr.find(); System.out.println("after reset= " + mr.group());
        mr.reset("gamma delta");
        mr.find(); System.out.println("new input  = " + mr.group());

        section("10) region() - restrict matching to a sub-range");
        Matcher mre = WORD.matcher("hello world java");
        mre.region(6, 11);      // only the substring "world"
        while (mre.find()) {
            System.out.println("  region match = " + mre.group());
        }

        section("11) Matcher.quoteReplacement() - escape user-supplied replacement text");
        // The user-supplied replacement contains $ and \ - escape it.
        String dangerous = "$100 \\money";
        String safe = Matcher.quoteReplacement(dangerous);
        System.out.println("PRICE -> " + Pattern.compile("PRICE").matcher("PRICE here").replaceAll(safe));

        // OUTPUT (matches the inline comments above)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
