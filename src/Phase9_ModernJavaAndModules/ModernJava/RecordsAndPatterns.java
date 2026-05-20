package Phase9_ModernJavaAndModules.ModernJava;

/**
 * Records (Java 16+) and Record Patterns (Java 21)
 * ------------------------------------------------
 * A RECORD is a special kind of class introduced in Java 16 for IMMUTABLE
 * data carriers. The compiler generates the boilerplate for you:
 *
 *      public record Point(int x, int y) {}
 *
 *  - Final class, cannot be subclassed.
 *  - Final fields x and y - immutable after construction.
 *  - Canonical constructor (int x, int y).
 *  - Accessor methods: x() and y().            (NOT getX() / getY())
 *  - equals(), hashCode(), toString() generated based on the components.
 *
 *
 * Why Records?
 * ------------
 * Before records, a simple data class needed 30+ lines of boilerplate. With
 * records:
 *
 *      record Point(int x, int y) {}     // that's it.
 *
 *
 * Record Patterns (Java 21)
 * -------------------------
 * Java 21 lets you DECONSTRUCT a record value inside an `instanceof` or a
 * switch case, binding the components to local variables:
 *
 *      if (obj instanceof Point(int x, int y)) {
 *          // x and y are usable directly here
 *      }
 *
 * Record patterns can NEST - deconstruct records inside records:
 *
 *      if (shape instanceof Rect(Point(int x1, int y1), Point(int x2, int y2))) {
 *          ...
 *      }
 *
 *
 * Customising a Record
 * --------------------
 * You can add:
 *  - Static fields and methods.
 *  - Instance methods (regular or default).
 *  - A "compact constructor" for validation (see below).
 *  - Static factory methods.
 * You CANNOT add instance fields beyond the components.
 */

public class RecordsAndPatterns {

    // --- 1) Simple record ---
    public record Point(int x, int y) {}

    // --- 2) Record with validation via the compact constructor ---
    public record Range(int low, int high) {
        public Range {           // compact constructor - no parameter list
            if (low > high) {
                throw new IllegalArgumentException("low must be <= high");
            }
        }
        public int span() { return high - low; }
    }

    // --- 3) Nested record for the record-pattern demo ---
    public record Line(Point start, Point end) {}

    public static void main(String[] args) {

        // --- a) Use the auto-generated bits ---
        Point p = new Point(3, 4);
        System.out.println("p.x() = " + p.x());
        System.out.println("p.y() = " + p.y());
        System.out.println("p     = " + p);                // Point[x=3, y=4]

        Point p2 = new Point(3, 4);
        System.out.println("p.equals(p2)? " + p.equals(p2));   // true - value equality

        // --- b) Compact constructor validates inputs ---
        Range ok = new Range(1, 10);
        System.out.println("Range span = " + ok.span());
        try {
            new Range(5, 2);
        } catch (IllegalArgumentException e) {
            System.out.println("Range rejected: " + e.getMessage());
        }

        // --- c) Java 21 record pattern with instanceof ---
        Object obj = new Point(7, 9);
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Deconstructed point: x=" + x + ", y=" + y);
        }

        // --- d) Nested record pattern ---
        Object thing = new Line(new Point(0, 0), new Point(3, 4));
        if (thing instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            int dx = x2 - x1, dy = y2 - y1;
            double dist = Math.hypot(dx, dy);
            System.out.println("Line length = " + dist);
        }

        // --- e) Record patterns inside a switch expression ---
        Object shape = new Line(new Point(1, 1), new Point(4, 5));
        String desc = switch (shape) {
            case Point(int x, int y)                    -> "Point at (" + x + "," + y + ")";
            case Line(Point(int x1, int y1),
                      Point(int x2, int y2))            -> "Line from (" + x1 + "," + y1 +
                                                            ") to (" + x2 + "," + y2 + ")";
            case null                                   -> "no shape";
            default                                     -> "unknown shape: " + shape;
        };
        System.out.println(desc);

        // OUTPUT
        // p.x() = 3
        // p.y() = 4
        // p     = Point[x=3, y=4]
        // p.equals(p2)? true
        // Range span = 9
        // Range rejected: low must be <= high
        // Deconstructed point: x=7, y=9
        // Line length = 5.0
        // Line from (1,1) to (4,5)
    }
}
