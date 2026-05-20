package Basics.Modules;

/**
 * Module Examples — sample `module-info.java` shapes
 * --------------------------------------------------
 * This file is a CATALOGUE of realistic module declarations. The
 * project itself is unnamed (no module-info.java), so the snippets are
 * inside comments — copy them into a modularised project to use.
 *
 *
 * 1) Tiny library module
 * ----------------------
 *      module com.example.utils {
 *          exports com.example.utils;
 *      }
 *
 *
 * 2) Library + internal helpers
 * -----------------------------
 *      module com.example.app {
 *          requires java.net.http;
 *          requires java.logging;
 *
 *          exports com.example.app.api;             // public
 *          // com.example.app.internal NOT exported - module-private
 *      }
 *
 *
 * 3) Web app using Spring (needs reflection access)
 * -------------------------------------------------
 *      module com.example.webapp {
 *          requires spring.boot.autoconfigure;
 *          requires spring.web;
 *          requires spring.context;
 *          requires java.sql;
 *
 *          exports com.example.webapp.controller;
 *
 *          // Spring needs reflection access to inject and proxy:
 *          opens com.example.webapp.controller   to spring.core;
 *          opens com.example.webapp.service      to spring.core;
 *          opens com.example.webapp.config       to spring.core;
 *      }
 *
 *
 * 4) Compile-time-only dependency
 * -------------------------------
 *      module com.example.tooling {
 *          // We use Lombok annotations at compile time only.
 *          requires static lombok;
 *          exports com.example.tooling;
 *      }
 *
 *
 * 5) Transitive dependency
 * ------------------------
 *      module com.example.core {
 *          requires transitive org.slf4j;          // consumers see slf4j too
 *          exports com.example.core;
 *      }
 *
 *
 * 6) Service provider
 * -------------------
 *      module com.example.payments.stripe {
 *          requires com.example.payments.api;
 *
 *          provides com.example.payments.api.Gateway
 *              with com.example.payments.stripe.StripeGateway;
 *      }
 *
 *      module com.example.payments.api {
 *          exports com.example.payments.api;
 *          uses    com.example.payments.api.Gateway;
 *      }
 *
 *
 * 7) Open module (rarely a great idea)
 * ------------------------------------
 *      open module com.legacy.app {
 *          requires java.sql;
 *          exports com.legacy.app.api;
 *          // Every package is implicitly `opens` — full reflection.
 *      }
 *
 *
 * 8) Compatibility — automatic + unnamed
 * --------------------------------------
 * A plain jar with no module-info.java becomes an AUTOMATIC module if
 * placed on the --module-path. Recommended: add to your jar's MANIFEST.MF:
 *
 *      Automatic-Module-Name: com.example.legacy
 *
 * so its module name is stable even before you write a real module-info.
 *
 *
 * 9) Multi-release module (Java 9+)
 * ---------------------------------
 * Jars can ship per-version classes under META-INF/versions/N/. Each
 * version uses the same module-info.java (or a per-version variant).
 *
 *
 * 10) `jdeps --generate-module-info`
 * ----------------------------------
 *      jdeps --generate-module-info ./mods build/libs/mylib.jar
 *
 * Reads a jar's bytecode and produces a stub module-info.java listing
 * its real dependencies.
 */

public class ModuleExamples {

    public static void main(String[] args) {
        section("This file is a catalogue — please open the source to read the snippets");
        // Reflective examples to show that the current process is in
        // the unnamed module:
        Module mine = ModuleExamples.class.getModule();
        System.out.println("unnamed?         = " + !mine.isNamed());
        System.out.println("reads java.base? = " + mine.canRead(String.class.getModule()));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
