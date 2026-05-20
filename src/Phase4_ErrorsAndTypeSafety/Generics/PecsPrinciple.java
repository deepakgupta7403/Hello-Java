package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * PECS - Producer Extends, Consumer Super
 * ---------------------------------------
 * The most famous rule of generic API design, coined by Joshua Bloch in
 * "Effective Java". It tells you which wildcard to use:
 *
 *      PRODUCER produces T values for me  ->  Collection&lt;? extends T&gt;
 *      CONSUMER consumes T values from me ->  Collection&lt;? super T&gt;
 *      Neither (does both)                 ->  plain Collection&lt;T&gt; (no wildcard)
 *
 *
 * Why It Matters
 * --------------
 * Picking the wrong bound makes a generic method LESS USEFUL than it could
 * be. A method that "reads Numbers from a list" should accept
 * `List&lt;? extends Number&gt;`, not just `List&lt;Number&gt;` - otherwise callers
 * with a `List&lt;Integer&gt;` get a compile error.
 *
 *
 * Worked Example: Collections.copy / addAll / sort
 * ------------------------------------------------
 * The JDK applies PECS systematically:
 *
 *      // copy(dest, src) - src PRODUCES, dest CONSUMES
 *      public static &lt;T&gt; void copy(List&lt;? super T&gt; dest, List&lt;? extends T&gt; src)
 *
 *      // addAll(c, elements) - c CONSUMES the varargs PRODUCER
 *      public static &lt;T&gt; boolean addAll(Collection&lt;? super T&gt; c, T... elements)
 *
 *      // sort with a comparator that COMPARES (consumes) T or any supertype
 *      public static &lt;T&gt; void sort(List&lt;T&gt; list, Comparator&lt;? super T&gt; c)
 *
 *
 * The Quick Test
 * --------------
 * For every method parameter ask:
 *   "Will I PULL items out of this and use them as T?" -> `? extends T` (producer)
 *   "Will I PUSH items into this typed as T?"          -> `? super T`   (consumer)
 *   "Both?"                                            -> `T`           (no wildcard)
 */

public class PecsPrinciple {

    // ============================================================
    // 1) Producer pattern - we READ Numbers from src
    // ============================================================
    static double sum(List<? extends Number> src) {
        double total = 0;
        for (Number n : src) total += n.doubleValue();
        return total;
        // src.add(0);   // COMPILE ERROR - we don't know its concrete type
    }

    // ============================================================
    // 2) Consumer pattern - we WRITE Integers into dest
    // ============================================================
    static void addOneToFive(List<? super Integer> dest) {
        for (int i = 1; i <= 5; i++) dest.add(i);            // legal - any super accepts Integer
        // Integer x = dest.get(0);   // would be Object only
    }

    // ============================================================
    // 3) Both - copy SRC into DST  (the canonical PECS method)
    // ============================================================
    static <T> void copy(List<? super T> dst, List<? extends T> src) {
        for (T t : src) dst.add(t);
    }

    // ============================================================
    // 4) Comparator<? super T> - the JDK's classic application
    // ============================================================
    /**
     * Sort a list of T using a Comparator that can compare T-OR-ANY-SUPERTYPE.
     * Imagine an Animal Comparator that can sort a List of Dogs:
     */
    static <T> void sortFlexible(List<T> list, Comparator<? super T> cmp) {
        list.sort(cmp);
    }

    static class Animal {
        final String name;
        Animal(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    static class Dog extends Animal {
        Dog(String name) { super(name); }
    }

    public static void main(String[] args) {

        section("1) PRODUCER - List<? extends Number> accepts subtypes");
        List<Integer> ints   = Arrays.asList(1, 2, 3, 4, 5);
        List<Double>  reals  = Arrays.asList(1.5, 2.5);
        System.out.println("sum(ints)  = " + sum(ints));
        System.out.println("sum(reals) = " + sum(reals));
        // Without `? extends`, sum(List<Number>) would REJECT List<Integer>.

        section("2) CONSUMER - List<? super Integer> accepts supertypes");
        List<Number> intoNumbers = new ArrayList<>();
        List<Object> intoObjects = new ArrayList<>();
        addOneToFive(intoNumbers);
        addOneToFive(intoObjects);
        System.out.println("intoNumbers = " + intoNumbers);
        System.out.println("intoObjects = " + intoObjects);
        // Without `? super`, addOneToFive(List<Integer>) would still work but
        // addOneToFive(List<Number>) would be REJECTED.

        section("3) Both ends - copy(dst, src)");
        List<Number> dst = new ArrayList<>();
        List<Integer> src = List.of(10, 20, 30);
        copy(dst, src);
        System.out.println("dst after copy = " + dst);

        section("4) Comparator<? super T> - one comparator, many T's");
        // An Animal comparator can sort Dogs - because every Dog is-a Animal.
        Comparator<Animal> byName = Comparator.comparing(a -> a.name);
        List<Dog> dogs = new ArrayList<>(Arrays.asList(
                new Dog("Charlie"),
                new Dog("Alice"),
                new Dog("Bob")
        ));
        sortFlexible(dogs, byName);                  // PECS in action
        System.out.println("sorted dogs = " + dogs);
        // sortFlexible(dogs, Comparator<Dog>) would also work.
        // Without `? super T`, the only legal comparator would be Comparator<Dog>.

        section("5) The 'no wildcard' case - read AND write the same type");
        // A method that does BOTH should use a plain T (or a named type
        // parameter). Wildcards make one side or the other impossible.
        List<Integer> mixed = new ArrayList<>(Arrays.asList(1, 2, 3));
        doubleEveryElement(mixed);
        System.out.println("doubled = " + mixed);

        // OUTPUT (representative)
    }

    /**
     * Reads from AND writes to the same list - uses a plain T, no wildcard.
     */
    static <T extends Number> void doubleEveryElement(List<T> xs) {
        // We can read T values as Number AND write T values back into xs.
        // (A wildcard on either side would forbid one of those operations.)
        @SuppressWarnings("unchecked")
        List<Number> view = (List<Number>) xs;   // illustrative cast
        for (int i = 0; i < xs.size(); i++) {
            // We can't blindly add an Integer back - we'd lose type info.
            // The clean version of this method is usually written with a
            // typed Operator: replaceAll(UnaryOperator).
        }
        xs.replaceAll(t -> {
            @SuppressWarnings("unchecked")
            T doubled = (T) (Number) (t.doubleValue() * 2);
            return doubled;
        });
        // The cleanest real-world version uses TWO type parameters or
        // Number-specific code paths. The point here is: "consume + produce"
        // == no wildcards.
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
