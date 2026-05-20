# Executor Framework

Introduced in Java 5 (`java.util.concurrent`), the framework decouples
**work** from the **thread** that runs it.

## Hierarchy
```
Executor                  -> execute(Runnable)
 └ ExecutorService        -> submit, invokeAll, invokeAny, shutdown
    └ ScheduledExecutorService -> schedule, scheduleAtFixedRate, ...
```

## Why use it
- Reuse threads (no spin-up cost per task).
- Bounded resources (no thread bombs).
- Structured result handling (`Future` / `Callable`).
- Built-in shutdown semantics.
- Java 19+: `AutoCloseable`, so `try (var es = ...)` shuts down cleanly.

## Common factories
| Factory | Behaviour |
|---|---|
| `newFixedThreadPool(n)` | `n` workers, unbounded queue |
| `newCachedThreadPool()` | 0–∞ workers, `SynchronousQueue`, 60-sec keep-alive |
| `newSingleThreadExecutor()` | Strict serial execution |
| `newScheduledThreadPool(n)` | Delayed and periodic tasks |
| `newVirtualThreadPerTaskExecutor()` | Java 21 — one virtual thread per task |
| `newWorkStealingPool(p)` | `ForkJoinPool` exposed as an `ExecutorService` |

⚠️ The first three have **unbounded queues / workers**. For production prefer
to construct a `ThreadPoolExecutor` yourself.

## Key methods
| Method | Returns | Use for |
|---|---|---|
| `execute(Runnable)` | `void` | Fire-and-forget |
| `submit(Runnable)` | `Future<?>` | Track completion |
| `submit(Callable<V>)` | `Future<V>` | Track + return value |
| `invokeAll(Collection)` | `List<Future<V>>` | Wait for **all** |
| `invokeAny(Collection)` | `V` | First success, cancel the rest |

## Shutdown
| Call | Effect |
|---|---|
| `shutdown()` | No new tasks; finish queued + running |
| `shutdownNow()` | No new tasks; **interrupt** running tasks; return the rest |
| `awaitTermination(timeout, unit)` | Block until terminated or timeout |
| `isShutdown()` / `isTerminated()` | Inspect state |

## Try-with-resources pattern (Java 19+)
```java
try (var es = Executors.newFixedThreadPool(4)) {
    es.submit(task);
}   // shuts down + awaits termination automatically
```

## Pattern: fan-out + collect
```java
List<Future<Integer>> futures = jobs.stream()
        .map(es::submit)
        .toList();
int total = 0;
for (Future<Integer> f : futures) total += f.get();
```

## When to use what
| Goal | Pick |
|---|---|
| Lots of short CPU tasks | Fixed pool ≈ #cores |
| Tons of I/O-bound tasks | Virtual-thread executor |
| Periodic / delayed work | `ScheduledExecutorService` |
| Divide-and-conquer | `ForkJoinPool` |
| Async chains | `CompletableFuture` (built on the same executors) |

## Run
```bash
cd src
java Basics.Multithreading.ExecutorFramework
```

## See also
- `ThreadPools.java` — the lower-level `ThreadPoolExecutor`.
- `ScheduledExecutorDemo.java` — delays and periodicity.
- `CompletableFutureDemo.java` — composable async.
- `VirtualThreads.java` — Java 21 virtual threads.
