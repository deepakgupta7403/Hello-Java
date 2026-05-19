package OOPSConcepts.Packages;

// ============================================================
// IMPORTS - bring other classes into scope (the file's PROLOGUE)
// ============================================================

import java.util.ArrayList;                 // single-type import
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Wildcard import - "any class from java.time"
import java.time.*;

// Static import - lets you use members without the class qualifier
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

/**
 * Packages and Imports
 * --------------------
 * A PACKAGE is a Java NAMESPACE - a logical container for a group of related
 * classes/interfaces/enums/records. Packages do two big things:
 *
 *   1. AVOID NAME CLASHES - your com.acme.user.User does not collide with
 *      org.example.user.User.
 *   2. ACCESS CONTROL - package-private (no modifier) members are visible
 *      ONLY inside the same package; this scopes implementation details.
 *
 *
 * Declaring a Package
 * -------------------
 * The first non-comment line of every .java file is the package declaration:
 *
 *      package OOPSConcepts.Packages;
 *
 * The package name must match the directory structure under your source root.
 * In this repository, this file lives at:
 *
 *      src/OOPSConcepts/Packages/PackagesAndImports.java
 *
 *
 * Imports
 * -------
 *   Single-type:    import java.util.ArrayList;
 *   Wildcard:       import java.util.*;             // any class in java.util
 *                   (NO recursion - java.util.* does NOT include java.util.concurrent)
 *   Static type:    import static java.lang.Math.PI;
 *   Static wild:    import static java.lang.Math.*;
 *
 * What you do NOT need to import:
 *   - java.lang.*  - always implicitly imported (String, System, Math, ...).
 *   - Classes in the SAME PACKAGE.
 *
 *
 * Naming Conventions
 * ------------------
 *   - All-lowercase, dot-separated, reverse-DNS for libraries you publish:
 *         com.mycompany.product.subsystem
 *   - One class per file for any class declared public; the file name
 *     must match the public class name.
 *
 *
 * The Unnamed (Default) Package
 * -----------------------------
 * A class without a package declaration is in the "unnamed" package. It is
 * legal but discouraged - you cannot import unnamed-package classes from
 * code that DOES have a package.
 *
 *
 * Java 9 Modules (One-Level Above Packages)
 * -----------------------------------------
 * Since Java 9 a `module-info.java` can group packages into a MODULE and
 * declare which packages to `exports`, what it `requires`, and so on. Modules
 * are optional and orthogonal to packages.
 *
 *
 * Running This Example
 * --------------------
 * From the project root, compile and run with the fully qualified name:
 *
 *      javac src/OOPSConcepts/Packages/PackagesAndImports.java
 *      cd src
 *      java OOPSConcepts.Packages.PackagesAndImports
 */

public class PackagesAndImports {

    public static void main(String[] args) {

        section("1) Classes imported by full name");
        List<String> names = new ArrayList<>();       // both came from java.util
        names.add("Alice");
        names.add("Bob");
        System.out.println("names = " + names);

        Map<String, Integer> ages = new HashMap<>();
        ages.put("Alice", 30);
        ages.put("Bob", 25);
        System.out.println("ages  = " + ages);

        section("2) Wildcard import - any class in java.time");
        LocalDate today = LocalDate.now();
        LocalTime now   = LocalTime.now();
        Duration ttl    = Duration.ofMinutes(30);
        System.out.println("today = " + today);
        System.out.println("now   = " + now);
        System.out.println("ttl   = " + ttl);

        section("3) Static imports - members used without their class prefix");
        // Because of `import static java.lang.Math.PI` and `... .sqrt` we can
        // write PI and sqrt(...) directly. Compare with Math.PI / Math.sqrt(2).
        System.out.println("PI         = " + PI);
        System.out.println("sqrt(2)    = " + sqrt(2));
        // Equivalent without the static import:
        System.out.println("Math.PI    = " + Math.PI);

        section("4) Same-package access - no import needed");
        // Sibling.java lives in the same package, so we can use it directly.
        Sibling s = new Sibling("hello");
        System.out.println("sibling   = " + s.greet());

        section("5) Fully qualified names - no import at all");
        java.util.Random r = new java.util.Random();
        System.out.println("random    = " + r.nextInt(100));

        // OUTPUT
        // ====== 1) Classes imported by full name ======
        // names = [Alice, Bob]
        // ages  = {Bob=25, Alice=30}
        // ====== 2) Wildcard import - any class in java.time ======
        // today = 2026-05-19
        // now   = 12:34:56.789
        // ttl   = PT30M
        // ====== 3) Static imports - members used without their class prefix ======
        // PI         = 3.141592653589793
        // sqrt(2)    = 1.4142135623730951
        // Math.PI    = 3.141592653589793
        // ====== 4) Same-package access - no import needed ======
        // sibling   = hello, friend!
        // ====== 5) Fully qualified names - no import at all ======
        // random    = 42
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}

/**
 * A SIBLING class in the same package, demonstrating that:
 *   - No import is needed to use it from PackagesAndImports.
 *   - Package-private (no modifier) members are visible across the package.
 *   - Only one PUBLIC top-level class is allowed per file - this one is
 *     package-private, so it is fine to put it in the same file.
 */
class Sibling {
    private final String message;
    Sibling(String message) { this.message = message; }
    String greet() { return message + ", friend!"; }
}
