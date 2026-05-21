package Phase4_ErrorsAndTypeSafety.Generics;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modern Generics (Java 7 -&gt; 21)
 * ------------------------------
 * Generics arrived in Java 5 but the syntax has been refined over many
 * releases. A short tour of the additions that matter.
 * <p>
 *
 * Java 7 - Diamond Operator
 * -------------------------
 * No need to repeat type arguments on the right-hand side:
 * <p>
 *
 *      Map&lt;String, List&lt;Integer&gt;&gt; m = new HashMap&lt;&gt;();
 *      // instead of:
 *      Map&lt;String, List&lt;Integer&gt;&gt; m = new HashMap&lt;String, List&lt;Integer&gt;&gt;();
 * <p>
 *
 * Java 8 - Lambda + Target Type Inference
 * ---------------------------------------
 * Lambdas pick up generic type information from their TARGET type:
 * <p>
 *
 *      Function&lt;String, Integer&gt; len = s -&gt; s.length();
 *      Comparator&lt;Person&gt; byAge      = (a, b) -&gt; Integer.compare(a.age, b.age);
 * <p>
 *
 * Generic methods returning functional interfaces let you build elegant
 * pipelines: Stream&lt;T&gt;, Optional&lt;T&gt;, Function&lt;T, R&gt;, ...
 * <p>
 *
 * Java 9 - Private interface methods + diamond with anonymous classes
 * -------------------------------------------------------------------
 *      Comparator&lt;Integer&gt; cmp = new Comparator&lt;&gt;() {        // diamond on anonymous
 *          public int compare(Integer a, Integer b) { return a - b; }
 *      };
 * <p>
 *
 * Java 10 - var + Generics
 * ------------------------
 *      var list = new ArrayList&lt;String&gt;();                    // OK - infers List/ArrayList<String>
 *      var list = new ArrayList&lt;&gt;();                          // BAD - infers ArrayList<Object>
 * <p>
 *
 * Java 16 - Records with type parameters
 * --------------------------------------
 *      record Pair&lt;K, V&gt;(K key, V value) { }
 * <p>
 *
 * Generic records compose well with collectors, streams, and pattern
 * matching.
 * <p>
 *
 * Java 17 - Sealed generic interfaces
 * -----------------------------------
 *      sealed interface Result&lt;T, E&gt; permits Ok, Err { }
 *      record Ok&lt;T, E&gt;(T value)  implements Result&lt;T, E&gt; { }
 *      record Err&lt;T, E&gt;(E error) implements Result&lt;T, E&gt; { }
 * <p>
 *
 * Java 21 - Pattern matching for switch on generic types
 * ------------------------------------------------------
 *      Result&lt;String, Throwable&gt; r = ...;
 *      String s = switch (r) {
 *          case Ok&lt;String, Throwable&gt;  ok  -&gt; ok.value();
 *          case Err&lt;String, Throwable&gt; err -&gt; "fail: " + err.error();
 *      };
 * <p>
 *
 * Generic record patterns can also DECONSTRUCT:
 * <p>
 *
 *      case Ok&lt;String, ?&gt;(String value) -&gt; ...
 */

public class ModernGenerics {

    // ============================================================
    // Generic record (Java 16+)
    // ============================================================
    record Pair<K, V>(K key, V value) {}

    // ============================================================
    // Sealed generic interface (Java 17+)
    // ============================================================
    sealed interface Result<T, E> {
        record Ok<T, E>(T value)  implements Result<T, E> {}
        record Err<T, E>(E error) implements Result<T, E> {}
    }

    public static void main(String[] args) {

        section("1) Java 7 - the diamond operator");
        Map<String, List<Integer>> index = new java.util.HashMap<>();   // no <String, List<Integer>> on the right
        index.computeIfAbsent("even", k -> new ArrayList<>()).add(2);
        index.computeIfAbsent("odd",  k -> new ArrayList<>()).add(1);
        System.out.println("index = " + index);

        section("2) Java 8 - lambdas + generic functional interfaces");
        Function<String, Integer> length = String::length;
        Comparator<String> byLen = Comparator.comparingInt(length::apply);
        var names = new java.util.ArrayList<>(List.of("Charlie", "Alice", "Bob"));
        names.sort(byLen);
        System.out.println("by length = " + names);

        section("3) Java 9 - diamond on anonymous classes");
        Comparator<Integer> reverseInts = new Comparator<>() {
            @Override public int compare(Integer a, Integer b) {
                return Integer.compare(b, a);
            }
        };
        var nums = new java.util.ArrayList<>(List.of(3, 1, 4, 1, 5));
        nums.sort(reverseInts);
        System.out.println("reversed = " + nums);

        section("4) Java 10 - var + generics (the good case)");
        var people = new ArrayList<Pair<String, Integer>>();   // var knows the type
        people.add(new Pair<>("Alice", 30));
        people.add(new Pair<>("Bob",   25));
        // Type of `people`: ArrayList<Pair<String, Integer>>
        System.out.println("people = " + people);

        section("5) Java 10 - var + diamond  (the BAD case)");
        // var WITHOUT type arguments collapses everything to Object - a real
        // source of bugs:
        var loose = new ArrayList<>();           // ArrayList<Object>
        loose.add("hello");
        loose.add(42);
        System.out.println("loose type really is: " + loose.getClass() +
                "  - but the elements lost their compile-time type info");

        section("6) Java 16+ - generic record");
        Pair<String, Integer> p = new Pair<>("score", 95);
        System.out.println("record = " + p);
        // equals/hashCode/toString generated automatically.

        section("7) Java 17+ - sealed generic interface + 21 pattern switch");
        Result<String, RuntimeException> ok  = new Result.Ok<>("yay");
        Result<String, RuntimeException> err = new Result.Err<>(new IllegalStateException("boom"));

        for (Result<String, RuntimeException> r : List.of(ok, err)) {
            String s = switch (r) {
                case Result.Ok<String, RuntimeException>  o -> "OK: "  + o.value();
                case Result.Err<String, RuntimeException> e -> "ERR: " + e.error().getMessage();
            };
            System.out.println(s);
        }

        section("8) Generic Optional / Stream - everyday usage");
        Optional<Integer> first = Stream.of(3, 1, 4, 1, 5, 9, 2, 6)
                                        .filter(n -> n > 4)
                                        .findFirst();
        System.out.println("first >4 = " + first.orElse(-1));

        Map<Boolean, List<Integer>> evenOdd = Stream.of(1, 2, 3, 4, 5)
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("partition = " + evenOdd);

        section("9) Java 21 - record pattern with generics");
        Result<Integer, String> r = new Result.Ok<>(99);
        if (r instanceof Result.Ok<Integer, ?>(Integer value)) {      // deconstructing generic record
            System.out.println("deconstructed value = " + value);
        }

        // OUTPUT (representative)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
