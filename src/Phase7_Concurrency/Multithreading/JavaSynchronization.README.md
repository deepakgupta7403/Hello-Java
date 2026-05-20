# Java Synchronization

The discipline of letting only **one thread at a time** mutate shared state.
Java's built-in primitive is the `synchronized` keyword, backed by an
**intrinsic monitor lock** that every object carries.

## Three forms

| Form | Locks |
|---|---|
| `public synchronized void m()` | `this` |
| `public static synchronized void m()` | `YourClass.class` |
| `synchronized (lockObj) { ... }` | Whatever `lockObj` is |

## What `synchronized` guarantees
- **Mutual exclusion** — only one thread inside the same monitor.
- **Visibility** — on lock acquire, the thread sees everything done before the matching release. (Happens-before edge.)
- **Ordering** — the JIT/CPU cannot reorder reads & writes across the lock boundary in any observable way.

## What it does *not* do
- Doesn't make individual method calls inside the block "atomic" — it's the *whole block* that is mutually exclusive.
- Doesn't prevent deadlock — that's your job (see `DeadlockDemo.java`).
- Doesn't time out — use `ReentrantLock.tryLock(...)` if you need that.

## Reentrancy
Java monitors are **reentrant**: the same thread can enter the same monitor any
number of times. Each entry needs a matching exit.

## Lock granularity
- Two **instances** of a class do **not** contend with each other on instance methods — each has its own monitor.
- Two **static** synchronized methods on the same class **do** contend.
- An instance method and a static method synchronize on **different** monitors.

## Best practices
- Always lock the **same** object for accesses to the same state.
- Keep critical sections **short**.
- Don't synchronize on `String` literals, boxed `Integer`/`Boolean`, `Class` objects of types you don't own — they're shared.
- Don't call foreign / overridable code while holding a lock.
- Prefer a `private final Object lock = new Object();` over `synchronized(this)` — gives you control over who can lock against you.

## When `synchronized` isn't enough
| Need | Use |
|---|---|
| Timed acquisition | `ReentrantLock.tryLock(timeout)` |
| Multiple condition variables | `Lock.newCondition()` |
| Reader / writer separation | `ReadWriteLock` / `StampedLock` |
| Lock-free atomic counters | `AtomicInteger`, `LongAdder` |
| Concurrent containers | `ConcurrentHashMap`, `CopyOnWriteArrayList` |

## Run
```bash
cd src
java Basics.Multithreading.JavaSynchronization
```

## See also
- `ThreadSafety.java` — the broader concept.
- `LocksInJava.java` — the explicit `Lock` interface.
- `VolatileKeyword.java` — visibility-only without exclusion.
- `LockVsMonitor.java` — monitor lock vs explicit lock side-by-side.
