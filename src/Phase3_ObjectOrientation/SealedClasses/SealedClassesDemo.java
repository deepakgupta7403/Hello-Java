package Phase3_ObjectOrientation.SealedClasses;

/**
 * Sealed Classes - Java 17 (LTS) onwards
 * --------------------------------------
 * A SEALED class or interface gives you EXPLICIT CONTROL over which classes
 * can extend or implement it. Before sealed classes you could only choose
 * between:
 *
 *   - public         - anyone can subclass / implement
 *   - final          - nobody can subclass
 *   - package-private- only the same package can subclass
 *
 * Sealed gives you a middle ground: list the ALLOWED subtypes by name.
 *
 *      public sealed interface Shape
 *          permits Circle, Square, Triangle { ... }
 *
 *      public final  class Circle   implements Shape { ... }
 *      public final  class Square   implements Shape { ... }
 *      public non-sealed class Triangle implements Shape { ... }
 *
 *
 * Three Subclass Choices (every permitted child must pick one)
 * ------------------------------------------------------------
 *   1. final         - cannot be extended further.
 *   2. sealed        - extendable, but only by another permits list.
 *   3. non-sealed    - extendable by anyone; ends the sealing chain.
 *
 *
 * Where Sealed Classes Are Useful
 * -------------------------------
 *   - Modelling a CLOSED set of variants (Result = Ok | Err, Json = Null |
 *     Bool | Number | String | Array | Object, etc.).
 *   - Letting the compiler verify EXHAUSTIVENESS in a `switch` expression -
 *     no `default` needed when you cover every permitted subtype.
 *   - Building algebraic-data-type-like designs without third-party libraries.
 *
 *
 * Layout Requirements
 * -------------------
 *   - All permitted subtypes must be in the SAME MODULE (or the same package
 *     if no module).
 *   - The permitted subtypes must be NAMED in the `permits` clause - unless
 *     they live in the SAME FILE, in which case the `permits` clause may be
 *     omitted and the compiler infers it.
 *   - Every permitted subtype must DIRECTLY extend or implement the sealed
 *     type.
 *
 *
 * Sealed Classes + Pattern Matching (Java 21)
 * -------------------------------------------
 * The big payoff: a `switch` over a sealed type does not need `default`
 * because the compiler knows all the cases. Add a new permitted subtype and
 * every switch becomes a compile error until you handle the new case -
 * exactly what you want.
 *
 *      String describe(Shape s) {
 *          return switch (s) {
 *              case Circle c    -> "circle r=" + c.radius();
 *              case Square sq   -> "square side=" + sq.side();
 *              case Triangle t  -> "triangle";
 *          };
 *      }
 *
 *
 * This file fits the whole demo into ONE file using nested classes; the
 * `permits` clause is OPTIONAL when the permitted subtypes are in the same
 * compilation unit.
 */

public class SealedClassesDemo {

    // ============================================================
    // Sealed interface - the closed type hierarchy
    // ============================================================
    sealed interface Shape /* permits Circle, Square, Triangle, RoundedRectangle */ {}

    // FINAL - end of the line for Circle.
    static final class Circle implements Shape {
        private final double radius;
        Circle(double radius) { this.radius = radius; }
        double radius() { return radius; }
    }

    // FINAL - end of the line for Square.
    static final class Square implements Shape {
        private final double side;
        Square(double side) { this.side = side; }
        double side() { return side; }
    }

    // NON-SEALED - anyone may extend Triangle further.
    static non-sealed class Triangle implements Shape {
        private final double base, height;
        Triangle(double base, double height) { this.base = base; this.height = height; }
        double base()   { return base; }
        double height() { return height; }
    }

    // A further subclass of the non-sealed Triangle is allowed.
    static class EquilateralTriangle extends Triangle {
        EquilateralTriangle(double side) { super(side, side * Math.sqrt(3) / 2); }
    }

    // SEALED - RoundedRectangle is sealed itself - it has its own permits list
    // (here implied: only the nested final subtypes in this file).
    sealed static class RoundedRectangle implements Shape {
        private final double w, h, r;
        RoundedRectangle(double w, double h, double r) {
            this.w = w; this.h = h; this.r = r;
        }
        double w() { return w; }
        double h() { return h; }
        double radius() { return r; }
    }
    static final class Stadium extends RoundedRectangle {
        Stadium(double w, double h) { super(w, h, h / 2); }     // r = h/2
    }

    // ============================================================
    // Pattern matching switch - exhaustive without a default
    // ============================================================
    static String area(Shape s) {
        return switch (s) {
            case Circle c             -> "area = " + (Math.PI * c.radius() * c.radius());
            case Square sq            -> "area = " + (sq.side() * sq.side());
            case Triangle t           -> "area = " + (0.5 * t.base() * t.height());
            case RoundedRectangle rr  -> "area ~= " + (rr.w() * rr.h() -
                                          (4 - Math.PI) * rr.radius() * rr.radius());
            // No `default` - the compiler verifies every permitted subtype is handled.
            // Add a new permitted subtype to Shape and this switch will fail to compile.
        };
    }

    public static void main(String[] args) {

        Shape[] shapes = {
                new Circle(2.0),
                new Square(3.0),
                new Triangle(4.0, 5.0),
                new EquilateralTriangle(6.0),
                new Stadium(10.0, 4.0)
        };

        for (Shape s : shapes) {
            System.out.printf("%-22s  %s%n", s.getClass().getSimpleName(), area(s));
        }

        // OUTPUT (approximate - depends on the area formulas)
        // Circle                  area = 12.566370614359172
        // Square                  area = 9.0
        // Triangle                area = 10.0
        // EquilateralTriangle     area = 15.588457268119896
        // Stadium                 area ~= 36.566370614359175
    }
}
