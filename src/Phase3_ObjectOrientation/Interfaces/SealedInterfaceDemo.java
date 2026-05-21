package Phase3_ObjectOrientation.Interfaces;

/**
 * Sealed Interfaces - Java 17 (LTS) onwards
 * -----------------------------------------
 * A SEALED INTERFACE restricts WHO IS ALLOWED TO IMPLEMENT IT. Before sealed
 * interfaces, every public interface was open to the entire world. Sealed
 * interfaces close that hole - you list the permitted implementors.
 * <p>
 *
 *      sealed interface Result permits Success, Failure {}
 *      record Success(String data) implements Result {}
 *      record Failure(String err)  implements Result {}
 * <p>
 *
 * Three Choices Each Permitted Subtype Must Make
 * ----------------------------------------------
 *   1. final         - cannot be extended further.            (record is implicitly final)
 *   2. sealed        - extendable, but with its OWN permits list.
 *   3. non-sealed    - extendable by anyone; ends the seal.
 * <p>
 *
 * Why Sealed?
 * -----------
 *  - ALGEBRAIC DATA TYPES   - model a closed union of variants.
 *  - EXHAUSTIVE SWITCH      - the compiler verifies every permitted case is
 *                              handled; no `default` needed.
 *  - VERSION SAFETY         - adding a new permitted variant turns every
 *                              previously-exhaustive switch into a compile
 *                              error - exactly what you want.
 *  - DOCUMENTATION          - the permits list IS the documentation of "all
 *                              implementors".
 * <p>
 *
 * Common Patterns
 * ---------------
 *   Result<T, E>      Success | Failure
 *   Json              Null | Boolean | Number | String | Array | Object
 *   Token             Plus | Minus | Number | Eof
 *   HttpResponse      Ok | Redirect | NotFound | ServerError
 * <p>
 *
 * Rules
 * -----
 *   - Permitted subtypes must live in the SAME MODULE (or the same package
 *     if no module).
 *   - When permitted subtypes are in the SAME FILE the `permits` clause may
 *     be omitted - the compiler infers it. This file uses that shortcut.
 *   - Every permitted subtype must DIRECTLY implement the sealed interface.
 * <p>
 *
 * Related: SealedClassesDemo.java in OOPSConcepts/SealedClasses shows the
 * same idea on classes; sealed interfaces tend to be more common because
 * they pair naturally with records.
 */

public class SealedInterfaceDemo {

    // ============================================================
    // A sealed interface representing the closed set of HTTP results.
    // ============================================================
    sealed interface HttpResult /* permits Ok, Redirect, NotFound, ServerError */ { }

    // Records are implicitly final - meets the "final" subtype requirement.
    record Ok(String body)                implements HttpResult {}
    record Redirect(String location)      implements HttpResult {}
    record NotFound(String resource)      implements HttpResult {}
    record ServerError(int code, String reason) implements HttpResult {}

    // ============================================================
    // A function that maps the result to a human-readable message.
    // Notice: NO `default` branch - the compiler verifies completeness.
    // ============================================================
    static String describe(HttpResult r) {
        return switch (r) {
            case Ok          o -> "200 OK: " + o.body();
            case Redirect    d -> "302 -> " + d.location();
            case NotFound    n -> "404 not found: " + n.resource();
            case ServerError s -> "5xx (" + s.code() + "): " + s.reason();
        };
    }

    // ============================================================
    // Another sealed interface, this time showing a non-sealed subtype.
    // ============================================================
    sealed interface Shape permits Circle, Polygon { }

    record Circle(double radius) implements Shape {}

    /**
     * Polygon is non-sealed, so anyone may extend it further. That is a
     * pragmatic choice: we want a closed CATEGORY (Circle vs Polygon) but
     * unlimited variety of Polygons.
     */
    non-sealed interface Polygon extends Shape {
        int sides();
    }

    record Triangle(double a, double b, double c)        implements Polygon {
        @Override public int sides() { return 3; }
    }
    record Pentagon(double side)                          implements Polygon {
        @Override public int sides() { return 5; }
    }

    public static void main(String[] args) {

        section("1) Exhaustive switch on a sealed interface");
        HttpResult[] outcomes = {
                new Ok("hello"),
                new Redirect("/login"),
                new NotFound("/users/99"),
                new ServerError(503, "database down")
        };
        for (HttpResult r : outcomes) {
            System.out.println(describe(r));
        }

        section("2) Sealed + non-sealed - closed category, open variety");
        Shape[] shapes = {
                new Circle(2.0),
                new Triangle(3, 4, 5),
                new Pentagon(2.0)
        };
        for (Shape s : shapes) {
            String label = switch (s) {
                case Circle c   -> "Circle (r=" + c.radius() + ")";
                case Polygon p  -> p.getClass().getSimpleName() + " (" + p.sides() + " sides)";
            };
            System.out.println(label);
        }

        section("3) Adding a new variant fails the build elsewhere");
        // If you add `record Gone(String resource) implements HttpResult {}`
        // to HttpResult's permits list, the `describe()` switch above will no
        // longer compile until you add a case for it. That compiler nudge is
        // the whole point of sealed.

        // OUTPUT
        // ====== 1) Exhaustive switch on a sealed interface ======
        // 200 OK: hello
        // 302 -> /login
        // 404 not found: /users/99
        // 5xx (503): database down
        // ====== 2) Sealed + non-sealed - closed category, open variety ======
        // Circle (r=2.0)
        // Triangle (3 sides)
        // Pentagon (5 sides)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
