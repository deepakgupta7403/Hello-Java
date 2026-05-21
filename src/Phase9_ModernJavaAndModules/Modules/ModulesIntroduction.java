package Phase9_ModernJavaAndModules.Modules;

/**
 * Java Platform Module System (JPMS) — Introduction
 * -------------------------------------------------
 * Introduced in Java 9 (JEP 261). Adds an EXPLICIT, COMPILE-TIME unit
 * called a MODULE: a named collection of related packages plus a
 * descriptor file `module-info.java`.
 * <p>
 *
 * Why modules?
 * ------------
 *   - REPLACE the public/package-private duality with explicit EXPORT.
 *     A module exports SOME packages; others are private to the module.
 * <p>
 *
 *   - STRONG ENCAPSULATION — you can't reflect into a module's internals
 *     unless that module `opens` the package to you.
 * <p>
 *
 *   - RELIABLE CONFIGURATION — `requires` declares dependencies that
 *     the linker / runtime verifies up front (no more classpath
 *     surprises).
 * <p>
 *
 *   - SCALABLE JDK — the JDK itself was split into ~100 modules. You
 *     can ship a runtime image with `jlink` that includes ONLY the
 *     modules your app uses.
 * <p>
 *
 * Anatomy of module-info.java
 * ---------------------------
 *      module com.example.app {
 *          requires java.net.http;                    // I depend on this
 *          requires transitive java.sql;              // exposed to consumers
 * <p>
 *
 *          exports com.example.app.api;               // public API
 *          exports com.example.app.internal to com.example.tests;
 * <p>
 *
 *          opens com.example.app.entity;              // allow reflection
 *          opens com.example.app.entity to spring.core;
 * <p>
 *
 *          provides com.example.api.SpiPort
 *              with com.example.app.SpiImpl;          // implements a SPI
 * <p>
 *
 *          uses com.example.api.SpiPort;              // consumes one
 *      }
 * <p>
 *
 * `requires` modifiers
 * --------------------
 *   requires       - usual dependency
 *   requires static - compile-time only (optional dependency)
 *   requires transitive - passed through to my consumers
 * <p>
 *
 * Open packages — the difference between `exports` and `opens`
 * ------------------------------------------------------------
 *      exports pkg       - compile-time AND runtime ACCESS for code
 *      opens   pkg       - additionally allows REFLECTION (setAccessible)
 * <p>
 *
 * Many frameworks (Spring, JPA, Jackson) need `opens` to access private
 * fields via reflection.
 * <p>
 *
 * Three kinds of modules
 * ----------------------
 *   - NAMED MODULES — declared in module-info.java.
 *   - AUTOMATIC MODULES — a regular jar on the module path; its name is
 *     derived from the jar filename or the Automatic-Module-Name manifest
 *     attribute. Exports ALL packages.
 *   - UNNAMED MODULE — the classpath. Reads every other module. Reverts
 *     to "no modules" behaviour. This is what THIS project uses.
 * <p>
 *
 * Status of this repo
 * -------------------
 * The HelloJava project is INTENTIONALLY NOT modularised. Modules
 * add ceremony that fights with the per-folder tutorial layout this
 * repo uses. This section is REFERENCE MATERIAL — read the comments,
 * study the example module-info syntax, and use jdeps to explore.
 */

public class ModulesIntroduction {

    public static void main(String[] args) {

        section("1) Find which module the JVM is in");
        Module mine = ModulesIntroduction.class.getModule();
        System.out.println("class module name = " + mine.getName());  // null = unnamed module
        System.out.println("isNamed?          = " + mine.isNamed());
        System.out.println("layer             = " + mine.getLayer());

        section("2) JDK modules — discoverable at runtime");
        ModuleLayer.boot().modules().stream()
                .map(Module::getName)
                .sorted()
                .limit(15)
                .forEach(name -> System.out.println("  " + name));
        System.out.println("  ... (" + ModuleLayer.boot().modules().size() + " modules total)");

        section("3) Where modules live in the JDK");
        System.out.println("  java.base       - lang, util, io, nio");
        System.out.println("  java.sql        - JDBC types");
        System.out.println("  java.net.http   - HTTP/2 + WebSocket client");
        System.out.println("  java.logging    - java.util.logging");
        System.out.println("  java.compiler   - the javac API");
        System.out.println("  jdk.compiler    - javac implementation");
        System.out.println("  java.management - JMX");

        section("4) Compile and run with modules");
        System.out.println("  javac -d out --module-source-path src $(find src -name '*.java')");
        System.out.println("  java  --module-path out -m com.example.app/com.example.app.Main");
        System.out.println("  jlink --module-path \"$JAVA_HOME/jmods\":out --add-modules com.example.app --output myapp-runtime");

        section("5) Diagnose dependencies — jdeps");
        System.out.println("  jdeps --module-path out myapp.jar");
        System.out.println("  jdeps --generate-module-info ./mods classpath.jar");

        section("done — see ModuleExamples.java and ServiceLoaderDemo.java");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
