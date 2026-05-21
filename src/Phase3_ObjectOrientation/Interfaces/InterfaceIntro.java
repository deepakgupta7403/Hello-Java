package Phase3_ObjectOrientation.Interfaces;

/**
 * Interfaces in Java - Introduction
 * ---------------------------------
 * An INTERFACE is a CONTRACT - a list of methods (and optional default code)
 * that any implementing class promises to provide. Interfaces give Java its
 * primary form of ABSTRACTION and let one class fulfil MULTIPLE TYPES, which
 * is Java's controlled answer to multiple inheritance.
 * <p>
 *
 *      interface Comparable<T> {
 *          int compareTo(T other);
 *      }
 * <p>
 *
 *      class Customer implements Comparable<Customer> {
 *          public int compareTo(Customer other) { ... }
 *      }
 * <p>
 *
 * Why Use Interfaces?
 * -------------------
 *   - ABSTRACTION  - separate the "what" from the "how".
 *   - DECOUPLING   - code against interfaces, not classes; swap implementations.
 *   - POLYMORPHISM - one type, many concrete behaviours.
 *   - MULTI-TYPING - a class can implement many interfaces simultaneously
 *                    (Comparable + Serializable + AutoCloseable, etc.).
 *   - TESTABILITY  - pass a stub/mock in tests by implementing the interface.
 * <p>
 *
 * Members an Interface May Contain
 * --------------------------------
 * <p>
 *
 *   1. ABSTRACT methods         implicit `public abstract`, no body.
 *      void doWork();
 * <p>
 *
 *   2. DEFAULT methods (Java 8) have a body; inherited unless overridden.
 *      default void run() { ... }
 * <p>
 *
 *   3. STATIC methods  (Java 8) belong to the interface itself.
 *      static MyInterface create() { ... }
 * <p>
 *
 *   4. PRIVATE methods (Java 9) helpers for default/static methods.
 *      private boolean valid(...) { ... }
 * <p>
 *
 *   5. CONSTANTS                 implicit `public static final`.
 *      int MAX = 100;
 * <p>
 *
 *   6. NESTED TYPES              static by default - any class/interface/enum/record
 *      interface Builder { ... }
 * <p>
 *
 *   Interfaces canNOT contain:
 *      - instance fields (no per-object state)
 *      - constructors
 *      - protected / package-private methods (default-method modifier is implicit public)
 * <p>
 *
 * Implementing an Interface
 * -------------------------
 *      class Customer implements Comparable<Customer>, Serializable { ... }
 * <p>
 *
 * The class must provide ALL abstract methods of every interface it implements,
 * or it must itself be declared `abstract`.
 * <p>
 *
 * Interfaces in the Type Hierarchy
 * --------------------------------
 *   - A class can extend at most ONE class but implement MANY interfaces.
 *   - An interface can extend MANY other interfaces ("interface inheritance").
 *   - You can declare variables of interface type to hold any implementor:
 * <p>
 *
 *         Comparable<Integer> c = 5;        // Integer implements Comparable<Integer>
 * <p>
 *
 * Java Version Timeline
 * ---------------------
 *   Java 1.0  - interfaces with abstract methods only.
 *   Java 5    - generic interfaces.
 *   Java 8    - default + static methods, functional interfaces, lambdas.
 *   Java 9    - private methods inside interfaces.
 *   Java 17   - sealed interfaces (permits-clause).
 *   Java 21   - sealed interfaces + pattern matching switch make exhaustive
 *               dispatch ergonomic.
 * <p>
 *
 * This file is a guided tour. For more depth see:
 *   - FunctionalInterfaceDemo.java  - SAM types + lambdas + java.util.function
 *   - NestedInterface.java          - interfaces nested in classes/interfaces
 *   - MarkerInterface.java          - empty "tag" interfaces
 *   - ClassVsInterface.java         - when to pick which
 *   - SealedInterfaceDemo.java      - permits and exhaustive switch (Java 17+)
 */

public class InterfaceIntro {

