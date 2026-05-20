# Virtual Threads — Java 21 (JEP 444)

A **virtual thread** is a thread implemented by the JVM rather than the OS.
It behaves exactly like a normal `Thread`, but:

- **Cheap to create** — microseconds, kilobytes.
- The JVM multiplexes **millions** of them onto a small pool of *carrier*
  threads (platform threads, typically equal to your CPU count).
- When a virtual thread blocks on an I/O call or other JDK blocking
  primitive, the JVM **unmounts** it from its carrier so the carrier can
  run another virtual thread.

Net effect: write old-fashioned synchronous, blocking code with
`Thread.sleep` / `readLine` / `Socket.read` and get event-loop-style
scalability.

## Creating
```java
Thread.startVirtualThread(runnable);
Thread.ofVirtual().name("vt-1").start(runnable);
ThreadFactory vf = Thread.ofVirtual().factory();
ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
```

## What they're great at
- I/O-bound fan-out — thousands of concurrent HTTP / DB calls.
- Per-request workers without "reactive" obfuscation.
- Replacing thread pools whose only purpose was to limit thread *count*.

## What they don't help with
- Pure CPU-bound work — your real parallelism is the carrier count.
- Code that **pins** the virtual thread to its carrier.

## Pinning
A virtual thread is pinned (can't be unmounted) while:
- It's inside a `synchronized` block.
- It's executing a JNI native method.

Symptom: throughput collapses to "number of carriers." Diagnose with
`-Djdk.tracePinnedThreads=full`.

**Fixes:**
- Replace `synchronized` around long operations with `ReentrantLock`.
- Future Java releases plan to remove the `synchronized` pinning constraint.

## `ThreadLocal` cost
Each virtual thread has its own `ThreadLocal` map. With millions of VTs,
that's a lot of state. Prefer **Scoped Values** (Java 21 preview) for
per-call context.

## When to prefer VTs over a fixed pool
| Workload | Pick |
|---|---|
| I/O-bound HTTP server, DB pipeline | Virtual threads |
| CPU-bound number crunching | Fixed pool ≈ #cores or `ForkJoinPool` |
| Mixed — UI render + I/O | Mix: VT executor for I/O, dedicated platform pool for compute |

## Migration tips
- Don't pool virtual threads — create one per task.
- Don't `interrupt` to "kill" a VT; cooperate via interruption like before.
- Don't size based on memory; size based on **upstream rate** (still need a `Semaphore` to bound concurrent calls to a downstream system).

## Run
```bash
cd src
java Basics.Multithreading.VirtualThreads
```

## See also
- `StructuredConcurrency.java` — `StructuredTaskScope` works hand-in-hand with VTs.
- `ScopedValuesDemo.java` — modern replacement for `ThreadLocal`.
- `ExecutorFramework.java` — `newVirtualThreadPerTaskExecutor`.
- `ThreadPools.java` — when a classical pool still wins.
