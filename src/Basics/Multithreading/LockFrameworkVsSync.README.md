# Lock Framework vs Thread Synchronization (`synchronized`)

Same goal — guard shared state. Different ergonomics and feature sets.

## Decision cheatsheet
| Feature you need | `synchronized` | Lock framework |
|---|---|---|
| Mutual exclusion | ✅ | ✅ |
| Auto-release on scope exit | ✅ | ❌ (manual `try/finally`) |
| Reentrant | ✅ | ✅ |
| Timed acquire | ❌ | ✅ `tryLock(time, unit)` |
| Interruptible acquire | ❌ | ✅ `lockInterruptibly()` |
| Fair acquisition | ❌ | ✅ `new ReentrantLock(true)` |
| Multiple condition variables | ❌ | ✅ `newCondition()` × N |
| Reader / writer separation | ❌ | ✅ `ReentrantReadWriteLock` |
| Optimistic read | ❌ | ✅ `StampedLock` |
| Simplest possible code | ✅ | ❌ |

## Rule of thumb
1. Default to **`synchronized`** for plain mutual exclusion on short blocks.
2. Reach for **`ReentrantLock`** the moment you need `tryLock(timeout)`,
   interruptible acquire, fairness, or multiple condition variables.
3. For pure counters, use **`AtomicInteger`** / **`LongAdder`** — no lock at all.
4. For "many readers, occasional writer," use **`ReentrantReadWriteLock`** or
   **`StampedLock`**.

## Migrating: `synchronized` → `ReentrantLock`
```java
// Before
synchronized (this) {
    if (balance >= amount) { balance -= amount; }
}

// After
lock.lock();
try {
    if (balance >= amount) { balance -= amount; }
} finally {
    lock.unlock();
}
```
The behaviour is the same — but now you have access to `tryLock`,
`lockInterruptibly`, and as many `Condition`s as you want.

## Common mistakes when switching to `Lock`
- Forgetting `unlock()` — wrap in `try/finally` always.
- Acquiring the lock inside the `try` — if `lock()` throws, you'd run
  `unlock()` on an unowned lock. Order is **`lock(); try { ... } finally { unlock(); }`**.
- Sharing the lock object via `new ReentrantLock()` inline — every caller
  gets its own lock. Make it `final` field-level.

## Performance
On modern HotSpot, `synchronized` and `ReentrantLock` are usually within a
few percent of each other. Pick based on **features**, not micro-benchmarks.

## Run
```bash
cd src
java Basics.Multithreading.LockFrameworkVsSync
```

## See also
- `LockVsMonitor.java` — same comparison, different angle.
- `ReentrantLockDemo.java` — fairness, conditions, tryLock in depth.
- `ReadWriteLockDemo.java`, `StampedLockDemo.java`.
- `AtomicVariables.java` — when you don't need a lock at all.
