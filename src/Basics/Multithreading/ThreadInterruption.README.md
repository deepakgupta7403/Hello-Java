# Thread Interruption

Java has no safe way to forcibly kill a thread. The standard mechanism is
**cooperative cancellation** via the **interrupt flag**.

## The three calls
| Call | Effect |
|---|---|
| `t.interrupt()` | Sets `t`'s interrupt flag. If `t` is in a *blocking* call, the call throws `InterruptedException` and clears the flag. |
| `t.isInterrupted()` | Reads `t`'s flag without changing it. |
| `Thread.interrupted()` (static) | Reads **and clears** the current thread's flag. |

## Which calls throw `InterruptedException`?
- `Thread.sleep`, `Thread.join`, `Object.wait`
- `BlockingQueue.put` / `take` / timed `offer`
- `CountDownLatch.await`, `CyclicBarrier.await`, `Semaphore.acquire`
- `Lock.lockInterruptibly`, `Condition.await`
- `Future.get`

`LockSupport.park` is *interruptible* but **does not throw** — it just returns and sets the flag.

Plain compute loops are **not** interrupted automatically — you must check the flag yourself.

## Best-practice template

```java
try {
    while (!Thread.currentThread().isInterrupted()) {
        // ... work ...
        someBlockingCall();          // may throw InterruptedException
    }
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();   // restore the flag for callers
    // optionally rethrow as a domain exception
}
```

## Rules
1. **Never swallow `InterruptedException` silently.** Always either restore the flag with `Thread.currentThread().interrupt()`, or wrap and rethrow.
2. In long compute loops, periodically check `isInterrupted()` and break.
3. Prefer interruption over a custom `volatile boolean stop` — it composes with every JDK blocking primitive for free.
4. Catching `InterruptedException` *clears* the flag. If you didn't expect to be cancelled, re-set it.
5. Don't ever call `Thread.stop()` / `suspend()` / `resume()`. They are deprecated for removal.

## When to use what
| Goal | Tool |
|---|---|
| "Please stop the worker" | `t.interrupt()` |
| "Has the worker been asked to stop?" | `t.isInterrupted()` |
| "I want to check and reset my own flag" | `Thread.interrupted()` |
| "Stop after this batch" | Volatile boolean + interrupt for blocking ops |

## Run
```bash
cd src
java Basics.Multithreading.ThreadInterruption
```

## See also
- `ThreadSleepMethod.java`, `ThreadJoinMethod.java` — interruptible blocking calls.
- `WaitNotifyNotifyAll.java` — `wait()` is interruptible too.
- `ExecutorFramework.java` — `Future.cancel(true)` interrupts the running task.
