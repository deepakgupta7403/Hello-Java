# Locks in Java

Java has **two** families of locking primitives:

| Family | API | Used via |
|---|---|---|
| **Intrinsic locks** (monitors) | Built into every `Object` | `synchronized`, `wait`/`notify` |
| **Explicit locks** | `java.util.concurrent.locks` | `Lock`, `ReadWriteLock`, `StampedLock` |

## The `Lock` interface
```java
public interface Lock {
    void lock();
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    void unlock();
    Condition newCondition();
}
```

Always use it with `try`/`finally`:
```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

## Implementations
| Class | Notes |
|---|---|
| `ReentrantLock` | Drop-in for `synchronized`; reentrant; optional fairness; multiple `Condition`s; timed / interruptible acquire. |
| `ReentrantReadWriteLock` | Two locks: many readers OR one writer. Good when reads vastly outnumber writes. |
| `StampedLock` | Optimistic reads + read / write modes. **Not reentrant**, no `Condition`s. Best raw throughput for read-heavy workloads. |

## `Lock` vs `synchronized`
| Feature | `synchronized` | `Lock` |
|---|---|---|
| Timed acquisition | ❌ | `tryLock(time, unit)` |
| Interruptible acquire | ❌ | `lockInterruptibly()` |
| Fairness | ❌ | Optional (`new ReentrantLock(true)`) |
| Multiple condition variables | ❌ (one per monitor) | `newCondition()` × N |
| Automatic release on scope exit | ✅ | ❌ — `finally` is mandatory |
| Hard to forget | ✅ | ❌ |

## Common pitfalls
- **Forgetting `unlock()`** — always use `try`/`finally`.
- **Acquiring locks in different orders** in different threads → deadlock.
- **Calling foreign code while holding a lock** — overrides / callbacks → deadlock.
- **Holding a lock for too long** — kills concurrency.
- **Using `synchronized(this)` *and* `lock.lock()`** on the same code — two mechanisms guarding the same state. Pick one.

## When to use each
| Need | Pick |
|---|---|
| Small block, no extra features | `synchronized` |
| Timed or interruptible acquisition | `ReentrantLock` |
| Multiple condition variables | `ReentrantLock` + `Condition`s |
| Reads >> writes | `ReentrantReadWriteLock` or `StampedLock` |
| Counter-like ops only | `AtomicInteger` / `LongAdder` — no lock at all |

## Run
```bash
cd src
java Basics.Multithreading.LocksInJava
```

## See also
- `JavaSynchronization.java` — the intrinsic side.
- `ReentrantLockDemo.java`, `ReadWriteLockDemo.java`, `StampedLockDemo.java` — each lock in depth.
- `LockVsMonitor.java`, `LockFrameworkVsSync.java` — side-by-side comparisons.
- `DeadlockDemo.java` — how to *break* locks.
