package Basics.ModernJava;

/**
 * Pattern Matching for switch (Java 21)
 * -------------------------------------
 * Java 21 finalises a powerful upgrade to switch: you can now match on the
 * TYPE of the value (and combine with guards) inside switch labels - no more
 * long chains of `if (x instanceof T) ... else if (...)`.
 *
 *
 * Three Building Blocks
 * ---------------------
 *  1. Type Pattern in a case label:
 *        case Integer i -> ...
 *
 *  2. Guarded Pattern with `when`:
 *        case Integer i when i > 0 -> "positive int"
 *
 *  3. null Handling - traditionally switch threw NullPointerException on null.
 *     With pattern matching you can match null with an explicit case:
 *        case null -> "missing"
 *
 *
 * Exhaustiveness
 * --------------
 * The compiler tracks the set of possible types and either you cover all of
 * them or you add a `default`. For SEALED types it can verify exhaustiveness
 * without any default at all.
 *
 *
 * Old vs New
 * ----------
 *      // Old: if/else with instanceof
 *      String describe(Object o) {
 *          if (o instanceof Integer i)        return "int " + i;
 *          else if (o instanceof String s)    return "str of length " + s.length();
 *          else if (o == null)                return "null";
 *          else                                return "other";
 *      }
 *
 *      // New: pattern matching switch
 *      String describe(Object o) {
 *          return switch (o) {
 *              case Integer i       -> "int " + i;
 *              case String  s       -> "str of length " + s.length();
 *              case null            -> "null";
 *              default              -> "other";
 *          };
 *      }
 */

public class PatternMatchingSwitch {

    public static void main(String[] args) {
        Object[] samples = {
                42,
                "Hello",
                3.14,
                -5,
                "",
                null,
                new java.util.ArrayList<>(java.util.List.of("a", "b"))
        };

        for (Object o : samples) {
            System.out.println(o + "  ->  " + describe(o));
        }

        // OUTPUT
        // 42  ->  positive Integer: 42
        // Hello  ->  non-empty String of length 5
        // 3.14  ->  Double: 3.14
        // -5  ->  non-positive Integer: -5
        //   ->  empty String
        // null  ->  null reference
        // [a, b]  ->  some other Object: ArrayList
    }

    /** Classify any Object using a pattern-matching switch expression. */
    static String describe(Object o) {
        return switch (o) {

            // Guarded patterns - the `when` clause adds a condition
            case Integer i when i > 0  -> "positive Integer: " + i;
            case Integer i             -> "non-positive Integer: " + i;

            // Type patterns combined with extra logic
            case String s when s.isEmpty() -> "empty String";
            case String s                  -> "non-empty String of length " + s.length();

            case Double d -> "Double: " + d;

            // Explicit null branch - no NullPointerException
            case null -> "null reference";

            // default catches everything else
            default -> "some other Object: " + o.getClass().getSimpleName();
        };
    }
}
