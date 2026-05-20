# Structured Concurrency (Java 21 preview, JEP 453)

`StructuredTaskScope` treats a group of concurrent subtasks as a **single
unit of work** with a **scoped lifetime**, the same way try-with-resources
made resource handling structured.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> user = scope.fork(() -> loadUser(id));
    Subtask<Cart>   cart = scope.fork(() -> loadCart(id));
    scope.join();             // wait for both, or for first failure
    scope.throwIfFailed();    // re-raise first error if any
    return new Bundle(user.get(), cart.get());
}
```

When the try-with-resources exits, **every forked subtask has finished or
been cancelled**. No leaked threads, no leaked Futures.

## Why it matters
- Replaces the lifetime-leaking world of bare `ExecutorService` + dangling
  `CompletableFuture` chains.
- Cancellation flows down the tree; errors flow up.
- Stack traces line up: parent stack → child stack.

## Built-in policies (Java 21)
| Policy | When done |
|---|---|
| `ShutdownOnFailure` | First failure cancels the rest. Use for "I need all of them." |
| `ShutdownOnSuccess<T>` | First success cancels the rest. Use for "any of them will do." |

You can subclass `StructuredTaskScope` for custom policies (e.g., majority,
quorum, deadline).

## Preview API
`StructuredTaskScope` is in `java.util.concurrent` but **preview** in Java 21.
To compile and run:
```
javac --release 21 --enable-preview YourCode.java
java  --enable-preview YourCode
```

This file in the repo demonstrates the **shape** of the JEP 453 API in
comments, plus a portable equivalent built on virtual threads +
`CompletableFuture` so the file compiles without preview flags.

## Where Structured Concurrency fits next to other tools
| Goal | Pick |
|---|---|
| Fan out N concurrent calls, abort on first failure | `StructuredTaskScope.ShutdownOnFailure` |
| Race N candidates, take first success | `StructuredTaskScope.ShutdownOnSuccess` |
| Chained transformations with branching | `CompletableFuture` |
| Long-lived background workers | Dedicated executor |

## Mental model
| Old way | Structured |
|---|---|
| `executor.submit(...)` everywhere; track futures manually | Subtasks live inside a *scope* you close |
| Cancellation is ad-hoc (`Future.cancel(true)` per future) | Closing the scope cancels everything |
| Exception bookkeeping is ad-hoc | `throwIfFailed()` re-raises the first error |
| Threads outlive the request | Scope ends → threads end |

## Run
```bash
cd src
java Basics.Multithreading.StructuredConcurrency
# (preview form requires: --enable-preview)
```

## See also
- `VirtualThreads.java` — perfect partner; one VT per subtask.
- `ScopedValuesDemo.java` — per-call context that survives scope-bound async.
- `CompletableFutureDemo.java` — the older composable model.
- `DeadlockDemo.java` — what structured concurrency is trying to make impossible.
