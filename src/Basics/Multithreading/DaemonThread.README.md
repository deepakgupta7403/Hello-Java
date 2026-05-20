# Daemon Threads

A thread is either a **user** thread (default) or a **daemon**. The only
difference is what happens at **JVM shutdown**:

> The JVM exits when all **user** threads have finished. Any remaining
> **daemon** threads are killed abruptly.

## Rules
1. **`setDaemon(true)` must be called before `start()`.** After `start()` it
   throws `IllegalThreadStateException`.
2. A new thread **inherits** the daemon flag from its creator.
3. Daemons die abruptly — `finally` blocks and shutdown hooks may not run.
   So **never** use a daemon for work that must clean up:
   - Database writes
   - Log file appenders
   - Network flushes / RPC drains
   - Lock releases that other JVMs care about

## When to use a daemon
| Use case | Daemon? |
|---|---|
| GC, JIT internals, finalizer | ✅ |
| Metric/heartbeat publisher | ✅ |
| Cache eviction sweeper | ✅ |
| Anything that must finish before exit | ❌ |
| Pool of request workers | ❌ (unless intentional auto-shutdown) |

## ExecutorService gotcha
`Executors.newFixedThreadPool(n)` returns workers that are **user threads** by
default. A pool you forget to `shutdown()` will keep your JVM alive. Two fixes:
- Call `shutdown()` (or `shutdownNow()`) at the end of your work.
- Or supply a `ThreadFactory` that produces daemons.

```java
ExecutorService es = Executors.newFixedThreadPool(4, r -> {
    Thread t = new Thread(r);
    t.setDaemon(true);
    return t;
});
```

## Run
```bash
cd src
java Basics.Multithreading.DaemonThread
```

## See also
- `MainThread.java` — main is a user thread; that's why the JVM doesn't quit instantly.
- `ExecutorFramework.java` — default factories and lifecycle.
- `ThreadPriority.java` — the other "is this thread important?" attribute.
