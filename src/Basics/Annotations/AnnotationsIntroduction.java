package Basics.Annotations;

/**
 * Annotations — Introduction
 * --------------------------
 * Annotations are METADATA you attach to code (classes, methods,
 * fields, parameters, type uses). They don't change the program's
 * behaviour directly — tools and runtime libraries read them and act
 * on them (compiler checks, frameworks, code generators, IDEs).
 *
 *
 * The four families
 * -----------------
 *   1. Built-in marker annotations            - @Override, @Deprecated, ...
 *   2. Compiler hint annotations              - @SuppressWarnings, @SafeVarargs
 *   3. Custom annotations YOU write           - @MyAnnotation
 *   4. Meta-annotations on annotations        - @Retention, @Target, @Inherited,
 *                                                @Repeatable, @Documented
 *
 *
 * Anatomy of an annotation
 * ------------------------
 *      @MyAnnotation(name = "alice", priority = 5)
 *      public class C { ... }
 *
 *   - The leading '@' marks it.
 *   - Optional ELEMENTS (name=..., priority=...) — must be compile-time
 *     constants.
 *   - One-element annotations can drop the element name when it's
 *     called `value`:
 *          @Author("alice")
 *
 *
 * Where annotations live
 * ----------------------
 *   - Types (classes, interfaces, enums, records)
 *   - Methods, constructors
 *   - Parameters, return types
 *   - Fields, local variables
 *   - Type uses (Java 8+): List<@NonNull String>
 *   - Modules and packages
 *
 *
 * Retention levels
 * ----------------
 *   SOURCE     - only visible to the compiler (lint).
 *   CLASS      - in the .class file but NOT loaded at runtime.
 *   RUNTIME    - in the .class AND visible via reflection at runtime.
 *
 *
 * Real-world examples
 * -------------------
 *   - JUnit: @Test, @BeforeEach
 *   - Spring: @Component, @Autowired, @Transactional
 *   - JPA:   @Entity, @Id, @Column
 *   - Lombok: @Data, @Builder (source-level code generation)
 *   - Validation: @NotNull, @Size, @Email (Jakarta Validation)
 */

public class AnnotationsIntroduction {

    /** Marker annotation usage. */
    @Override public String toString() { return "intro"; }

    /** Compiler hint. */
    @SuppressWarnings("unchecked")
    static <T> T cast(Object o) { return (T) o; }

    /** Deprecated method. */
    @Deprecated(since = "1.5", forRemoval = true)
    static void oldApi() {}

    public static void main(String[] args) {
        section("1) See the source — annotations decorate code");
        System.out.println(new AnnotationsIntroduction());
        oldApi();
        Integer x = cast(42);
        System.out.println("x = " + x);

        section("2) Built-in annotations covered next door — BuiltInAnnotations.java");
        System.out.println("3) Custom annotations — CustomAnnotations.java");
        System.out.println("4) Reading them at runtime — RuntimeAnnotations.java");

        section("done");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
