# `Thread.sleep(...)`

Pauses the **current** thread for at least the given amount of time.

## Overloads
| Signature | Since |
|---|---|
| `Thread.sleep(long millis)` | 1.0 |
| `Thread.sleep(long millis, int nanos)` | 1.0 |
| `Thread.sleep(Duration d)` | 19 |

## Key rules
1. **It's static** and only affects `Thread.currentThread()`. You cannot sleep another thread.
2. **State becomes `TIMED_WAITING`** — visible in thread dumps.
3. **Does NOT release locks.** A sleeping thread inside `synchronized` still owns the monitor.
4. **It's interruptible** — another thread calling `.interrupt()` throws `InterruptedException` *and clears the interrupt flag*. Restore it with `Thread.currentThread().interrupt()`.
5. **Duration is a lower bound.** OS scheduling / GC / pre-emption can extend it.
6. **`Thread.sleep(0)`** isn't a no-op; it offers the scheduler a chance to switch.

## What sleep is **not** for
| Goal | Use instead |
|---|---|
| Wait for a condition | `wait`/`notify`, `LockSupport.park`, `CountDownLatch`, `await` on a `Condition` |
| Rate-limit | `Semaphore`, a real rate limiter, `ScheduledExecutorService` |
| Coordinate "I'm done" | `Thread.join()`, `Future.get()`, `CompletableFuture` |
| Retry until ready | Exponential backoff with a real retry library — *and* sleep, OK |

Polling something with `sleep` in a loop is almost always the wrong shape.

## Interruption pattern
```java
try {
    Thread.sleep(5_000);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();   // restore the flag
    // exit early or rethrow as a domain exception
}
```

## Run
```bash
cd src
java Basics.Multithreading.ThreadSleepMethod
```

## See also
- `ThreadInterruption.java` — what `.interrupt()` actually does.
- `ThreadYieldMethod.java` — the closest cousin (give up CPU without waiting).
- `WaitNotifyNotifyAll.java` — sleep-with-coordination.
