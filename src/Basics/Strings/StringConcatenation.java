package Basics.Strings;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * String Concatenation
 * --------------------
 * "Concatenation" means joining two or more Strings end-to-end. Java offers
 * several mechanisms:
 *
 *      1. The '+' operator                  "Hello, " + name
 *      2. String.concat(other)              "Hello, ".concat(name)
 *      3. StringBuilder / StringBuffer      use when looping
 *      4. String.join(delim, parts...)      Java 8+
 *      5. java.util.StringJoiner            Java 8+, with prefix/suffix
 *      6. Stream.collect(Collectors.joining(...))   Java 8+
 *      7. String.format / String.formatted  printf-style composition
 *      8. Text blocks                       Java 15+, multi-line literals
 *
 *
 * Behind the scenes
 * -----------------
 * Each `+` between String operands is compiled (since Java 9) into a
 * `makeConcatWithConstants` invoke-dynamic call that the JVM optimises at
 * runtime. For most simple expressions like `"x=" + x` you do NOT need to
 * reach for StringBuilder yourself - the compiler does the right thing.
 *
 * The big EXCEPTION is concatenation INSIDE A LOOP:
 *
 *      String s = "";
 *      for (int i = 0; i < N; i++) s += part(i);     // O(N^2) - BAD
 *
 *      StringBuilder sb = new StringBuilder();
 *      for (int i = 0; i < N; i++) sb.append(part(i)); // O(N) - GOOD
 *      String s = sb.toString();
 *
 * Each `+=` allocates and copies the whole prefix again because Strings are
 * immutable.
 *
 *
 * Type Coercion - the '+' Trap
 * ----------------------------
 *      "Sum = " + 1 + 2      ==  "Sum = 12"      (left-to-right concatenation)
 *      "Sum = " + (1 + 2)    ==  "Sum = 3"       (parens force int addition)
 *
 *
 * Performance Demo
 * ----------------
 * main() benchmarks loop-concat vs StringBuilder so you can see the
 * difference in milliseconds.
 */

public class StringConcatenation {

    public static void main(String[] args) {

        // ============================================================
        // 1. + operator - the everyday case
        // ============================================================
        String name = "Deepak";
        int    age  = 25;
        String greeting = "Hello " + name + ", you are " + age + " years old.";
        System.out.println(greeting);

        // ============================================================
        // 2. concat() - same effect as +, but only for two Strings, never null
        // ============================================================
        String a = "Hello, ".concat("World");
        System.out.println(a);

        // ============================================================
        // 3. The '+' trap: left-to-right evaluation
        // ============================================================
        System.out.println("Sum = " + 1 + 2);       // "Sum = 12"  (string + 1 -> string, + 2 -> string)
        System.out.println("Sum = " + (1 + 2));     // "Sum = 3"

        // ============================================================
        // 4. String.join (Java 8+) - joins many parts with a delimiter
        // ============================================================
        String csv = String.join(",", "alpha", "beta", "gamma");
        System.out.println("join("+","+",...) = " + csv);

        List<String> parts = List.of("alpha", "beta", "gamma");
        System.out.println("join(list)        = " + String.join(",", parts));

        // ============================================================
        // 5. StringJoiner - delimiter + optional prefix and suffix
        // ============================================================
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        sj.add("apple");
        sj.add("banana");
        sj.add("cherry");
        System.out.println("StringJoiner      = " + sj);   // [apple, banana, cherry]

        // ============================================================
        // 6. Collectors.joining - the streamy way
        // ============================================================
        String sj2 = parts.stream()
                          .map(String::toUpperCase)
                          .collect(Collectors.joining(" | ", "<", ">"));
        System.out.println("Collectors.join   = " + sj2);

        // ============================================================
        // 7. String.format / formatted (Java 15+) - printf-style
        // ============================================================
        String row = String.format("%-10s %5d %6.2f", name, age, 1.75);
        System.out.println("format            = " + row);

        String row2 = "Name=%s, Age=%d".formatted(name, age);  // Java 15+
        System.out.println("formatted         = " + row2);

        // ============================================================
        // 8. Performance - loop-+ vs StringBuilder
        // ============================================================
        final int N = 50_000;

        long t1 = System.nanoTime();
        String slow = "";
        for (int i = 0; i < N; i++) slow += "x";
        long slowMs = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) sb.append('x');
        String fast = sb.toString();
        long fastMs = (System.nanoTime() - t2) / 1_000_000;

        System.out.println();
        System.out.println("N=" + N);
        System.out.println("loop with += :  " + slowMs + " ms   length=" + slow.length());
        System.out.println("StringBuilder :  " + fastMs + " ms   length=" + fast.length());

        // SAMPLE OUTPUT (timings vary)
        // Hello Deepak, you are 25 years old.
        // Hello, World
        // Sum = 12
        // Sum = 3
        // join(,,...) = alpha,beta,gamma
        // join(list)        = alpha,beta,gamma
        // StringJoiner      = [apple, banana, cherry]
        // Collectors.join   = <ALPHA | BETA | GAMMA>
        // format            = Deepak        25   1.75
        // formatted         = Name=Deepak, Age=25
        //
        // N=50000
        // loop with += :  450 ms   length=50000
        // StringBuilder :  2   ms   length=50000
    }
}
