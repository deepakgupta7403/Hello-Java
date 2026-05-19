package Basics.WrapperClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Autoboxing and Auto-Unboxing
 * ----------------------------
 * Since Java 5 the compiler converts between primitives and their wrappers
 * automatically:
 *
 *      Integer boxed = 42;       // autoboxing  -> Integer.valueOf(42)
 *      int prim       = boxed;   // auto-unbox  -> boxed.intValue()
 *
 * Conceptually this is just syntactic sugar - the compiler inserts the calls
 * to Integer.valueOf / intValue() for you.
 *
 *
 * Where Autoboxing Helps
 * ----------------------
 *  - Putting primitives into collections:        list.add(5)        -> list.add(Integer.valueOf(5))
 *  - Arithmetic on wrappers:                     Integer sum = a + b
 *  - Methods that accept Object:                 sysout(int)        -> sysout(Integer.valueOf(int))
 *
 *
 * Three Common Pitfalls
 * ---------------------
 *  1. NullPointerException when unboxing null - the most common bug.
 *     Integer i = null;     int x = i;     //  NullPointerException
 *
 *  2. Using == on boxed values - reference comparison, not value comparison.
 *     Integer a = 200, b = 200;  a == b -> false (outside the -128..127 cache)
 *     Always use .equals().
 *
 *  3. Performance cost in tight loops - boxing/unboxing allocates new objects.
 *     Prefer the primitive specialisations (IntStream over Stream<Integer>) when
 *     hot.
 */

public class AutoboxingUnboxing {

    public static void main(String[] args) {
        // --- 1) Autoboxing ---
        Integer boxed = 42;                       // int -> Integer (compiler inserts valueOf)
        Double  pi    = 3.14;                     // double -> Double
        Boolean ok    = true;                     // boolean -> Boolean

        System.out.println("boxed = " + boxed + ", pi = " + pi + ", ok = " + ok);

        // --- 2) Auto-unboxing ---
        Integer src = 100;
        int     dst = src;                        // Integer -> int (compiler inserts intValue())
        System.out.println("Auto-unboxed dst = " + dst);

        // --- 3) Arithmetic on wrappers mixes both directions ---
        Integer a = 10;
        Integer b = 20;
        Integer sum = a + b;   // unboxes a and b, adds as int, then re-boxes the result
        System.out.println("sum = " + sum);

        // --- 4) Generics force wrappers - autoboxing makes it painless ---
        List<Integer> nums = new ArrayList<>();
        nums.add(1);            // autoboxing
        nums.add(2);
        nums.add(3);
        int total = 0;
        for (int n : nums) {    // auto-unboxing
            total += n;
        }
        System.out.println("List total = " + total);

        // --- 5) PITFALL 1 - null unboxing ---
        Integer maybe = null;
        try {
            int crash = maybe;          // <-- NullPointerException here
            System.out.println(crash);
        } catch (NullPointerException npe) {
            System.out.println("NPE caught - cannot unbox null Integer");
        }

        // --- 6) PITFALL 2 - == on boxed values ---
        Integer p = 200, q = 200;
        System.out.println("p == q       ? " + (p == q));        // false
        System.out.println("p.equals(q)  ? " + p.equals(q));     // true

        // --- 7) PITFALL 3 - hidden cost in a hot loop ---
        // The 'Long' (capital L) here triggers autoboxing on every iteration.
        // The whole 1_000_000-element loop allocates a million Long objects.
        long start = System.nanoTime();
        Long slow = 0L;
        for (long i = 0; i < 1_000_000; i++) {
            slow += i;          // unbox -> add -> box -> assign
        }
        long slowMs = (System.nanoTime() - start) / 1_000_000;

        // The primitive version avoids any boxing - typically several times faster.
        start = System.nanoTime();
        long fast = 0L;
        for (long i = 0; i < 1_000_000; i++) {
            fast += i;
        }
        long fastMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Boxed Long loop  : " + slowMs + " ms (sum=" + slow + ")");
        System.out.println("Primitive loop   : " + fastMs + " ms (sum=" + fast + ")");

        // SAMPLE OUTPUT (timings vary)
        // boxed = 42, pi = 3.14, ok = true
        // Auto-unboxed dst = 100
        // sum = 30
        // List total = 6
        // NPE caught - cannot unbox null Integer
        // p == q       ? false
        // p.equals(q)  ? true
        // Boxed Long loop  : 18 ms (sum=499999500000)
        // Primitive loop   : 2  ms (sum=499999500000)
    }
}
