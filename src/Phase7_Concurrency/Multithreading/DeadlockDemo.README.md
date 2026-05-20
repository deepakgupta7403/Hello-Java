# Deadlock in Multithreading

Two or more threads each holding a lock another needs. None ever progresses.

```
T1: holds A, waiting for B
T2: holds B, waiting for A
```

## The Coffman conditions
All four are required for deadlock. Break **any one** to prevent it.
1. **Mutual exclusion** — resources can be held by only one thread.
2. **Hold-and-wait** — a holder requests another resource without releasing.
3. **No pre-emption** — resources release only voluntarily.
4. **Circular wait** — a cycle of threads each waiting on the next.

## Prevention strategies
| Strategy | How |
|---|---|
| **Global lock ordering** | Sort locks (e.g. by `System.identityHashCode`) and acquire in that order. Cheapest fix. |
| **Timed `tryLock` + backoff** | `ReentrantLock.tryLock(timeout)`; release and retry if you can't get them all. |
| **Single coarse lock** | Acceptable if performance allows. |
| **Lock-free data structures** | `ConcurrentHashMap`, `Atomic*`, immutable + CAS. |
| **Structured concurrency** | Java 21 `StructuredTaskScope` propagates cancellation. |

## Detection at runtime
```java
ThreadMXBean mx = ManagementFactory.getThreadMXBean();
long[] ids = mx.findDeadlockedThreads();
if (ids != null) {
    ThreadInfo[] infos = mx.getThreadInfo(ids);
    // log + alert + maybe fail the liveness probe
}
```
You can also dump stacks with `jstack <pid>` — the JVM annotates threads
participating in a detected deadlock.

## Lock-ordering pattern
```java
void transfer(Account a, Account b, BigDecimal amt) {
    Account first  = a.id() < b.id() ? a : b;
    Account second = first == a ? b : a;
    synchronized (first) {
        synchronized (second) {
            // ... transfer ...
        }
    }
}
```

## `tryLock` pattern
```java
while (true) {
    if (la.tryLock()) {
        try {
            if (lb.tryLock()) {
                try { /* work */ return; }
                finally { lb.unlock(); }
            }
        } finally { la.unlock(); }
    }
    Thread.sleep(jitter());     // randomised backoff
}
```

## Cousins
- **Livelock** — threads are *running* but mutual reactions prevent progress. See `RaceConditionStarvationLivelock.java`.
- **Starvation** — one thread is consistently passed over.
- **Priority inversion** — low-priority lock holder blocks a high-priority thread.

## Run
```bash
cd src
java Basics.Multithreading.DeadlockDemo
```

(The demo deadlocks on purpose, then `System.exit(0)`s to leave cleanly.)

## See also
- `LocksInJava.java`, `ReentrantLockDemo.java` (tryLock).
- `RaceConditionStarvationLivelock.java`.
- `StructuredConcurrency.java` (Java 21).
