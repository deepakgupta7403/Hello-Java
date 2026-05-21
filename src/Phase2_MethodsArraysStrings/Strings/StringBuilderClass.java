package Phase2_MethodsArraysStrings.Strings;

/**
 * StringBuilder - Mutable, Single-Threaded Character Sequence (Java 5+)
 * --------------------------------------------------------------------
 * StringBuilder is the modern, NON-SYNCHRONIZED counterpart to StringBuffer.
 * It has the EXACT SAME API but skips the locking, so it is faster. Use it
 * for almost every "build up a string" scenario - StringBuilder is the
 * default choice in single-threaded code (which most code is).
 * <p>
 *
 *      StringBuilder sb = new StringBuilder();
 *      sb.append("Hello").append(", ").append("World");
 *      String s = sb.toString();      // "Hello, World"
 * <p>
 *
 * StringBuffer vs StringBuilder - One-Line Recap
 * ----------------------------------------------
 *      StringBuffer  - synchronized, thread-safe, slower.
 *      StringBuilder - NOT synchronized, NOT thread-safe, faster.
 * <p>
 *
 * When To Use It
 * --------------
 *  - Building a String from many pieces in a loop.
 *  - Composing structured text (CSV, JSON, HTML) at runtime.
 *  - Anywhere you used to write `s += ...` inside a loop.
 * <p>
 *
 * Common Methods - Quick Catalogue
 * --------------------------------
 *      Constructors
 *          new StringBuilder()                   capacity 16
 *          new StringBuilder(int cap)            custom capacity
 *          new StringBuilder(String s)           seeded with s
 *          new StringBuilder(CharSequence)       seeded with any CharSequence
 * <p>
 *
 *      Append / Insert / Replace / Delete - identical to StringBuffer
 *          append(...) / insert(...) / replace(...) / delete(...) / deleteCharAt(...)
 * <p>
 *
 *      Read / write
 *          charAt(i) / setCharAt(i, ch) / getChars(...)
 *          codePointAt(i) / codePointBefore(i) / codePointCount(beg,end)
 * <p>
 *
 *      Length & capacity
 *          length() / capacity() / ensureCapacity(min) / trimToSize() / setLength(n)
 * <p>
 *
 *      Search
 *          indexOf(str)        indexOf(str, from)
 *          lastIndexOf(str)    lastIndexOf(str, from)
 * <p>
 *
 *      Sub-sequence and output
 *          substring(begin) / substring(begin, end) / subSequence(begin, end)
 *          toString()
 * <p>
 *
 *      Misc / modern additions
 *          reverse()
 *          chars() / codePoints()                       (Java 9+)
 *          compareTo(StringBuilder)                     (Java 11+)
 *          isEmpty()                                    (Java 15+)
 * <p>
 *
 * Each method is demonstrated below.
 */

public class StringBuilderClass {

