package Phase6_RuntimeMemoryRegexReflection.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Dynamic Invocation
 * ------------------
 * Two faster / safer alternatives to Method.invoke:
 *
 *   1. MethodHandles — typed, closer to direct call speed.
 *   2. Dynamic Proxies — create a class at runtime that implements
 *                        a set of interfaces by routing every call
 *                        through an InvocationHandler.
 *
 *
 * MethodHandle vs Method
 * ----------------------
 *      Method                              MethodHandle
 *      ----------------------------        ----------------------------
 *      Reflective; older API                Indy-style invocation
 *      ~10x slower than direct              Approaches direct-call speed
 *      Wraps user exceptions                Throws user exceptions directly
 *      Auto-boxes primitives                Strongly typed
 *      Generic via getClass / String        Explicit MethodType
 *
 *
 * Dynamic Proxy — when to use
 * ---------------------------
 *   - Build a Mock object that records calls.
 *   - Add cross-cutting concerns (logging, timing, transactions) around
 *     a set of interfaces.
 *   - This is the trick Spring AOP uses for JDK-proxy beans.
 *
 *
 * Limitations
 * -----------
 *   - JDK Proxy supports INTERFACES only. For proxying classes, use
 *     CGLIB or ByteBuddy or the JDK's Hidden Classes via ASM.
 *   - JDK 21 still bans creating proxies for sealed interfaces unless
 *     all permitted subclasses are proxied.
 */

public class DynamicInvocation {

    /** Service interface to demo dynamic proxies on. */
    public interface Greeting {
        String greet(String name);
        default String farewell(String name) { return "bye " + name; }
    }

    /** Concrete implementation. */
    public static class GreetingImpl implements Greeting {
        public String greet(String name) { return "hello " + name; }
    }

    public static void main(String[] args) throws Throwable {

        // --------------- 1) MethodHandle path ---------------
        section("1) MethodHandle — strongly-typed dynamic call");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle = lookup.findVirtual(
                String.class, "toUpperCase", MethodType.methodType(String.class));
        Object upper = handle.invoke("hello");                // returns "HELLO"
        System.out.println("uppered = " + upper);

        section("2) Static MethodHandle");
        MethodHandle parse = lookup.findStatic(
                Integer.class, "parseInt", MethodType.methodType(int.class, String.class));
        int n = (int) parse.invokeExact("12345");             // exact: types must match
        System.out.println("parsed = " + n);

        section("3) Bind arguments");
        MethodHandle bound = parse.bindTo(null);              // (static) - just for show
        System.out.println("bound parse = " + bound);

        // --------------- 4) Dynamic Proxy path ---------------
        section("4) Dynamic Proxy — wrap an interface to log every call");
        Greeting real = new GreetingImpl();
        Greeting proxy = (Greeting) Proxy.newProxyInstance(
                Greeting.class.getClassLoader(),
                new Class<?>[]{Greeting.class},
                new LoggingHandler(real));

        System.out.println(proxy.greet("alice"));
        System.out.println(proxy.farewell("bob"));

        section("5) Mocking pattern — proxy that ignores the target");
        Greeting mock = (Greeting) Proxy.newProxyInstance(
                Greeting.class.getClassLoader(),
                new Class<?>[]{Greeting.class},
                (p, method, methodArgs) -> {
                    System.out.println("  intercepted " + method.getName()
                            + (methodArgs == null ? "" : " args=" + methodArgs[0]));
                    return "mocked!";
                });
        System.out.println(mock.greet("zara"));

        section("done");
    }

    /** InvocationHandler that times each call and forwards to the target. */
    static class LoggingHandler implements InvocationHandler {
        private final Object target;
        LoggingHandler(Object target) { this.target = target; }
        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long t0 = System.nanoTime();
            try {
                Object out = method.invoke(target, args);
                long us = (System.nanoTime() - t0) / 1_000;
                System.out.println("[" + method.getName() + " took " + us + "us]");
                return out;
            } catch (java.lang.reflect.InvocationTargetException ite) {
                throw ite.getCause();
            }
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
