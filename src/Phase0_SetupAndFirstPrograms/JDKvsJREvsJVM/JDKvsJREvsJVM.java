package Phase0_SetupAndFirstPrograms.JDKvsJREvsJVM;

/**
 * JDK vs JRE vs JVM
 * -----------------
 * These three are the most commonly confused acronyms in the Java world. They
 * are nested - the JDK contains the JRE, and the JRE contains the JVM:
 * <p>
 *
 *      +----------------------------------------------+
 *      |                    JDK                       |
 *      |  (compiler javac, javadoc, jar, jshell, ...) |
 *      |   +----------------------------------+       |
 *      |   |              JRE                 |       |
 *      |   |  (Java class libraries, rt.jar)  |       |
 *      |   |   +--------------------------+   |       |
 *      |   |   |          JVM             |   |       |
 *      |   |   | (ClassLoader, Bytecode   |   |       |
 *      |   |   |  Verifier, Interpreter,  |   |       |
 *      |   |   |  JIT compiler, GC)       |   |       |
 *      |   |   +--------------------------+   |       |
 *      |   +----------------------------------+       |
 *      +----------------------------------------------+
 * <p>
 *
 * 1) JVM - Java Virtual Machine
 * -----------------------------
 * - An abstract specification of a virtual computer that runs Java bytecode.
 * - Provides platform independence: the same .class file runs on Windows, macOS,
 *   and Linux because each OS has its own JVM implementation.
 * - JVM is platform-dependent itself, but the bytecode it executes is not.
 * - Responsibilities:
 *     * Loading classes (ClassLoader subsystem)
 *     * Verifying bytecode (Bytecode Verifier)
 *     * Executing bytecode (Interpreter + Just-In-Time compiler)
 *     * Allocating memory and reclaiming it (Garbage Collector)
 *     * Managing native method calls (JNI)
 * <p>
 *
 * 2) JRE - Java Runtime Environment
 * ---------------------------------
 * - JVM + Java standard class libraries (java.lang, java.util, java.io, java.net...).
 * - Enough to RUN a compiled Java program but NOT to compile one.
 * - Was distributed separately until Java 8. Since Java 9, the JRE is part of the
 *   JDK and Oracle no longer ships a standalone JRE.
 * <p>
 *
 * 3) JDK - Java Development Kit
 * -----------------------------
 * - The full development kit: JRE + development tools.
 * - Required to COMPILE Java code.
 * - Tools shipped with the JDK:
 *     javac    - compiler
 *     java     - launcher for class files
 *     javadoc  - generates HTML documentation from Javadoc-style comments
 *     jar      - packages classes into .jar archives
 *     jshell   - interactive REPL (Java 9+)
 *     jdb      - Java debugger
 *     jlink    - creates a custom runtime image (Java 9+)
 * <p>
 *
 * Side-by-Side Comparison
 * -----------------------
 * +-----------+---------------------------+---------------------+----------------------+
 * |           | JDK                       | JRE                 | JVM                  |
 * +-----------+---------------------------+---------------------+----------------------+
 * | Purpose   | Develop + run Java apps   | Run Java apps       | Execute bytecode     |
 * | Contains  | JRE + dev tools           | JVM + libraries     | Spec implementation  |
 * | Platform  | Platform dependent        | Platform dependent  | Platform dependent   |
 * |           |                           |                     | (bytecode independent)|
 * | Need it?  | Required to compile       | Required to run     | Embedded in JRE/JDK  |
 * +-----------+---------------------------+---------------------+----------------------+
 * <p>
 *
 * JVM Internal Architecture (Deep Dive)
 * -------------------------------------
 * 1. Class Loader Subsystem
 *    - Bootstrap ClassLoader  -> loads core Java classes (java.lang.*)
 *    - Extension ClassLoader  -> loads classes from JAVA_HOME/lib/ext
 *    - Application ClassLoader-> loads classes from the application classpath
 *    Class loading happens in three phases:
 *      a) Loading      - locates and reads the .class file
 *      b) Linking      - verification + preparation (default values) + resolution
 *      c) Initialization - executes static blocks and assigns static fields
 * <p>
 *
 * 2. Runtime Data Areas (Memory)
 *    - Method Area (per JVM)  - class metadata, static variables, constant pool
 *    - Heap         (per JVM) - all objects and instance variables; GC works here
 *    - Stack    (per thread)  - frames per method call, local variables, partial results
 *    - PC Register (per thread) - address of the current bytecode instruction
 *    - Native Method Stack (per thread) - for native (C/C++) code
 * <p>
 *
 * 3. Execution Engine
 *    - Interpreter        - reads bytecode line by line (slow but starts fast)
 *    - JIT Compiler       - converts hot bytecode into native machine code at runtime
 *    - Garbage Collector  - reclaims unreachable objects from the heap
 * <p>
 *
 * 4. Native Method Interface (JNI)
 *    - Bridge between Java code and native code (libraries written in C/C++)
 * <p>
 *
 * Common Question - "Is JVM platform independent?"
 * ------------------------------------------------
 * NO. The JVM is platform DEPENDENT (different binaries for Windows / Mac / Linux).
 * The BYTECODE that the JVM consumes is platform INDEPENDENT.
 * <p>
 *
 * That is what enables WORA - Write Once, Run Anywhere.
 * <p>
 *
 * This file is documentation only. Running main() simply confirms which JVM
 * implementation is executing this code.
 */

public class JDKvsJREvsJVM {

    public static void main(String[] args) {
        // The "java.vm.*" properties identify the JVM in use.
        System.out.println("JVM Name      : " + System.getProperty("java.vm.name"));
        System.out.println("JVM Vendor    : " + System.getProperty("java.vm.vendor"));
        System.out.println("JVM Version   : " + System.getProperty("java.vm.version"));
        System.out.println("Spec Version  : " + System.getProperty("java.specification.version"));

        // Memory details from the Runtime API (driven by the JVM):
        Runtime rt = Runtime.getRuntime();
        System.out.println("Available processors : " + rt.availableProcessors());
        System.out.println("Max heap (MB)        : " + (rt.maxMemory() / (1024 * 1024)));
        System.out.println("Total heap (MB)      : " + (rt.totalMemory() / (1024 * 1024)));
        System.out.println("Free heap (MB)       : " + (rt.freeMemory() / (1024 * 1024)));

        // OUTPUT (sample - will vary by your machine):
        // JVM Name      : OpenJDK 64-Bit Server VM
        // JVM Vendor    : Eclipse Adoptium
        // JVM Version   : 21.0.2+13-LTS
        // Spec Version  : 21
        // Available processors : 8
        // Max heap (MB)        : 4096
        // Total heap (MB)      : 256
        // Free heap (MB)       : 248
    }
}
