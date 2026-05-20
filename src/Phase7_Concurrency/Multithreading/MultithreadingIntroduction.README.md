# Multithreading — Introduction

## What is multithreading?
Running multiple **threads of execution** inside the same JVM process. Threads
share the process's heap and class metadata, but each has its own stack and
program counter. This makes communication cheap (shared variables) but makes
*correctness* hard (races, deadlocks, visibility).

## Concurrency vs parallelism
| | Concurrency | Parallelism |
|---|---|---|
| Idea | Structure: dealing with many things at once | Execution: doing many things at once |
| CPUs | Works on a single core via time-slicing | Needs multiple cores |
| Example | Async I/O event loop | Parallel sort across 8 cores |

Concurrency is a *property of your program*; parallelism is a *property of the
hardware*. A well-designed concurrent program will benefit from parallel hardware,
but it doesn't require it.

## Why threads?
- **Utilise the CPU** while another thread waits for I/O.
- **Keep the UI responsive** — render thread separate from work threads.
- **Increase throughput** — handle many requests concurrently on a server.
- **Model natural concurrency** — producer/consumer, game loops, simulations.

## Java's concurrency toolkit
| API | Since | What it's for |
|---|---|---|
| `Thread`, `Runnable` | 1.0 | Building blocks for threads |
| `synchronized`, `wait/notify` | 1.0 | Built-in monitor locking |
| `volatile` | 1.0 | Visibility guarantees |
| `java.util.concurrent` (executors, queues, atomics, futures) | 5 | High-level utilities |
| `Lock`, `ReadWriteLock` | 5 | Explicit lock objects |
| `StampedLock`, `CompletableFuture` | 8 | Optimistic reads, async chains |
| `Virtual Threads` | 21 | Millions of cheap user-mode threads |
| `Structured Concurrency` | 21 (preview) | Treat task groups as one unit |
| `Scoped Values` | 21 (preview) | Immutable per-call context |

## Pitfalls beginners hit
1. Calling `run()` directly instead of `start()` — runs on the *current* thread.
2. Sharing mutable state without synchronization — silent data corruption.
3. Trusting `Thread.sleep` for coordination — use `wait/notify`, `join`, or a
   `CountDownLatch` instead.
4. Catching `InterruptedException` and ignoring it — always restore the flag
   with `Thread.currentThread().interrupt()`.
5. Holding a lock while calling foreign code — invitation to deadlock.

## Run
```bash
cd src
java Basics.Multithreading.MultithreadingIntroduction
```

## See also
- `Threads.java` — the `Thread` API in depth.
- `ThreadLifecycle.java` — the six states a thread can be in.
- `JavaSynchronization.java` — `synchronized` blocks and monitors.
- `VirtualThreads.java` — Java 21 virtual threads.
