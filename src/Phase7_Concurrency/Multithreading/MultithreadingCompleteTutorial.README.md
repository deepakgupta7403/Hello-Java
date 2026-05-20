# Multithreading — Complete Tutorial (One-File Tour)

Runs through every major topic in the section in a single program:

| # | Section in code | Dedicated file |
|---|---|---|
| 1 | Spawning a thread | `Threads.java`, `RunnableInterface.java` |
| 2 | Lifecycle snapshot | `ThreadLifecycle.java` |
| 3 | Synchronized + race condition fix | `JavaSynchronization.java`, `ThreadSafety.java`, `AtomicVariables.java` |
| 4 | Producer / consumer (`wait`/`notify`) | `WaitNotifyNotifyAll.java`, `ProducerConsumer.java` |
| 5 | `ReentrantLock` + `tryLock` | `LocksInJava.java`, `ReentrantLockDemo.java` |
| 6 | Executor framework + `Future` | `ExecutorFramework.java`, `CallableAndFuture.java` |
| 7 | `CountDownLatch` + `Semaphore` | `CountDownLatchDemo.java`, `SemaphoreDemo.java` |
| 8 | `CompletableFuture` pipeline | `CompletableFutureDemo.java` |
| 9 | Virtual threads at scale | `VirtualThreads.java` |

## Suggested study order
1. **Foundations** — `MultithreadingIntroduction`, `Threads`, `ThreadLifecycle`, `StartVsRun`, `MainThread`.
2. **Lifecycle controls** — `ThreadSleepMethod`, `ThreadJoinMethod`, `ThreadYieldMethod`, `ThreadInterruption`, `ThreadPriority`, `DaemonThread`.
3. **Creating work** — `RunnableInterface`, `CallableAndFuture`.
4. **Correctness** — `JavaSynchronization`, `ThreadSafety`, `RaceConditionStarvationLivelock`, `JavaMemoryModel`, `VolatileKeyword`.
5. **Coordination** — `WaitNotifyNotifyAll`, `ProducerConsumer`, `ThreadLocalDemo`, `AtomicVariables`.
6. **Explicit locks** — `LocksInJava`, `LockVsMonitor`, `LockFrameworkVsSync`, `ReentrantLockDemo`, `ReadWriteLockDemo`, `StampedLockDemo`, `DeadlockDemo`.
7. **Higher-level concurrency** — `ThreadPools`, `ExecutorFramework`, `ScheduledExecutorDemo`, `ForkJoinPoolDemo`, `CompletableFutureDemo`.
8. **Synchronizers** — `CountDownLatchDemo`, `CyclicBarrierDemo`, `SemaphoreDemo`, `PhaserDemo`.
9. **Java 21** — `VirtualThreads`, `StructuredConcurrency`, `ScopedValuesDemo`.
10. **Project** — `SnakeGame/` (Swing + game loop + input thread).

## Pocket cheatsheet
- **Mutual exclusion** → `synchronized`, `ReentrantLock`, `Semaphore(1)`.
- **Visibility only** → `volatile`, `Atomic*`.
- **Atomic counter** → `AtomicInteger` or `LongAdder` (high contention).
- **One-shot completion** → `CountDownLatch`.
- **Repeated lockstep** → `CyclicBarrier` / `Phaser`.
- **Cap concurrency** → `Semaphore(N)`.
- **Many readers, few writers** → `ReadWriteLock` or `StampedLock`.
- **Background workers** → `ExecutorService` (Java 19+ `AutoCloseable`).
- **Periodic / delayed** → `ScheduledExecutorService`.
- **Divide & conquer** → `ForkJoinPool`.
- **Async chains** → `CompletableFuture`.
- **High-fan-out I/O** → Virtual threads.
- **Bounded task group** → `StructuredTaskScope` (Java 21 preview).
- **Per-call context** → `ScopedValue` (Java 21 preview); `ThreadLocal` otherwise.

## Run
```bash
cd src
java Basics.Multithreading.MultithreadingCompleteTutorial
```

## See also
- Each topic's dedicated file (linked above) for depth.
- `SnakeGame/SnakeGame.java` for the practical project.
