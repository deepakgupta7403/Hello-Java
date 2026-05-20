# `Thread.join(...)`

Block the current thread until another thread terminates.

## Overloads
| Signature | Behaviour |
|---|---|
| `t.join()` | Wait forever |
| `t.join(ms)` | Wait up to `ms` milliseconds (0 means "forever") |
| `t.join(ms, nanos)` | Plus nanosecond precision |
| `t.join(Duration d)` | Java 19+ |

## What it gives you
- **Coordination** — "don't move on until this is done."
- **Memory visibility** — everything `t` did before terminating is visible *after* `t.join()` returns. This is one of the cleanest happens-before edges in the JMM.

## Pitfalls
- **`Thread.currentThread().join()` hangs forever** — don't.
- **`join(0)` is "wait forever"**, not "don't wait." `join(1)` is the smallest real timeout.
- **`InterruptedException`** — `join` is interruptible. Catch it and re-set the flag.
- **One join per logical wait** — once `t` has terminated, further joins return immediately.

## When to prefer something else
| Scenario | Better tool |
|---|---|
| Many short tasks | `ExecutorService` + `Future.get()` / `invokeAll` |
| Async chain of computations | `CompletableFuture` |
| "Wait for N workers to ready up" | `CountDownLatch` |
| "Run N rounds in lockstep" | `CyclicBarrier` or `Phaser` |
| Structured task group | Java 21 `StructuredTaskScope` |

## Fan-out / fan-in idiom
```java
Thread[] workers = new Thread[N];
for (int i = 0; i < N; i++) {
    final int idx = i;
    workers[i] = new Thread(() -> partial[idx] = compute(idx));
    workers[i].start();
}
for (Thread w : workers) w.join();      // safe to read partial[] now
```

## Run
```bash
cd src
java Basics.Multithreading.ThreadJoinMethod
```

## See also
- `ThreadInterruption.java` — handling `InterruptedException` properly.
- `CountDownLatchDemo.java` — multi-thread arrival coordination.
- `CompletableFutureDemo.java` — async results without raw threads.
