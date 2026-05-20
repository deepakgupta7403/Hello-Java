# Reading Annotations at Runtime

For runtime-retained annotations (`@Retention(RUNTIME)`), the **Reflection
API** gives you the metadata. This is how Spring, JUnit, Jackson, JPA, and
every annotation-driven framework actually work.

## API on `Class<?>`
| Method | Returns |
|---|---|
| `getAnnotation(MyAnn.class)` | `MyAnn` or `null` |
| `getAnnotationsByType(MyAnn.class)` | `MyAnn[]` — for repeatables |
| `isAnnotationPresent(MyAnn.class)` | `boolean` |
| `getAnnotations()` | All annotations (including inherited) |
| `getDeclaredAnnotations()` | Just the ones on this class |

## API on `Method`, `Field`, `Constructor`, `Parameter`
Same shape:
- `getAnnotation(...)`, `getAnnotationsByType(...)`, `isAnnotationPresent(...)`
- `getParameterAnnotations()` on `Method` / `Constructor` for an
  `Annotation[][]` per parameter.

## The interceptor pattern (mini-AOP)
```java
for (Method m : target.getClass().getMethods()) {
    if (m.isAnnotationPresent(Audited.class)) {
        Audited a = m.getAnnotation(Audited.class);
        long t0 = System.nanoTime();
        m.invoke(target, args);
        long elapsed = (System.nanoTime() - t0) / 1_000_000;
        log("[AUDIT] " + a.label() + " took " + elapsed + "ms");
    }
}
```
This is the conceptual core of Spring AOP and similar frameworks.

## Mapping example: tiny validator
```java
@Retention(RUNTIME) @Target(FIELD)
@interface NotBlank {}

for (Field f : obj.getClass().getDeclaredFields()) {
    if (f.isAnnotationPresent(NotBlank.class)) {
        f.setAccessible(true);
        Object v = f.get(obj);
        if (v == null || v.toString().isBlank())
            throw new ValidationException(f.getName() + " is blank");
    }
}
```

## Performance & caveats
- Reflection has overhead — cache the results if you're invoking hot.
- `setAccessible(true)` can be blocked by the **module system** unless the target package is `opens`-exported. See the Modules section.
- Hidden classes (Java 15+) and records have nuanced reflection behaviour — check the Reflection API section in this repo.

## Run
```bash
cd src
java Basics.Annotations.RuntimeAnnotations
```

## See also
- `CustomAnnotations.java` — the annotations being read here.
- `BuiltInAnnotations.java` — what the JDK ships.
- `Basics/Reflection/` — the reflection API in depth.
