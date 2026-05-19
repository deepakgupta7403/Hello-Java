package OOPSConcepts.Polymorphism;

/**
 * Polymorphism in Java
 * --------------------
 * POLYMORPHISM means "many forms" - the ability of ONE name (a method or a
 * reference) to behave differently depending on context. Java offers TWO
 * kinds:
 *
 *  1. COMPILE-TIME POLYMORPHISM (a.k.a. Static / Early-binding)
 *     - Resolved at COMPILE time by the compiler based on argument types.
 *     - Implemented via METHOD OVERLOADING and OPERATOR OVERLOADING (only
 *       `+` for Strings - Java does not let you overload other operators).
 *
 *  2. RUNTIME POLYMORPHISM (a.k.a. Dynamic / Late-binding)
 *     - Resolved at RUNTIME based on the ACTUAL OBJECT TYPE.
 *     - Implemented via METHOD OVERRIDING.
 *
 *
 * Method Overloading - Rules
 * --------------------------
 *  Two methods CAN share a name if they DIFFER in:
 *      - number of parameters,
 *      - type of parameters,
 *      - order of parameter types.
 *  Return type ALONE is NOT enough.
 *
 *      int    add(int a, int b)       { ... }   // overload
 *      double add(double a, double b) { ... }   // overload
 *      int    add(int b, int a)       { ... }   // ERROR - same signature
 *
 *
 * Method Overriding - Rules
 * -------------------------
 *  A subclass redefines a method declared in its parent. The compiler enforces:
 *      - Same name and same parameter list.
 *      - Return type the same or a SUBTYPE (covariant return).
 *      - Throws clause: cannot add NEW broader checked exceptions.
 *      - Access modifier may be the SAME or MORE PERMISSIVE (cannot narrow).
 *      - Cannot override: private, static, or final methods (you'd be hiding,
 *        not overriding).
 *  Mark overrides with @Override - the compiler then verifies the override
 *  contract for you.
 *
 *
 * Dynamic Dispatch
 * ----------------
 * When you call a method on a REFERENCE, the JVM looks at the actual OBJECT
 * to decide which body to run. This is what makes polymorphism powerful.
 *
 *      Animal a = new Dog();
 *      a.makeSound();        // runs Dog.makeSound(), not Animal.makeSound()
 *
 *
 * What Dynamic Dispatch Does NOT Apply To
 * ---------------------------------------
 *  - STATIC methods    - dispatched at compile time by the declared type.
 *  - FIELDS            - same: resolved by the declared type.
 *  - PRIVATE methods   - cannot be overridden, so always the declaring class's.
 *  - FINAL methods     - cannot be overridden by design.
 *  - CONSTRUCTORS      - never overridden (they are not inherited).
 *
 *
 * Pattern Matching (Java 21) and Polymorphism
 * -------------------------------------------
 * Java 21 lets you write SWITCH-BASED polymorphism via pattern matching:
 *
 *      String describe(Object o) {
 *          return switch (o) {
 *              case Integer i  -> "int " + i;
 *              case String  s  -> "str " + s;
 *              case null       -> "nothing";
 *              default         -> o.getClass().getSimpleName();
 *          };
 *      }
 */

public class Polymorphism {

    // ============================================================
    // 1) COMPILE-TIME POLYMORPHISM - method overloading
    // ============================================================
    static int add(int a, int b)             { return a + b; }
    static double add(double a, double b)    { return a + b; }
    static String add(String a, String b)    { return a + b; }
    static int add(int a, int b, int c)      { return a + b + c; }

    // ============================================================
    // 2) RUNTIME POLYMORPHISM - method overriding + dynamic dispatch
    // ============================================================
    static class Animal {
        public String makeSound() { return "...some animal noise..."; }
        public String describe()  {                       // template using polymorphism
            return getClass().getSimpleName() + " says: " + makeSound();
        }
    }

    static class Dog extends Animal {
        @Override public String makeSound() { return "Woof"; }
    }

    static class Cat extends Animal {
        @Override public String makeSound() { return "Meow"; }
    }

    static class Cow extends Animal {
        @Override public String makeSound() { return "Moo"; }
    }

    // ============================================================
    // 3) Covariant return type - a legal narrowing of the return type
    // ============================================================
    static class Box {
        public Object get() { return "anything"; }
    }

    static class StringBox extends Box {
        @Override
        public String get() { return "specific string"; }   // String is-a Object
    }

    // ============================================================
    // 4) Static methods are NOT polymorphic - they are HIDDEN
    // ============================================================
    static class Parent {
        public  static String staticMsg()  { return "Parent.staticMsg"; }
        public         String inst()       { return "Parent.inst"; }
    }
    static class Child extends Parent {
        public  static String staticMsg()  { return "Child.staticMsg"; }
        @Override public String inst()     { return "Child.inst"; }
    }

    public static void main(String[] args) {

        section("Overloading - compile-time polymorphism");
        System.out.println("add(2, 3)         = " + add(2, 3));
        System.out.println("add(2.5, 3.0)     = " + add(2.5, 3.0));
        System.out.println("add(\"Hi \", \"Java\") = " + add("Hi ", "Java"));
        System.out.println("add(1, 2, 3)      = " + add(1, 2, 3));

        section("Overriding - runtime polymorphism");
        Animal[] zoo = { new Dog(), new Cat(), new Cow(), new Animal() };
        for (Animal a : zoo) {
            // Same call site - different actual implementation per object
            System.out.println(a.describe());
        }

        section("Covariant return type");
        Box b = new StringBox();
        Object got = b.get();         // declared Object
        System.out.println("Box.get()        -> " + got + " (actual type: "
                                                  + got.getClass().getSimpleName() + ")");

        StringBox sb = new StringBox();
        String s = sb.get();          // declared String thanks to covariance
        System.out.println("StringBox.get()  -> " + s);

        section("Static methods are HIDDEN, not overridden");
        Parent p = new Child();
        System.out.println("p.staticMsg()    -> " + Parent.staticMsg());   // resolved by declared type
        System.out.println("p.inst()         -> " + p.inst());              // resolved by actual type

        // OUTPUT
        // ====== Overloading - compile-time polymorphism ======
        // add(2, 3)         = 5
        // add(2.5, 3.0)     = 5.5
        // add("Hi ", "Java") = Hi Java
        // add(1, 2, 3)      = 6
        // ====== Overriding - runtime polymorphism ======
        // Dog says: Woof
        // Cat says: Meow
        // Cow says: Moo
        // Animal says: ...some animal noise...
        // ====== Covariant return type ======
        // Box.get()        -> specific string (actual type: String)
        // StringBox.get()  -> specific string
        // ====== Static methods are HIDDEN, not overridden ======
        // p.staticMsg()    -> Parent.staticMsg
        // p.inst()         -> Child.inst
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
