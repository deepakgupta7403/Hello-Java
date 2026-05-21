package Phase1_CoreLanguage.Identifiers;

/**
 * Identifiers in Java
 * -------------------
 * An IDENTIFIER is the name given to any program element: a class, method,
 * variable, package, label, interface, etc. Identifiers are how you refer to
 * things in your code.
 * <p>
 *
 *      int age;                  //   ^^^   "age" is an identifier
 *      class Customer { ... }    //   ^^^^^^^^ "Customer" is an identifier
 * <p>
 *
 * Rules for Valid Identifiers (Strict - enforced by the compiler)
 * ---------------------------------------------------------------
 *  1. May contain letters (A-Z, a-z), digits (0-9), underscore (_) and dollar ($).
 *  2. MUST start with a letter, underscore (_), or dollar sign ($).
 *     -> CANNOT start with a digit.
 *  3. Case-sensitive: "name", "Name", and "NAME" are three different identifiers.
 *  4. No length limit (but be sensible).
 *  5. CANNOT be a Java reserved keyword (class, int, void, ...).
 *  6. CANNOT be the literals true, false, or null.
 *  7. Cannot contain whitespace.
 *  8. Java allows Unicode letters - so non-English names like  android , greek
 *     letters work. Usually avoided in professional code.
 * <p>
 *
 * Style Conventions (Soft Rules - not enforced, but everyone follows them)
 * -----------------------------------------------------------------------
 *  - Classes/Interfaces: PascalCase            -> Customer, OrderService
 *  - Methods/Variables : camelCase             -> getOrderId, totalAmount
 *  - Constants         : UPPER_SNAKE_CASE      -> MAX_RETRIES, PI
 *  - Packages          : lowercase, dot-       -> com.example.payments
 *                        separated, no _
 *  - Generic type vars : single uppercase      -> T, K, V, E
 *  - Boolean variables : start with is/has/can -> isActive, hasNext, canEdit
 * <p>
 *
 * Special About Dollar Sign and Underscore
 * ----------------------------------------
 * - $ is technically allowed but reserved for code-generation tools (the Java
 *   compiler itself uses it for inner-class file names like Outer$Inner.class).
 *   Avoid using $ in your own identifiers.
 * <p>
 *
 * - Standalone underscore "_" was deprecated as an identifier in Java 9 and is
 *   actually a RESERVED keyword from Java 21 onward (used for unnamed variables
 *   in pattern matching). Use a meaningful name instead.
 * <p>
 *
 * This class demonstrates legal and illegal identifiers (the illegal ones are
 * commented out - uncomment one at a time to see the compile errors).
 */

public class Identifiers {

    // --- Legal identifiers ---
    int    age;
    String firstName;
    double _internalRate;          // legal but unusual - underscore prefix
    long   $generatedId;           // legal but reserved for tools - avoid
    static final int MAX_RETRIES = 3;
    boolean isActive;
    String  customer2;             // digits are fine after the first character

    // --- Illegal identifiers (uncomment to see compiler errors) ---
    // int 1stPlace;        // ERROR: identifier cannot start with a digit
    // int my age;          // ERROR: cannot contain whitespace
    // int class;           // ERROR: 'class' is a reserved keyword
    // int my-age;          // ERROR: hyphen is not allowed
    // int true;            // ERROR: 'true' is a boolean literal
    // int _;               // ERROR (Java 9+): underscore alone is reserved

    public static void main(String[] args) {
        Identifiers id = new Identifiers();
        id.age = 25;
        id.firstName = "Deepak";
        id.isActive = true;

        System.out.println("firstName    = " + id.firstName);
        System.out.println("age          = " + id.age);
        System.out.println("isActive     = " + id.isActive);
        System.out.println("MAX_RETRIES  = " + MAX_RETRIES);

        // Case sensitivity demo
        int value = 10;
        int Value = 20;
        int VALUE = 30;
        System.out.println("value=" + value + ", Value=" + Value + ", VALUE=" + VALUE);

        // OUTPUT
        // firstName    = Deepak
        // age          = 25
        // isActive     = true
        // MAX_RETRIES  = 3
        // value=10, Value=20, VALUE=30
    }
}
