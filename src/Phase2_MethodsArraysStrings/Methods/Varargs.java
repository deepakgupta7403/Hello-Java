package Phase2_MethodsArraysStrings.Methods;

import java.util.Arrays;
import java.util.List;

/**
 * Variable Arguments (Varargs) - Java 5+
 * --------------------------------------
 * A VARARGS parameter lets a method accept ANY NUMBER of arguments of a given
 * type, including zero. Syntax:
 * <p>
 *
 *      returnType methodName(Type... name) { ... }
 * <p>
 *
 * Inside the method, `name` is a regular array (Type[]). At the CALL SITE the
 * caller can pass a comma-separated list of values - the compiler bundles them
 * into an array automatically.
 * <p>
 *
 * Familiar Example
 * ----------------
 *      System.out.printf("%s is %d years old%n", name, age);
 *                                            ^^^ varargs
 *      String.format("%d + %d = %d", 1, 2, 3);
 *      List.of(1, 2, 3, 4, 5);
 *      Arrays.asList("a", "b", "c");
 * <p>
 *
 * The Four Rules
 * --------------
 *  1. ONLY ONE varargs parameter per method.
 *  2. It must be the LAST parameter.
 * <p>
 *
 *         void f(int x, String... names)   // OK
 *         void f(String... names, int x)   // ERROR
 * <p>
 *
 *  3. The caller can pass:
 *       a) zero or more values    ->   f("a", "b", "c");
 *       b) an explicit array      ->   f(new String[] {"a","b","c"});
 *  4. Inside the method, the varargs parameter is just an array.
 * <p>
 *
 * Overloading and Ambiguity
 * -------------------------
 * Overload resolution prefers EXACT matches and fixed-arity methods before
 * varargs. Mixing varargs with overloads can create surprises - keep it simple.
 * <p>
 *
 * Generics + Varargs Warning
 * --------------------------
 * "unchecked / unsafe operations" warnings can appear when the vararg type is
 * a generic (e.g. `List<String>...`). Java permits this but the runtime cannot
 * actually create a generic array, so the JVM uses an Object[] under the hood.
 * Annotate with @SafeVarargs (on static/final/private methods) once you have
 * convinced yourself the usage is safe.
 */

public class Varargs {

    public static void main(String[] args) {

        // --- 1) Call with 0, 1, several values ---
        System.out.println("sum()          = " + sum());           // 0
        System.out.println("sum(5)         = " + sum(5));           // 5
        System.out.println("sum(1, 2, 3)   = " + sum(1, 2, 3));     // 6
        System.out.println("sum(1..5)      = " + sum(1, 2, 3, 4, 5));// 15

        // --- 2) Call with an explicit array (also legal) ---
        int[] given = {10, 20, 30};
        System.out.println("sum(int[])     = " + sum(given));       // 60

        // --- 3) Mixing a regular parameter with varargs ---
        format("Total", 1, 2, 3);        // -> "Total: 1, 2, 3"
        format("Empty");                 // -> "Empty:"

        // --- 4) Calling printf - a familiar varargs API ---
        System.out.printf("%s scored %d/%d%n", "Deepak", 87, 100);

        // --- 5) List.of - a generic varargs factory ---
        List<String> friends = List.of("Alice", "Bob", "Charlie");
        System.out.println("friends        = " + friends);

        // --- 6) Generics-aware varargs with @SafeVarargs ---
        printSizes(List.of(1, 2, 3), List.of("a"), List.of());

        // OUTPUT
        // sum()          = 0
        // sum(5)         = 5
        // sum(1, 2, 3)   = 6
        // sum(1..5)      = 15
        // sum(int[])     = 60
        // Total: 1, 2, 3
        // Empty:
        // Deepak scored 87/100
        // friends        = [Alice, Bob, Charlie]
        // sizes = [3, 1, 0]
    }

    /** Adds any number of ints; inside the method, `nums` is an int[]. */
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n;
        return total;
    }

    /** Combines a fixed parameter with a trailing varargs parameter. */
    static void format(String label, int... values) {
        // Arrays.toString prints the array nicely; we strip the brackets for output.
        String body = Arrays.toString(values).replace("[", "").replace("]", "");
        System.out.println(label + ": " + body);
    }

    /**
     * Generics + varargs - the @SafeVarargs annotation suppresses the compiler
     * warning. It is safe here because we only READ from `lists` and never
     * write into it or expose it to outside code.
     */
    @SafeVarargs
    static void printSizes(List<?>... lists) {
        int[] sizes = new int[lists.length];
        for (int i = 0; i < lists.length; i++) {
            sizes[i] = lists[i].size();
        }
        System.out.println("sizes = " + Arrays.toString(sizes));
    }
}
