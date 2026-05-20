package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metacharacters and Anchors
 * --------------------------
 * A METACHARACTER is a character that has special meaning inside a regex
 * rather than matching itself. An ANCHOR is a zero-width metacharacter that
 * matches a POSITION in the input, not an actual character.
 *
 *
 * The 12 Metacharacters You Must Know
 * -----------------------------------
 *      .   ?   *   +   {   }   |   ^   $   (   )   [   ]   \
 *
 * To match one of them LITERALLY, prefix it with a backslash:
 *
 *      \.    matches a literal dot
 *      \(    matches a literal opening paren
 *
 * In a Java string literal, that becomes "\\." and "\\(" respectively.
 *
 *
 * Anchors (Zero-Width)
 * --------------------
 *      ^         start of the input (or start of line with MULTILINE flag)
 *      $         end   of the input (or end   of line with MULTILINE flag)
 *      \b        word boundary
 *      \B        NOT a word boundary
 *      \A        absolute start of the input (ignores MULTILINE)
 *      \z        absolute end   of the input (ignores MULTILINE)
 *      \Z        absolute end except final newline
 *      \G        end of the previous match (useful in scan loops)
 *
 * "Zero-width" means they match a POSITION between characters and consume
 * nothing.
 *
 *
 * Alternation
 * -----------
 *      X | Y      matches X or Y (lowest precedence; group it with (...))
 *
 *      gray|grey            -> matches "gray" or "grey"
 *      gr(a|e)y             -> same, with the alternation grouped
 *
 *
 * Word Boundaries
 * ---------------
 * `\b` matches the POSITION between a word character (\w) and a non-word
 * character. Use it to match WHOLE words:
 *
 *      regex \bcat\b
 *      "cat"      -> match
 *      "catnap"   -> no match
 *      "scat"     -> no match
 *      "the cat sat"  -> match on "cat"
 *
 *
 * Escaping User Input
 * -------------------
 * To match arbitrary user-supplied text literally, use Pattern.quote(str)
 * - it produces a regex that matches `str` exactly, escaping every meta.
 *
 *      String userInput = "Hello? (yes)";
 *      Pattern p = Pattern.compile(Pattern.quote(userInput));
 */

public class MetacharactersAndAnchors {

    public static void main(String[] args) {

        section("1) Common metacharacters need escaping");
        // Match a literal "3.14"
        match("\\d+\\.\\d+", "Pi is 3.14");
        // vs unescaped dot - matches ANY char between digits
        match("\\d+.\\d+",  "Pi is 3X14");

        section("2) Anchors ^ and $");
        match("^Hello", "Hello world");        // true
        match("^Hello", "say Hello");          // false - input does not start with Hello
        match("world$", "Hello world");        // true
        match("world$", "world champion");     // false

        section("3) Word boundary \\b - whole-word match");
        find("\\bcat\\b", "the cat sat on the catnap and scat");   // only "cat"
        find("cat",       "the cat sat on the catnap and scat");   // "cat", "cat", "cat"

        section("4) Alternation X|Y");
        find("gray|grey", "gray and grey are spelled differently");
        find("gr(a|e)y",  "gray and grey are spelled differently");

        section("5) \\A and \\z - absolute anchors, ignore MULTILINE");
        Pattern start = Pattern.compile("\\AHello");
        Pattern end   = Pattern.compile("world\\z");
        System.out.println("\\AHello on \"Hello world\" = " + start.matcher("Hello world").find());
        System.out.println("\\AHello on \"say Hello\"   = " + start.matcher("say Hello").find());
        System.out.println("world\\z on \"hello world\" = " + end.matcher("hello world").find());

        section("6) \\G - continue from end of previous match");
        // Match a sequence of words from the start, one after another, but
        // stop as soon as the chain is broken.
        Pattern p6 = Pattern.compile("\\G(\\w+)\\s*");
        Matcher m6 = p6.matcher("alpha beta gamma 99 delta");
        while (m6.find()) {
            System.out.println("contig word: " + m6.group(1));
        }
        // After the digits "99" the \G chain is broken so "delta" is not matched.

        section("7) Pattern.quote - escape user input safely");
        String userInput = "1+1=2 (right?)";
        Pattern literal = Pattern.compile(Pattern.quote(userInput));
        System.out.println("literal find = " + literal.matcher("Trivia: 1+1=2 (right?) here.").find());

        section("8) Mixing it all - validate hex colour");
        // ^#?(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})$
        Pattern hex = Pattern.compile("^#?(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
        for (String s : new String[]{"#FFF", "FFA500", "#1ab", "0xff", "GGG", "#1234"}) {
            System.out.printf("%-8s -> %s%n", s, hex.matcher(s).matches());
        }

        // SAMPLE OUTPUT (representative)
        // ====== 1) Common metacharacters need escaping ======
        // matches \d+\.\d+   "Pi is 3.14"             -> true (after find)
        // ====== 3) Word boundary \\b - whole-word match ======
        // find \bcat\b in "the cat sat on the catnap and scat" -> [cat]
        // find cat     in "the cat sat on the catnap and scat" -> [cat, cat, cat]
    }

    private static void match(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        boolean fullMatch = p.matcher(input).matches();
        boolean anyMatch  = p.matcher(input).find();
        System.out.printf("matches %-18s in %-30s -> matches()=%-5s find()=%s%n",
                regex, "\"" + input + "\"", fullMatch, anyMatch);
    }

    private static void find(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        java.util.List<String> hits = new java.util.ArrayList<>();
        while (m.find()) hits.add(m.group());
        System.out.printf("find    %-18s in %-30s -> %s%n", regex, "\"" + input + "\"", hits);
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
