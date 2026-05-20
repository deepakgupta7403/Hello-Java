# `CompletableFuture` — Composable Async

`Future` is "submit and get later." `CompletableFuture` is **composable**:
chain transformations, combine multiple futures, handle errors inline, and
run async stages on the executor of your choice — without blocking on `.get()`.

## Creating
| | |
|---|---|
| `CompletableFuture.completedFuture(v)` | Already-done future |
| `CompletableFuture.failedFuture(t)` *(Java 9+)* | Already-failed future |
| `supplyAsync(Supplier<V>)` | Async value, common pool |
| `supplyAsync(Supplier<V>, Executor)` | Async value, specific executor |
| `runAsync(Runnable[, Executor])` | Async side-effect (no result) |

## Transformations
| Method | Like |
|---|---|
| `thenApply(Function)` | `map` (sync, on completing thread) |
| `thenApplyAsync(Function)` | `map` (on default async executor) |
| `thenAccept(Consumer)` | Side-effect on the value |
| `thenRun(Runnable)` | Side-effect, ignoring the value |
| `thenCompose(F → CF<V>)` | `flatMap` — chain another async stage |

## Combinators
| Method | Effect |
|---|---|
| `thenCombine(otherCF, BiFn)` | Wait for both, combine values |
| `applyToEither` / `acceptEither` | Whichever finishes first |
| `allOf(cf...)` | `CF<Void>` that completes when all complete |
| `anyOf(cf...)` | `CF<Object>` that completes when any completes |

## Error handling
| Method | Effect |
|---|---|
| `exceptionally(fn)` | Fallback when failed |
| `handle((v, ex) → ...)` | Inspect both value and exception |
| `whenComplete((v, ex) → ...)` | Side-effect on either outcome |

## Timeouts (Java 9+)
| Method | Effect |
|---|---|
| `orTimeout(time, unit)` | Fail with `TimeoutException` |
| `completeOnTimeout(value, time, unit)` | Fall back to `value` |

## Delayed execution (Java 9+)
```java
CompletableFuture.supplyAsync(supplier,
    CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
```

## Threading rules
- The `*Async` variants run on the supplied executor (or `commonPool`).
- The non-`Async` variants run on whichever thread completed the parent — could be a worker, could be the caller.
- For **I/O-bound** stages, supply your own executor (or virtual threads). Don't starve the common pool.

## Picking `thenApply` vs `thenApplyAsync`
| | Use |
|---|---|
| `thenApply` | The stage is fast (synchronous, cheap, no I/O) |
| `thenApplyAsync` | The stage is slow or you want to hop off the producing thread |

## Composing two services
```java
CompletableFuture<User> u    = userService.lookup(id);
CompletableFuture<Cart> c    = cartService.fetch(id);
CompletableFuture<Bundle> b  = u.thenCombine(c, Bundle::of);
```

## Run
```bash
cd src
java Basics.Multithreading.CompletableFutureDemo
```

## See also
- `CallableAndFuture.java` — the simpler `Future`.
- `ExecutorFramework.java` — the executors `CompletableFuture` runs on.
- `StructuredConcurrency.java` — Java 21 task groups for fan-out.
- `VirtualThreads.java` — perfect partner for I/O-bound stages.
