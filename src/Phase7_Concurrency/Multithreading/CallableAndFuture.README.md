# `Callable<V>` and `Future<V>`

Where `Runnable` returns nothing and can't `throws`, `Callable` returns a
value and may throw any checked exception.

## The interfaces

```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}

public interface Future<V> {
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit) throws ..., TimeoutException;
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isDone();
    boolean isCancelled();
}
```

## How they fit together
```
ExecutorService.submit(Callable<V>)   →  Future<V>
ExecutorService.submit(Runnable, V)   →  Future<V>  (result is `V`)
ExecutorService.submit(Runnable)      →  Future<?>  (result is null)
```

## What `get()` throws
| Exception | Meaning |
|---|---|
| `InterruptedException` | The **caller** was interrupted while waiting |
| `ExecutionException` | The **task** threw — `e.getCause()` is the original |
| `CancellationException` | The task was cancelled before completion |
| `TimeoutException` | Timed `get` ran out of time |

## Cancel semantics
- `cancel(false)` — just mark as cancelled; if not yet running, never runs.
- `cancel(true)` — also **interrupt** the worker thread if running. Your code needs to be interruption-aware to react.

## `FutureTask`
A class that is both `Runnable` and `Future`. Useful for running a `Callable`
on a raw `Thread`:
```java
FutureTask<Integer> ft = new FutureTask<>(() -> 42);
new Thread(ft).start();
int v = ft.get();
```

## Fan-out / fan-in with `invokeAll`
```java
List<Future<Integer>> futures = es.invokeAll(jobs);   // waits for ALL
int total = futures.stream().mapToInt(f -> {
    try { return f.get(); } catch (Exception e) { throw new RuntimeException(e); }
}).sum();
```

`invokeAny(jobs)` returns the first successful result and cancels the rest.

## When to prefer `CompletableFuture`
- You want to chain `.thenApply` / `.thenCompose` instead of blocking on `.get()`.
- You want to handle exceptions inline (`.exceptionally`).
- You're orchestrating many async stages.

## Run
```bash
cd src
java Basics.Multithreading.CallableAndFuture
```

## See also
- `RunnableInterface.java` — when no result is needed.
- `ExecutorFramework.java` — submitting tasks to pools.
- `CompletableFutureDemo.java` — composable async chains.
- `StructuredConcurrency.java` — Java 21 task groups.
