package Phase0_SetupAndFirstPrograms.JDKvsJREvsJVM;

/**
 * <h1>JDK vs JRE vs JVM</h1>
 *
 * <p>These three are the most commonly confused acronyms in the Java world. They
 * are nested &mdash; the JDK contains the JRE, and the JRE contains the JVM:</p>
 *
 * <pre>
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
 * </pre>
 *
 * <h2>1) JVM &mdash; Java Virtual Machine</h2>
 * <ul>
 *   <li>An abstract specification of a virtual computer that runs Java bytecode.</li>
 *   <li>Provides platform independence: the same <code>.class</code> file runs on Windows, macOS,
 *       and Linux because each OS has its own JVM implementation.</li>
 *   <li>JVM is platform-dependent itself, but the bytecode it executes is not.</li>
 *   <li>Responsibilities:
 *     <ul>
 *       <li>Loading classes (ClassLoader subsystem)</li>
 *       <li>Verifying bytecode (Bytecode Verifier)</li>
 *       <li>Executing bytecode (Interpreter + Just-In-Time compiler)</li>
 *       <li>Allocating memory and reclaiming it (Garbage Collector)</li>
 *       <li>Managing native method calls (JNI)</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>2) JRE &mdash; Java Runtime Environment</h2>
 * <ul>
 *   <li>JVM + Java standard class libraries (<code>java.lang</code>, <code>java.util</code>,
 *       <code>java.io</code>, <code>java.net</code>...).</li>
 *   <li>Enough to RUN a compiled Java program but NOT to compile one.</li>
 *   <li>Was distributed separately until Java 8. Since Java 9, the JRE is part of the
 *       JDK and Oracle no longer ships a standalone JRE.</li>
 * </ul>
 *
 * <h2>3) JDK &mdash; Java Development Kit</h2>
 * <ul>
 *   <li>The full development kit: JRE + development tools.</li>
 *   <li>Required to COMPILE Java code.</li>
 *   <li>Tools shipped with the JDK:
 *     <pre>
 *     javac    - compiler
 *     java     - launcher for class files
 *     javadoc  - generates HTML documentation from Javadoc-style comments
 *     jar      - packages classes into .jar archives
 *     jshell   - interactive REPL (Java 9+)
 *     jdb      - Java debugger
 *     jlink    - creates a custom runtime image (Java 9+)
 *     </pre>
 *   </li>
 * </ul>
 *
 * <h2>Side-by-Side Comparison</h2>
 * <pre>
 * +-----------+---------------------------+---------------------+----------------------+
 * |           | JDK                       | JRE                 | JVM                  |
 * +-----------+---------------------------+---------------------+----------------------+
 * | Purpose   | Develop + run Java apps   | Run Java apps       | Execute bytecode     |
 * | Contains  | JRE + dev tools           | JVM + libraries     | Spec implementation  |
 * | Platform  | Platform dependent        | Platform dependent  | Platform dependent   |
 * |           |                           |                     | (bytecode independent)|
 * | Need it?  | Required to compile       | Required to run     | Embedded in JRE/JDK  |
 * +-----------+---------------------------+---------------------+----------------------+
 * </pre>
 *
 * <h2>JVM Internal Architecture (Deep Dive)</h2>
 *
 * <p><b>1. Class Loader Subsystem</b></p>
 * <ul>
 *   <li>Bootstrap ClassLoader &mdash; loads core Java classes (<code>java.lang.*</code>)</li>
 *   <li>Extension ClassLoader &mdash; loads classes from <code>JAVA_HOME/lib/ext</code></li>
 *   <li>Application ClassLoader &mdash; loads classes from the application classpath</li>
 * </ul>
 * <p>Class loading happens in three phases:</p>
 * <ul>
 *   <li><b>Loading</b> &mdash; locates and reads the <code>.class</code> file</li>
 *   <li><b>Linking</b> &mdash; verification + preparation (default values) + resolution</li>
 *   <li><b>Initialization</b> &mdash; executes static blocks and assigns static fields</li>
 * </ul>
 *
 * <p><b>2. Runtime Data Areas (Memory)</b></p>
 * <pre>
 *    - Method Area (per JVM)  - class metadata, static variables, constant pool
 *    - Heap         (per JVM) - all objects and instance variables; GC works here
 *    - Stack    (per thread)  - frames per method call, local variables, partial results
 *    - PC Register (per thread) - address of the current bytecode instruction
 *    - Native Method Stack (per thread) - for native (C/C++) code
 * </pre>
 *
 * <p><b>3. Execution Engine</b></p>
 * <ul>
 *   <li><b>Interpreter</b> &mdash; reads bytecode line by line (slow but starts fast)</li>
 *   <li><b>JIT Compiler</b> &mdash; converts hot bytecode into native machine code at runtime</li>
 *   <li><b>Garbage Collector</b> &mdash; reclaims unreachable objects from the heap</li>
 * </ul>
 *
 * <p><b>4. Native Method Interface (JNI)</b></p>
 * <ul>
 *   <li>Bridge between Java code and native code (libraries written in C/C++)</li>
 * </ul>
 *
 * <h2>Common Question &mdash; "Is JVM platform independent?"</h2>
 * <p>NO. The JVM is platform DEPENDENT (different binaries for Windows / Mac / Linux).
 * The BYTECODE that the JVM consumes is platform INDEPENDENT.</p>
 *
 * <p>That is what enables WORA &mdash; Write Once, Run Anywhere.</p>
 *
 * <p>This file is documentation only. Running <code>main()</code> simply confirms which JVM
 * implementation is executing this code.</p>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
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
