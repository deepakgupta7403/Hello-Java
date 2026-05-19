package Basics.Strings;

import java.util.Arrays;

/**
 * String Methods - Comprehensive Reference
 * ----------------------------------------
 * This file is a guided tour through the most-used methods of java.lang.String.
 * Each method has a short comment explaining what it does and a runnable
 * example with the expected output.
 *
 *
 * Methods Grouped By Purpose
 * --------------------------
 *
 *  LENGTH / EMPTINESS
 *      length()                    - number of UTF-16 code units
 *      isEmpty()                   - length == 0
 *      isBlank()                   - empty or only whitespace (Java 11+)
 *
 *  CHAR ACCESS / ARRAYS
 *      charAt(i)                   - char at index i
 *      toCharArray()               - copy to a new char[]
 *      codePointAt(i)              - int code point (supports supplementary chars)
 *      getBytes() / getBytes(charset) - encode as byte[]
 *
 *  SEARCH
 *      indexOf(ch | str | from)    - first occurrence, or -1
 *      lastIndexOf(ch | str | from)- last  occurrence, or -1
 *      contains(seq)               - true if `seq` appears
 *      startsWith(prefix)
 *      endsWith(suffix)
 *
 *  COMPARISON
 *      equals(other)               - case-sensitive content equality
 *      equalsIgnoreCase(other)
 *      compareTo(other)            - lexicographic; <0 / 0 / >0
 *      compareToIgnoreCase(other)
 *
 *  CASE / WHITESPACE
 *      toUpperCase() / toLowerCase()
 *      trim()                      - strips ASCII control + space
 *      strip() / stripLeading() / stripTrailing()  (Java 11+, Unicode-aware)
 *
 *  TRANSFORM
 *      replace(old, new)           - replaces ALL (chars OR CharSequences)
 *      replaceFirst(regex, new)    - regex first match
 *      replaceAll(regex, new)      - regex all matches
 *      substring(begin) / substring(begin, end)
 *      concat(other)
 *      repeat(count)               - Java 11+
 *      indent(n)                   - Java 12+
 *      transform(fn)               - Java 12+, apply any Function<String, R>
 *
 *  SPLIT / JOIN / FORMAT
 *      split(regex)
 *      String.join(delim, parts...)            (static)
 *      String.format(fmt, args...)             (static; printf-style)
 *      formatted(args...)                      (Java 15+, instance form)
 *      lines()                                 (Java 11+, Stream of lines)
 *
 *  CONVERSION FROM OTHER TYPES
 *      String.valueOf(int|double|boolean|Object|char[])
 *      Integer.parseInt(s), Double.parseDouble(s) ...
 *
 *  REGEX QUICK PATH
 *      matches(regex)              - matches() must match the WHOLE string
 *
 *
 * Pitfalls Quick Reference
 * ------------------------
 *  - `==` compares references, not content. Use `.equals(...)`.
 *  - `replace()` takes a literal; `replaceAll()` takes a regex.
 *  - `split("|")` splits on EVERY char because `|` is a regex meta-character.
 *    Use `split("\\|")` or `Pattern.quote("|")`.
 *  - `substring(begin, end)` is HALF-OPEN: includes begin, excludes end.
 *  - `length()` counts UTF-16 code UNITS, not grapheme clusters. Emojis and
 *    other supplementary characters take 2 units.
 */

public class StringMethods {

