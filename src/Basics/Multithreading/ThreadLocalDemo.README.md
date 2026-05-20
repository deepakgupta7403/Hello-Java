# `ThreadLocal<T>`

A **per-thread** variable. Each thread that reads a `ThreadLocal` sees its
own copy of the value — there is no shared storage to corrupt.

## Why it's useful
| Use | Example |
|---|---|
| **Confinement** | Avoid synchronization on something inherently per-thread. |
| **Per-request context** | Trace id, user, locale, transaction, security principal — implicitly available deep in the call stack. |
| **Per-thread caching** | One `SimpleDateFormat` per thread instead of synchronising a shared one. |
| **Mutable scratch space** | Per-thread `StringBuilder` for hot serialisation paths. |

## API
| Method | Purpose |
|---|---|
| `new ThreadLocal<>()` | No initial value (`get()` returns `null`) |
| `ThreadLocal.withInitial(Supplier<T>)` | Lazy default |
| `T get()` | This thread's value |
| `void set(T)` | This thread's value |
| `void remove()` | Discard this thread's value |
| `InheritableThreadLocal<T>` | Child threads inherit a (shallow) copy from their parent at construction |

## The classic `SimpleDateFormat` pattern
```java
static final ThreadLocal<SimpleDateFormat> FMT =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

String fmt = FMT.get().format(new Date());
```

## Pitfalls
1. **Memory leaks in thread pools.** Workers live forever; a value set by one
   task is still set when the worker handles the next. Always `remove()` in a
   `finally` block at the end of a unit of work — or use a wrapping helper.
2. `InheritableThreadLocal` copies the **reference**, not the object's
   internal state. Mutating it from the child also mutates the parent's
   object.
3. Hard to reason about across asynchronous boundaries (CompletableFuture,
   executors, virtual threads). The value of `ThreadLocal` belongs to the
   *carrier* — switch threads and you lose context.

## Try-with-remove idiom
```java
traceId.set(id);
try {
    handleRequest();
} finally {
    traceId.remove();
}
```

## When not to use `ThreadLocal`
- For data that should propagate across async boundaries → use **Scoped
  Values** (Java 21 preview, `ScopedValue<T>`).
- For sharing state between threads — that's the opposite of confinement;
  use a thread-safe container.
- In virtual-thread-heavy code where you have *millions* of threads — each
  with its own ThreadLocal entries — memory cost adds up.

## Run
```bash
cd src
java Basics.Multithreading.ThreadLocalDemo
```

## See also
- `ScopedValuesDemo.java` — Java 21 alternative.
- `VirtualThreads.java` — why ThreadLocal can be expensive in vt-land.
- `JavaSynchronization.java` — the contrast: shared state with sync.
