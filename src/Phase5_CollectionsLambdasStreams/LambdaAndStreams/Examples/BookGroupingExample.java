package Phase5_CollectionsLambdasStreams.LambdaAndStreams.Examples;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Real-World Example 3 - Grouping Books by Author
 *
 * Demonstrates: groupingBy with downstream collectors, multi-level grouping,
 * mapping + counting + summarizing combinators, sorted stream output,
 * partition by year range.
 */
public class BookGroupingExample {

    record Book(String title, String author, String genre, int year, double price) {}

    public static void main(String[] args) {

        List<Book> shelf = List.of(
                new Book("The Pragmatic Programmer", "Hunt",   "Tech",     1999, 29.99),
                new Book("Clean Code",                "Martin", "Tech",     2008, 34.99),
                new Book("Clean Architecture",        "Martin", "Tech",     2017, 39.99),
                new Book("Refactoring",                "Fowler", "Tech",     1999, 44.99),
                new Book("Domain-Driven Design",       "Evans",  "Tech",     2003, 49.99),
                new Book("The Mythical Man-Month",     "Brooks", "Mgmt",     1975, 19.99),
                new Book("Effective Java",             "Bloch",  "Tech",     2018, 45.00),
                new Book("Working Effectively",        "Feathers","Tech",    2004, 32.50),
                new Book("Patterns of EAA",            "Fowler", "Tech",     2002, 54.99)
        );

        section("1) Books grouped by author");
        Map<String, List<Book>> byAuthor = shelf.stream()
                .collect(Collectors.groupingBy(Book::author));
        byAuthor.forEach((a, list) -> {
            System.out.println("  " + a + ":");
            list.forEach(b -> System.out.println("      " + b.title()));
        });

        section("2) Just the TITLES per author (mapping downstream)");
        Map<String, List<String>> titlesByAuthor = shelf.stream()
                .collect(Collectors.groupingBy(
                        Book::author,
                        Collectors.mapping(Book::title, Collectors.toList())));
        titlesByAuthor.forEach((a, ts) -> System.out.println("  " + a + " -> " + ts));

        section("3) Book count per author");
        Map<String, Long> count = shelf.stream()
                .collect(Collectors.groupingBy(Book::author, Collectors.counting()));
        System.out.println(count);

        section("4) Most expensive book per author");
        Map<String, Book> top = shelf.stream()
                .collect(Collectors.toMap(
                        Book::author,
                        b -> b,
                        (a, b) -> a.price() >= b.price() ? a : b));
        top.forEach((a, b) -> System.out.printf("  %-8s -> %s ($%.2f)%n",
                a, b.title(), b.price()));

        section("5) Average price per genre");
        Map<String, Double> avgByGenre = shelf.stream()
                .collect(Collectors.groupingBy(
                        Book::genre,
                        Collectors.averagingDouble(Book::price)));
        avgByGenre.forEach((g, p) -> System.out.printf("  %-5s avg = $%.2f%n", g, p));

        section("6) Two-level group: genre -> author -> list");
        Map<String, Map<String, List<String>>> twoLevel = shelf.stream()
                .collect(Collectors.groupingBy(
                        Book::genre,
                        Collectors.groupingBy(
                                Book::author,
                                Collectors.mapping(Book::title, Collectors.toList()))));
        twoLevel.forEach((g, inner) -> {
            System.out.println("  " + g + ":");
            inner.forEach((a, ts) -> System.out.println("      " + a + " -> " + ts));
        });

        section("7) Partition: pre-2000 vs post-2000");
        Map<Boolean, List<String>> pre2k = shelf.stream()
                .collect(Collectors.partitioningBy(
                        b -> b.year() < 2000,
                        Collectors.mapping(Book::title, Collectors.toList())));
        System.out.println("  pre-2000  = " + pre2k.get(true));
        System.out.println("  2000-up   = " + pre2k.get(false));

        section("8) Sorted shelf by year, oldest first");
        shelf.stream()
             .sorted(Comparator.comparingInt(Book::year))
             .forEach(b -> System.out.printf("  %d  %-30s  %s%n",
                     b.year(), b.title(), b.author()));

        section("9) Statistics (count / sum / min / max / avg) of prices");
        var stats = shelf.stream()
                .collect(Collectors.summarizingDouble(Book::price));
        System.out.println("  " + stats);

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
