package Phase2_MethodsArraysStrings.Methods;

/**
 * Interface Methods - default, static, private (Java 8 / 9)
 * ---------------------------------------------------------
 * Before Java 8, every method declared in an interface was implicitly PUBLIC
 * ABSTRACT - no body allowed. To evolve an interface (e.g. add a new method),
 * you had to break every existing implementor. Java 8 fixed this with
 * `default` methods, and Java 9 finished the story with `private` methods.
 *
 *
 * The Four Method Flavours Allowed in an Interface (since Java 9)
 * ---------------------------------------------------------------
 *
 *      1. ABSTRACT  - no body, must be implemented by every concrete class.
 *                     `void doStuff();`
 *
 *      2. DEFAULT   - has a body, inherited by implementors unless overridden.
 *                     `default void greet() { ... }`
 *
 *      3. STATIC    - utility belonging to the interface itself.
 *                     `static Comparator<...> byLength() { ... }`
 *
 *      4. PRIVATE   - helper for the interface's own default/static methods.
 *                     `private boolean isValid(String s) { ... }`
 *                     (Java 9+)
 *
 *
 * Why `default` Was Added
 * -----------------------
 * Interfaces could not evolve without breaking implementors. Adding
 * `default forEach(Consumer)` to java.lang.Iterable in Java 8 was the
 * motivating example - every existing collection class got it for free.
 *
 *
 * Why `private` Was Added (Java 9)
 * --------------------------------
 * To avoid duplicating helper code between default/static methods of the same
 * interface. Without `private`, you would have to expose the helper or copy it.
 *
 *
 * Multiple Inheritance and the "Diamond" Problem
 * ----------------------------------------------
 * A class may implement multiple interfaces. If two of them provide CONFLICTING
 * default methods with the same signature, the compiler forces you to override
 * the method in your class to disambiguate (you can call
 * `Interface.super.method()` to pick one).
 *
 *
 * Modifier Quick Recap
 * --------------------
 *  - Abstract methods           : implicitly public abstract.
 *  - Default / static methods   : may be public (default) - no modifier needed.
 *  - Private methods            : private only; cannot be public/protected.
 *  - Fields in interfaces       : implicitly public static final (constants).
 */

public class InterfaceMethods {

    // ============ A simple interface using all four method flavours ============

    interface Greeter {

        /** Implementors must provide this. */
        String getName();

        /** Java 8 default - the standard greeting; can be overridden. */
        default String greet() {
            // Calls a private helper to keep this method tidy.
            return "Hello, " + capitalize(getName()) + "!";
        }

        /** Java 8 default - a louder greeting that reuses greet(). */
        default String shout() {
            return greet().toUpperCase();
        }

        /** Java 8 static - utility tied to the interface, not to instances. */
        static Greeter anonymous() {
            return () -> "stranger";
        }

        /** Java 9 private - helper used by greet(); not part of the public API. */
        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }

    // ============ An implementation that uses the defaults ============

    static class Customer implements Greeter {
        private final String name;
        Customer(String name) { this.name = name; }

        @Override public String getName() { return name; }
        // Inherits greet() and shout() from the interface.
    }

    // ============ An implementation that overrides a default ============

    static class Robot implements Greeter {
        @Override public String getName() { return "unit-9"; }
        @Override public String greet() {
            return "BEEP BOOP. I am " + getName() + ".";
        }
    }

    // ============ Diamond conflict - two interfaces with the same default ============

    interface English { default String hello() { return "Hello"; } }
    interface French  { default String hello() { return "Bonjour"; } }

    static class Polyglot implements English, French {
        // Required to disambiguate - compile error if omitted.
        @Override public String hello() {
            return English.super.hello() + " / " + French.super.hello();
        }
    }

    public static void main(String[] args) {

        Greeter g1 = new Customer("deepak");
        System.out.println(g1.greet());     // uses inherited default
        System.out.println(g1.shout());     // default that calls another default

        Greeter g2 = new Robot();
        System.out.println(g2.greet());     // overridden version

        // Static method on the interface
        Greeter g3 = Greeter.anonymous();
        System.out.println(g3.greet());

        // Diamond resolution
        Polyglot p = new Polyglot();
        System.out.println(p.hello());

        // OUTPUT
        // Hello, Deepak!
        // HELLO, DEEPAK!
        // BEEP BOOP. I am unit-9.
        // Hello, Stranger!
        // Hello / Bonjour
    }
}
