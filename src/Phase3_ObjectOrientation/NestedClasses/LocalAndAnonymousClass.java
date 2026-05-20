package Phase3_ObjectOrientation.NestedClasses;

import java.util.Comparator;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Local and Anonymous Classes
 * ---------------------------
 * Two ways to declare a class INSIDE A METHOD or other block. They:
 *
 *   - Can use the surrounding method's locals — but ONLY EFFECTIVELY
 *     FINAL ones (assigned once, never reassigned after).
 *   - Cannot be declared `public` / `protected` / `private` — they have
 *     no scope larger than the enclosing block.
 *   - Cannot have static members (except compile-time constants).
 *
 *
 * Local class
 * -----------
 *      void doStuff() {
 *          class Helper { ... }
 *          new Helper().something();
 *      }
 *
 *   - Has a name. Can be instantiated multiple times.
 *   - Can extend / implement anything.
 *   - Useful when you need TWO instances of the helper, or you want a
 *     constructor with arguments.
 *
 *
 * Anonymous class
 * ---------------
 *      Runnable r = new Runnable() {
 *          @Override public void run() { ... }
 *      };
 *
 *   - No name. Instantiated EXACTLY ONCE at the declaration site.
 *   - Can extend ONE class OR implement ONE interface — never both.
 *   - Constructor arguments forwarded to the parent.
 *
 *
 * Lambda vs anonymous class
 * -------------------------
 * If the target is a FUNCTIONAL interface (one abstract method),
 * prefer a lambda. Anonymous class wins when:
 *   - The target has MULTIPLE abstract methods.
 *   - You need to override `equals` / `hashCode` / `toString`.
 *   - You need a name for `this` (lambdas' `this` is the enclosing
 *     instance, not the lambda).
 *
 *
 * Capture rules
 * -------------
 *   - Outer fields:                  visible, read/write.
 *   - Outer LOCAL variables:         visible, READ-ONLY (effectively final).
 *   - The enclosing 'this':           accessible as Outer.this (anon/local).
 *
 *
 * Java 16+
 * --------
 *   Local records and local enums are allowed:
 *      void m() { record Pair(int a, int b) {} ... }
 */

public class LocalAndAnonymousClass {

    public static void main(String[] args) {

        section("1) Local class — named, scoped to the method");
        IntSupplier counter = makeCounter();
        System.out.println(counter.getAsInt());          // 1
        System.out.println(counter.getAsInt());          // 2
        System.out.println(counter.getAsInt());          // 3

        section("2) Local class with two instances");
        // Each call to makeCounter() returns its OWN counter state.
        IntSupplier a = makeCounter();
        IntSupplier b = makeCounter();
        a.getAsInt(); a.getAsInt(); a.getAsInt();
        System.out.println("a = " + a.getAsInt() + ", b = " + b.getAsInt());

        section("3) Anonymous class — single-use override");
        List<String> names = new java.util.ArrayList<>(List.of("dia", "alex", "ben"));
        names.sort(new Comparator<String>() {
            @Override public int compare(String x, String y) {
                return x.length() - y.length();          // sort by length
            }
        });
        System.out.println(names);

        section("4) The same comparator as a lambda — preferred");
        names.sort((x, y) -> x.length() - y.length());
        System.out.println(names);

        section("5) Anonymous class when a lambda WON'T do");
        // Implementing a NON-functional interface, or carrying state.
        Object o = new Object() {
            int hits;
            @Override public String toString() {
                hits++;
                return "anon visit #" + hits;
            }
        };
        System.out.println(o);
        System.out.println(o);
        System.out.println(o);

        section("6) Capture — variables must be effectively final");
        int captured = 7;
        Runnable r = () -> System.out.println("captured = " + captured);
        r.run();
        // captured = 8;     // <-- uncommenting breaks compilation (no longer effectively final)

        section("7) Local record (Java 16+)");
        record Pair(int a, int b) {}
        Pair p = new Pair(3, 4);
        System.out.println("local record = " + p);

        section("done");
    }

    /** Returns an IntSupplier that yields 1, 2, 3, ... — driven by a local class. */
    private static IntSupplier makeCounter() {
        // Local classes can hold their own state across calls — unlike a
        // lambda capturing a primitive (which would need a mutable box).
        class Counter implements IntSupplier {
            int n = 0;
            @Override public int getAsInt() { return ++n; }
        }
        return new Counter();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
