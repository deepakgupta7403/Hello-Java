package Phase0_SetupAndFirstPrograms;

/**
 * <h1>Hello, World! &mdash; your first Java program</h1>
 *
 * <p>This is the classic starting point for learning any language. It defines a
 * single class holding a <code>main</code> method &mdash; the entry point the JVM
 * calls when the program runs &mdash; and prints one line of text to the console.</p>
 *
 * <h2>Anatomy of the program</h2>
 * <ul>
 *   <li><code>public class HelloWorld</code> &mdash; every Java program lives inside a class.</li>
 *   <li><code>public static void main(String[] args)</code> &mdash; the method the JVM runs first.</li>
 *   <li><code>System.out.println(...)</code> &mdash; writes a line of text to standard output.</li>
 * </ul>
 *
 * <h2>Compile and run</h2>
 * <pre>
 * javac HelloWorld.java     // produces HelloWorld.class (bytecode)
 * java  HelloWorld          // prints: Hello World
 * </pre>
 *
 * @author Deepak Gupta
 * @version 1.0
 * @since 2026-05-21
 */
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
