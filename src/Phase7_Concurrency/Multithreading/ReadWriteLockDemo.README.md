# `ReadWriteLock` / `ReentrantReadWriteLock`

Two locks for the price of one:
- **Read lock** — many threads may hold it simultaneously.
- **Write lock** — exclusive; no readers or writers active.

Use when reads vastly outnumber writes — configuration cache, lookup table,
in-memory index.

## Skeleton
```java
ReadWriteLock rw = new ReentrantReadWriteLock();
Lock r = rw.readLock();
Lock w = rw.writeLock();

r.lock(); try { /* read */ }  finally { r.unlock(); }
w.lock(); try { /* write */ } finally { w.unlock(); }
```

## Properties
| | Detail |
|---|---|
| Reentrant | Yes (the `ReentrantReadWriteLock` impl) |
| Fairness | Optional (`new ReentrantReadWriteLock(true)`) |
| Lock downgrade (W → R) | **Allowed** — see pattern below |
| Lock upgrade (R → W) | **Not allowed** — releases first, then acquire |
| Conditions | Only on the write lock |

## Downgrade idiom
```java
rw.writeLock().lock();
try {
    // mutate
    rw.readLock().lock();          // hold both simultaneously
    rw.writeLock().unlock();       // release write while keeping read
} finally {
    // ... read ... then rw.readLock().unlock();
}
```

## Why upgrade deadlocks
If two threads both hold the read lock and each tries to upgrade, neither
can — each needs the *other* reader to release first. Always release the
read lock, then acquire the write lock, then re-check your assumption.

## Write starvation
Under heavy read load, an unfair rwlock can leave writers waiting forever.
Use `new ReentrantReadWriteLock(true)` to force FIFO — writers waiting block
new readers from jumping ahead. Cost: lower throughput.

## When to pick something else
| Alternative | When |
|---|---|
| `StampedLock` | Lots of reads, want optimistic reads (no read lock cost). Not reentrant. |
| `ConcurrentHashMap` | The shared structure **is** a map — skip the lock. |
| `CopyOnWriteArrayList` | Many reads, very rare writes, snapshot iteration is OK. |

## Run
```bash
cd src
java Basics.Multithreading.ReadWriteLockDemo
```

## See also
- `LocksInJava.java`, `ReentrantLockDemo.java`.
- `StampedLockDemo.java` — the next step up for read-heavy workloads.
- `ThreadSafety.java` — picking a strategy.
