package Phase4_ErrorsAndTypeSafety.ExceptionHandling;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Null Pointer Exceptions (NPE)
 * -----------------------------
 * The famous NullPointerException is thrown when your code tries to USE a
 * variable that holds `null` as if it were a real object. Six operations
 * can trigger it:
 *
 *   1. Calling an instance method on null         null.foo()
 *   2. Reading or writing an instance field       null.f = 1
 *   3. Indexing into a null array                 ((int[]) null)[0]
 *   4. Throwing a null Throwable                  throw null
 *   5. Acquiring the monitor of null              synchronized (null) { }
 *   6. Auto-unboxing a null wrapper               int x = (Integer) null
 *
 * Anything else (e.g. comparing with `==`) is safe.
 *
 *
 * Helpful NullPointerException (Java 14+)
 * ---------------------------------------
 * Since Java 14 the JVM enhances the NPE message to tell you EXACTLY WHICH
 * variable was null. Compare:
 *
 *      OLD : Exception in thread "main" java.lang.NullPointerException
 *      NEW : Cannot invoke "String.length()" because "name" is null
 *
 * Run with -XX:+ShowCodeDetailsInExceptionMessages on Java 14 (enabled by
 * default since Java 15). Project SDK is 21 in this repo, so you get it
 * automatically.
 *
 *
 * How To Avoid NPEs
 * -----------------
 *   - PROGRAM TO IMMUTABILITY      records, final fields, value-like types.
 *   - PREFER EMPTY OVER NULL       return List.of(), "", or Optional.empty().
 *   - USE Objects.requireNonNull   fail FAST at the boundary with a clear msg.
 *   - USE Optional<T>              for "value might be absent" return types.
 *   - DEFENSIVE checks at edges    APIs / user input / parsed data.
 *   - SAFE-NAVIGATION patterns     a == null ? "" : a.toUpperCase()
 *   - REACH for libraries          @NonNull / @Nullable annotations + tools.
 *
 *
 * Optional - the modern null replacement
 * --------------------------------------
 *      Optional.empty()                    // explicit "no value"
 *      Optional.of(x)                      // x must NOT be null
 *      Optional.ofNullable(maybeX)         // accepts null
 *      opt.isPresent() / isEmpty()
 *      opt.orElse(default) / orElseGet / orElseThrow
 *      opt.map(...).flatMap(...).filter(...)
 *
 * Optional is meant for RETURN TYPES, not fields or parameters - the JDK
 * authors are explicit about that.
 */

public class NullPointerExceptions {

    public static void main(String[] args) {

        section("1) Six common NPE triggers (each in its own try)");

        // 1. Method call on null
        try {
            String s = null;
            s.length();
        } catch (NullPointerException e) {
            System.out.println("method on null   : " + e.getMessage());
        }

        // 2. Field access on null
        try {
            Box b = null;
            b.value = 1;
        } catch (NullPointerException e) {
            System.out.println("field access     : " + e.getMessage());
        }

        // 3. Indexing a null array
        try {
            int[] a = null;
            int v = a[0];
            System.out.println(v);
        } catch (NullPointerException e) {
            System.out.println("array index      : " + e.getMessage());
        }

        // 4. Throwing null
        try {
            Throwable t = null;
            throw t;
        } catch (Throwable t) {
            System.out.println("throw null       : " + t.getClass().getSimpleName());
        }

        // 5. synchronized on null
        try {
            Object lock = null;
            synchronized (lock) { /* ... */ }
        } catch (NullPointerException e) {
            System.out.println("synchronized null: " + e.getMessage());
        }

        // 6. Auto-unboxing a null Integer
        try {
            Integer maybe = null;
            int prim = maybe;
            System.out.println(prim);
        } catch (NullPointerException e) {
            System.out.println("unbox null       : " + e.getMessage());
        }

        section("2) Helpful NPE message - which variable was null?");
        try {
            // A small chain so the helpful-NPE message has something interesting to say.
            Person p = new Person(null);
            System.out.println(p.address().street());          // NPE here
        } catch (NullPointerException e) {
            System.out.println("message: " + e.getMessage());
            // On Java 14+ the message will be:
            //   Cannot invoke "...Address.street()" because the return value of
            //   "...Person.address()" is null
        }

        section("3) Defensive validation with Objects.requireNonNull");
        try {
            greet(null);
        } catch (NullPointerException e) {
            System.out.println("caught: " + e.getMessage());   // friendly message
        }
        greet("Deepak");                                       // OK

        section("4) The null-coalesce pattern with Objects.requireNonNullElse");
        String name = null;
        String safe = Objects.requireNonNullElse(name, "<anonymous>");
        System.out.println("safe name = " + safe);

        // Lazy variant - only computed when needed:
        String safeLazy = Objects.requireNonNullElseGet(name, () -> "computed default");
        System.out.println("safeLazy  = " + safeLazy);

        section("5) Returning empty collections instead of null");
        // findAll() returns List.of() if there are no matches - safe to iterate.
        for (String n : findAll(false)) {
            System.out.println("  found: " + n);
        }
        System.out.println("  list size = " + findAll(false).size());     // 0, no NPE

        section("6) Optional for might-be-absent return values");
        Optional<String> hit = lookup(1);
        Optional<String> miss = lookup(999);

        System.out.println("hit.orElse(unknown) = " + hit.orElse("unknown"));
        System.out.println("miss.orElse(unknown)= " + miss.orElse("unknown"));

        // Chain transformations safely
        String display = lookup(1)
                .map(String::toUpperCase)
                .filter(s -> s.length() > 0)
                .orElse("DEFAULT");
        System.out.println("display = " + display);

        section("7) Pre-Java-8: the manual null-check pattern");
        Map<String, String> cfg = Map.of("host", "localhost");
        String host = cfg.get("host");
        if (host != null) {
            System.out.println("host = " + host);
        }
        // versus the modern way:
        cfg.entrySet().forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));

        // OUTPUT
        // (matches the inline comments above; helpful NPE messages will vary
        //  in wording depending on the JVM version)
    }

    // --- helpers ---

    static class Box { int value; }

    static class Person {
        private final Address address;
        Person(Address address) { this.address = address; }
        public Address address() { return address; }
    }
    static class Address {
        private final String street;
        Address(String street) { this.street = street; }
        public String street() { return street; }
    }

    /** Validates input at the boundary - throws with a clear message. */
    static void greet(String name) {
        Objects.requireNonNull(name, "name must not be null");
        System.out.println("Hello, " + name + "!");
    }

    /** Returns an empty list instead of null. */
    static List<String> findAll(boolean any) {
        return any ? List.of("Alice", "Bob") : List.of();
    }

    /** Returns Optional instead of null. */
    static Optional<String> lookup(int id) {
        Map<Integer, String> table = Map.of(1, "alpha", 2, "beta");
        return Optional.ofNullable(table.get(id));
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
