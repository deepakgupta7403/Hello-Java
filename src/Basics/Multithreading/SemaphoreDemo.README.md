# `Semaphore`

A counter that hands out **permits**. `acquire()` blocks until a permit is
available; `release()` returns one. With N permits you can let up to N
threads through a section at once.

```java
Semaphore slots = new Semaphore(N);
slots.acquire();
try {
    // use the resource
} finally {
    slots.release();
}
```

## What semaphores model
- A **pool** of N identical resources (DB connections, HTTP workers, GPU slots).
- A **cap** on concurrency — at most N requests in flight.
- A **signal** — `release()` can be called by a *different* thread than `acquire()`.

## Methods
| Method | Effect |
|---|---|
| `acquire()` | Take one permit, blocking |
| `acquireUninterruptibly()` | Ignore interrupts while waiting |
| `tryAcquire()` | Non-blocking attempt |
| `tryAcquire(time, unit)` | Timed attempt |
| `release()` | Return one permit |
| `acquire(n)` / `release(n)` | Bulk permits |
| `availablePermits()` | Approximate free count (monitoring only) |
| `drainPermits()` | Take all available; returns how many |

## Fair vs unfair
| | `new Semaphore(N)` | `new Semaphore(N, true)` |
|---|---|---|
| Order | Unfair (may barge) | FIFO |
| Throughput | Higher | Lower |
| Risk | Starvation | Slow tail |

## Binary semaphore — a lock that need not be released by its acquirer
`new Semaphore(1)` looks like a mutex, but unlike `ReentrantLock` the
**releasing thread doesn't have to be the acquiring thread**. Great for
hand-off between threads:
```java
Semaphore handoff = new Semaphore(0);
// thread A
handoff.acquire();   // waits for the signal
// thread B
handoff.release();   // signals A
```

## Pitfalls
- Always `release()` in `finally` — otherwise permits leak forever.
- Don't double-release on the same logical acquire — permits inflate silently.
- Initial permits aren't a maximum cap; `release()` can take you past it.

## When to pick what
| Goal | Pick |
|---|---|
| Cap concurrent requests | `Semaphore(N)` |
| Mutex with cross-thread release | `Semaphore(1)` |
| Resource pool (connections, etc.) | `Semaphore(N)` around acquire/release |
| Wait for one event | `CountDownLatch(1)` |
| Repeated lockstep rounds | `CyclicBarrier` / `Phaser` |

## Run
```bash
cd src
java Basics.Multithreading.SemaphoreDemo
```

## See also
- `CountDownLatchDemo.java`, `CyclicBarrierDemo.java`, `PhaserDemo.java`.
- `BlockingQueueDemo.java` in `../Collections/` — bounded queue uses semaphore-like backpressure internally.
