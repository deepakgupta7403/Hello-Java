package Basics.LambdaAndStreams.Examples;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Real-World Example 2 - Streams in a Grocery Store
 *
 * Demonstrates: filtering by category, sorting, summing prices, grouping
 * by category, finding cheapest item, joining names, building a receipt.
 */
public class GroceryStoreExample {

    record Item(String name, String category, double price, int quantity) {
        double subtotal() { return price * quantity; }
    }

    public static void main(String[] args) {

        List<Item> cart = List.of(
                new Item("Apples",     "Produce",  3.50, 4),
                new Item("Bread",      "Bakery",   2.25, 2),
                new Item("Coffee",     "Pantry",   8.00, 1),
                new Item("Cheese",     "Dairy",   12.50, 1),
                new Item("Milk",       "Dairy",    3.00, 2),
                new Item("Eggs",       "Dairy",    4.00, 1),
                new Item("Croissants", "Bakery",   4.50, 6),
                new Item("Bananas",    "Produce",  1.50, 6)
        );

        section("1) Items costing more than $5 - filter + sorted");
        cart.stream()
            .filter(i -> i.subtotal() > 5)
            .sorted(Comparator.comparingDouble(Item::subtotal).reversed())
            .forEach(i -> System.out.printf("  %-12s  $%6.2f%n", i.name(), i.subtotal()));

        section("2) Grand total - mapToDouble + sum");
        double total = cart.stream().mapToDouble(Item::subtotal).sum();
        System.out.printf("  grand total = $%.2f%n", total);

        section("3) Items per category");
        Map<String, List<String>> byCat = cart.stream()
                .collect(Collectors.groupingBy(
                        Item::category,
                        Collectors.mapping(Item::name, Collectors.toList())));
        byCat.forEach((c, names) -> System.out.println("  " + c + " -> " + names));

        section("4) Sub-total per category");
        Map<String, Double> totalByCat = cart.stream()
                .collect(Collectors.groupingBy(
                        Item::category,
                        Collectors.summingDouble(Item::subtotal)));
        totalByCat.forEach((c, t) -> System.out.printf("  %-8s $%6.2f%n", c, t));

        section("5) Cheapest item per category");
        Map<String, Item> cheapest = cart.stream()
                .collect(Collectors.toMap(
                        Item::category,
                        i -> i,
                        (a, b) -> a.price() <= b.price() ? a : b));
        cheapest.forEach((c, i) -> System.out.printf("  %-8s -> %s @ $%.2f%n",
                c, i.name(), i.price()));

        section("6) Single-line receipt");
        String receipt = cart.stream()
                .map(i -> i.name() + " x" + i.quantity() + " ($" + String.format("%.2f", i.subtotal()) + ")")
                .collect(Collectors.joining(", ", "Cart: [", "]"));
        System.out.println(receipt);

        section("7) Item count per category");
        Map<String, Long> countByCat = cart.stream()
                .collect(Collectors.groupingBy(Item::category, Collectors.counting()));
        System.out.println("countByCat = " + countByCat);

        section("8) Apply a 10% discount on all dairy items (returns a NEW list)");
        List<Item> discounted = cart.stream()
                .map(i -> i.category().equals("Dairy")
                          ? new Item(i.name(), i.category(), i.price() * 0.9, i.quantity())
                          : i)
                .toList();
        discounted.forEach(i -> System.out.printf("  %-12s $%5.2f%n", i.name(), i.price()));

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
