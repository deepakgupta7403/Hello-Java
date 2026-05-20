# `StampedLock` (Java 8+)

Three modes:
| Mode | Acquired via | Cost |
|---|---|---|
| **Write** | `writeLock()` | Exclusive |
| **Read** | `readLock()` | Shared |
| **Optimistic read** | `tryOptimisticRead()` | Free — no lock acquired |

Optimistic reads are almost as cheap as a `volatile` read. The catch:
the value you read **may** be stale, so you copy fields locally and
then validate the stamp. If a writer ran during your read, `validate`
returns false and you fall back to a real read lock.

## Skeleton: read-mostly object
```java
class Point {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead();
        double lx = x, ly = y;                 // local copies
        if (!sl.validate(stamp)) {             // a writer ran — re-read for real
            stamp = sl.readLock();
            try { lx = x; ly = y; }
            finally { sl.unlockRead(stamp); }
        }
        return Math.hypot(lx, ly);
    }

    void move(double dx, double dy) {
        long stamp = sl.writeLock();
        try { x += dx; y += dy; }
        finally { sl.unlockWrite(stamp); }
    }
}
```

## Differences vs `ReentrantReadWriteLock`
| | `StampedLock` | `ReentrantReadWriteLock` |
|---|---|---|
| Reentrant | **No** | Yes |
| Conditions | No | On write lock |
| Optimistic read | Yes (the killer feature) | No |
| Performance under read-heavy load | Often best | Good |
| Easy to use correctly | Harder | Easier |

## Key API
```java
long s = sl.writeLock();          sl.unlockWrite(s);
long s = sl.readLock();           sl.unlockRead(s);
long s = sl.tryOptimisticRead();  sl.validate(s);
long ws = sl.tryConvertToWriteLock(s);   // 0 on failure
long rs = sl.tryConvertToReadLock(s);
sl.unlock(s);                     // works for any stamp
```

## Pitfalls
- **Non-reentrant** — re-acquiring on the same thread deadlocks.
- The optimistic read pattern must **copy fields to locals** before validating; dereferencing live state during the optimistic window can yield inconsistent values.
- Never call user code (callbacks, overridden methods) inside the optimistic window.
- No `Condition` support — use `wait`/`notify` or another lock if you need it.

## Run
```bash
cd src
java Basics.Multithreading.StampedLockDemo
```

## See also
- `ReadWriteLockDemo.java` — the simpler reentrant alternative.
- `LocksInJava.java`, `LockFrameworkVsSync.java`.
- `JavaMemoryModel.java` — why validation works (release/acquire semantics).
