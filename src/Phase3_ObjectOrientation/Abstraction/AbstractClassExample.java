package Phase3_ObjectOrientation.Abstraction;

/**
 * Abstraction - Part 1: ABSTRACT CLASSES
 * --------------------------------------
 * ABSTRACTION is the OOP pillar that hides implementation details and exposes
 * only the behaviour a caller needs. In Java you achieve abstraction with two
 * mechanisms:
 *
 *      1. abstract classes  (this file)
 *      2. interfaces        (see InterfaceExample.java)
 *
 *
 * What is an abstract class?
 * --------------------------
 * A class declared with the `abstract` keyword. It MAY contain:
 *   - concrete fields and methods (with bodies)
 *   - abstract methods (no body, must end with a semicolon)
 *   - constructors (called by subclass constructors via super(...))
 *
 * Properties:
 *   - You CANNOT instantiate an abstract class directly:
 *         new Shape();        // ERROR
 *   - A subclass either provides bodies for ALL inherited abstract methods,
 *     or it must ALSO be declared abstract.
 *   - An abstract class is essentially a "partial class" - you finish it.
 *
 *
 * When to use an abstract class
 * -----------------------------
 *   - You have a TEMPLATE with some shared behaviour AND some pieces left
 *     for the subclass to fill in (Template Method pattern).
 *   - You want to share STATE (fields) across all subclasses - interfaces
 *     can have constants but not regular fields.
 *   - You want non-public constructors (to control how subclasses construct).
 *
 *
 * Template Method Pattern
 * -----------------------
 * The classic use case. A concrete method in the abstract class defines the
 * overall ALGORITHM and calls abstract "hooks" that subclasses override:
 *
 *      abstract class Game {
 *          public final void play() {            // template method - final
 *              start();
 *              while (!isOver()) takeTurn();
 *              finish();
 *          }
 *          protected abstract void start();
 *          protected abstract void takeTurn();
 *          protected abstract boolean isOver();
 *          protected abstract void finish();
 *      }
 *
 *
 * The Example Below
 * -----------------
 * Shape is an abstract class with:
 *   - a shared field `name`
 *   - a concrete `describe()` that uses the abstract methods
 *   - two abstract methods area() and perimeter()
 * Circle and Rectangle complete the contract.
 */

public class AbstractClassExample {

    /** Abstract base - cannot be instantiated directly. */
    static abstract class Shape {
        private final String name;

        protected Shape(String name) {
            this.name = name;
        }

        // Concrete - shared by all subclasses
        public final String getName() {
            return name;
        }

        // Abstract - each subclass MUST provide an implementation
        public abstract double area();
        public abstract double perimeter();

        // Template method - uses abstract hooks to do shared work
        public void describe() {
            System.out.printf("%-10s  area=%.2f  perimeter=%.2f%n",
                              name, area(), perimeter());
        }
    }

    /** Concrete subclass - completes the abstract contract. */
    static class Circle extends Shape {
        private final double r;
        public Circle(double r) { super("Circle"); this.r = r; }
        @Override public double area()      { return Math.PI * r * r; }
        @Override public double perimeter() { return 2 * Math.PI * r; }
    }

    /** Concrete subclass - completes the abstract contract. */
    static class Rectangle extends Shape {
        private final double w, h;
        public Rectangle(double w, double h) { super("Rectangle"); this.w = w; this.h = h; }
        @Override public double area()      { return w * h; }
        @Override public double perimeter() { return 2 * (w + h); }
    }

    /**
     * A subclass that does NOT override all abstract methods would itself be
     * abstract. Uncomment to see the compile error:
     *
     *   static class Triangle extends Shape {
     *       public Triangle() { super("Triangle"); }
     *       // missing area() and perimeter()  -> compile error
     *   }
     */

    public static void main(String[] args) {

        // 1) Cannot instantiate the abstract base directly
        // Shape impossible = new Shape("foo");   // ERROR

        // 2) Use concrete subclasses through the abstract reference
        Shape[] shapes = {
                new Circle(2.0),
                new Rectangle(3.0, 4.0),
                new Circle(5.0)
        };

        // 3) The describe() template method calls into each subclass's
        //    overridden area() and perimeter() - runtime polymorphism.
        for (Shape s : shapes) {
            s.describe();
        }

        // 4) Type identity - getName() comes from the abstract class,
        //    area()/perimeter() come from the concrete subclass.
        System.out.println("\nReflection of shapes[1]:");
        System.out.println("getName()   = " + shapes[1].getName());
        System.out.println("getClass()  = " + shapes[1].getClass().getSimpleName());
        System.out.println("instanceof Shape    = " + (shapes[1] instanceof Shape));
        System.out.println("instanceof Rectangle= " + (shapes[1] instanceof Rectangle));

        // OUTPUT
        // Circle      area=12.57  perimeter=12.57
        // Rectangle   area=12.00  perimeter=14.00
        // Circle      area=78.54  perimeter=31.42
        //
        // Reflection of shapes[1]:
        // getName()   = Rectangle
        // getClass()  = Rectangle
        // instanceof Shape    = true
        // instanceof Rectangle= true
    }
}
