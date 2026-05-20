package Phase3_ObjectOrientation.Abstraction;

/**
 * Abstract Class vs Interface
 * ---------------------------
 * Both achieve ABSTRACTION but they have different trade-offs. Picking the
 * right one is one of the most common design decisions in Java.
 *
 *
 * Side-by-Side Comparison
 * -----------------------
 *
 *  Aspect            | Abstract Class                | Interface
 *  ------------------+-------------------------------+--------------------------------
 *  Keyword           | abstract class                | interface
 *  Inheritance       | single inheritance only       | multiple inheritance of TYPE
 *                    | (class extends ONE class)     | (class implements MANY)
 *  Members allowed   | abstract + concrete methods,  | abstract + default + static +
 *                    | instance fields, all          | private methods, ONLY
 *                    | access modifiers, constructors| public static final fields,
 *                    |                               | no constructors
 *  Method default    | package-private               | public
 *  visibility        |                               |
 *  Default method    | concrete method               | `default` keyword (Java 8+)
 *  body              |                               |
 *  Private helpers   | yes (private methods)         | yes since Java 9
 *  State (fields)    | yes - any kind                | only constants
 *  Constructors      | yes (called via super(...))   | no - cannot define one
 *  Instantiation     | NO (cannot be `new`-ed)       | NO (cannot be `new`-ed)
 *  Used for          | shared CODE + partial state   | shared TYPE / CONTRACT
 *  Java keyword      | extends                       | implements
 *
 *
 * When To Use Abstract Class
 * --------------------------
 *  - Subclasses share STATE (instance fields).
 *  - You want a TEMPLATE method that calls hooks subclasses fill in.
 *  - You want to control how subclasses are constructed (non-public ctors).
 *  - Common code is more important than multi-typing.
 *
 *
 * When To Use Interface
 * ---------------------
 *  - You want a behavioural CONTRACT without imposing a class hierarchy.
 *  - You need a class to participate in multiple "roles" (e.g.  Comparable +
 *    Serializable + AutoCloseable).
 *  - You want a target type for lambdas (functional interfaces).
 *  - The behaviour is orthogonal to the inheritance chain.
 *
 *
 * Hybrid Approach
 * ---------------
 * It is very common to define an INTERFACE for the contract and provide a
 * SKELETON ABSTRACT CLASS with common code that implementors can extend.
 * The JDK does this all over the place:
 *
 *      List<E>            <- interface (contract)
 *      AbstractList<E>    <- abstract class (skeleton)
 *      ArrayList<E>       <- concrete class extends AbstractList implements List
 *
 *
 * The Example Below
 * -----------------
 * We model a "Vehicle" using BOTH abstractions side by side so you can see
 * the trade-off:
 *   - Vehicle           - an abstract class with state (numWheels) + a template method
 *   - Drivable          - an interface (pure contract)
 *   - Car               - extends Vehicle AND implements Drivable
 *   - Boat              - extends Vehicle but does NOT implement Drivable
 */

public class AbstractClassVsInterface {

    // ============================================================
    // Abstract CLASS - carries state + template method
    // ============================================================
    static abstract class Vehicle {
        protected final int numWheels;            // shared STATE
        protected Vehicle(int numWheels) {        // controlled construction
            this.numWheels = numWheels;
        }

        // Template method - shared algorithm using subclass hooks
        public final void describe() {
            System.out.print("This " + name() + " has " + numWheels + " wheel(s) and ");
            move();
        }

        protected abstract String name();         // hook
        protected abstract void   move();         // hook
    }

    // ============================================================
    // Interface - pure contract, orthogonal to the Vehicle hierarchy
    // ============================================================
    interface Drivable {
        void turnLeft();
        void turnRight();
        default void uTurn() {                    // default body for free
            turnLeft();
            turnLeft();
        }
    }

    // ============================================================
    // Concrete classes
    // ============================================================
    static class Car extends Vehicle implements Drivable {
        public Car() { super(4); }
        @Override protected String name()        { return "Car"; }
        @Override protected void   move()        { System.out.println("rolls on the road."); }
        @Override public    void   turnLeft()    { System.out.println("Car turns left."); }
        @Override public    void   turnRight()   { System.out.println("Car turns right."); }
    }

    static class Boat extends Vehicle /* does NOT implement Drivable */ {
        public Boat() { super(0); }                 // no wheels
        @Override protected String name()        { return "Boat"; }
        @Override protected void   move()        { System.out.println("floats on water."); }
    }

    public static void main(String[] args) {

        section("Abstract class behaviour");
        Vehicle car  = new Car();
        Vehicle boat = new Boat();
        car.describe();
        boat.describe();

        section("Interface behaviour - only on Drivables");
        Drivable d = new Car();          // up-cast to the interface
        d.turnLeft();
        d.turnRight();
        d.uTurn();                       // inherited default method

        // Boat does NOT implement Drivable - cannot do this:
        // Drivable d2 = new Boat();   // compile ERROR

        section("Polymorphism through the abstract class");
        Vehicle[] fleet = { new Car(), new Boat(), new Car() };
        for (Vehicle v : fleet) v.describe();

        section("Polymorphism through the interface - the subset that drives");
        for (Vehicle v : fleet) {
            if (v instanceof Drivable drv) {
                drv.uTurn();
            }
        }

        // OUTPUT
        // ====== Abstract class behaviour ======
        // This Car has 4 wheel(s) and rolls on the road.
        // This Boat has 0 wheel(s) and floats on water.
        // ====== Interface behaviour - only on Drivables ======
        // Car turns left.
        // Car turns right.
        // Car turns left.
        // Car turns left.
        // ====== Polymorphism through the abstract class ======
        // This Car has 4 wheel(s) and rolls on the road.
        // This Boat has 0 wheel(s) and floats on water.
        // This Car has 4 wheel(s) and rolls on the road.
        // ====== Polymorphism through the interface - the subset that drives ======
        // Car turns left.
        // Car turns left.
        // Car turns left.
        // Car turns left.
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
