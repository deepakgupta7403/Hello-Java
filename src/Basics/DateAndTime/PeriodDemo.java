package Basics.DateAndTime;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Period — a Calendar-Length Difference
 * -------------------------------------
 * Period represents an interval in YEARS, MONTHS, and DAYS — the units
 * humans use to talk about ages and trial periods.
 *
 *      P1Y2M3D   = 1 year 2 months 3 days
 *      P10D      = 10 days
 *
 * Unlike Duration, Period is CALENDAR-AWARE: adding 1 month to Jan 31
 * does NOT give Feb 31. It clamps to the last valid day.
 *
 *
 * Construction
 * ------------
 *   Period.ofDays / ofWeeks / ofMonths / ofYears
 *   Period.of(y, m, d)
 *   Period.between(start, end)          - LocalDate to LocalDate
 *   Period.parse("P1Y2M3D")
 *
 *
 * Arithmetic
 * ----------
 *   plus / minus / multipliedBy / negated
 *   normalized()   - rolls 13 months into 1Y1M
 *
 *
 * Accessors
 * ---------
 *   getYears() / getMonths() / getDays()
 *   isZero() / isNegative()
 *
 *
 * Common use cases
 * ----------------
 *   - "How old is someone?"   -> Period.between(birthday, today).getYears()
 *   - Trial / lease periods.
 *   - Recurring schedules expressed in months / years.
 *
 *
 * The Jan 31 + 1 month puzzle
 * ---------------------------
 *   LocalDate.of(2026, 1, 31).plus(Period.ofMonths(1))   -> 2026-02-28
 *   The library "clamps" overflow to the last valid day. Stable, but
 *   non-commutative: adding twice doesn't always equal adding the sum.
 */

public class PeriodDemo {

    public static void main(String[] args) {

        section("1) Construction");
        Period p1 = Period.ofMonths(3);
        Period p2 = Period.of(1, 2, 3);
        Period p3 = Period.parse("P1Y2M3D");
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("p3 = " + p3);

        section("2) Period.between(start, end)");
        LocalDate birthday = LocalDate.of(1990, 6, 15);
        LocalDate today    = LocalDate.now();
        Period age = Period.between(birthday, today);
        System.out.println("age = " + age.getYears() + "y "
                + age.getMonths() + "m " + age.getDays() + "d");

        section("3) Adding a Period to a LocalDate");
        LocalDate trialEnd = today.plus(Period.ofDays(30));
        LocalDate anniv    = today.plus(Period.ofYears(1));
        System.out.println("30-day trial ends = " + trialEnd);
        System.out.println("1-year anniv      = " + anniv);

        section("4) Jan 31 + 1 month → Feb 28 (or 29 in leap)");
        LocalDate jan31 = LocalDate.of(2026, 1, 31);
        System.out.println("Jan 31 +1M  = " + jan31.plusMonths(1));
        // adding twice is not the same as adding the sum:
        System.out.println("Jan 31 +1M +1M = " + jan31.plusMonths(1).plusMonths(1));
        System.out.println("Jan 31 +2M     = " + jan31.plusMonths(2));

        section("5) normalized()");
        Period weird = Period.of(0, 14, 5);                  // 14 months
        System.out.println("weird            = " + weird);
        System.out.println("weird.normalized = " + weird.normalized());

        section("6) Don't confuse calendar with clock");
        // ChronoUnit.DAYS.between gives EXACT days; period gives years/months/days.
        long calendarDays = ChronoUnit.DAYS.between(birthday, today);
        System.out.println("days alive = " + calendarDays);
        // The Period above said e.g. 35y 11m 5d — same range, different shape.

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
