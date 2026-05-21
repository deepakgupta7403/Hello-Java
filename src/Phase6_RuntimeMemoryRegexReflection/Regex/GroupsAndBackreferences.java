package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Groups and Backreferences
 * -------------------------
 * A GROUP is a section of a regex enclosed in parentheses ( ... ). Groups
 * serve TWO purposes:
 * <p>
 *
 *   1. STRUCTURE      - they constrain alternation and quantifiers:
 *                          (gray|grey)        groups the alternatives
 *                          (ab)+              repeats "ab", not just "b"
 * <p>
 *
 *   2. CAPTURE        - the matched substring is saved and can be read
 *                       back later via Matcher.group(...) or as a
 *                       BACKREFERENCE inside the regex itself.
 * <p>
 *
 * Numbering
 * ---------
 *   group(0)   - the WHOLE match
 *   group(1)   - the substring captured by the FIRST opening paren
 *   group(2)   - second, and so on (counted by opening parens, left to right)
 * <p>
 *
 * Variants of (... )
 * ------------------
 *   (X)               capturing group
 *   (?:X)             NON-capturing group   - same grouping, no save
 *   (?<name>X)        NAMED capturing group - reference by name later
 *   (?>X)             ATOMIC group - no backtracking inside X
 *   (?=X)             positive lookahead    (see LookaroundAssertions.java)
 *   (?!X)             negative lookahead
 *   (?<=X)            positive lookbehind
 *   (?<!X)            negative lookbehind
 *   (?idmsuxU-idmsuxU:X)  inline flags applied to X only
 * <p>
 *
 * Backreferences
 * --------------
 * Inside the SAME regex:
 *      \1 \2 \3 ...        refer to capturing groups by number
 *      \k<name>            refer to a named group
 * <p>
 *
 * Inside the REPLACEMENT string (used by replaceAll / replaceFirst):
 *      $1 $2 $3 ...        substitute group n
 *      ${name}             substitute a named group
 *      \$                  literal dollar
 *      \\                  literal backslash
 * <p>
 *
 * Why Non-Capturing Groups?
 * -------------------------
 *   - Performance: the engine doesn't save the substring.
 *   - Cleaner numbering: only the groups you actually need have indices.
 * <p>
 *
 * Why Named Groups?
 * -----------------
 *   - Readable: m.group("year") beats m.group(3).
 *   - Stable: adding a new group later does not renumber the others.
 */

public class GroupsAndBackreferences {

    public static void main(String[] args) {

        section("1) Capturing groups numbered 1, 2, 3, ...");
        Pattern p1 = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher m1 = p1.matcher("date 2026-05-19, end");
        while (m1.find()) {
            System.out.printf("  full=%s  year=%s  month=%s  day=%s%n",
                    m1.group(0), m1.group(1), m1.group(2), m1.group(3));
        }

        section("2) Non-capturing group (?:...)");
        // gray|grey alternation grouped without consuming an index.
        Pattern p2 = Pattern.compile("(?:gray|grey) (\\w+)");
        Matcher m2 = p2.matcher("the gray cat and the grey dog");
        while (m2.find()) {
            System.out.printf("  group(1)=%s  (only ONE numbered group, even though there are TWO parens)%n",
                    m2.group(1));
        }

        section("3) Named groups (?<name>...)");
        Pattern p3 = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher m3 = p3.matcher("2026-05-19");
        if (m3.matches()) {
            System.out.println("  year  = " + m3.group("year"));
            System.out.println("  month = " + m3.group("month"));
            System.out.println("  day   = " + m3.group("day"));
        }

        section("4) Backreferences inside the regex - find doubled words");
        // \b(\w+)\b\s+\1\b finds "the the", "is is", etc.
        Pattern p4 = Pattern.compile("\\b(\\w+)\\b\\s+\\1\\b");
        Matcher m4 = p4.matcher("the the quick brown fox is is lazy");
        while (m4.find()) {
            System.out.println("  duplicate: " + m4.group());
        }

        section("5) Backreference by name - find palindromic letter trios");
        Pattern p5 = Pattern.compile("\\b(?<first>\\w)(?<middle>\\w)\\k<first>\\b");
        Matcher m5 = p5.matcher("eve mom dad cat noon pop");
        while (m5.find()) {
            System.out.printf("  trio %s (first=%s middle=%s)%n",
                    m5.group(), m5.group("first"), m5.group("middle"));
        }

        section("6) Replacement using $n");
        String swapped = Pattern.compile("(\\w+) (\\w+)")
                                .matcher("Deepak Gupta")
                                .replaceAll("$2, $1");
        System.out.println("  $2, $1  -> " + swapped);

        section("7) Replacement using ${name}");
        String reorderedDate = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})")
                                      .matcher("2026-05-19")
                                      .replaceAll("${day}/${month}/${year}");
        System.out.println("  reordered date -> " + reorderedDate);

        section("8) Nested groups - numbering by opening paren");
        Pattern p8 = Pattern.compile("((\\d{3}) (\\d{4}))");
        Matcher m8 = p8.matcher("phone 555 1234 ext.");
        if (m8.find()) {
            System.out.println("  group(0) = " + m8.group(0));   // whole
            System.out.println("  group(1) = " + m8.group(1));   // outer
            System.out.println("  group(2) = " + m8.group(2));   // first inner (\\d{3})
            System.out.println("  group(3) = " + m8.group(3));   // second inner (\\d{4})
        }

        section("9) Capturing inside an alternation - watch out for nulls");
        Pattern p9 = Pattern.compile("(yes)|(no)");
        for (String s : new String[]{"yes", "no", "maybe"}) {
            Matcher m9 = p9.matcher(s);
            if (m9.matches()) {
                System.out.println("  " + s + " -> g1=" + m9.group(1) + ", g2=" + m9.group(2));
            } else {
                System.out.println("  " + s + " -> no match");
            }
        }

        // OUTPUT
        // ====== 1) Capturing groups numbered 1, 2, 3, ... ======
        //   full=2026-05-19  year=2026  month=05  day=19
        // ====== 2) Non-capturing group (?:...) ======
        //   group(1)=cat
        //   group(1)=dog
        // ====== 3) Named groups (?<name>...) ======
        //   year  = 2026
        //   month = 05
        //   day   = 19
        // ====== 4) Backreferences inside the regex - find doubled words ======
        //   duplicate: the the
        //   duplicate: is is
        // ====== 5) Backreference by name - find palindromic letter trios ======
        //   trio eve (first=e middle=v)
        //   trio mom (first=m middle=o)
        //   trio dad (first=d middle=a)
        //   trio pop (first=p middle=o)
        // ====== 6) Replacement using $n ======
        //   $2, $1  -> Gupta, Deepak
        // ====== 7) Replacement using ${name} ======
        //   reordered date -> 19/05/2026
        // ====== 8) Nested groups - numbering by opening paren ======
        //   group(0) = 555 1234
        //   group(1) = 555 1234
        //   group(2) = 555
        //   group(3) = 1234
        // ====== 9) Capturing inside an alternation - watch out for nulls ======
        //   yes -> g1=yes, g2=null
        //   no  -> g1=null, g2=no
        //   maybe -> no match
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
