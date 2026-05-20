package Phase3_ObjectOrientation.Introduction;

/**
 * Object-Oriented Programming in Java - Introduction
 * --------------------------------------------------
 * Java is fundamentally OBJECT-ORIENTED: every line of meaningful code lives
 * inside a class, and the program runs by sending messages (method calls)
 * between objects. The OOP paradigm models a system as a network of
 * cooperating objects, each holding STATE (fields) and exposing BEHAVIOUR
 * (methods).
 *
 *
 * The Four Pillars of OOP
 * -----------------------
 *
 *  1. ABSTRACTION
 *     Hiding implementation details and exposing only the relevant interface.
 *     "What does it do" without "how does it do it".
 *
 *         abstract class Shape {
 *             abstract double area();
 *         }
 *
 *
 *  2. ENCAPSULATION
 *     Bundling data + the code that operates on the data inside a single
 *     unit (a class) and controlling access via modifiers.
 *
 *         class Account {
 *             private double balance;          // private state
 *             public  void deposit(double v){  // controlled access
 *                 if (v > 0) balance += v;
 *             }
 *         }
 *
 *
 *  3. INHERITANCE
 *     A subclass acquires fields and methods from a parent class and can
 *     add or override behaviour. Reuses code, expresses an "is-a" relationship.
 *
 *         class Animal { void eat() {...} }
 *         class Dog extends Animal { void bark() {...} }    // Dog IS-A Animal
 *
 *
 *  4. POLYMORPHISM
 *     One name, many forms. Two flavours in Java:
 *       - Compile-time (overloading) - same method name, different signatures.
 *       - Runtime (overriding) - subclass redefines a parent method; the call
 *         is dispatched based on the ACTUAL OBJECT TYPE at runtime.
 *
 *         Animal a = new Dog();
 *         a.makeSound();        // runs Dog's version
 *
 *
 * Why OOP?
 * --------
 *  - MODULAR    - classes are reusable, testable units.
 *  - SCALABLE   - inheritance + polymorphism let you grow a system without
 *                  rewriting existing code.
 *  - MAINTAIN-  - encapsulation isolates change behind interfaces.
 *  - REAL-WORLD - models domains the way humans think about them ("a
 *                  customer has accounts", "a car is-a vehicle").
 *
 *
 * OOP Building Blocks in Java
 * ---------------------------
 *      class            blueprint for objects
 *      object           a runtime instance of a class
 *      field            a variable owned by an object (state)
 *      method           a function owned by a class (behaviour)
 *      constructor      a special method that initialises a new object
 *      package          a namespace grouping related classes
 *      interface        a contract listing methods a class promises to provide
 *      abstract class   a partial blueprint - mixes implementation and contract
 *      record           a concise immutable data class (Java 16+)
 *      enum             a fixed set of named instances
 *      sealed class     a class with an EXPLICIT list of allowed subclasses
 *                       (Java 17+)
 *
 *
 * What This Folder Covers
 * -----------------------
 *  - Introduction         (this file)
 *  - Classes and Objects  (../ClassesAndObject/...)
 *  - Constructors         (../Constructors/...)
 *  - Object class         (../ObjectClass/...)
 *  - Abstraction          (../Abstraction/...)
 *  - Encapsulation        (../Encapsulation/...)
 *  - Inheritance          (../Inheritance/...)
 *  - Polymorphism         (../Polymorphism/...)
 *  - Packages             (../Packages/...)
 *  - Sealed Classes       (../SealedClasses/...)
 *  - Banking Project      (../BankingApp/...)
 */

public class OopIntroduction {

    // A tiny worked example showing the four pillars in one place.

    /** ENCAPSULATION + ABSTRACTION - a Shape contract with hidden internals. */
    static abstract class Shape {
        private final String name;                       // hidden state
        protected Shape(String name) { this.name = name; }
        public String name() { return name; }
        public abstract double area();                   // contract only
    }

    /** INHERITANCE + POLYMORPHISM - Circle is-a Shape with its own area. */
    static class Circle extends Shape {
        private final double radius;
        public Circle(double radius) { super("Circle"); this.radius = radius; }
        @Override public double area() { return Math.PI * radius * radius; }
    }

    /** INHERITANCE + POLYMORPHISM - Rectangle is-a Shape with its own area. */
    static class Rectangle extends Shape {
        private final double w, h;
        public Rectangle(double w, double h) { super("Rectangle"); this.w = w; this.h = h; }
        @Override public double area() { return w * h; }
    }

    public static void main(String[] args) {

        // POLYMORPHISM in action - one Shape[] holds different concrete subtypes.
        Shape[] shapes = {
                new Circle(2),
                new Rectangle(3, 4),
                new Circle(5)
        };

        double totalArea = 0;
        for (Shape s : shapes) {
            // The same call - s.area() - runs different code per object.
            System.out.printf("%-10s area = %.2f%n", s.name(), s.area());
            totalArea += s.area();
        }
        System.out.printf("Total area = %.2f%n", totalArea);

        // OUTPUT
        // Circle     area = 12.57
        // Rectangle  area = 12.00
        // Circle     area = 78.54
        // Total area = 103.10
    }
}
