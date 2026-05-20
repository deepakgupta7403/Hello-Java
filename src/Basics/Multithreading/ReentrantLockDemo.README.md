# `ReentrantLock`

The workhorse explicit lock. A drop-in upgrade from `synchronized` with extras:
- `tryLock()` — non-blocking attempt.
- `tryLock(time, unit)` — timed acquire.
- `lockInterruptibly()` — cancellable acquire.
- `new ReentrantLock(boolean fair)` — FIFO acquisition order.
- `newCondition()` × N — many condition variables on one lock.

Reentrant: the same thread can lock it many times, each needing a matching
`unlock()`.

## Always wrap in `try`/`finally`
```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

## Fair vs unfair
| | Unfair (default) | Fair |
|---|---|---|
| Behaviour | New thread may "barge" ahead of waiters | FIFO queueing |
| Throughput | Higher | Lower |
| Latency tail | Worse — possible starvation | Better |
| Use when | Performance > order | You actually need fairness (rare) |

Don't reach for `fair=true` reflexively — most code is happier with the
default.

## Conditions
Each `ReentrantLock` can produce any number of `Condition` objects. Each
maintains its own wait queue.

```java
ReentrantLock lock = new ReentrantLock();
Condition notEmpty = lock.newCondition();
Condition notFull  = lock.newCondition();
```

Methods on `Condition`:
| Method | Effect |
|---|---|
| `await()` | Release lock, wait, re-acquire on signal |
| `await(time, unit)` | Timed wait |
| `awaitUntil(deadline)` | Wall-clock deadline |
| `awaitUninterruptibly()` | Don't throw `InterruptedException` |
| `signal()` / `signalAll()` | Wake one / all waiters on this condition |

`Condition` is the **way** you build a bounded buffer where producers wait
on `notFull` and consumers wait on `notEmpty` — no spurious cross-signal.

## Diagnostics
| Method | What it tells you |
|---|---|
| `isLocked()` | Anyone holds it |
| `isHeldByCurrentThread()` | Do I hold it |
| `getHoldCount()` | How many nested locks I hold |
| `getQueueLength()` | Approximate waiter count |

## Common mistakes
- Acquiring the lock inside `try { ... }` — if `lock()` throws (it doesn't, but rule-of-thumb), `finally` would unlock something you don't own.
- Calling `unlock()` from a thread that didn't lock → `IllegalMonitorStateException`.
- Storing the lock in a local variable — every method gets its own. Make it `private final`.
- Mixing `synchronized` and `ReentrantLock` on the same field.

## When to pick `ReentrantLock` over `synchronized`
- Need to give up after a timeout.
- Need to cancel a waiter mid-acquire.
- Need fair queueing.
- Need multiple condition variables.
- Need to interrogate lock state for monitoring.

## Run
```bash
cd src
java Basics.Multithreading.ReentrantLockDemo
```

## See also
- `LocksInJava.java`, `LockFrameworkVsSync.java`.
- `WaitNotifyNotifyAll.java` — the monitor equivalent.
- `ProducerConsumer.java` — the bounded buffer in three styles.
