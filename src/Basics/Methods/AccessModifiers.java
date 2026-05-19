package Basics.Methods;

/**
 * Access Modifiers in Java
 * ------------------------
 * Access modifiers control the VISIBILITY of classes, methods, fields, and
 * constructors. Java has FOUR access levels:
 *
 *      Modifier      | Same class | Same pkg | Subclass | World
 *      --------------+------------+----------+----------+-------
 *      public        |   YES      |   YES    |   YES    |  YES
 *      protected     |   YES      |   YES    |   YES    |  no
 *      (no modifier) |   YES      |   YES    |   no     |  no
 *      (package-     |            |          |          |
 *       private)     |            |          |          |
 *      private       |   YES      |   no     |   no     |  no
 *
 *
 * Quick Rule Of Thumb
 * -------------------
 *  - Default to `private` for fields and helper methods.
 *  - Expose `public` only for the API you intend callers to use.
 *  - Use `protected` when you specifically want subclasses to override or call.
 *  - Package-private (no modifier) is great for "internal to this package"
 *    helpers - not visible outside the package but more open than private.
 *
 *
 * Where Each Modifier May Appear
 * ------------------------------
 *  CLASSES   (top-level): only `public` or package-private. (Inner classes may
 *                          also be private/protected.)
 *  METHODS   : all four levels.
 *  FIELDS    : all four levels.
 *  CONSTRUCT-: all four levels. A `private` constructor disables direct
 *  ORS         instantiation (useful for utility classes and singletons).
 *  INTERFACE : interface methods are implicitly `public`. Java 9+ also allows
 *  MEMBERS     `private` interface methods (helper code) - see InterfaceMethods.
 *
 *
 * Subtle Point - `protected` Across Packages
 * ------------------------------------------
 * A `protected` member is visible to a subclass in a different package, but
 * ONLY through a reference of the subclass type (or its subtypes). Direct
 * access via a parent-typed reference outside the package is not allowed.
 *
 *
 * This file demonstrates all four modifiers on a single Box class plus the
 * common patterns (private fields + public getters/setters, private constructor
 * for a singleton).
 */

public class AccessModifiers {

    // === Demo class with all four access levels ===
    static class Box {
        public    int  publicField    = 1;   // visible everywhere
        protected int  protectedField = 2;   // visible in package + subclasses
                  int  packageField   = 3;   // visible in same package only
        private   int  privateField   = 4;   // visible only inside Box

        // Methods at each level
        public    void publicAction()    { System.out.println("publicAction()"); }
        protected void protectedAction() { System.out.println("protectedAction()"); }
                  void packageAction()   { System.out.println("packageAction()"); }
        private   void privateAction()   { System.out.println("privateAction()"); }

        // A public method exposing private state via getters - classic encapsulation
        public int  getPrivateField()         { return privateField; }
        public void setPrivateField(int v)    {
            if (v < 0) throw new IllegalArgumentException("must be >= 0");
            this.privateField = v;
        }

        // Public method that calls private helpers - private code is reusable
        // INSIDE the class but invisible OUTSIDE.
        public void process() {
            privateAction();
            System.out.println("processed, internal value = " + privateField);
        }
    }

    // === A singleton using a private constructor ===
    static class Singleton {
        private static final Singleton INSTANCE = new Singleton();
        private int counter = 0;

        // private - blocks `new Singleton()` from outside
        private Singleton() { }

        public static Singleton getInstance() { return INSTANCE; }

        public int next() { return ++counter; }
    }

    public static void main(String[] args) {

        Box b = new Box();

        // --- 1) Access from the SAME class (this main() lives in
        //        AccessModifiers, but Box is a nested static class) -
        //        because Box is inside AccessModifiers, we can still see
        //        all four levels. In a TRULY external class in a DIFFERENT
        //        package, only the public bits would be visible.
        System.out.println("public    = " + b.publicField);
        System.out.println("protected = " + b.protectedField);
        System.out.println("package   = " + b.packageField);
        System.out.println("private   = " + b.privateField);

        b.publicAction();
        b.protectedAction();
        b.packageAction();
        b.privateAction();

        // --- 2) Encapsulation: setter validates input ---
        b.setPrivateField(42);
        System.out.println("after set, privateField = " + b.getPrivateField());

        try {
            b.setPrivateField(-1);          // setter rejects bad input
        } catch (IllegalArgumentException e) {
            System.out.println("setter rejected: " + e.getMessage());
        }

        // --- 3) public method internally orchestrates private helpers ---
        b.process();

        // --- 4) Singleton via private constructor ---
        // new Singleton();                 // ERROR - constructor is private
        Singleton s = Singleton.getInstance();
        System.out.println("singleton.next() = " + s.next());
        System.out.println("singleton.next() = " + s.next());
        System.out.println("same instance?   = " + (s == Singleton.getInstance()));

        // OUTPUT
        // public    = 1
        // protected = 2
        // package   = 3
        // private   = 4
        // publicAction()
        // protectedAction()
        // packageAction()
        // privateAction()
        // after set, privateField = 42
        // setter rejected: must be >= 0
        // privateAction()
        // processed, internal value = 42
        // singleton.next() = 1
        // singleton.next() = 2
        // same instance?   = true
    }
}