    public static void main(String[] args) {

        section("Constructors & basics");
        StringBuilder empty = new StringBuilder();
        StringBuilder sized = new StringBuilder(50);
        StringBuilder fromS = new StringBuilder("Java");
        System.out.println("empty.length()    = " + empty.length());
        System.out.println("empty.capacity()  = " + empty.capacity());
        System.out.println("sized.capacity()  = " + sized.capacity());
        System.out.println("fromS             = " + fromS);
        System.out.println("fromS.capacity()  = " + fromS.capacity());   // 4 + 16 = 20

        section("append() - overloaded for almost every type");
        StringBuilder a = new StringBuilder();
        a.append("Score: ")
         .append(87)
         .append('/')
         .append(100)
         .append(" - pass=")
         .append(true)
         .append(' ')
         .append(3.14)
         .append(new char[]{' ', '!', '!'});
        System.out.println(a);

        section("insert() at an index");
        StringBuilder b = new StringBuilder("Hello World");
        b.insert(5, ",");
        b.insert(0, ">>> ");
        b.insert(b.length(), " <<<");
        System.out.println("after insert       = " + b);

        section("replace() / delete() / deleteCharAt()");
        StringBuilder c = new StringBuilder("Hello, World!");
        c.replace(7, 12, "Java");
        System.out.println("after replace      = " + c);     // Hello, Java!
        c.delete(5, 7);
        System.out.println("after delete       = " + c);     // HelloJava!
        c.deleteCharAt(c.length() - 1);
        System.out.println("after deleteCharAt = " + c);     // HelloJava

        section("reverse()");
        StringBuilder r = new StringBuilder("abcdef");
        System.out.println("reverse            = " + r.reverse());

        // Palindrome check using reverse()
        String word = "racecar";
        boolean isPalin = word.equals(new StringBuilder(word).reverse().toString());
        System.out.println("'racecar' palindrome? " + isPalin);

        section("setCharAt() / charAt() / getChars()");
        StringBuilder m = new StringBuilder("Java");
        m.setCharAt(0, 'L');
        System.out.println("after setCharAt    = " + m);    // Lava
        char[] dst = new char[4];
        m.getChars(0, m.length(), dst, 0);
        System.out.println("getChars dst[2]    = " + dst[2]);

        section("indexOf() / lastIndexOf()");
        StringBuilder s = new StringBuilder("abracadabra");
        System.out.println("indexOf('a')       = " + s.indexOf("a"));        // 0
        System.out.println("indexOf('a', 1)    = " + s.indexOf("a", 1));     // 3
        System.out.println("lastIndexOf('a')   = " + s.lastIndexOf("a"));    // 10
        System.out.println("indexOf('xyz')     = " + s.indexOf("xyz"));      // -1

        section("substring() / subSequence()");
        StringBuilder sub = new StringBuilder("Hello, World");
        System.out.println("substring(7)       = " + sub.substring(7));         // World
        System.out.println("substring(7,12)    = " + sub.substring(7, 12));     // World
        System.out.println("subSequence(7,12)  = " + sub.subSequence(7, 12));   // World

        section("length() / capacity() / setLength()");
        StringBuilder cap = new StringBuilder();
        System.out.println("initial capacity   = " + cap.capacity());      // 16
        cap.append("0123456789ABCDEF").append("!");
        System.out.println("after 17 chars cap = " + cap.capacity());      // grew
        cap.ensureCapacity(200);
        System.out.println("after ensureCapacity=" + cap.capacity());
        cap.trimToSize();
        System.out.println("after trimToSize   = " + cap.capacity());
        cap.setLength(5);
        System.out.println("after setLength(5) = '" + cap + "' length=" + cap.length());

        section("chars() / codePoints() - Java 9 streams");
        new StringBuilder("Hi!").chars().forEach(ch -> System.out.print(ch + " "));
        System.out.println();
        long ascii = new StringBuilder("abc").chars().filter(ch -> ch < 128).count();
        System.out.println("ascii count        = " + ascii);

        section("compareTo() / isEmpty() - Java 11 / 15");
        StringBuilder p = new StringBuilder("apple");
        StringBuilder q = new StringBuilder("banana");
        System.out.println("compareTo          = " + p.compareTo(q));      // negative
        System.out.println("empty.isEmpty()    = " + empty.isEmpty());

        section("toString() - finalise into an immutable String");
        StringBuilder t = new StringBuilder("Score: ");
        t.append(95).append('%');
        String result = t.toString();
        System.out.println("result             = " + result);
        System.out.println("result.getClass()  = " + result.getClass().getSimpleName());

        section("CSV builder - typical real-world use");
        StringBuilder csv = new StringBuilder();
        String[] header = {"id", "name", "age"};
        for (int j = 0; j < header.length; j++) {
            if (j > 0) csv.append(',');
            csv.append(header[j]);
        }
        csv.append('\n').append("1,Alice,30").append('\n').append("2,Bob,25");
        System.out.println(csv);

        // OUTPUT (representative - matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
