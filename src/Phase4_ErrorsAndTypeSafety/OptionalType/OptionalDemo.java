package Phase4_ErrorsAndTypeSafety.OptionalType;

import java.util.*;
import java.util.stream.Stream;

/**
 * Optional&lt;T&gt;
 * -----------
 * A container that holds EITHER a value or no value. Used to make
 * "this method may not return anything" explicit in the type system —
 * removes the trap of forgetting to null-check.
 * <p>
 *
 *      Optional<User> u = repo.find(id);
 *      u.ifPresent(this::greet);
 * <p>
 *
 * What Optional is FOR
 * --------------------
 *   - Return values when "nothing" is a legitimate result.
 *   - Stream pipelines that may not find a match (findFirst, findAny,
 *     max, min, reduce).
 * <p>
 *
 * What Optional is NOT for
 * ------------------------
 *   - Fields. Use a real default value or a clearly-nullable field.
 *   - Method parameters. Just take a regular value and validate. Use
 *     overloads if "absent" is meaningful.
 *   - Collections. Use an empty collection instead.
 *   - Map values. Use Map.getOrDefault or .computeIfAbsent.
 * <p>
 *
 * Optional is HEAP-ALLOCATED and not free; using it everywhere is a
 * code smell. Use it where it earns its keep — return types.
 * <p>
 *
 * Primitive specialisations
 * -------------------------
 *      OptionalInt / OptionalLong / OptionalDouble
 * Avoid boxing overhead and pair with IntStream / LongStream / DoubleStream.
 * <p>
 *
 * Key API
 * -------
 *   of(t) / ofNullable(t) / empty()           - construction
 *   isPresent() / isEmpty()                    - probe
 *   get()                                       - extract (throws if empty)
 *   orElse(default) / orElseGet(supplier)      - safe extract
 *   orElseThrow() / orElseThrow(supplier)      - explicit error
 *   ifPresent(consumer) / ifPresentOrElse(...) - side-effect
 *   map(fn) / flatMap(fn) / filter(predicate)  - transform
 *   stream()                                    - Java 9+, 0-or-1 element Stream
 *   or(supplier)                                - Java 9+, fallback Optional
 */

public class OptionalDemo {

    public static void main(String[] args) {

        section("1) Construction");
        Optional<String> some = Optional.of("hello");
        Optional<String> none = Optional.empty();
        Optional<String> maybe = Optional.ofNullable(System.getenv("MAYBE_MISSING"));
        System.out.println("some  = " + some);
        System.out.println("none  = " + none);
        System.out.println("maybe = " + maybe);

        section("2) Safe extraction");
        System.out.println("orElse           = " + none.orElse("default"));
        System.out.println("orElseGet        = " + none.orElseGet(() -> "lazy default"));
        try { none.orElseThrow(); }
        catch (RuntimeException e) { System.out.println("orElseThrow      = " + e.getClass().getSimpleName()); }

        section("3) ifPresent / ifPresentOrElse");
        some.ifPresent(v -> System.out.println("  present -> " + v));
        none.ifPresentOrElse(
                v -> System.out.println("  present -> " + v),
                () -> System.out.println("  absent — running the else branch"));

        section("4) map / flatMap / filter");
        Optional<Integer> len = some.map(String::length);
        Optional<String>  big = some.filter(s -> s.length() > 3);
        Optional<String>  flat = Optional.of("alice").flatMap(OptionalDemo::lookup);
        System.out.println("length        = " + len);
        System.out.println("filter > 3    = " + big);
        System.out.println("flatMap lookup= " + flat);

        section("5) or() — fallback to ANOTHER Optional (Java 9+)");
        Optional<String> fallback = none.or(() -> Optional.of("alt"));
        System.out.println("or -> " + fallback);

        section("6) stream() — 0-or-1 element stream (Java 9+)");
        long count = Stream.of(Optional.of("a"), Optional.<String>empty(), Optional.of("c"))
                .flatMap(Optional::stream)
                .count();
        System.out.println("non-empty count = " + count);

        section("7) Primitive specialisations — avoid Optional<Integer>");
        OptionalInt firstEven = List.of(3, 5, 7, 8, 11).stream().mapToInt(Integer::intValue)
                .filter(n -> n % 2 == 0).findFirst();
        System.out.println("first even = " + firstEven.orElse(-1));

        OptionalDouble avg = List.of(1, 2, 3, 4, 5).stream().mapToInt(Integer::intValue).average();
        System.out.println("avg        = " + avg.orElse(Double.NaN));

        OptionalLong maxL = List.of(1L, 100L, 50L).stream().mapToLong(Long::longValue).max();
        System.out.println("max long   = " + maxL.orElse(-1L));

        section("8) Anti-patterns to avoid");
        System.out.println("  - Optional fields in domain objects");
        System.out.println("  - Optional method parameters");
        System.out.println("  - Optional<Collection> — use empty collection");
        System.out.println("  - calling get() without isPresent()");

        section("done");
    }

    /** A pretend lookup that may or may not return a value. */
    private static Optional<String> lookup(String name) {
        if (name.startsWith("a")) return Optional.of("found " + name);
        return Optional.empty();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
