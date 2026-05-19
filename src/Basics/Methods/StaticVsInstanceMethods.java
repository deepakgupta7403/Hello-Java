package Basics.Methods;

/**
 * Static Methods vs Instance Methods
 * ----------------------------------
 * A method in Java is one of two flavours:
 *
 *      STATIC METHOD                  | INSTANCE METHOD
 *      -------------------------------+-----------------------------------------
 *      Declared with the `static`     | No `static` keyword.
 *      keyword.                       |
 *      Belongs to the CLASS itself.   | Belongs to each OBJECT (instance).
 *      Called on the class:           | Called on an object:
 *          Math.max(1, 2);            |     obj.toString();
 *      Has NO `this` reference.       | Has access to `this`.
 *      Cannot directly access         | Can access static AND instance members.
 *      instance fields/methods of     |
 *      the same class (you must use   |
 *      an explicit object).           |
 *      One copy per CLASS.            | One copy of state per OBJECT.
 *      Can be called BEFORE any       | Requires an object to exist.
 *      object is created.             |
 *
 *
 * When To Use a Static Method
 * ---------------------------
 *  - Pure utility/helper functions that depend ONLY on their arguments
 *    (Math.sqrt, Collections.sort, Integer.parseInt).
 *  - Factory methods that build instances (Optional.of, List.of).
 *  - The application entry point - `public static void main(String[] args)`.
 *
 *
 * When To Use an Instance Method
 * ------------------------------
 *  - Behaviour that depends on or modifies the OBJECT'S state.
 *  - When you want polymorphism (instance methods can be OVERRIDDEN in
 *    subclasses; static methods cannot - they can only be HIDDEN).
 *
 *
 * Static Methods Are NOT Polymorphic
 * ----------------------------------
 * If a subclass declares a static method with the same signature as the
 * superclass, it HIDES the parent's method (resolved at compile time by the
 * declared type of the reference). Instance methods, in contrast, are
 * dispatched at runtime based on the actual object type.
 *
 *
 * Common Beginner Mistake
 * -----------------------
 * Calling a non-static field/method directly from main():
 *
 *      class Demo {
 *          int x = 10;
 *          public static void main(String[] args) {
 *              System.out.println(x);     // ERROR - x is instance, main is static
 *          }
 *      }
 *
 * Fix: create an object first  ->  new Demo().x;
 */

public class StaticVsInstanceMethods {

    // --- Static state shared by ALL instances ---
    static int totalInstances = 0;

    // --- Instance state - one per object ---
    int id;
    String name;

    /** Instance constructor - increments the static counter and stores per-object state. */
    public StaticVsInstanceMethods(String name) {
        this.id = ++totalInstances;          // touches both static and instance state
        this.name = name;
    }

    // --- Instance method - uses `this` and the object's state ---
    void describe() {
        System.out.println("Instance #" + id + " name=" + name);
    }

    // --- Static method - utility, no `this`, no instance fields directly ---
    static int square(int n) {
        return n * n;
    }

    // --- Static "factory" method - constructs and returns an instance ---
    static StaticVsInstanceMethods anonymous() {
        return new StaticVsInstanceMethods("(anonymous)");
    }

    /** Demonstrating static hiding vs instance overriding. */
    static class Parent {
        static  String staticMsg()   { return "Parent.staticMsg"; }
        public  String instanceMsg() { return "Parent.instanceMsg"; }
    }
    static class Child extends Parent {
        static  String staticMsg()   { return "Child.staticMsg"; }
        @Override public String instanceMsg() { return "Child.instanceMsg"; }
    }

    public static void main(String[] args) {

        // --- 1) Calling a static method - no object required ---
        System.out.println("square(5)        = " + square(5));      // 25

        // --- 2) Creating instances + calling instance methods ---
        StaticVsInstanceMethods a = new StaticVsInstanceMethods("Alice");
        StaticVsInstanceMethods b = new StaticVsInstanceMethods("Bob");
        a.describe();
        b.describe();
        System.out.println("totalInstances    = " + totalInstances);

        // --- 3) Static factory method ---
        StaticVsInstanceMethods anon = anonymous();
        anon.describe();

        // --- 4) Static methods are HIDDEN, not overridden ---
        Parent p = new Child();
        // For STATIC: dispatched by the declared type of the reference -> Parent
        System.out.println("p.staticMsg()   -> " + Parent.staticMsg()); // Parent.staticMsg
        // For INSTANCE: dispatched at runtime by the actual object type -> Child
        System.out.println("p.instanceMsg() -> " + p.instanceMsg());    // Child.instanceMsg

        // --- 5) Static methods can be called on a reference, but it is misleading ---
        // The compiler resolves p.staticMsg() at compile time based on the
        // declared type 'Parent', NOT the actual object type 'Child'.
        // Modern IDEs warn about this - prefer ClassName.staticMethod().
        System.out.println("p.staticMsg() via ref -> " + p.staticMsg()); // still Parent.staticMsg

        // OUTPUT
        // square(5)        = 25
        // Instance #1 name=Alice
        // Instance #2 name=Bob
        // totalInstances    = 3
        // Instance #3 name=(anonymous)
        // p.staticMsg()   -> Parent.staticMsg
        // p.instanceMsg() -> Child.instanceMsg
        // p.staticMsg() via ref -> Parent.staticMsg
    }
}
