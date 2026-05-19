package Basics.Generics;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Programming in Java - Introduction
 * ------------------------------------------
 * GENERICS let you write classes, interfaces, and methods that work with a
 * PARAMETER TYPE chosen by the caller. The result is code that is reusable
 * AND type-safe at compile time:
 *
 *      List&lt;String&gt;  names = new ArrayList&lt;&gt;();
 *      List&lt;Integer&gt; ints  = new ArrayList&lt;&gt;();
 *
 * One ArrayList class, two specialised types. The compiler enforces that
 * `names.add(42)` is a COMPILE error - you cannot accidentally mix types.
 *
 *
 * Before Generics (Pre-Java 5)
 * ----------------------------
 *      List names = new ArrayList();          // raw list - holds Objects
 *      names.add("Alice");
 *      names.add(42);                          // legal! mixed bag
 *      String s = (String) names.get(1);       // ClassCastException at runtime
 *
 *
 * After Generics (Java 5+)
 * ------------------------
 *      List&lt;String&gt; names = new ArrayList&lt;&gt;();
 *      names.add("Alice");
 *      names.add(42);                          // COMPILE ERROR - caught early
 *      String s = names.get(0);                // no cast needed
 *
 *
 * What Generics Give You
 * ----------------------
 *   - TYPE SAFETY at compile time.
 *   - No more redundant casts.
 *   - Reusable, generic algorithms (Collections.sort, Comparator.comparing,
 *     Function&lt;T, R&gt;, Optional&lt;T&gt;, Stream&lt;T&gt;...).
 *   - Better IDE autocomplete and refactoring.
 *
 *
 * Type Parameters - The Conventional Names
 * ----------------------------------------
 *      T       Type (a generic placeholder)
 *      E       Element  (for collections)
 *      K       Key      (for maps)
 *      V       Value    (for maps)
 *      N       Number
 *      R       Return type
 *      ?       Wildcard ("some type, I don't care which")
 *
 * Use these single-letter names by convention. Pick longer names only if
 * the meaning is non-obvious in context.
 *
 *
 * Where Generics Appear in Java
 * -----------------------------
 *   - Generic CLASSES         class Box&lt;T&gt; { T value; }
 *   - Generic INTERFACES      interface Comparable&lt;T&gt; { ... }
 *   - Generic METHODS         &lt;T&gt; List&lt;T&gt; singleton(T t) { ... }
 *   - Generic CONSTRUCTORS    &lt;T&gt; Box(T t) { ... }
 *   - Bounded type params     &lt;T extends Number&gt;
 *   - Wildcards               List&lt;? extends Number&gt;
 *
 *
 * What This Folder Covers
 * -----------------------
 *   GenericsIntroduction.java         (this file)
 *   GenericClasses.java               - parameterised classes
 *   GenericMethods.java               - parameterised methods
 *   GenericInterfaces.java            - parameterised interfaces
 *   BoundedTypeParameters.java        - &lt;T extends X&gt; bounds
 *   Wildcards.java                    - ?, ? extends, ? super
 *   PecsPrinciple.java                - Producer Extends, Consumer Super
 *   TypeErasure.java                  - how generics actually work at runtime
 *   GenericRestrictions.java          - what you CANNOT do with generics
 *   RecursiveTypeBounds.java          - &lt;T extends Comparable&lt;T&gt;&gt;
 *   HeapPollutionAndSafeVarargs.java  - varargs + generics warnings
 *   ModernGenerics.java               - diamond, var, records, lambdas
 */

public class GenericsIntroduction {

    // ============================================================
    // A tiny generic class so we can compare "before" and "after"
    // ============================================================
    static class Box<T> {
        private T value;
        public Box(T value) { this.value = value; }
        public T get() { return value; }
        public void set(T value) { this.value = value; }
    }

    public static void main(String[] args) {

        section("1) The big idea - one class, many specialised types");
        Box<String>  s = new Box<>("hello");
        Box<Integer> i = new Box<>(42);
        Box<Double>  d = new Box<>(3.14);
        System.out.println("string box  = " + s.get());
        System.out.println("integer box = " + i.get());
        System.out.println("double box  = " + d.get());

        section("2) Without generics: raw types and casts everywhere");
        // The compiler still allows this (with a warning) for backwards
        // compatibility with pre-1.5 code.
        @SuppressWarnings({"rawtypes", "unchecked"})
        List raw = new ArrayList();
        raw.add("Alice");
        raw.add(42);                       // legal but suspicious
        Object first = raw.get(0);
        System.out.println("raw[0] = " + first + "  (we don't know its type at compile time)");

        section("3) With generics: compile-time checking");
        List<String> names = new ArrayList<>();
        names.add("Alice");
        // names.add(42);                   // COMPILE ERROR (which is the point)
        String head = names.get(0);          // no cast required
        System.out.println("typed list head = " + head);

        section("4) Generic types are checked by the COMPILER, erased at RUNTIME");
        Box<String>  sb = new Box<>("hi");
        Box<Integer> ib = new Box<>(7);
        // At runtime, sb and ib have the SAME class.
        System.out.println("sb.getClass() = " + sb.getClass().getName());
        System.out.println("ib.getClass() = " + ib.getClass().getName());
        System.out.println("same class?    " + (sb.getClass() == ib.getClass()));
        // We'll explain this in TypeErasure.java.

        section("5) The diamond operator <> infers the type (Java 7+)");
        Box<String> diamond = new Box<>("inferred");           // compiler infers <String>
        Box<String> verbose = new Box<String>("verbose");      // legal but redundant
        System.out.println(diamond.get() + " / " + verbose.get());

        section("6) var + generics together (Java 10+) - very tidy");
        var inferred = new Box<>("type inferred end-to-end");
        System.out.println(inferred.get() + "  (type was: " + inferred.getClass().getName() + ")");

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
