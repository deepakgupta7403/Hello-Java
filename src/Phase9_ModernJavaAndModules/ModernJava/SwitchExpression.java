package Phase9_ModernJavaAndModules.ModernJava;

/**
 * Switch Expressions (Java 14+)
 * -----------------------------
 * Classic switch is a STATEMENT - it does not return a value, and the colon
 * style with `break` is verbose and easy to get wrong (fall-through bugs).
 * Java 14 promoted switch to also being an EXPRESSION that produces a value,
 * with a safer arrow form.
 *
 *
 * Two New Things
 * --------------
 *  1. Arrow labels         ->     -  no fall-through, no break needed
 *  2. The yield keyword           -  return a value from a multi-statement case
 *
 *
 * Old (Switch Statement)
 * ----------------------
 *      String name;
 *      switch (day) {
 *          case 1:  name = "Mon"; break;
 *          case 2:  name = "Tue"; break;
 *          default: name = "?";   break;
 *      }
 *
 * New (Switch Expression)
 * -----------------------
 *      String name = switch (day) {
 *          case 1 -> "Mon";
 *          case 2 -> "Tue";
 *          default -> "?";
 *      };
 *
 *
 * Key Properties
 * --------------
 *  - Each arrow case is a single expression OR a block ending in `yield`.
 *  - NO fall-through with arrow form - one case runs, that is it.
 *  - Multiple labels per arm:  case 1, 2, 3 -> "low";
 *  - The switch expression must be EXHAUSTIVE - either cover every possibility
 *    or include a `default`. Compiler enforces this for enums and sealed types.
 *
 *
 * yield - When You Need More Than One Statement
 * ---------------------------------------------
 *      int code = switch (s) {
 *          case "OK" -> 0;
 *          case "WARN" -> {
 *              log("warn observed");
 *              yield 1;
 *          }
 *          default -> -1;
 *      };
 */

public class SwitchExpression {

    public static void main(String[] args) {

        // --- 1) Arrow form, single expression per case ---
        int day = 3;
        String name = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6, 7 -> "Weekend";          // multiple labels per arm
            default -> "Invalid day";
        };
        System.out.println("Day " + day + " -> " + name);

        // --- 2) yield with a block ---
        String status = "WARN";
        int code = switch (status) {
            case "OK" -> 0;
            case "WARN" -> {
                System.out.println("warn observed");
                yield 1;
            }
            case "ERROR" -> {
                System.out.println("error logged");
                yield 2;
            }
            default -> -1;
        };
        System.out.println("status=" + status + " -> code=" + code);

        // --- 3) Exhaustiveness with enums - no default needed ---
        Size size = Size.MEDIUM;
        String label = switch (size) {     // enum exhaustiveness
            case SMALL  -> "S";
            case MEDIUM -> "M";
            case LARGE  -> "L";
        };
        System.out.println("size=" + size + " -> " + label);

        // --- 4) Old colon-style switch expression with yield (rarely used) ---
        int n = 2;
        String parity = switch (n % 2) {
            case 0: yield "even";
            case 1: yield "odd";
            default: yield "?";
        };
        System.out.println("n=" + n + " is " + parity);

        // OUTPUT
        // Day 3 -> Wednesday
        // warn observed
        // status=WARN -> code=1
        // size=MEDIUM -> M
        // n=2 is even
    }

    enum Size { SMALL, MEDIUM, LARGE }
}
