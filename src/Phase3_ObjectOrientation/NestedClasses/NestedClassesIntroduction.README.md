# Nested Classes — Introduction

Java has **four** kinds of nested classes.

| # | Kind | Static? | Needs outer instance? | Can capture locals? |
|---|---|---|---|---|
| 1 | Static nested class | ✅ | ❌ | n/a |
| 2 | Inner (member) class | ❌ | ✅ | outer fields, not locals |
| 3 | Local class | ❌ | sometimes | ✅ (effectively final locals) |
| 4 | Anonymous class | ❌ | sometimes | ✅ (effectively final locals) |

## Why nest at all?
- Group helpers tightly with the class that uses them.
- Keep visibility tight — a static nested class can be `private`.
- Cleaner namespaces. (Each nested class still becomes one `.class` file.)

## Modern relatives
- **Lambdas** replaced 95% of anonymous classes (when the target is a functional interface).
- **Records** are commonly declared `public static record` inside their owner.
- **Local records / classes** are sometimes useful for "tiny helper visible only inside one method."

## Picking one (decision flow)
```
Need shared mutable state with an outer instance?
 └── No  ──┬── Need to capture locals (e.g., from inside a method)?
           │     ├── Yes, one-shot, single use     → ANONYMOUS / LOCAL class
           │     └── No                            → STATIC NESTED class
           └── Just a tightly-grouped helper       → STATIC NESTED class

Need shared mutable state with an outer instance?
 └── Yes                                          → INNER (member) class
```

## Don't reach for inner classes unless you mean it
The hidden reference to the outer instance:
- **Prevents GC** of the outer until the inner is collected — a leak source in event-listener code.
- Hurts serialization (the outer must also serialize).
- Surprises callers who expected a "pure helper."

If you don't need that reference, **make it `static`**.

## Run
```bash
cd src
java Basics.NestedClasses.NestedClassesIntroduction
```

## See also
- `StaticNestedClass.java` — the workhorse.
- `InnerClass.java` — when you actually want the outer reference.
- `LocalAndAnonymousClass.java` — function-scoped types.
