package Phase0_SetupAndFirstPrograms.Introduction;

/**
 * Java - An Introduction
 * ----------------------
 * Java is a high-level, class-based, object-oriented programming language designed by
 * James Gosling at Sun Microsystems in 1995 (now owned by Oracle Corporation).
 * It follows the principle of WORA - "Write Once, Run Anywhere" - which means
 * compiled Java code can run on all platforms that support Java without the need
 * for recompilation.
 * <p>
 *
 * History (Brief Timeline)
 * ------------------------
 * 1991 - Project "Oak" started by James Gosling at Sun Microsystems (for set-top boxes).
 * 1995 - Renamed to "Java" and officially released as Java 1.0.
 * 2006 - Sun released Java as open source under the GNU GPL.
 * 2010 - Oracle acquired Sun Microsystems and became the steward of Java.
 * 2014 - Java 8 introduced Lambdas, Streams, and the new Date/Time API.
 * 2018 - Java moved to a 6-month release cadence.
 * 2021 - Java 17 released as the next LTS (Long Term Support) version.
 * 2023 - Java 21 released as the latest LTS at the time, adding Virtual Threads.
 * <p>
 *
 * Why the name "Java"?
 * --------------------
 * The original name was "Oak" but it was already trademarked. The team picked "Java"
 * after the Java coffee that they consumed during long meetings - which is also why
 * the official Java logo is a steaming coffee cup.
 * <p>
 *
 * Key Features of Java
 * --------------------
 * 1. Simple        - Clean syntax derived from C/C++ but without complex features
 *                    like explicit pointers, operator overloading, and manual memory
 *                    management.
 * 2. Object-Oriented - Everything in Java revolves around classes and objects.
 *                      Supports the four OOP pillars: Encapsulation, Inheritance,
 *                      Polymorphism, Abstraction.
 * 3. Platform Independent - Java source code (.java) is compiled to bytecode (.class)
 *                           that runs on any machine which has the JVM installed.
 * 4. Secure        - No explicit pointers, runs inside the sandbox of the JVM, and
 *                    has a built-in Security Manager.
 * 5. Robust        - Strong memory management (Garbage Collector), exception handling,
 *                    and type checking at compile-time and run-time.
 * 6. Multithreaded - Built-in support for concurrent programming through the
 *                    java.lang.Thread class and java.util.concurrent package.
 * 7. Architecture Neutral - The bytecode is independent of the underlying hardware.
 * 8. Portable      - Primitive sizes are fixed (int is always 32 bits, long is always
 *                    64 bits) - unlike C/C++ where the size depends on the platform.
 * 9. High Performance - Just-In-Time (JIT) compiler translates bytecode into native
 *                       machine code at runtime for faster execution.
 * 10. Distributed  - Supports networking and distributed computing through APIs like
 *                    RMI, sockets, and HTTP client.
 * 11. Dynamic      - Java loads classes on demand (lazy loading) and supports
 *                    reflection for introspecting code at runtime.
 * <p>
 *
 * Java Editions
 * -------------
 * - Java SE (Standard Edition)   - Core language and base APIs (java.lang, java.util etc.)
 * - Java EE (Enterprise Edition) - Now Jakarta EE - APIs for web, enterprise apps
 *                                  (Servlets, JSP, EJB).
 * - Java ME (Micro Edition)      - For embedded and mobile devices.
 * - JavaFX                       - For rich desktop GUI applications.
 * <p>
 *
 * How Java Works (One-Line Summary)
 * ---------------------------------
 *      Source (.java)  --javac-->  Bytecode (.class)  --JVM-->  Native Machine Code
 * <p>
 *
 * This file is documentation only. The first runnable program lives in
 * src/Overview/HelloWorld.java.
 */

public class Introduction {

    public static void main(String[] args) {
        System.out.println("Welcome to Java!");
        System.out.println("Java version running this code: " + System.getProperty("java.version"));
        System.out.println("Java vendor: " + System.getProperty("java.vendor"));
        System.out.println("Operating system: " + System.getProperty("os.name"));

        // OUTPUT (will vary by your machine):
        // Welcome to Java!
        // Java version running this code: 21.0.2
        // Java vendor: Oracle Corporation
        // Operating system: Windows 11
    }
}
