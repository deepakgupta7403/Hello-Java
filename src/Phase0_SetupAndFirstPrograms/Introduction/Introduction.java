package Phase0_SetupAndFirstPrograms.Introduction;

/**
 * <h1>Java &mdash; An Introduction</h1>
 *
 * <p>Java is a high-level, class-based, object-oriented programming language designed by
 * James Gosling at Sun Microsystems in 1995 (now owned by Oracle Corporation).
 * It follows the principle of WORA &mdash; "Write Once, Run Anywhere" &mdash; which means
 * compiled Java code can run on all platforms that support Java without the need
 * for recompilation.</p>
 *
 * <h2>History (Brief Timeline)</h2>
 * <ul>
 *   <li><b>1991</b> &mdash; Project "Oak" started by James Gosling at Sun Microsystems (for set-top boxes).</li>
 *   <li><b>1995</b> &mdash; Renamed to "Java" and officially released as Java 1.0.</li>
 *   <li><b>2006</b> &mdash; Sun released Java as open source under the GNU GPL.</li>
 *   <li><b>2010</b> &mdash; Oracle acquired Sun Microsystems and became the steward of Java.</li>
 *   <li><b>2014</b> &mdash; Java 8 introduced Lambdas, Streams, and the new Date/Time API.</li>
 *   <li><b>2018</b> &mdash; Java moved to a 6-month release cadence.</li>
 *   <li><b>2021</b> &mdash; Java 17 released as the next LTS (Long Term Support) version.</li>
 *   <li><b>2023</b> &mdash; Java 21 released as the latest LTS at the time, adding Virtual Threads.</li>
 * </ul>
 *
 * <h2>Why the name "Java"?</h2>
 * <p>The original name was "Oak" but it was already trademarked. The team picked "Java"
 * after the Java coffee that they consumed during long meetings &mdash; which is also why
 * the official Java logo is a steaming coffee cup.</p>
 *
 * <h2>Key Features of Java</h2>
 * <ul>
 *   <li><b>Simple</b> &mdash; Clean syntax derived from C/C++ but without complex features
 *       like explicit pointers, operator overloading, and manual memory management.</li>
 *   <li><b>Object-Oriented</b> &mdash; Everything in Java revolves around classes and objects.
 *       Supports the four OOP pillars: Encapsulation, Inheritance, Polymorphism, Abstraction.</li>
 *   <li><b>Platform Independent</b> &mdash; Java source code (.java) is compiled to bytecode (.class)
 *       that runs on any machine which has the JVM installed.</li>
 *   <li><b>Secure</b> &mdash; No explicit pointers, runs inside the sandbox of the JVM, and
 *       has a built-in Security Manager.</li>
 *   <li><b>Robust</b> &mdash; Strong memory management (Garbage Collector), exception handling,
 *       and type checking at compile-time and run-time.</li>
 *   <li><b>Multithreaded</b> &mdash; Built-in support for concurrent programming through the
 *       <code>java.lang.Thread</code> class and <code>java.util.concurrent</code> package.</li>
 *   <li><b>Architecture Neutral</b> &mdash; The bytecode is independent of the underlying hardware.</li>
 *   <li><b>Portable</b> &mdash; Primitive sizes are fixed (int is always 32 bits, long is always
 *       64 bits) &mdash; unlike C/C++ where the size depends on the platform.</li>
 *   <li><b>High Performance</b> &mdash; Just-In-Time (JIT) compiler translates bytecode into native
 *       machine code at runtime for faster execution.</li>
 *   <li><b>Distributed</b> &mdash; Supports networking and distributed computing through APIs like
 *       RMI, sockets, and HTTP client.</li>
 *   <li><b>Dynamic</b> &mdash; Java loads classes on demand (lazy loading) and supports
 *       reflection for introspecting code at runtime.</li>
 * </ul>
 *
 * <h2>Java Editions</h2>
 * <ul>
 *   <li><b>Java SE (Standard Edition)</b> &mdash; Core language and base APIs (java.lang, java.util etc.)</li>
 *   <li><b>Java EE (Enterprise Edition)</b> &mdash; Now Jakarta EE &mdash; APIs for web, enterprise apps
 *       (Servlets, JSP, EJB).</li>
 *   <li><b>Java ME (Micro Edition)</b> &mdash; For embedded and mobile devices.</li>
 *   <li><b>JavaFX</b> &mdash; For rich desktop GUI applications.</li>
 * </ul>
 *
 * <h2>How Java Works (One-Line Summary)</h2>
 * <pre>
 * Source (.java)  --javac--&gt;  Bytecode (.class)  --JVM--&gt;  Native Machine Code
 * </pre>
 *
 * <p>This file is documentation only. The first runnable program lives in
 * <code>src/Overview/HelloWorld.java</code>.</p>
 *
 * @author  Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
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
