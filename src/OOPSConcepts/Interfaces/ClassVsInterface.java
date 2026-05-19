package OOPSConcepts.Interfaces;

/**
 * Class vs Interface - Practical Comparison
 * -----------------------------------------
 * Both classes and interfaces describe a TYPE in Java. The difference is in
 * what each can carry and how Java's inheritance model treats them.
 *
 *
 * Side-by-Side
 * ------------
 *
 *  Aspect              | class                         | interface
 *  --------------------+-------------------------------+-------------------------------
 *  Keyword             | class                         | interface
 *  Instantiable?       | YES (unless abstract)         | NO  (cannot be `new`-ed)
 *  Single vs Multiple  | single inheritance:           | multiple type inheritance:
 *  inheritance         |   class A extends B           |   class A implements X, Y, Z
 *  Methods             | concrete + abstract           | abstract + default + static
 *                      |                               |   + private (Java 9+)
 *  Default method      | concrete method               | `default` keyword
 *  visibility          | package-private               | implicitly public
 *  Instance fields     | any                           | NONE (no per-object state)
 *  Constants           | any                           | implicitly public static final
 *  Constructors        | yes                           | NO
 *  Access modifiers    | public / protected / pkg /    | members may be public (default)
 *  on members          |   private                     |   or private (Java 9+)
 *  Used to express     | "what an object IS"           | "what an object CAN DO"
 *
 *
 * Picking Between Them
 * --------------------
 *   - Need SHARED STATE across implementations? -> abstract CLASS.
 *   - Want a CONTRACT a class can adopt alongside its existing parent? -> INTERFACE.
 *   - Need to target LAMBDAS? -> functional INTERFACE (single abstract method).
 *   - Want a CLOSED set of subtypes you control? -> sealed CLASS or sealed INTERFACE.
 *
 *
 * Real-World JDK Examples
 * -----------------------
 *   List<E>            - INTERFACE  (the contract)
 *   AbstractList<E>    - CLASS      (a skeleton implementation - shared code)
 *   ArrayList<E>       - CLASS extends AbstractList implements List
 *
 *   The JDK consistently pairs interfaces (contract) with abstract classes
 *   (shared code) - a pattern worth copying in your own designs.
 *
 *
 * See Also
 * --------
 *   - OOPSConcepts/Abstraction/AbstractClassVsInterface.java  - longer discussion
 *   - InterfaceIntro.java                                     - interface members tour
 *   - FunctionalInterfaceDemo.java                            - SAM + lambdas
 */

public class ClassVsInterface {

    // ============================================================
    // SAME contract expressed two ways:
    //   1) abstract CLASS (carries shared state)
    //   2) INTERFACE      (pure contract)
    // ============================================================

    // 1) Abstract class - shared state + a template method
    static abstract class VehicleClass {
        protected final int wheels;                 // shared state
        protected VehicleClass(int wheels) { this.wheels = wheels; }

        public abstract String name();              // hook

        public void describe() {                    // template using the hook
            System.out.printf("%s with %d wheels%n", name(), wheels);
        }
    }

    static class CarA extends VehicleClass {
        public CarA() { super(4); }
        @Override public String name() { return "CarA"; }
    }

    // 2) Interface - no state, just contracts (+ default helper)
    interface VehicleInterface {
        int    wheels();
        String name();

        default void describe() {
            System.out.printf("%s with %d wheels%n", name(), wheels());
        }
    }

    static class CarB implements VehicleInterface {
        @Override public int    wheels() { return 4; }
        @Override public String name()   { return "CarB"; }
    }

    // ============================================================
    // ONE class can extend AT MOST ONE class but many interfaces
    // ============================================================
    interface Insurable  { default boolean insured()  { return true;  } }
    interface Serviceable{ default void    service()  { System.out.println("serviced"); } }

    /** Truck extends VehicleClass AND implements two extra contracts. */
    static class Truck extends VehicleClass implements Insurable, Serviceable {
        public Truck() { super(6); }
        @Override public String name() { return "Truck"; }
    }

    public static void main(String[] args) {

        section("Abstract class style");
        VehicleClass a = new CarA();
        a.describe();
        System.out.println("a.wheels (inherited state) = " + a.wheels);

        section("Interface style");
        VehicleInterface b = new CarB();
        b.describe();
        System.out.println("b.wheels()                 = " + b.wheels());

        section("Mixing both - extend ONE class, implement MANY interfaces");
        Truck t = new Truck();
        t.describe();
        System.out.println("t.insured() = " + t.insured());
        t.service();

        // The same Truck can be referenced through any of its types
        VehicleClass viaClass    = t;
        Insurable    viaIns      = t;
        Serviceable  viaSvc      = t;
        System.out.println("\nAll three references point to the same object: "
                + (viaClass == viaIns && viaIns == viaSvc));

        // OUTPUT
        // ====== Abstract class style ======
        // CarA with 4 wheels
        // a.wheels (inherited state) = 4
        // ====== Interface style ======
        // CarB with 4 wheels
        // b.wheels()                 = 4
        // ====== Mixing both - extend ONE class, implement MANY interfaces ======
        // Truck with 6 wheels
        // t.insured() = true
        // serviced
        //
        // All three references point to the same object: true
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
