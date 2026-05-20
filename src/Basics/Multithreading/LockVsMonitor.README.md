# Lock vs Monitor in Concurrency

"Monitor" = the **intrinsic lock** every `Object` carries, accessed via
`synchronized` / `wait` / `notify`.
"Lock" = the `java.util.concurrent.locks.Lock` family (typically
`ReentrantLock` + `Condition`).

## Side-by-side
| | Monitor (`synchronized`) | Lock (j.u.c.locks) |
|---|---|---|
| Acquire | `synchronized (obj) { ... }` | `lock.lock()` |
| Release | Automatic on block exit | `try/finally` + `unlock()` |
| Timed acquire | ❌ | `tryLock(time, unit)` |
| Interruptible acquire | ❌ | `lockInterruptibly()` |
| Fair acquire | ❌ | `new ReentrantLock(true)` |
| Reentrant | ✅ | ✅ (`ReentrantLock`) |
| Condition variables | One per object (`wait`/`notify`) | N per lock (`newCondition()`) |
| Read/write split | ❌ | `ReentrantReadWriteLock` |
| Optimistic read | ❌ | `StampedLock` |
| Easy to use correctly | ✅ | Requires `try/finally` |
| Performance | Excellent on modern JVMs | Comparable; sometimes better under contention |

## Choose by what you need
| Need | Pick |
|---|---|
| Short, obvious critical section | `synchronized` |
| Give up if I can't acquire in X ms | `Lock` |
| Cancel a waiter mid-acquire | `Lock` |
| Multiple distinct condition variables | `Lock` + `Condition`s |
| Many readers, few writers | `ReadWriteLock` / `StampedLock` |
| Counter-like update | `Atomic*` — no lock at all |

## Don't mix
Guarding the same field with **both** `synchronized` and a `Lock` is a bug.
The two mechanisms are not mutually exclusive — pick one per field.

## Style guidance
- Default to `synchronized` for simple short blocks.
- Reach for `Lock` when you need a feature `synchronized` doesn't give you.
- Always pair `lock.lock()` with `try { ... } finally { lock.unlock(); }`.

## Run
```bash
cd src
java Basics.Multithreading.LockVsMonitor
```

## See also
- `LockFrameworkVsSync.java` — focuses on the practical decision tree.
- `JavaSynchronization.java`, `LocksInJava.java`.
- `ReentrantLockDemo.java` — fairness, conditions, tryLock.
