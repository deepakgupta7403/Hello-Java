# `ScheduledExecutorService`

Delayed and recurring tasks without writing your own scheduler.

```java
ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
```

## The methods
| Method | Purpose |
|---|---|
| `schedule(Runnable, delay, unit)` | One-shot after delay |
| `schedule(Callable<V>, delay, unit)` | One-shot returning `V` |
| `scheduleAtFixedRate(r, initial, period, unit)` | Start every `period` regardless of duration |
| `scheduleWithFixedDelay(r, initial, delay, unit)` | Wait `delay` **after** each run finishes |

## `fixedRate` vs `fixedDelay`
```
fixedRate(period = 100ms):
  start  0   100   200   300 ...
         even if a run took 150ms, the next still starts at 200.

fixedDelay(delay = 100ms):
  run, wait 100, run, wait 100, ...
```

## Cancelling
```java
ScheduledFuture<?> f = ses.scheduleAtFixedRate(...);
f.cancel(false);    // pass true to interrupt mid-run
```

## Failure semantics (very important)
- If a **periodic** task throws, subsequent runs are **suppressed** — the
  `ScheduledFuture`'s `get()` will rethrow.
- If you want the task to keep firing despite failures, catch *inside* the task:
  ```java
  ses.scheduleAtFixedRate(() -> {
      try {
          doWork();
      } catch (Throwable t) {
          log.error("scheduled task failed", t);
      }
  }, 0, 1, TimeUnit.SECONDS);
  ```

## Sizing the pool
- One delay queue. The scheduler still runs each task on a worker.
- For long tasks, increase the pool size so a slow task doesn't delay short ones.

## Java 21
- Pass a virtual-thread factory to schedule each fire onto a virtual thread:
  ```java
  Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
  ```

## Use cases
| Use | Pick |
|---|---|
| Heartbeat / health probe | `scheduleAtFixedRate` |
| Cache refresh that takes variable time | `scheduleWithFixedDelay` |
| Retry-after-X | `schedule` |
| Cron-style ("every weekday at 9 AM") | A library (Quartz, ShedLock) — JDK only does period/delay |

## Run
```bash
cd src
java Basics.Multithreading.ScheduledExecutorDemo
```

## See also
- `ExecutorFramework.java` — the broader API.
- `ThreadPools.java` — sizing and queue rules.
- `CompletableFutureDemo.java` — `delayedExecutor` for async delays.
