package Phase8_PracticalAPIs.DateAndTime;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Legacy <-> Modern Date Conversions
 * ----------------------------------
 * Most code should use java.time. But you'll hit legacy APIs:
 * <p>
 *
 *   java.util.Date           - mutable long-wrapper
 *   java.util.Calendar       - "subclasses for various calendars" (mostly Gregorian)
 *   java.sql.Date / Time / Timestamp - JDBC subclasses (deprecated for new code)
 *   java.util.TimeZone       - legacy zone class
 * <p>
 *
 * Conversion table
 * ----------------
 *      Date  -> Instant            : date.toInstant()
 *      Instant -> Date             : Date.from(instant)
 * <p>
 *
 *      Calendar -> ZonedDateTime   : (Calendar).toInstant().atZone(zoneId)
 *      ZonedDateTime -> Calendar   : GregorianCalendar.from(zdt)
 * <p>
 *
 *      Date -> LocalDate           : date.toInstant().atZone(zone).toLocalDate()
 *      LocalDate -> Date           : Date.from(ld.atStartOfDay(zone).toInstant())
 * <p>
 *
 *      java.sql.Timestamp <-> Instant  : .toInstant() / Timestamp.from(...)
 *      java.sql.Date     <-> LocalDate : .toLocalDate() / java.sql.Date.valueOf(ld)
 *      java.sql.Time     <-> LocalTime : .toLocalTime() / java.sql.Time.valueOf(lt)
 * <p>
 *
 *      TimeZone -> ZoneId          : tz.toZoneId()
 *      ZoneId   -> TimeZone        : TimeZone.getTimeZone(zoneId)
 * <p>
 *
 * Best practice
 * -------------
 * Convert AT THE BOUNDARY. The minute a legacy call gives you a Date,
 * turn it into the right java.time type. The minute you must hand a
 * Date out, convert from your modern value. Never let the legacy types
 * spread into your domain code.
 */

public class LegacyDateConversions {

    public static void main(String[] args) {

        section("1) Date <-> Instant");
        Date oldDate = new Date();
        Instant inst = oldDate.toInstant();
        Date back   = Date.from(inst);
        System.out.println("oldDate = " + oldDate);
        System.out.println("instant = " + inst);
        System.out.println("back    = " + back);

        section("2) Date -> LocalDate (zone-dependent!)");
        ZoneId here = ZoneId.systemDefault();
        LocalDate ld = oldDate.toInstant().atZone(here).toLocalDate();
        System.out.println("LocalDate (in " + here + ") = " + ld);

        section("3) LocalDate -> Date (chose start-of-day)");
        Date fromLd = Date.from(ld.atStartOfDay(here).toInstant());
        System.out.println("Date from LocalDate = " + fromLd);

        section("4) Calendar -> ZonedDateTime");
        Calendar cal = new GregorianCalendar(2026, Calendar.MAY, 20, 14, 30);
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        ZonedDateTime z = cal.toInstant().atZone(cal.getTimeZone().toZoneId());
        System.out.println("ZonedDateTime = " + z);

        section("5) ZonedDateTime -> Calendar");
        GregorianCalendar cal2 = GregorianCalendar.from(z);
        System.out.println("Calendar back = " + cal2.getTime() + " (in " + cal2.getTimeZone().getID() + ")");

        section("6) java.sql.Timestamp <-> Instant");
        Timestamp ts = Timestamp.from(inst);
        Instant back2 = ts.toInstant();
        System.out.println("Timestamp = " + ts);
        System.out.println("back to Instant = " + back2);

        section("7) java.sql.Date / Time conversion");
        java.sql.Date sqlDate = java.sql.Date.valueOf(ld);
        LocalDate ldBack = sqlDate.toLocalDate();
        System.out.println("sql.Date  = " + sqlDate + ", back = " + ldBack);
        java.sql.Time sqlTime = java.sql.Time.valueOf(java.time.LocalTime.of(14, 30));
        System.out.println("sql.Time  = " + sqlTime + ", back = " + sqlTime.toLocalTime());

        section("8) TimeZone <-> ZoneId");
        ZoneId zone = TimeZone.getTimeZone("Europe/Berlin").toZoneId();
        TimeZone tz2 = TimeZone.getTimeZone(zone);
        System.out.println("zone = " + zone + ", tz = " + tz2.getID());

        section("9) Best practice — convert at the boundary");
        // method takes a legacy Date, internally uses java.time:
        Instant got = receiveLegacy(oldDate);
        System.out.println("internal Instant = " + got);
        // before returning to a legacy caller, convert back:
        Date out = exposeLegacy(got);
        System.out.println("legacy out = " + out);

        section("done");
    }

    private static Instant receiveLegacy(Date d) {
        return d.toInstant();
    }
    private static Date exposeLegacy(Instant i) {
        return Date.from(i);
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
