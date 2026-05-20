package Phase1_CoreLanguage.Operators;

/**
 * The instanceof Operator
 * -----------------------
 * Tests whether an object is an instance of a particular class, sub-class, or
 * implements a particular interface. Result is a boolean.
 *
 *      obj instanceof Type        // true if obj is non-null AND of that type
 *
 * Returns FALSE if obj is null - so instanceof is also a handy null-safe check.
 *
 *
 * Pattern Matching (Java 16+)
 * ---------------------------
 * Since Java 16 you can bind the cast variable in the same expression:
 *
 *      if (obj instanceof String s) {
 *          // here 's' is already a String - no extra cast needed
 *          System.out.println(s.length());
 *      }
 *
 * Inside the 'if' body 's' is in scope; outside it is not.
 *
 *
 * When To Use It
 * --------------
 * - When you really do not know the runtime type (e.g. dealing with Object,
 *   external data, sealed hierarchies).
 * - In equals() implementations.
 *
 *
 * When NOT To Use It
 * ------------------
 * - As a stand-in for polymorphism. If you find yourself writing a long chain of
 *   instanceof / cast, consider overriding a method in each subclass instead.
 */

public class InstanceofOperator {

    public static void main(String[] args) {
        Object[] items = {
                "Hello",
                42,
                3.14,
                java.util.List.of(1, 2, 3),
                null
        };

        for (Object item : items) {
            describe(item);
        }

        // OUTPUT
        // String with 5 characters
        // Integer with value 42
        // Number subtype: Double
        // List of 3 elements: [1, 2, 3]
        // null
    }

    private static void describe(Object o) {
        // null check is implicit - instanceof on null is always false
        if (o instanceof String s) {                       // pattern matching
            System.out.println("String with " + s.length() + " characters");
        } else if (o instanceof Integer i) {
            System.out.println("Integer with value " + i);
        } else if (o instanceof Number n) {                // parent interface
            System.out.println("Number subtype: " + n.getClass().getSimpleName());
        } else if (o instanceof java.util.List<?> list) {  // generic wildcard ok
            System.out.println("List of " + list.size() + " elements: " + list);
        } else {
            System.out.println("null");
        }
    }
}
