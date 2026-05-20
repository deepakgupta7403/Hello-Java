# Producer-Consumer Pattern

Producers and consumers communicate via a **shared, bounded queue**.

```
producer -> [ . . . . ] -> consumer
```

Bounding gives you **backpressure**: if consumers fall behind, the queue
fills, producers block, and the system stops swallowing work it can't keep
up with.

## Where the pattern shows up
- Task pipelines (read → parse → transform → write).
- Log aggregators, metrics pipelines.
- Web crawlers (URLs to fetch).
- Media buffers (decoded frames waiting for rendering).
- Request workers behind an accept loop.

## Three idiomatic implementations
| # | Tools | When to use |
|---|---|---|
| 1 | `synchronized` + `wait` / `notifyAll` | Educational; legacy code |
| 2 | `ReentrantLock` + two `Condition`s (`notEmpty`, `notFull`) | Need fine-grained control, multiple condition vars, fairness |
| 3 | `BlockingQueue` (`ArrayBlockingQueue`, `LinkedBlockingQueue`, ...) | **Default choice in modern code** |

The first two are great for understanding what the third hides from you.

## Correctness checklist
- [ ] Take/put under the **same lock** as the size check.
- [ ] **Loop** the predicate (`while`, not `if`) — spurious wakeups exist.
- [ ] Signal the correct side. With two `Condition`s use `notEmpty.signal()` on put and `notFull.signal()` on take. With `wait/notifyAll`, use `notifyAll()` so neither side is starved.
- [ ] Don't hold the lock while doing the producer / consumer's actual work; only access the queue.
- [ ] Handle interruption: catch `InterruptedException`, restore the flag, exit cleanly.
- [ ] Drain or sentinel on shutdown — see `take()` sentinels in the demo.

## BlockingQueue cheatsheet
| Need | Operation |
|---|---|
| Block until ready | `put(e)` / `take()` |
| Try without blocking | `offer(e)` / `poll()` |
| Time-bounded | `offer(e, time, unit)` / `poll(time, unit)` |
| Throw if full/empty | `add(e)` / `remove()` |

## When to pick which `BlockingQueue`
| | When |
|---|---|
| `ArrayBlockingQueue` | Bounded, FIFO, optional fairness |
| `LinkedBlockingQueue` | Optionally bounded, separate locks for head/tail (better throughput) |
| `SynchronousQueue` | Capacity zero — direct hand-off, every `put` matches a `take` |
| `PriorityBlockingQueue` | Unbounded, heap-ordered |
| `DelayQueue` | Elements with a delay — `take` blocks until expiry |

## Run
```bash
cd src
java Basics.Multithreading.ProducerConsumer
```

## See also
- `WaitNotifyNotifyAll.java`
- `ReentrantLockDemo.java` — multiple condition vars in detail.
- `BlockingQueueDemo.java` in `../Collections/` for the full BlockingQueue tour.
