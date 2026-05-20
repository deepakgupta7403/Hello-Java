# `Runnable` Interface

The simplest description of "a unit of work that can be run by a thread."
A **functional interface** with one method:

```java
@FunctionalInterface
public interface Runnable {
    void run();
}
```

- No arguments
- No return value
- **No checked exceptions** — must be caught / wrapped

## Why Runnable beats `extends Thread`
| | `extends Thread` | `implements Runnable` |
|---|---|---|
| Couples work to a Thread? | Yes | No |
| Can extend other classes? | No | Yes |
| Works with executors? | Awkward | Yes |
| Lambdas? | No | Yes — `() -> {}` |
| Idiomatic in modern Java? | No | **Yes** |

## Related types
| Type | Signature | Where used |
|---|---|---|
| `Runnable` | `void run()` | `Thread`, `Executor.execute` |
| `Callable<V>` | `V call() throws Exception` | `ExecutorService.submit` |
| `RunnableFuture<V>` | `Runnable + Future<V>` | Inside `FutureTask` |

## Common patterns

**Lambda:**
```java
new Thread(() -> doWork(), "worker").start();
```

**Method reference:**
```java
new Thread(this::doWork, "worker").start();
```

**Composition:**
```java
Runnable both = () -> { a.run(); b.run(); };
```

**Decoration (e.g., timing wrapper):**
```java
Runnable timed = () -> {
    long t0 = System.nanoTime();
    try { delegate.run(); }
    finally { logTimeSince(t0); }
};
```

## Checked exceptions
`run()` has no `throws` clause. If your work raises a checked exception you
must catch it inside the lambda and wrap as a `RuntimeException`, **or** use
`Callable` instead.

## Run
```bash
cd src
java Basics.Multithreading.RunnableInterface
```

## See also
- `CallableAndFuture.java` — when you need a return value or `throws`.
- `ExecutorFramework.java` — submitting runnables to pools.
- `Threads.java` — passing a Runnable to a Thread.
