package Basics.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Reflection API — Introduction
 * -----------------------------
 * Reflection lets your code INSPECT and MANIPULATE other code at
 * runtime: classes, methods, fields, constructors, annotations,
 * generic information.
 *
 *
 * Why use it?
 * -----------
 *   - Frameworks: Spring autowiring, Hibernate entity mapping, Jackson
 *     JSON binding, JUnit test discovery — all built on reflection.
 *   - Plugins / service loaders.
 *   - Tooling: debuggers, profilers, IDE-level features.
 *   - Generic test helpers.
 *
 *
 * Why NOT to use it (in business code)
 * ------------------------------------
 *   - Slow vs direct method calls (JIT helps a lot but it's not free).
 *   - Bypasses compile-time type checks.
 *   - May break under the module system without `opens`.
 *   - Confusing stack traces (InvocationTargetException unwraps).
 *   - Hard to refactor — renaming a field becomes a runtime bomb.
 *
 *
 * The starting point: Class<?>
 * ----------------------------
 *   - Class<T> object describes a class loaded into the JVM.
 *   - Three ways to obtain one:
 *
 *       String.class
 *       someString.getClass()
 *       Class.forName("java.lang.String")
 *
 *
 * The headline types in java.lang.reflect
 * ---------------------------------------
 *   Class<?>          - a class itself
 *   Method            - a method of a class
 *   Field             - a field of a class
 *   Constructor<T>    - a constructor
 *   Parameter         - a parameter of a method/constructor
 *   Modifier          - static helpers to decode int modifier bits
 *   Array             - reflective array creation/access
 *
 *
 * Java 9+ module restrictions
 * ---------------------------
 * Accessing PRIVATE members of a module-protected class requires the
 * target package to be `opens`-exported to your code. Use `setAccessible(true)`
 * only on things you're allowed to touch.
 */

public class ReflectionIntroduction {

    /** Sample target class for the demos. */
    public static class Greeter {
        private final String name;
        public Greeter(String name) { this.name = name; }
        public String greet(String tone) { return tone + ", " + name; }
        private String secret() { return "hidden"; }
    }

    public static void main(String[] args) throws Exception {

        section("1) Three ways to obtain a Class<?>");
        Class<?> c1 = String.class;
        Class<?> c2 = "hello".getClass();
        Class<?> c3 = Class.forName("java.lang.String");
        System.out.println(c1 + " == " + c2 + " == " + c3);

        section("2) Inspect a class");
        Class<Greeter> g = Greeter.class;
        System.out.println("name        = " + g.getName());
        System.out.println("simpleName  = " + g.getSimpleName());
        System.out.println("packageName = " + g.getPackageName());
        System.out.println("modifiers   = " + Modifier.toString(g.getModifiers()));
        System.out.println("super       = " + g.getSuperclass().getSimpleName());

        section("3) List declared methods, fields, constructors");
        for (Method m : g.getDeclaredMethods()) {
            System.out.println("  method:      " + m);
        }
        for (Field f : g.getDeclaredFields()) {
            System.out.println("  field:       " + f);
        }
        for (Constructor<?> ctor : g.getDeclaredConstructors()) {
            System.out.println("  constructor: " + ctor);
        }

        section("4) Instantiate via reflection");
        Constructor<Greeter> ctor = g.getDeclaredConstructor(String.class);
        Greeter ge = ctor.newInstance("alice");
        System.out.println("instance = " + ge.greet("hello"));

        section("5) Invoke a method by name");
        Method greet = g.getMethod("greet", String.class);
        Object result = greet.invoke(ge, "yo");
        System.out.println("invoke result = " + result);

        section("6) Read / write a field — including private");
        Field nameField = g.getDeclaredField("name");
        nameField.setAccessible(true);                       // bypass access check
        System.out.println("field 'name' = " + nameField.get(ge));
        // Note: 'name' is final; trying to set it on a record / record-like type
        // would be ignored after construction.

        section("7) Call a private method");
        Method secret = g.getDeclaredMethod("secret");
        secret.setAccessible(true);
        System.out.println("private secret() = " + secret.invoke(ge));

        section("done — see ClassAndMethodReflection.java and DynamicInvocation.java");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
