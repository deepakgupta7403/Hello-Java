package Basics.Collections;

import java.util.EnumSet;
import java.util.Set;

/**
 * java.util.EnumSet&lt;E extends Enum&lt;E&gt;&gt; - Fastest Set for Enum Keys
 * --------------------------------------------------------------
 * EnumSet is a SPECIALISED Set implementation for enum types. Internally it
 * stores membership as a single BIT VECTOR (a long for ≤64 constants, a
 * long[] otherwise). add/remove/contains are TRIVIAL bitwise operations.
 *
 *
 * Why It Exists
 * -------------
 *   - SPEED. Bit operations are dramatically faster than hashing.
 *   - MEMORY. One bit per constant, period.
 *   - SAFETY. The enum's compile-time type system rules out invalid members.
 *
 *
 * When To Use It
 * --------------
 *   - Any time the elements come from an enum type and you need a Set.
 *   - Flags / option sets: EnumSet of Permission, Day, MimeType ...
 *   - Pre-computed lookups: EnumSet.range(Day.MON, Day.FRI) for weekdays.
 *
 *
 * Construction (no public constructors - use the static factories)
 * ----------------------------------------------------------------
 *   EnumSet.noneOf(Day.class)                  - empty
 *   EnumSet.allOf(Day.class)                   - every constant
 *   EnumSet.of(Day.MON)                        - one or more constants
 *   EnumSet.of(Day.MON, Day.TUE, Day.WED, ...)
 *   EnumSet.range(Day.MON, Day.FRI)            - inclusive on both ends
 *   EnumSet.copyOf(otherEnumSet)
 *   EnumSet.copyOf(anyCollectionOfEnum)
 *   EnumSet.complementOf(otherEnumSet)          - "everything NOT in the other"
 *
 *
 * Restrictions
 * ------------
 *   - All elements MUST be of the SAME enum type (the generic E is bounded).
 *   - NULLS are not allowed.
 *   - Not thread-safe. Use Collections.synchronizedSet(...) if needed.
 *
 *
 * Big-O
 * -----
 *   add / remove / contains / size                            O(1)
 *   union / intersection / difference (via addAll/removeAll)   O(1) or O(words)
 */

public class EnumSetDemo {

    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    enum Permission { READ, WRITE, EXECUTE, DELETE, ADMIN }

    public static void main(String[] args) {

        section("1) Construction with the static factories");
        EnumSet<Day> empty       = EnumSet.noneOf(Day.class);
        EnumSet<Day> everyDay    = EnumSet.allOf(Day.class);
        EnumSet<Day> weekend     = EnumSet.of(Day.SAT, Day.SUN);
        EnumSet<Day> workdays    = EnumSet.range(Day.MON, Day.FRI);
        EnumSet<Day> notWeekend  = EnumSet.complementOf(weekend);

        System.out.println("empty       = " + empty);
        System.out.println("everyDay    = " + everyDay);
        System.out.println("weekend     = " + weekend);
        System.out.println("workdays    = " + workdays);
        System.out.println("notWeekend  = " + notWeekend);

        section("2) The usual Set methods - just like any other Set");
        EnumSet<Permission> perms = EnumSet.of(Permission.READ, Permission.WRITE);
        System.out.println("perms.contains(READ)  = " + perms.contains(Permission.READ));
        System.out.println("perms.contains(ADMIN) = " + perms.contains(Permission.ADMIN));
        perms.add(Permission.EXECUTE);
        perms.remove(Permission.WRITE);
        System.out.println("after add/remove      = " + perms);

        section("3) Set algebra - union / intersection / difference");
        EnumSet<Day> a = EnumSet.of(Day.MON, Day.TUE, Day.WED);
        EnumSet<Day> b = EnumSet.of(Day.WED, Day.THU, Day.FRI);

        EnumSet<Day> u = EnumSet.copyOf(a); u.addAll(b);          // union
        EnumSet<Day> i = EnumSet.copyOf(a); i.retainAll(b);       // intersection
        EnumSet<Day> d = EnumSet.copyOf(a); d.removeAll(b);       // difference

        System.out.println("a u b = " + u);
        System.out.println("a n b = " + i);
        System.out.println("a \\ b = " + d);

        section("4) Iteration is in DECLARATION order, always");
        EnumSet<Day> shuffled = EnumSet.of(Day.FRI, Day.MON, Day.WED);
        for (Day day : shuffled) System.out.print(day + " ");
        System.out.println();
        // MON WED FRI - enum constants come out in their declaration order
        // even though we added them out of order.

        section("5) Type safety - mixing enums is a COMPILE error");
        // EnumSet<Day> bad = EnumSet.of(Day.MON, Permission.READ);  // does not compile

        section("6) NULL is rejected at runtime");
        try {
            EnumSet.of(Day.MON).add(null);
        } catch (NullPointerException e) {
            System.out.println("EnumSet.add(null) -> NullPointerException");
        }

        section("7) Use case - flag set for a permission system");
        // Two users, two permission sets:
        EnumSet<Permission> alice = EnumSet.of(Permission.READ, Permission.WRITE);
        EnumSet<Permission> root  = EnumSet.allOf(Permission.class);

        // Can Alice perform DELETE?
        System.out.println("alice can DELETE = " + alice.contains(Permission.DELETE));

        // Is root a superset of Alice?
        System.out.println("root  superset of alice = " + root.containsAll(alice));

        section("8) Use case - calendar weekdays via EnumSet.range");
        Day today = Day.WED;
        Set<Day> weekday = EnumSet.range(Day.MON, Day.FRI);
        System.out.println("today is a workday? " + weekday.contains(today));

        section("9) Performance teaser - EnumSet vs HashSet");
        long t = System.nanoTime();
        for (int n = 0; n < 1_000_000; n++) {
            EnumSet<Day> es = EnumSet.of(Day.MON, Day.WED, Day.FRI);
            es.contains(Day.FRI);
        }
        long es = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        for (int n = 0; n < 1_000_000; n++) {
            java.util.HashSet<Day> hs = new java.util.HashSet<>();
            hs.add(Day.MON); hs.add(Day.WED); hs.add(Day.FRI);
            hs.contains(Day.FRI);
        }
        long hs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("EnumSet 1M iterations : " + es + " ms");
        System.out.println("HashSet  1M iterations: " + hs + " ms  (slower for enums)");

        // OUTPUT (timings vary)
    }

    private static void section(String title) {
        System.out.println("\n====== " + title + " ======");
    }
}
