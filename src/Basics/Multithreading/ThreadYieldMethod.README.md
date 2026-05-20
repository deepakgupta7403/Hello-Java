# `Thread.yield()`

A **hint** to the scheduler that the current thread is OK to be pre-empted.
That's it. The JVM is free to ignore it.

## What yield does (and doesn't)
| | yield |
|---|---|
| State after the call | Still `RUNNABLE` |
| Releases locks | **No** |
| Releases CPU | Maybe — purely a hint |
| Wakes when | Scheduler decides |
| Useful for synchronization | **No** |

## yield vs sleep vs wait
| | `yield()` | `sleep(ms)` | `wait()` |
|---|---|---|---|
| State | `RUNNABLE` | `TIMED_WAITING` | `WAITING` |
| Releases monitor lock? | No | No | **Yes** |
| Timed? | — | Yes | Optional |
| Wakes by | Scheduler | Timeout / interrupt | notify / interrupt |

## When to actually reach for `yield`
Honestly, almost never. If you find yourself typing `yield()`, ask:
- Am I really trying to **wait for a condition**? → use `wait`/`notify`, `CountDownLatch`, or `Condition`.
- Am I trying to **rate-limit**? → use a `Semaphore` or `ScheduledExecutorService`.
- Am I in a **tight spin loop**? → prefer `Thread.onSpinWait()` (Java 9+) — it's a CPU-pipeline hint and is cheaper.

## The two niches `yield` still earns a place
1. Custom benchmark loops that want the OS to consider other work.
2. Inside a hand-rolled spin-wait where `onSpinWait()` alone isn't enough.

## Run
```bash
cd src
java Basics.Multithreading.ThreadYieldMethod
```

## See also
- `ThreadSleepMethod.java` — actually pause.
- `WaitNotifyNotifyAll.java` — actually wait for a condition.
- `LocksInJava.java` / `ReentrantLockDemo.java` — real coordination.
