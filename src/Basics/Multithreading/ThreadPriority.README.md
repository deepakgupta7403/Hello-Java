# Thread Priority

Every Java thread has an integer **priority** in `[1, 10]`. It is a **hint** to
the OS scheduler — not a guarantee.

## Constants
| Constant | Value |
|---|---|
| `Thread.MIN_PRIORITY` | 1 |
| `Thread.NORM_PRIORITY` | 5 (the default) |
| `Thread.MAX_PRIORITY` | 10 |

## How it actually maps to the OS
The JVM maps Java's 1–10 onto whatever the host OS supports:
- **Linux** — under CFS, priority often has small-to-zero effect on user threads.
- **Windows** — mapped to thread priority levels (Idle … Time-Critical).
- **macOS** — influenced by Quality of Service classes.

On modern desktops and servers, **don't rely on priorities for correctness**.

## Inheritance
A new `Thread` inherits the priority of the thread that created it, unless
you call `setPriority` before `start()`.

## Validation
- `setPriority(int)` throws `IllegalArgumentException` outside `[1, 10]`.
- A `ThreadGroup`'s `getMaxPriority()` further caps members of that group.

## When priority is **not** the answer
| Want | Use instead |
|---|---|
| Ordering / fairness | A `BlockingQueue` or `Semaphore(fair = true)` |
| Avoid starvation | Bounded queues, work stealing |
| "Run before this other one" | An explicit dependency: latch, future chain, `join` |
| "Cap CPU usage" | A `ThreadPoolExecutor` with a smaller pool size |

## When it *can* help
- Set background / cleanup threads to `MIN_PRIORITY` so they don't crowd
  out interactive work.
- On dedicated real-time-ish workloads where you own the scheduler tuning.

## Run
```bash
cd src
java Basics.Multithreading.ThreadPriority
```

## See also
- `DaemonThread.java` — the other "background" attribute.
- `MainThread.java` — default priority of `main`.
