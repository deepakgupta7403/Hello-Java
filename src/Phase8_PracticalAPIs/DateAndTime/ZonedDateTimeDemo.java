package Phase8_PracticalAPIs.DateAndTime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * ZonedDateTime and OffsetDateTime
 * --------------------------------
 * <p>
 *
 *   ZonedDateTime    - LocalDateTime + ZoneId  (named zone like "Asia/Kolkata")
 *   OffsetDateTime   - LocalDateTime + ZoneOffset  (fixed offset like +05:30)
 * <p>
 *
 * ZoneId knows about DAYLIGHT SAVING rules and historical changes —
 * "America/New_York" can be -05:00 or -04:00 depending on the date.
 * ZoneOffset is a static number of hours/minutes from UTC.
 * <p>
 *
 * When to use which
 * -----------------
 *   ZonedDateTime    - meeting times, recurring schedules, anything that
 *                      humans understand as "in city X."
 *   OffsetDateTime   - DATABASE columns where you want a constant offset.
 *                      ISO-8601 string with offset (no DST surprises).
 *   Instant          - the global moment behind both of the above.
 * <p>
 *
 * The DST gotcha
 * --------------
 * In a DST transition (say, US spring-forward), local time JUMPS from
 * 02:00 to 03:00 — 02:30 simply doesn't exist. ZonedDateTime resolves
 * that intelligently. LocalDateTime doesn't even know there's a problem.
 * <p>
 *
 * Useful methods
 * --------------
 *      ZonedDateTime.now()
 *      ZonedDateTime.now(zone)
 *      LocalDateTime.atZone(zone)
 *      ZonedDateTime.of(localDate, localTime, zone)
 * <p>
 *
 *      z.toLocalDate(), z.toLocalTime(), z.toLocalDateTime()
 *      z.toInstant(), z.toOffsetDateTime()
 *      z.withZoneSameInstant(otherZone)   - same point in time, new zone
 *      z.withZoneSameLocal(otherZone)     - same wall-clock fields, new zone
 * <p>
 *
 *      ZoneId.systemDefault()
 *      ZoneId.of("Asia/Kolkata")
 *      ZoneId.getAvailableZoneIds()
 */

public class ZonedDateTimeDemo {

    public static void main(String[] args) {

        section("1) System default zone and a named one");
        ZoneId here   = ZoneId.systemDefault();
        ZoneId tokyo  = ZoneId.of("Asia/Tokyo");
        ZoneId london = ZoneId.of("Europe/London");
        System.out.println("here   = " + here);
        System.out.println("tokyo  = " + tokyo);
        System.out.println("london = " + london);

        section("2) ZonedDateTime.now()");
        ZonedDateTime z = ZonedDateTime.now(here);
        System.out.println("now in default zone = " + z);

        section("3) Convert one zone to another — SAME instant");
        ZonedDateTime inTokyo  = z.withZoneSameInstant(tokyo);
        ZonedDateTime inLondon = z.withZoneSameInstant(london);
        System.out.println("Tokyo  = " + inTokyo);
        System.out.println("London = " + inLondon);

        section("4) withZoneSameLocal — wall clock unchanged, zone replaced");
        ZonedDateTime sameLocal = z.withZoneSameLocal(tokyo);
        System.out.println("same local in Tokyo = " + sameLocal);
        // This is RARELY what you want; usually you want SameInstant.

        section("5) DST transition — Spring forward");
        // US Eastern, 2026-03-08, clocks jump 02:00 -> 03:00
        ZoneId nyc = ZoneId.of("America/New_York");
        ZonedDateTime springForward = ZonedDateTime.of(
                LocalDateTime.of(2026, 3, 8, 2, 30), nyc);
        System.out.println("2026-03-08 02:30 in NYC -> " + springForward);
        // 02:30 doesn't exist; it gets bumped to 03:30.

        section("6) OffsetDateTime — fixed offset, no zone");
        OffsetDateTime odt = OffsetDateTime.of(
                LocalDateTime.of(2026, 5, 20, 14, 30), ZoneOffset.of("+05:30"));
        System.out.println("OffsetDateTime = " + odt);

        section("7) ISO-8601 string with offset (e.g., from a JSON API)");
        OffsetDateTime parsed = OffsetDateTime.parse("2026-05-20T14:30:00+05:30");
        System.out.println("parsed = " + parsed);

        section("8) Formatting a ZonedDateTime for a human");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a zzz");
        System.out.println(z.format(fmt));

        section("9) Discover zones");
        Set<String> all = ZoneId.getAvailableZoneIds();
        System.out.println("total zones = " + all.size());
        all.stream().filter(s -> s.startsWith("Asia/")).limit(5).sorted().forEach(System.out::println);

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