    // ============================================================
    // 1) A simple abstract-method interface
    // ============================================================
    interface Shape {
        double area();
        double perimeter();

        // 2) DEFAULT method - inherited by all implementors
        default void describe() {
            System.out.printf("%s : area=%.2f, perimeter=%.2f%n",
                              getClass().getSimpleName(), area(), perimeter());
        }

        // 3) STATIC method - utility tied to the interface
        static Shape unitCircle() { return new Circle(1.0); }

        // 4) PRIVATE method - reusable helper inside the interface (Java 9+)
        private static double square(double x) { return x * x; }

        // 5) CONSTANT - implicitly public static final
        double PI = Math.PI;

        // Default method that uses the private helper:
        default double areaPlusPerimeter() { return area() + perimeter(); }
        // (private helper is intentionally not called here - kept simple.)
    }

    // A class can EXTEND an interface conceptually via `implements`.
    static class Circle implements Shape {
        private final double r;
        Circle(double r) { this.r = r; }

        @Override public double area()      { return Shape.PI * r * r; }
        @Override public double perimeter() { return 2 * Shape.PI * r; }
    }

    static class Rectangle implements Shape {
        private final double w, h;
        Rectangle(double w, double h) { this.w = w; this.h = h; }

        @Override public double area()      { return w * h; }
        @Override public double perimeter() { return 2 * (w + h); }

        // Default `describe` is overridable
        @Override public void describe() {
            System.out.println("Rectangle of " + w + " x " + h);
        }
    }

    // ============================================================
    // 6) A class can implement MULTIPLE interfaces
    // ============================================================
    interface Resizable {
        void resize(double factor);
    }

    static class ResizableCircle implements Shape, Resizable {
        private double r;
        ResizableCircle(double r) { this.r = r; }
        @Override public double area()      { return Shape.PI * r * r; }
        @Override public double perimeter() { return 2 * Shape.PI * r; }
        @Override public void   resize(double f) { this.r *= f; }
    }

    // ============================================================
    // 7) Interface inheritance (an interface extends another interface)
    // ============================================================
    interface Drawable {
        void draw();
    }
    interface ColoredDrawable extends Drawable {
        String color();
        default void drawColored() {
            System.out.print("(" + color() + ") ");
            draw();
        }
    }

    static class Dot implements ColoredDrawable {
        @Override public void   draw()  { System.out.println("."); }
        @Override public String color() { return "red"; }
    }

    public static void main(String[] args) {

        section("Polymorphism through an interface reference");
        Shape[] shapes = {
                new Circle(2.0),
                new Rectangle(3.0, 4.0),
                Shape.unitCircle()        // static factory on the interface
        };
        for (Shape s : shapes) s.describe();

        section("Default methods - inherited and overrideable");
        new Circle(1.0).describe();       // inherits the default
        new Rectangle(2, 3).describe();   // overrides the default

        section("Multi-interface implementation");
        ResizableCircle rc = new ResizableCircle(5);
        rc.describe();
        rc.resize(0.5);
        rc.describe();
        System.out.println("rc instanceof Shape    = " + (rc instanceof Shape));
        System.out.println("rc instanceof Resizable= " + (rc instanceof Resizable));

        section("Interface inheritance");
        Dot d = new Dot();
        d.drawColored();                   // method inherited from ColoredDrawable

        // OUTPUT (representative)
        // ====== Polymorphism through an interface reference ======
        // Circle : area=12.57, perimeter=12.57
        // Rectangle of 3.0 x 4.0
        // Circle : area=3.14, perimeter=6.28
        // ====== Default methods - inherited and overrideable ======
        // Circle : area=3.14, perimeter=6.28
        // Rectangle of 2.0 x 3.0
        // ====== Multi-interface implementation ======
        // ResizableCircle : area=78.54, perimeter=31.42
        // ResizableCircle : area=19.63, perimeter=15.71
        // rc instanceof Shape    = true
        // rc instanceof Resizable= true
        // ====== Interface inheritance ======
        // (red) .
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
