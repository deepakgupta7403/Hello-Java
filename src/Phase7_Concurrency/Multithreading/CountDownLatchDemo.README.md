# `CountDownLatch`

A **one-shot** counter initialised with N. Threads call `await()` to block
until the counter reaches zero. Other threads call `countDown()` to decrement.

```java
CountDownLatch latch = new CountDownLatch(N);

latch.await();          // blocks until count == 0
latch.countDown();      // decrements
```

## Two classic uses
1. **Starting gate** — main thread holds N workers behind a latch of **1**. When setup is complete, main calls `countDown()` and they all start together.
2. **Completion gate** — main thread waits behind a latch of **N**. Each worker calls `countDown()` when finished. Main wakes when the last one done.

The first is "ready, set, go." The second is "wait for all of you to come back."

## Methods
| Method | Effect |
|---|---|
| `await()` | Block until count reaches 0 (interruptible) |
| `await(time, unit)` | Block with timeout |
| `countDown()` | Decrement; no-op once at 0 |
| `getCount()` | Current value (monitoring only) |

## Pitfalls
- **Always `countDown()` in a `finally`** so a thrown worker doesn't leave the latch above zero forever.
- **The latch is one-shot.** Need a resettable barrier? Use `CyclicBarrier` or `Phaser`.
- The initial count must match the number of workers that will actually run.

## `CountDownLatch` vs `CyclicBarrier`
| | `CountDownLatch` | `CyclicBarrier` |
|---|---|---|
| Reusable | No | **Yes** |
| Who waits | Some threads call `await`, others `countDown` | Every party calls `await` and all release together |
| Optional barrier action | No | Yes (the constructor's `Runnable`) |

## Pattern: completion of N workers
```java
CountDownLatch done = new CountDownLatch(N);
for (int i = 0; i < N; i++) {
    new Thread(() -> {
        try { doWork(); }
        finally { done.countDown(); }
    }).start();
}
done.await();
```

## When to use what
| Goal | Pick |
|---|---|
| One-time "go!" | `CountDownLatch(1)` |
| One-time "all done" | `CountDownLatch(N)` |
| Repeated lockstep rounds | `CyclicBarrier` or `Phaser` |
| Unknown / changing party count | `Phaser` |
| Cap concurrency | `Semaphore` |

## Run
```bash
cd src
java Basics.Multithreading.CountDownLatchDemo
```

## See also
- `CyclicBarrierDemo.java`, `SemaphoreDemo.java`, `PhaserDemo.java`.
- `ThreadJoinMethod.java` — the "wait for one thread" cousin.
