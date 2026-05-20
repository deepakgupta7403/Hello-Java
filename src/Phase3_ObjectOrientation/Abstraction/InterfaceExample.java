package Phase3_ObjectOrientation.Abstraction;

/**
 * Abstraction - Part 2: INTERFACES
 * --------------------------------
 * An INTERFACE is a CONTRACT - a list of methods a class promises to provide.
 * Interfaces are pure abstraction: they describe WHAT a type can do without
 * forcing HOW it does it.
 *
 *      interface Comparable<T> {
 *          int compareTo(T other);
 *      }
 *      class Customer implements Comparable<Customer> {
 *          public int compareTo(Customer other) { ... }
 *      }
 *
 *
 * Modern Interface Members (Java 8 / 9)
 * -------------------------------------
 *  - ABSTRACT methods - implicit public, no body. The original purpose.
 *  - default methods  (Java 8)  - have a body; implementors inherit them.
 *  - static  methods  (Java 8)  - utility tied to the interface itself.
 *  - private methods  (Java 9)  - helper for default/static methods.
 *  - public static final constants (no other fields are allowed).
 *
 * (For a deeper dive into default/static/private interface methods see
 *  Basics/Methods/InterfaceMethods.java.)
 *
 *
 * Key Properties
 * --------------
 *  - A class can IMPLEMENT MULTIPLE interfaces (Java's answer to multiple
 *    inheritance of TYPE - not of state).
 *  - All members are implicitly PUBLIC unless declared private.
 *  - All fields are implicitly PUBLIC STATIC FINAL.
 *  - Interfaces cannot have INSTANCE fields and cannot define a constructor.
 *  - An interface variable can hold any concrete implementor:
 *
 *         List<String> xs = new ArrayList<>();   // List is an interface
 *
 *
 * Functional Interfaces
 * ---------------------
 * An interface with exactly ONE abstract method (SAM - Single Abstract
 * Method). It can be the target of a lambda expression or method reference.
 * Annotate with @FunctionalInterface so the compiler enforces the SAM rule.
 *
 *      @FunctionalInterface
 *      interface Calculator {
 *          int apply(int a, int b);
 *      }
 *      Calculator add = (a, b) -> a + b;
 *      add.apply(2, 3);    // -> 5
 *
 *
 * The Example Below
 * -----------------
 *   - Drawable           - interface with an abstract method + a default + a static helper
 *   - Resizable          - separate interface (demonstrates MULTIPLE implementation)
 *   - Calculator         - functional interface used with a lambda
 *   - Circle implements Drawable, Resizable
 *   - Rectangle implements Drawable (only)
 */

public class InterfaceExample {

    /** A behavioural contract. */
    interface Drawable {
        // Abstract - all implementors must provide it
        void draw();

        // default - inherited unless overridden
        default void drawTwice() {
            draw();
            draw();
        }

        // static - tied to the interface, callable as Drawable.brush(...)
        static String brush() { return "default-brush"; }
    }

    /** Another contract - a class can implement BOTH. */
    interface Resizable {
        void resize(double factor);
    }

    /** Functional interface - exactly one abstract method. */
    @FunctionalInterface
    interface Calculator {
        int apply(int a, int b);
    }

    // ============================================================
    // Implementations
    // ============================================================
    static class Circle implements Drawable, Resizable {
        private double radius;
        Circle(double radius) { this.radius = radius; }

        @Override
        public void draw() {
            System.out.println("Drawing a circle of radius " + radius);
        }

        @Override
        public void resize(double factor) {
            this.radius *= factor;
            System.out.println("Circle resized to radius " + radius);
        }
    }

    static class Rectangle implements Drawable {
        double w, h;
        Rectangle(double w, double h) { this.w = w; this.h = h; }

        @Override
        public void draw() {
            System.out.println("Drawing a " + w + "x" + h + " rectangle");
        }
    }

    public static void main(String[] args) {

        section("Polymorphism through an interface");
        Drawable[] items = { new Circle(2), new Rectangle(3, 4), new Circle(5) };
        for (Drawable d : items) {
            d.draw();
        }

        section("Default method inherited from the interface");
        items[0].drawTwice();        // uses the inherited default

        section("Static method on the interface");
        System.out.println("Drawable.brush() = " + Drawable.brush());

        section("Multiple interface implementation");
        Circle c = new Circle(1);
        c.draw();           // Drawable behaviour
        c.resize(2.5);      // Resizable behaviour

        // instanceof works for any interface the object implements
        System.out.println("c instanceof Drawable  = " + (c instanceof Drawable));
        System.out.println("c instanceof Resizable = " + (c instanceof Resizable));

        section("Functional interface + lambda");
        Calculator add = (a, b) -> a + b;
        Calculator mul = (a, b) -> a * b;
        System.out.println("add.apply(3, 4) = " + add.apply(3, 4));   // 7
        System.out.println("mul.apply(3, 4) = " + mul.apply(3, 4));   // 12

        // OUTPUT
        // (matches the inline comments above)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
