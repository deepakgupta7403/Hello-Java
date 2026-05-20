# Thread Lifecycle

A thread moves through six states, captured by `Thread.State`:

| State | When | How to leave |
|---|---|---|
| `NEW` | After `new Thread(...)`, before `start()` | `start()` → `RUNNABLE` |
| `RUNNABLE` | Eligible to run on a CPU (running *or* waiting for a core) | Block on a monitor, wait, sleep, finish |
| `BLOCKED` | Waiting to enter a `synchronized` block whose monitor another thread holds | Other thread releases → `RUNNABLE` |
| `WAITING` | `wait()`, `join()`, `LockSupport.park()` (no timeout) | `notify/notifyAll`, target finishes, `unpark` |
| `TIMED_WAITING` | Timed variants: `sleep(ms)`, `wait(ms)`, `join(ms)`, `parkNanos`, `parkUntil` | Wake-up, timeout, interrupt |
| `TERMINATED` | `run()` returned (normally or via uncaught exception) | **No exit** — create a new thread |

## State diagram (compact)

```
       start()                schedule
  NEW --------> RUNNABLE <--------------> running on CPU
                  |  ^
  synchronized -->|  |
                  v  |
              BLOCKED                     (waiting for monitor)

  wait/join/park ->|
                   v
              WAITING / TIMED_WAITING
                   |
            notify/timeout/interrupt
                   v
              RUNNABLE

       run() returns
                   v
              TERMINATED
```

## Common confusions
- **`RUNNABLE` ≠ "currently running."** It just means "eligible to run." The
  OS scheduler picks who actually executes.
- **`BLOCKED` is only for `synchronized` contention.** Threads waiting on a
  `ReentrantLock`/`Semaphore`/`CountDownLatch` are `WAITING` or
  `TIMED_WAITING` — those primitives use `LockSupport.park` internally.
- **`sleep` doesn't release locks.** A sleeping thread holding a monitor
  still blocks everyone trying to enter that monitor.
- **You cannot restart a `TERMINATED` thread.** `start()` throws
  `IllegalThreadStateException`. Create a new instance.

## Inspecting state
- `Thread.getState()` returns the enum.
- `jstack <pid>` or VisualVM/JFR shows it in a thread dump.
- `Thread.getAllStackTraces()` returns a snapshot of every live thread.

## Run
```bash
cd src
java Basics.Multithreading.ThreadLifecycle
```

## See also
- `JavaSynchronization.java` — what causes `BLOCKED`.
- `WaitNotifyNotifyAll.java` — what causes `WAITING`.
- `ThreadSleepMethod.java` — what causes `TIMED_WAITING`.
