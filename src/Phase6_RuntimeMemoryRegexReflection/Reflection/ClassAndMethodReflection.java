package Phase6_RuntimeMemoryRegexReflection.Reflection;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Class, Method, Field — in depth
 * --------------------------------
 * Most useful inspection / manipulation patterns.
 * <p>
 *
 * Finding the right method
 * ------------------------
 *   getMethod("name", paramTypes...)            - public only, includes inherited
 *   getDeclaredMethod("name", paramTypes...)    - includes private, this class only
 *   getMethods()                                  - all PUBLIC (incl. inherited)
 *   getDeclaredMethods()                          - declared here, any access
 * <p>
 *
 * For OVERLOADED methods you must match parameter types EXACTLY.
 * <p>
 *
 * Invocation
 * ----------
 *   method.invoke(instance, args...)
 * <p>
 *
 *   - Static method? Pass null for instance.
 *   - Returns Object (boxed for primitives).
 *   - Wraps user exceptions in InvocationTargetException — unwrap with
 *     getCause().
 * <p>
 *
 * Fields
 * ------
 *   field.get(instance)     - read
 *   field.set(instance, v)  - write
 *   field.getInt(instance)  - primitive variant (faster, no boxing)
 * <p>
 *
 *   - Final fields: writable via setAccessible(true) ONLY on instance
 *     fields, NOT on `static final` constants (the JIT may have inlined
 *     them).
 * <p>
 *
 * Modifiers
 * ---------
 *   int m = method.getModifiers();
 *   Modifier.isStatic(m), isFinal(m), isPublic(m), isPrivate(m), ...
 * <p>
 *
 * Parameters (Java 8+, requires `-parameters` at compile time for names)
 * ----------------------------------------------------------------------
 *   method.getParameters() -> Parameter[]
 *   parameter.getName(), .getType(), .isVarArgs()
 * <p>
 *
 * Generic type info
 * -----------------
 *   field.getGenericType()                  - includes <T>
 *   method.getGenericReturnType()
 *   method.getGenericParameterTypes()
 *   ParameterizedType type = (ParameterizedType) field.getGenericType();
 *   type.getActualTypeArguments()           -> [String.class]
 */

public class ClassAndMethodReflection {

    public static class Counter {
        private int n;
        private static int instances;

        public Counter() { instances++; }

        public int  add(int delta)        { return n += delta; }
        public int  add(long longDelta)   { return n += (int) longDelta; }   // overload
        public int  current()             { return n; }
        public static int totalInstances(){ return instances; }
    }

    public static void main(String[] args) throws Exception {

        section("1) Find a specific overload by parameter types");
        Method addInt  = Counter.class.getMethod("add", int.class);
        Method addLong = Counter.class.getMethod("add", long.class);
        System.out.println("found: " + addInt);
        System.out.println("found: " + addLong);

        section("2) Invoke instance + static methods");
        Counter c = Counter.class.getDeclaredConstructor().newInstance();
        Object r1 = addInt.invoke(c, 5);
        Object r2 = addLong.invoke(c, 10L);
        System.out.println("after add(5)+add(10) = " + Counter.class.getMethod("current").invoke(c));
        Object total = Counter.class.getMethod("totalInstances").invoke(null);  // static
        System.out.println("instances = " + total);

        section("3) Field access");
        Field n = Counter.class.getDeclaredField("n");
        n.setAccessible(true);
        System.out.println("n via reflection = " + n.getInt(c));
        n.setInt(c, 100);
        System.out.println("after set        = " + c.current());

        section("4) Modifiers");
        for (Method m : Counter.class.getDeclaredMethods()) {
            String mods = Modifier.toString(m.getModifiers());
            System.out.println("  " + m.getName() + "(" + Arrays.toString(m.getParameterTypes()) + ") : " + mods);
        }

        section("5) Parameters — requires -parameters at compile time for names");
        Method greet = Sample.class.getDeclaredMethod("greet", String.class, int.class);
        for (Parameter p : greet.getParameters()) {
            System.out.println("  " + p.getType().getSimpleName() + " " + p.getName());
        }

        section("6) Throw / InvocationTargetException — wrapped exceptions");
        try {
            Method boom = ClassAndMethodReflection.class.getDeclaredMethod("explode");
            boom.invoke(null);
        } catch (InvocationTargetException ite) {
            System.out.println("wrapped: " + ite.getCause());
        }

        section("7) Generic type info");
        Field samples = Sample.class.getDeclaredField("samples");
        System.out.println("type        = " + samples.getType().getName());
        System.out.println("genericType = " + samples.getGenericType());

        section("done");
    }

    static void explode() { throw new IllegalStateException("kaboom"); }

    /** Target with a parameter name (only readable if compiled with -parameters). */
    public static class Sample {
        public java.util.List<String> samples;
        public String greet(String name, int times) { return name + " x " + times; }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
