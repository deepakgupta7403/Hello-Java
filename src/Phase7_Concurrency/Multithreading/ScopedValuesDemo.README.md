# Scoped Values — Java 21 preview (JEP 446)

A modern alternative to `ThreadLocal` for sharing **immutable** per-call data
deep into nested code, especially in a virtual-thread world.

## Differences from `ThreadLocal`
| | `ThreadLocal` | `ScopedValue` |
|---|---|---|
| Lifetime | "Until you `remove()`" | Scope-bound; auto-unbound on exit |
| Mutability | `set(v)` any time | Immutable — open a nested scope to override |
| Memory per VT | Per-thread map entry | Lighter; shared shape across threads |
| Inheritance | Manual / `InheritableThreadLocal` | Inherited automatically by `StructuredTaskScope` children |
| Leak risk in pools | Yes — must `remove()` | None — scope ends → unbound |

## API shape (preview)
```java
public static final ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.where(USER, "alice").run(() -> {
    // USER.get() == "alice" anywhere inside this lambda
});

ScopedValue.where(USER, "alice")
           .where(LOCALE, Locale.US)
           .run(...);                       // bind several

ScopedValue.where(USER, "alice").call(() -> someResult());
```

Inside the body:
| Method | Effect |
|---|---|
| `USER.get()` | The value (throws if unbound) |
| `USER.isBound()` | Is there a value in scope? |
| `USER.orElse(defaultV)` | Safe accessor |

## Inheritance via `StructuredTaskScope`
```java
ScopedValue.where(USER, "alice").run(() -> {
    try (var scope = new StructuredTaskScope<>()) {
        scope.fork(() -> handle(USER.get()));   // child inherits "alice"
        scope.join();
    }
});
```

## Why preview, why care
Scoped values are designed for the **virtual-thread era**. Each VT inherits
the scope; you don't pay the per-thread `ThreadLocal` map cost across
millions of threads. Tracing, request id, security principal, locale, current
user — all fit naturally.

## Compile / run
This is a preview API in Java 21:
```
javac --release 21 --enable-preview YourCode.java
java  --enable-preview YourCode
```

The file in this repo shows the API in comments **and** ships a portable
`ThreadLocal`-based fallback so the file compiles without `--enable-preview`.

## Migration tips
| `ThreadLocal` pattern | `ScopedValue` equivalent |
|---|---|
| `tl.set(v); try { ... } finally { tl.remove(); }` | `ScopedValue.where(sv, v).run(() -> ...)` |
| `InheritableThreadLocal` | Plain `ScopedValue` — auto-inherits inside scopes |
| `tl.get()` deep in a call stack | `sv.get()` |
| `tl.set(v2)` mid-call (mutating) | Open a nested `where(...).run(...)` |

## When `ThreadLocal` is still right
- You truly need per-thread **mutable** scratch space (`StringBuilder` reuse, formatter caches).
- You need a value to persist beyond a structured scope (long-lived).
- You're on Java <= 20.

## Run
```bash
cd src
java Basics.Multithreading.ScopedValuesDemo
```

## See also
- `ThreadLocalDemo.java` — the predecessor.
- `VirtualThreads.java` — why scoped values matter now.
- `StructuredConcurrency.java` — auto-inheritance.
