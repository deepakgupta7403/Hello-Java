package Phase9_ModernJavaAndModules.ModernJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * var - Local Variable Type Inference (Java 10+)
 * ----------------------------------------------
 * Since Java 10 you can declare a LOCAL variable with the keyword 'var' instead
 * of writing the full type. The compiler INFERS the type from the right-hand
 * side - this is purely a compile-time feature, the bytecode is identical.
 *
 *      var x = 42;                  // inferred as int
 *      var s = "Hello";             // inferred as String
 *      var list = new ArrayList<String>();  // ArrayList<String>
 *
 *
 * Where 'var' Works
 * -----------------
 *  - Local variables inside methods, constructors, initializer blocks.
 *  - The loop variable in for / for-each loops.
 *  - Try-with-resources variable.
 *
 *
 * Where 'var' Does NOT Work
 * -------------------------
 *  - Class fields                      var name = "Deepak";   // ERROR (field)
 *  - Method parameters                 void m(var x) { ... }  // ERROR
 *  - Return types                      var foo() { ... }      // ERROR
 *  - Without an initializer            var x;                 // ERROR
 *  - With null literal                 var x = null;          // ERROR
 *  - With a lambda                     var f = () -> 1;       // ERROR (no target type)
 *  - With an array initializer         var a = {1, 2, 3};     // ERROR
 *
 *
 * Note - 'var' is a RESTRICTED IDENTIFIER, not a reserved keyword. You can
 * still declare a variable named 'var' (legacy code) - but please do not.
 *
 *
 * When To Use It (Style Guide)
 * ----------------------------
 *  - When the inferred type is obvious from the RHS:
 *        var list = new ArrayList<Customer>();   // GOOD - reduces duplication
 *  - When the inferred type is opaque or surprising, prefer the explicit type:
 *        var result = service.process();         // BAD - what's the type?
 *        OrderResult result = service.process(); // GOOD
 *  - var does NOT make code dynamically typed - the type is fixed once inferred.
 */

public class VarLocalTypeInference {

    public static void main(String[] args) {

        // --- 1) Obvious inferences ---
        var i  = 42;                                 // int
        var s  = "Hello, var!";                      // String
        var d  = 3.14;                               // double
        var b  = true;                               // boolean
        var arr = new int[]{1, 2, 3};                // int[]

        System.out.println("i = " + i + " (class " + ((Object) i).getClass().getSimpleName() + ")");
        System.out.println("s = " + s + " (class " + s.getClass().getSimpleName() + ")");
        System.out.println("d = " + d + " (class " + ((Object) d).getClass().getSimpleName() + ")");
        System.out.println("b = " + b + " (class " + ((Object) b).getClass().getSimpleName() + ")");
        System.out.println("arr length = " + arr.length);

        // --- 2) Cuts the noise on generic types ---
        var customers = new ArrayList<String>();
        customers.add("Alice");
        customers.add("Bob");
        System.out.println("customers = " + customers);

        var index = new HashMap<String, List<Integer>>();
        index.computeIfAbsent("primes", k -> new ArrayList<>()).add(2);
        index.computeIfAbsent("primes", k -> new ArrayList<>()).add(3);
        System.out.println("index = " + index);

        // --- 3) var in a for-each loop ---
        for (var name : customers) {
            System.out.println("Hello " + name);
        }

        // --- 4) var in a classic for loop ---
        for (var n = 0; n < 3; n++) {
            System.out.println("n = " + n);
        }

        // --- 5) var with Map.entry types - this is where it really shines ---
        for (var entry : index.entrySet()) {
            // entry is Map.Entry<String, List<Integer>> - no need to spell it out
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // --- 6) Types are STILL static - this would not compile ---
        var x = 10;
        // x = "now I'm a String";   // ERROR: incompatible types

        // --- 7) Things 'var' CANNOT do - all commented out ---
        // var n;                  // ERROR: no initializer to infer from
        // var z = null;           // ERROR: null has no type
        // var lambda = () -> 1;   // ERROR: lambda needs a target type
        // var arr2 = {1, 2, 3};   // ERROR: array initializer needs a type

        // OUTPUT
        // i = 42 (class Integer)
        // s = Hello, var! (class String)
        // d = 3.14 (class Double)
        // b = true (class Boolean)
        // arr length = 3
        // customers = [Alice, Bob]
        // index = {primes=[2, 3]}
        // Hello Alice
        // Hello Bob
        // n = 0
        // n = 1
        // n = 2
        // primes -> [2, 3]
    }
}
