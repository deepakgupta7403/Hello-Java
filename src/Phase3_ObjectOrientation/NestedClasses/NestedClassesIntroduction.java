package Phase3_ObjectOrientation.NestedClasses;

/**
 * Nested Classes — Introduction
 * -----------------------------
 * Java has FOUR kinds of nested classes. They differ in:
 * <p>
 *
 *   - Whether they need an enclosing instance.
 *   - Whether they can capture variables from outside.
 *   - Where they can be declared.
 * <p>
 *
 *     Kind                  Static?   Needs outer instance?   Can capture locals?
 *     -------------------   -------   ----------------------  ---------------------
 *  1) Static nested class    YES       NO                      N/A (no enclosing
 *                                                              method scope)
 *  2) Inner (member) class   NO        YES                     fields, not locals
 *  3) Local class            NO        Sometimes               YES (effectively
 *                                                              final locals)
 *  4) Anonymous class        NO        Sometimes               YES (effectively
 *                                                              final locals)
 * <p>
 *
 * Why nest at all?
 * ----------------
 *   - Group helpers tightly with the class that uses them.
 *   - Keep visibility tight — a static nested class can be private.
 *   - Cleaner namespaces. Counts as one .class file per nested class.
 * <p>
 *
 * Modern relatives
 * ----------------
 *   - LAMBDAS replaced 95% of anonymous classes (especially when the
 *     target was a functional interface).
 *   - RECORDS are commonly declared as nested public static — natural
 *     home for "DTO inside its owner."
 *   - LOCAL classes / records are sometimes useful for a "tiny helper
 *     visible only inside one method."
 */

public class NestedClassesIntroduction {

    // 1) STATIC nested class — no link to an instance of the outer.
    static class StaticNested {
        int x;
        StaticNested(int x) { this.x = x; }
        int square() { return x * x; }
    }

    // 2) INNER (non-static) class — every instance carries a hidden
    //    reference to an instance of the outer class.
    class Inner {
        int multiply(int factor) {
            // can read outer fields directly:
            return outerField * factor;
        }
    }

    private final int outerField = 10;

    public static void main(String[] args) {

        section("1) Static nested — no outer instance needed");
        StaticNested s = new StaticNested(7);
        System.out.println("square = " + s.square());

        section("2) Inner — need an outer instance first");
        NestedClassesIntroduction outer = new NestedClassesIntroduction();
        Inner i = outer.new Inner();
        System.out.println("multiply 5 = " + i.multiply(5));

        section("3) Local class — declared inside a method");
        // Visible ONLY inside the method that contains it.
        class Local {
            String label() { return "local class"; }
        }
        Local local = new Local();
        System.out.println(local.label());

        section("4) Anonymous class — define-and-instantiate in one expression");
        Runnable r = new Runnable() {
            @Override public void run() {
                System.out.println("anonymous class run()");
            }
        };
        r.run();

        section("5) Same thing as a lambda — 90% of anon classes can become this");
        Runnable r2 = () -> System.out.println("lambda run()");
        r2.run();

        section("done — see each kind's dedicated file for details");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