    public static void main(String[] args) {
        String s = "  Hello, Java World!  ";

        section("LENGTH / EMPTINESS");
        System.out.println("length()    = " + s.length());
        System.out.println("isEmpty()   = " + s.isEmpty());
        System.out.println("isBlank()   = " + "  \t\n ".isBlank());

        section("CHAR ACCESS");
        System.out.println("charAt(2)        = " + s.charAt(2));
        System.out.println("toCharArray()[0] = " + s.toCharArray()[0]);
        System.out.println("codePointAt(2)   = " + s.codePointAt(2));
        System.out.println("getBytes().length= " + s.getBytes().length);

        section("SEARCH");
        String t = "Hello, World, Hello";
        System.out.println("indexOf('o')     = " + t.indexOf('o'));        //  4
        System.out.println("indexOf('o', 5)  = " + t.indexOf('o', 5));     //  8
        System.out.println("lastIndexOf('o') = " + t.lastIndexOf('o'));    // 16
        System.out.println("contains 'World' = " + t.contains("World"));
        System.out.println("startsWith Hello = " + t.startsWith("Hello"));
        System.out.println("endsWith   Hello = " + t.endsWith("Hello"));

        section("COMPARISON");
        System.out.println("equals('hello',Hello)        = " + "hello".equals("Hello"));
        System.out.println("equalsIgnoreCase             = " + "hello".equalsIgnoreCase("HELLO"));
        System.out.println("'a'.compareTo('b')           = " + "a".compareTo("b"));    // -1
        System.out.println("'apple'.compareTo('banana')  = " + "apple".compareTo("banana"));

        section("CASE / WHITESPACE");
        System.out.println("toUpperCase()    = " + "Java".toUpperCase());
        System.out.println("toLowerCase()    = " + "Java".toLowerCase());
        System.out.println("trim()           = '" + s.trim() + "'");
        System.out.println("strip()          = '" + "   hi  ".strip() + "'");
        System.out.println("stripLeading()   = '" + "   hi".stripLeading() + "'");
        System.out.println("stripTrailing()  = '" + "hi   ".stripTrailing() + "'");

        section("TRANSFORM");
        System.out.println("replace(',','-')        = " + t.replace(',', '-'));
        System.out.println("replace('Hello','Hi')   = " + t.replace("Hello", "Hi"));
        System.out.println("replaceAll('o','0')     = " + t.replaceAll("o", "0"));
        System.out.println("replaceFirst('o','0')   = " + t.replaceFirst("o", "0"));
        System.out.println("substring(7)            = " + t.substring(7));
        System.out.println("substring(7, 12)        = " + t.substring(7, 12));
        System.out.println("repeat(3)               = " + "ab".repeat(3));
        System.out.println("'X'.repeat(0)           = '" + "X".repeat(0) + "'");
        System.out.println("indent(4) shown below:");
        System.out.print(  "line1\nline2".indent(4));
        System.out.println("transform(toUpper+!) = " + "java".transform(x -> x.toUpperCase() + "!"));

        section("SPLIT / JOIN / FORMAT");
        String[] toks = "a,b,c,,d".split(",");
        System.out.println("split(\",\")   = " + Arrays.toString(toks));
        System.out.println("split(\",\",-1)= " + Arrays.toString("a,b,c,,d".split(",", -1))); // keep trailing empty
        System.out.println("String.join(\"-\", a, b, c) = " + String.join("-", "a", "b", "c"));
        System.out.println("String.format            = " + String.format("%-5s %3d", "ID", 42));
        System.out.println("\"x=%d\".formatted(7)      = " + "x=%d".formatted(7));
        System.out.println("lines().count()          = " + "one\ntwo\nthree".lines().count());

        section("CONVERSION");
        System.out.println("String.valueOf(3.14)  = " + String.valueOf(3.14));
        System.out.println("String.valueOf(true)  = " + String.valueOf(true));
        System.out.println("Integer.parseInt(42)  = " + Integer.parseInt("42"));
        System.out.println("Double.parseDouble    = " + Double.parseDouble("3.14"));

        section("REGEX");
        System.out.println("'abc123'.matches(\\\\w+) = " + "abc123".matches("\\w+"));
        System.out.println("'abc 123'.matches(\\\\w+) = " + "abc 123".matches("\\w+"));

        section("COMMON PITFALL EXAMPLES");
        // split("|") - | is a regex OR
        String[] bad = "a|b|c".split("|");
        String[] good = "a|b|c".split("\\|");
        System.out.println("split(\"|\")    = " + Arrays.toString(bad));
        System.out.println("split(\"\\\\|\") = " + Arrays.toString(good));

        // == vs equals
        String x = new String("java");
        String y = "java";
        System.out.println("x == y           = " + (x == y));
        System.out.println("x.equals(y)      = " + x.equals(y));

        // OUTPUT
        // ====== LENGTH / EMPTINESS ======
        // length()    = 22
        // isEmpty()   = false
        // isBlank()   = true
        // ====== CHAR ACCESS ======
        // charAt(2)        = H
        // toCharArray()[0] =  (a space)
        // codePointAt(2)   = 72
        // getBytes().length= 22
        // (... etc - matches the inline comments above ...)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
