# Threads (`java.lang.Thread`)

## What it is
`Thread` is the JVM-level representation of a thread of execution. Every Java
program starts with **one** thread named `main`; everything else you create.

## Two ways to define the work
| Approach | Pros | Cons |
|---|---|---|
| Extend `Thread` and override `run()` | Easy to discover | You can't extend anything else; couples work to the thread |
| Implement `Runnable` (or `Callable`) | Works with `Thread`, `ExecutorService`, lambdas; separates *what* from *how* | One extra object |

Prefer **Runnable**. Reserve `extends Thread` for tiny demos.

## Key API at a glance
| Method | Purpose |
|---|---|
| `start()` | Schedule the thread; the JVM calls `run()` on the new thread |
| `run()` | The work itself. **Never call it directly** — that runs on the *current* thread |
| `join()` | Block until the thread finishes |
| `interrupt()` / `isInterrupted()` | Cooperative cancellation |
| `setDaemon(true)` | Don't keep the JVM alive — see `DaemonThread.java` |
| `setName(String)` / `getName()` | Names show up in stack traces and profilers |
| `getState()` | One of `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED` |
| `Thread.currentThread()` | The thread running this code |
| `Thread.sleep(ms)` | Pause this thread; **does not release locks** |
| `Thread.yield()` | Hint to the scheduler that it's OK to pre-empt |
| `Thread.onSpinWait()` | Hint inside a busy-wait loop (Java 9+) |
| `Thread.ofPlatform()` / `Thread.ofVirtual()` | Java 21 builders |

## Lifecycle rules
- `start()` can be called **only once**. A second call throws `IllegalThreadStateException`.
- A thread cannot be restarted after it terminates. Create a new one.
- A daemon thread must have `setDaemon(true)` called **before** `start()`.

## Don't
- Don't subclass `Thread` just to override `run()` when a lambda will do.
- Don't call `Thread.stop()`, `Thread.suspend()`, `Thread.resume()` — they're deprecated for removal.
- Don't ignore `InterruptedException` — see `ThreadInterruption.java`.

## Run
```bash
cd src
java Basics.Multithreading.Threads
```

## See also
- `RunnableInterface.java` for Runnable in depth.
- `ThreadLifecycle.java` for the six states.
- `StartVsRun.java` for the classic `start()` vs `run()` mistake.
