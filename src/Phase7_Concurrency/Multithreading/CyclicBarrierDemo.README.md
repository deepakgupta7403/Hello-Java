# `CyclicBarrier`

A **resettable** barrier for a fixed number of parties. Each party calls
`await()` and blocks; when all N have arrived, they're released together
and the barrier **resets** for the next round.

```java
CyclicBarrier b = new CyclicBarrier(N);
CyclicBarrier b = new CyclicBarrier(N, () -> System.out.println("end of round"));
```

Optional **barrier action** runs once per round on the last-arriving thread,
before any party returns from `await()`. Great for "commit the round's
results."

## Use cases
- Parallel iterative algorithms — every worker computes a slice, all wait
  for the round to finish, then start the next.
- Game-loop tick synchronisation across worker threads.
- Step-locked simulations.

## Methods
| Method | Effect |
|---|---|
| `await()` | Wait at the barrier |
| `await(time, unit)` | Timed wait — timeout breaks the barrier |
| `getParties()` | N (fixed) |
| `getNumberWaiting()` | How many have arrived |
| `isBroken()` | Is the barrier broken? |
| `reset()` | Break and reset; waiters throw `BrokenBarrierException` |

## Broken barrier semantics
If **any** party fails the round (timeout, interrupt, barrier-action threw),
**all** waiters get `BrokenBarrierException` and the barrier must be `reset()`
before re-use. This makes silent stalls impossible.

## `CountDownLatch` vs `CyclicBarrier`
| | `CountDownLatch` | `CyclicBarrier` |
|---|---|---|
| Reusable | No | Yes |
| Caller pattern | Producers `countDown`, consumers `await` | Every party calls `await` |
| Barrier action | No | Yes |
| Breaks on failure | No (count stays where it was) | Yes — explicit |

## Skeleton
```java
CyclicBarrier b = new CyclicBarrier(N);
for (int i = 0; i < N; i++) {
    new Thread(() -> {
        for (int r = 0; r < ROUNDS; r++) {
            doWorkForRound();
            try { b.await(); }
            catch (BrokenBarrierException | InterruptedException e) { return; }
        }
    }).start();
}
```

## When to pick what
| Goal | Pick |
|---|---|
| Repeated lockstep rounds, fixed party count | `CyclicBarrier` |
| Variable party count (parties join/leave per round) | `Phaser` |
| One-shot "all done" | `CountDownLatch` |
| Cap concurrent access | `Semaphore` |

## Run
```bash
cd src
java Basics.Multithreading.CyclicBarrierDemo
```

## See also
- `CountDownLatchDemo.java`, `PhaserDemo.java`, `SemaphoreDemo.java`.
- `ForkJoinPoolDemo.java` — when divide-and-conquer is a better fit.
