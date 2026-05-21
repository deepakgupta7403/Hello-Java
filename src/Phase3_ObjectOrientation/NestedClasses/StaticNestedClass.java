package Phase3_ObjectOrientation.NestedClasses;

import java.util.Objects;

/**
 * Static Nested Classes
 * ---------------------
 * A nested class declared `static`. It is just a top-level class that
 * happens to live INSIDE another for namespace reasons. It has NO
 * implicit reference to an instance of the outer class.
 * <p>
 *
 *      public class Owner {
 *          public static class Helper { ... }
 *      }
 * <p>
 *
 *      Owner.Helper h = new Owner.Helper();    // no Owner instance required
 * <p>
 *
 * Use cases
 * ---------
 *   - Builders                 (Builder lives inside Pizza)
 *   - DTOs / value records     (public static record Position(...))
 *   - Enums (every enum IS a static nested class implicitly)
 *   - Iterators                (private static class Itr ...)
 *   - Strategy / Comparator helpers tightly bound to the owner.
 * <p>
 *
 * Visibility tricks
 * -----------------
 *   - A private static nested class is INVISIBLE outside the file —
 *     great for implementation detail.
 *   - It can ACCESS the outer class's private static members.
 */

public class StaticNestedClass {

    // ----------------- 1) Plain static nested helper -----------------
    static class Pair<A, B> {
        final A first; final B second;
        Pair(A a, B b) { this.first = a; this.second = b; }
        @Override public String toString() { return "(" + first + ", " + second + ")"; }
    }

    // ----------------- 2) The Builder pattern -----------------------
    public static class Pizza {
        public enum Topping { CHEESE, MUSHROOM, PEPPER, OLIVE }   // enum = static nested
        private final java.util.EnumSet<Topping> toppings;
        private final boolean extraSauce;
        private Pizza(Builder b) { this.toppings = b.toppings; this.extraSauce = b.extraSauce; }
        public static class Builder {
            private final java.util.EnumSet<Topping> toppings = java.util.EnumSet.noneOf(Topping.class);
            private boolean extraSauce;
            public Builder add(Topping t) { toppings.add(t); return this; }
            public Builder extraSauce()    { this.extraSauce = true; return this; }
            public Pizza   build()         { return new Pizza(this); }
        }
        @Override public String toString() {
            return "Pizza" + toppings + (extraSauce ? " (extra sauce)" : "");
        }
    }

    // ----------------- 3) Static nested record (Java 16+) -----------
    public static record Point(int x, int y) {
        public double distance(Point other) {
            int dx = x - other.x, dy = y - other.y;
            return Math.hypot(dx, dy);
        }
    }

    // ----------------- 4) Private static nested — implementation detail
    public static class Counters {
        private static class CountingPair extends Pair<String, Integer> {
            CountingPair(String s, Integer n) { super(s, n); }
        }
        public static Object countingPair(String s, int n) {
            return new CountingPair(s, n);    // type leaks only as Object
        }
    }

    public static void main(String[] args) {

        section("1) Plain static nested — value-like helper");
        Pair<String, Integer> p = new Pair<>("answer", 42);
        System.out.println(p);

        section("2) Builder pattern");
        Pizza pizza = new Pizza.Builder()
                .add(Pizza.Topping.CHEESE)
                .add(Pizza.Topping.OLIVE)
                .extraSauce()
                .build();
        System.out.println(pizza);

        section("3) Static nested record");
        Point a = new Point(0, 0);
        Point b = new Point(3, 4);
        System.out.println("distance = " + a.distance(b));
        System.out.println("equals?  = " + Objects.equals(a, new Point(0, 0)));

        section("4) Private static nested — type is hidden");
        Object o = Counters.countingPair("x", 1);
        System.out.println("hidden class instance = " + o);

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
