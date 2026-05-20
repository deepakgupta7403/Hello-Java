package Phase6_RuntimeMemoryRegexReflection.Regex;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modern Regex Features (Java 8 -> 21)
 * ------------------------------------
 * Pattern / Matcher have collected a few quality-of-life additions over the
 * years that fit the modern functional / stream style.
 *
 *
 * Java 8
 * ------
 *   Pattern.splitAsStream(input)            - lazy Stream<String> of split parts
 *   Pattern.asPredicate()                   - Predicate<String> for "matches anywhere"
 *
 * Java 11
 * -------
 *   Pattern.asMatchPredicate()              - Predicate<String> for "full match"
 *
 * Java 9
 * ------
 *   Matcher.results()                       - Stream<MatchResult> of every match
 *   Matcher.replaceAll(Function<MatchResult,String>)
 *                                           - replace with a function per match
 *   Matcher.replaceFirst(Function<MatchResult,String>)
 *                                           - one-shot version of the above
 *
 * Java 20+ / 21
 * -------------
 *   - Pattern matching for switch (Java 21) - not regex per se, but it makes
 *     classifying strings ergonomic when combined with simple `String` checks.
 *   - There were no new regex primitives in Java 21, but the existing Stream
 *     APIs pair very well with collectors and records added in 16/17/21.
 *
 *
 * Why the Stream Versions Matter
 * ------------------------------
 *   - LAZY: parts of a huge input are not all materialised at once.
 *   - COMPOSABLE: map / filter / collect after a regex match becomes natural.
 *   - PARALLELISABLE: you can call .parallel() on the stream.
 */

public class ModernRegexFeatures {

    public static void main(String[] args) {

        section("1) Pattern.splitAsStream - lazy split into a Stream");
        String csv = "alpha,beta,gamma,,delta";
        Pattern comma = Pattern.compile(",");
        java.util.List<String> parts = comma.splitAsStream(csv).toList();
        System.out.println("parts = " + parts);

        section("2) Pattern.asPredicate / asMatchPredicate - regex as a Predicate");
        Pattern digit = Pattern.compile("\\d+");
        java.util.List<String> tokens = java.util.List.of("alpha", "42", "x99", "yes");
        // asPredicate -> "the input CONTAINS a match" (uses find)
        java.util.List<String> hasDigit = tokens.stream()
                .filter(digit.asPredicate())
                .toList();
        // asMatchPredicate (Java 11+) -> "the WHOLE input matches" (uses matches)
        java.util.List<String> allDigits = tokens.stream()
                .filter(digit.asMatchPredicate())
                .toList();
        System.out.println("contains a digit  = " + hasDigit);   // [42, x99]
        System.out.println("is all digits     = " + allDigits);  // [42]

        section("3) Matcher.results - stream of every match");
        String log = "[INFO] start  [WARN] disk 90%  [ERROR] db down  [INFO] done";
        Pattern level = Pattern.compile("\\[(?<lvl>INFO|WARN|ERROR)\\]");
        Map<String, Long> counts = level.matcher(log)
                .results()
                .map(r -> r.group("lvl"))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        System.out.println("counts = " + counts);

        section("4) Matcher.replaceAll(Function) - dynamic replacement (Java 9+)");
        String shouted = Pattern.compile("\\b\\w+\\b")
                                .matcher("hello big WORLD java")
                                .replaceAll(MatchResult::group);
        System.out.println("no change         = " + shouted);

        String addPrefix = Pattern.compile("\\d+")
                                  .matcher("a 1 b 22 c 333")
                                  .replaceAll(r -> "<" + r.group() + ">");
        System.out.println("wrapped numbers   = " + addPrefix);

        section("5) Stream + groups - structured extraction");
        String dates = "today 2026-05-19, tomorrow 2026-05-20, then 2027-01-01";
        Pattern p5 = Pattern.compile("(?<y>\\d{4})-(?<m>\\d{2})-(?<d>\\d{2})");
        record Date(int y, int m, int d) {}
        java.util.List<Date> parsed = p5.matcher(dates)
                .results()
                .map(r -> new Date(
                        Integer.parseInt(r.group("y")),
                        Integer.parseInt(r.group("m")),
                        Integer.parseInt(r.group("d"))))
                .toList();
        parsed.forEach(System.out::println);

        section("6) Functional pipeline - extract, transform, summarise");
        String prices = "Apples $3.50, Bread $2.25, Coffee $8.00, Cheese $12.50";
        double total = Pattern.compile("\\$(\\d+(?:\\.\\d+)?)")
                .matcher(prices)
                .results()
                .mapToDouble(r -> Double.parseDouble(r.group(1)))
                .sum();
        System.out.printf("total = $%.2f%n", total);

        section("7) Parallel stream - regex is thread-safe at the Pattern level");
        long lower = Stream.of("alpha", "beta", "Gamma", "delta", "Epsilon")
                .parallel()
                .filter(Pattern.compile("^[a-z]").asPredicate())
                .count();
        System.out.println("lowercase-start count = " + lower);

        section("8) Java 21 pattern-matching switch with a regex check");
        for (String raw : java.util.List.of("123", "yes", "no", "alpha")) {
            String kind = switch (raw) {
                case null                                  -> "null";
                case String s when s.matches("\\d+")        -> "number";
                case "yes", "y"                            -> "affirmative";
                case "no",  "n"                            -> "negative";
                default                                    -> "word";
            };
            System.out.printf("%-8s -> %s%n", raw, kind);
        }

        // OUTPUT (matches inline comments)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
