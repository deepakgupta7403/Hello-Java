# `Thread.start()` vs `Thread.run()`

One of the most common interview / debugging traps in Java.

| | `start()` | `run()` |
|---|---|---|
| Creates a new thread? | Yes | No |
| Executes `run()` on... | the new thread | the **current** thread |
| Allowed to call twice? | No — throws `IllegalThreadStateException` | Yes, like any method |
| Concurrency? | Yes | No (serial) |
| Use it? | **Yes** | Almost never |

## Why the trap is dangerous
Concurrent code that *appears to work* when you accidentally call `run()`
provides no parallelism and silently hides race conditions, because there
is only one thread of execution. The bug only manifests when you actually
push load through the system.

## The signature pattern
```java
Thread t = new Thread(task, "worker");
t.start();                      // RIGHT
t.run();                        // WRONG — runs on current thread
```

## After `start()` returns
- The new thread *may* have already finished, may be in the middle of `run()`, or may not have started yet.
- The only guarantee: `start()` *happens-before* every action inside the new thread's `run()`.

## In modern Java
- Prefer `ExecutorService.submit(task)` — the `start`/`run` confusion goes away.
- For one-off virtual threads use `Thread.startVirtualThread(task)` or `Thread.ofVirtual().start(task)`.

## Run
```bash
cd src
java Basics.Multithreading.StartVsRun
```

## See also
- `Threads.java` — the `Thread` API in general.
- `RunnableInterface.java` — defining the work itself.
