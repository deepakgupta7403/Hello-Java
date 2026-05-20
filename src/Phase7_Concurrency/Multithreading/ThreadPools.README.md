# Thread Pools

Creating one `Thread` per task is fine for tens of tasks; for thousands it's
a disaster. A **thread pool** keeps a small set of worker threads alive and
feeds them tasks from a queue.

## `ThreadPoolExecutor` — the core class
```java
new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime, unit,
    workQueue,
    threadFactory,
    rejectionHandler);
```

## How a task is routed
1. Fewer than `corePoolSize` workers? → spawn a new worker.
2. Otherwise try to enqueue on `workQueue`.
3. Queue full? → if `< maximumPoolSize`, spawn a new worker.
4. Pool also full? → run the **rejection policy**.

## Rejection policies
| Policy | What it does |
|---|---|
| `AbortPolicy` *(default)* | Throws `RejectedExecutionException` |
| `CallerRunsPolicy` | Runs the task on the **submitting** thread — natural backpressure |
| `DiscardPolicy` | Silently drops |
| `DiscardOldestPolicy` | Drops the oldest queued task, then submits |

## Queue choice dictates behaviour
| Queue | Effect |
|---|---|
| `SynchronousQueue` (capacity 0) | Direct hand-off. With unbounded max → "cached" pool that grows on demand. |
| `LinkedBlockingQueue` (unbounded) | Pool never grows past `corePoolSize`. Tasks pile up. |
| `ArrayBlockingQueue` (bounded) | Encourages pool to grow to `maximumPoolSize`. |
| `PriorityBlockingQueue` | Tasks must implement `Comparable`. |

## `Executors.*` defaults are dangerous in production
| Factory | Risk |
|---|---|
| `newFixedThreadPool(n)` | Unbounded queue → unbounded memory under overload |
| `newCachedThreadPool()` | Unbounded workers → thread explosion |
| `newSingleThreadExecutor()` | Unbounded queue |
| `newScheduledThreadPool(n)` | Unbounded delay-queue |

Prefer constructing a `ThreadPoolExecutor` yourself with a **bounded** queue,
a **bounded** max, and a sane rejection policy.

## Sizing guidance
| Workload | Approximate pool size |
|---|---|
| CPU-bound | `N` or `N + 1` cores |
| I/O-bound (waiting on a service) | Higher — see Little's Law: `threads ≈ rate × latency` |
| Mixed | Measure |

## Shutdown
| Call | Effect |
|---|---|
| `shutdown()` | Stop accepting new tasks; let queued + running tasks finish |
| `shutdownNow()` | Stop accepting + try to interrupt running tasks + return queued ones |
| `awaitTermination(timeout, unit)` | Wait for the pool to fully drain |
| `isShutdown()` / `isTerminated()` | Inspect state |

## Lifecycle pattern
```java
ExecutorService pool = ...;
try {
    pool.execute(task);
    pool.submit(callable);
} finally {
    pool.shutdown();
    if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
        pool.shutdownNow();
    }
}
```
(Or `try (var pool = ...)` — Java 19+ made `ExecutorService` `AutoCloseable`.)

## Run
```bash
cd src
java Basics.Multithreading.ThreadPools
```

## See also
- `ExecutorFramework.java` — what `ExecutorService` adds.
- `ScheduledExecutorDemo.java` — timed and periodic tasks.
- `ForkJoinPoolDemo.java` — divide-and-conquer.
- `VirtualThreads.java` — a different way to scale (Java 21).
