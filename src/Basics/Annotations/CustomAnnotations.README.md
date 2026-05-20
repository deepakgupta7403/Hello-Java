# Custom Annotations

Declare with `@interface` and decorate with **meta-annotations**.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Audited {
    String label()    default "";
    int    priority() default 0;
}
```

## Allowed element types
- Primitives, `String`, `Class`, an enum type.
- Other annotations.
- Arrays of any of the above.

## Meta-annotations cheat sheet
| Meta-annotation | What it controls |
|---|---|
| `@Retention` | `SOURCE` / `CLASS` / `RUNTIME` — when the annotation is visible |
| `@Target` | Where it can attach: `TYPE`, `METHOD`, `FIELD`, `PARAMETER`, `LOCAL_VARIABLE`, `CONSTRUCTOR`, `TYPE_PARAMETER`, `TYPE_USE`, `PACKAGE`, `MODULE` |
| `@Inherited` | A class annotation is inherited by subclasses |
| `@Repeatable(Container.class)` | Same annotation can appear more than once on the same target |
| `@Documented` | Include in Javadoc |

## Single-element shorthand
```java
@Author("alice")              // same as @Author(value = "alice")
```
Works only when the single element is literally named `value`.

## Defaults
Elements without `default` **must be provided** at the use site.

## Repeatable annotations (Java 8+)
```java
@Retention(RUNTIME) @Target(METHOD) @Repeatable(Schedules.class)
public @interface Schedule { String value(); }

@Retention(RUNTIME) @Target(METHOD)
public @interface Schedules { Schedule[] value(); }

@Schedule("MON") @Schedule("WED") @Schedule("FRI")
void cron() {}
```
Reading them at runtime: `method.getAnnotationsByType(Schedule.class)` returns
`Schedule[]`.

## Type-use annotations (Java 8+)
```java
@Target(ElementType.TYPE_USE) public @interface NonNull {}

List<@NonNull String> names;
String s = (@NonNull String) o;
```
Used by null-checkers like Checker Framework and tools.

## Don't forget retention
If the annotation needs to be read at runtime (Spring, JUnit, your own
reflection-based code), retention **must** be `RUNTIME`. Default is `CLASS`
(visible to bytecode tools but not to the running JVM).

## Run
```bash
cd src
java Basics.Annotations.CustomAnnotations
```

## See also
- `RuntimeAnnotations.java` — read these at runtime.
- `BuiltInAnnotations.java` — the standard ones.
- `Basics/Generics/HeapPollutionAndSafeVarargs.java` — applies `@SafeVarargs`.
