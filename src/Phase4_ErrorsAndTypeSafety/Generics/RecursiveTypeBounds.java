package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Recursive Type Bounds
 * ---------------------
 * A type parameter can have a bound that REFERENCES ITSELF. The canonical
 * example is:
 * <p>
 *
 *      &lt;T extends Comparable&lt;T&gt;&gt;
 * <p>
 *
 * Read as "T is comparable to ITS OWN TYPE". This is exactly the constraint
 * the JDK's Collections.max / Collections.min / Collections.sort etc. need.
 * <p>
 *
 * Why You Need It
 * ---------------
 * Without the recursive bound you could pass nonsensical types:
 * <p>
 *
 *      static &lt;T extends Comparable&gt; T max(List&lt;T&gt; xs)  // BAD
 * <p>
 *
 *      A class `Foo implements Comparable&lt;String&gt;` satisfies that signature
 *      but compareTo on a Foo expects a String - we'd mis-compare.
 * <p>
 *
 * The recursive bound forbids that.
 * <p>
 *
 * The Builder Pattern's Cousin
 * ----------------------------
 * Another classic recursive-bound use:
 * <p>
 *
 *      class Builder&lt;B extends Builder&lt;B&gt;&gt; {
 *          B self() { return (B) this; }
 *          B name(String n) { ...; return self(); }
 *      }
 * <p>
 *
 * Subclasses inherit the fluent return type as their OWN type, not the
 * parent's. That lets you keep chaining subclass methods.
 * <p>
 *
 * Enum&lt;E extends Enum&lt;E&gt;&gt; - the JDK's most famous example
 * ------------------------------------------------------
 * java.lang.Enum is declared:
 * <p>
 *
 *      public abstract class Enum&lt;E extends Enum&lt;E&gt;&gt;
 *          implements Comparable&lt;E&gt;, Serializable { ... }
 * <p>
 *
 * That is why Day.MONDAY.compareTo(Day.FRIDAY) returns an int but
 * Day.MONDAY.compareTo(Color.RED) is a COMPILE ERROR - the bound nails
 * E down to "your own enum type, please".
 * <p>
 *
 * Two demos below: a typed min/max method and a "fluent self-typed builder".
 */

public class RecursiveTypeBounds {

    // ============================================================
    // 1) Typed max - "T comparable to T"
    // ============================================================
    public static <T extends Comparable<T>> T max(List<T> xs) {
        if (xs.isEmpty()) throw new IllegalArgumentException("empty");
        T best = xs.get(0);
        for (T x : xs) if (x.compareTo(best) > 0) best = x;
        return best;
    }

    // ============================================================
    // 2) A "Comparable to a SUPERTYPE" variant - more flexible PECS-style
    // ============================================================
    /**
     * Accepts any T that is comparable to itself OR any of its SUPERTYPES.
     * The `? super T` lets you compare Dogs with an Animal Comparator, etc.
     */
    public static <T extends Comparable<? super T>> T maxFlexible(List<T> xs) {
        if (xs.isEmpty()) throw new IllegalArgumentException("empty");
        T best = xs.get(0);
        for (T x : xs) if (x.compareTo(best) > 0) best = x;
        return best;
    }

    // ============================================================
    // 3) Self-typed fluent builder via recursive bound
    // ============================================================
    static abstract class AbstractBuilder<B extends AbstractBuilder<B, R>, R> {
        protected String name;
        protected int    quantity;

        public B name(String n)     { this.name = n;   return self(); }
        public B quantity(int q)    { this.quantity = q; return self(); }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected B self() { return (B) this; }
    }

    /** A concrete builder that inherits the fluent chain seamlessly. */
    static class OrderBuilder extends AbstractBuilder<OrderBuilder, Order> {
        private String customer;
        public OrderBuilder customer(String c) { this.customer = c; return self(); }
        @Override
        public Order build() {
            return new Order(name, quantity, customer);
        }
    }

    record Order(String item, int quantity, String customer) {}

    // ============================================================
    // 4) A worked example: ENUMS get a recursive bound for free
    // ============================================================
    enum Size implements Comparable<Size> { S, M, L, XL }      // bound provided by java.lang.Enum

    public static void main(String[] args) {

        section("1) max with recursive bound");
        List<Integer> ints = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        List<String>  strs = Arrays.asList("banana", "apple", "cherry");
        System.out.println("max(ints) = " + max(ints));
        System.out.println("max(strs) = " + max(strs));

        section("2) maxFlexible accepts Comparable to a SUPERTYPE");
        // Imagine a Dog whose natural order is defined in its parent Animal.
        // The flexible bound accepts it; the strict bound would not.
        List<Dog> dogs = Arrays.asList(
                new Dog("Charlie"), new Dog("Alice"), new Dog("Bob")
        );
        Dog top = maxFlexible(dogs);
        System.out.println("max dog (by Animal order) = " + top);

        section("3) Self-typed fluent builder");
        Order o = new OrderBuilder()
                .customer("Alice")
                .name("book")
                .quantity(3)
                .build();
        System.out.println(o);

        section("4) Enum comparisons - free recursive bound from java.lang.Enum");
        System.out.println("S.compareTo(L) = " + Size.S.compareTo(Size.L));
        System.out.println("XL.compareTo(M)= " + Size.XL.compareTo(Size.M));
        // The signature is Enum<E extends Enum<E>> - so different enum types
        // are NOT comparable to each other:
        // Size.S.compareTo(Day.MONDAY);   // would be a COMPILE error

        section("5) Why the recursive bound exists");
        System.out.println(
                "Without `T extends Comparable<T>`, a class implementing\n" +
                "Comparable<SomeUnrelatedType> would 'satisfy' the bound -\n" +
                "but compareTo would type-mismatch at runtime."
        );

        // OUTPUT (representative)
    }

    // -------- supporting demo classes for #2 --------
    static class Animal implements Comparable<Animal> {
        final String name;
        Animal(String name) { this.name = name; }
        @Override public int compareTo(Animal o) { return this.name.compareTo(o.name); }
        @Override public String toString() { return name; }
    }
    static class Dog extends Animal {
        Dog(String name) { super(name); }
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }

    @SuppressWarnings("unused")
    private static Comparator<?> keepImport;
    @SuppressWarnings("unused")
    private static ArrayList<?> keepImport2;
}
