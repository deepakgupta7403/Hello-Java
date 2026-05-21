package Phase4_ErrorsAndTypeSafety.Annotations;

import java.lang.annotation.*;

/**
 * Custom Annotations
 * ------------------
 * Declare with `@interface` and decorate with meta-annotations.
 * <p>
 *
 *      @Retention(RetentionPolicy.RUNTIME)
 *      @Target(ElementType.METHOD)
 *      public @interface Audited {
 *          String label() default "";
 *      }
 * <p>
 *
 * Allowed element types
 * ---------------------
 *   - Primitives, String, Class, an enum type
 *   - Other annotations
 *   - Arrays of any of the above
 * <p>
 *
 * Defaults
 * --------
 * Elements without `default` MUST be provided at the use site.
 * <p>
 *
 * Single-element shorthand
 * ------------------------
 * If the only element is named `value`, callers can omit the name:
 * <p>
 *
 *      @Tag("urgent")          // same as @Tag(value = "urgent")
 * <p>
 *
 * Repeatable annotations (Java 8+)
 * --------------------------------
 *      @Schedule("MON") @Schedule("WED")
 *      void cron() { ... }
 * <p>
 *
 * Requires:
 *   - A "container" annotation whose value is an array of the repeatable.
 *   - The repeatable annotation is marked @Repeatable(Container.class).
 * <p>
 *
 * Type-use annotations (Java 8+)
 * ------------------------------
 *      @Target(ElementType.TYPE_USE)
 *      public @interface NonNull {}
 * <p>
 *
 *      List<@NonNull String> names;
 *      String s = (@NonNull String) o;
 */

public class CustomAnnotations {

    // -------- 1) Method-level annotation with a default --------
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Audited {
        String label() default "";
        int    priority() default 0;
    }

    // -------- 2) Single-element annotation — 'value' shorthand --------
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Author {
        String value();
    }

    // -------- 3) Repeatable annotation --------
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(Schedules.class)
    public @interface Schedule {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Schedules {
        Schedule[] value();
    }

    // -------- 4) Type-use annotation (marker only, no behaviour) --------
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    public @interface NonNull {}

    // ---------- Sample target code using the above ----------

    @Author("alice")
    public static class Service {

        @Audited(label = "save", priority = 1)
        @Author("alice")
        public void save(@NonNull String key) {
            System.out.println("save(" + key + ")");
        }

        @Schedule("MON")
        @Schedule("WED")
        @Schedule("FRI")
        public void cron() {
            System.out.println("cron tick");
        }
    }

    public static void main(String[] args) {

        section("1) Use annotations at the call site");
        Service svc = new Service();
        svc.save("hello");
        svc.cron();

        section("2) See — RuntimeAnnotations.java reads these via reflection");
        System.out.println("they're decorated but otherwise invisible at runtime");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
