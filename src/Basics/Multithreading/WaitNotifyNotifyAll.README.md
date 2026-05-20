# `Object.wait` / `notify` / `notifyAll`

Java's original inter-thread signalling primitives. They live on **every
object**, because every object's intrinsic monitor doubles as a condition
variable.

## The methods
| Method | Effect |
|---|---|
| `obj.wait()` | Release the monitor, block until `notify` / `notifyAll` / `interrupt` / spurious |
| `obj.wait(ms)` | Same but with a timeout |
| `obj.notify()` | Wake **one** of the threads currently waiting on `obj` |
| `obj.notifyAll()` | Wake **all** threads currently waiting on `obj` |

## Rules
1. You **must** hold `obj`'s monitor when calling these methods, i.e., you must be inside `synchronized (obj) { ... }`. Otherwise → `IllegalMonitorStateException`.
2. `wait()` releases the monitor while blocked and re-acquires it before returning.
3. Always loop the predicate (spurious wakeups are real):
   ```java
   synchronized (lock) {
       while (!ready) lock.wait();
   }
   ```
4. **Notify within the lock.** If you set the predicate and notify outside the monitor, you can lose signals.

## `notify` vs `notifyAll`
- Prefer **`notifyAll`** when waiters can be waiting on *different* predicates of the same object. Only the ones whose predicate became true will leave; the rest re-check and go back to waiting.
- Use **`notify`** only when *one* predicate guards everything and waking one waiter is provably correct. The cost of getting it wrong is a missed-signal deadlock.

## Standard template (single producer, single consumer toy)
```java
private boolean ready;
public synchronized void await() throws InterruptedException {
    while (!ready) wait();
}
public synchronized void signal() {
    ready = true;
    notifyAll();
}
```

## Modern alternative: `Lock` + `Condition`
```java
Lock lock = new ReentrantLock();
Condition notEmpty = lock.newCondition();
Condition notFull  = lock.newCondition();   // can have multiple condition vars
```
- Multiple condition variables per lock.
- `await()` instead of `wait()`; `signal()` / `signalAll()` instead of `notify`/`notifyAll`.
- Supports interruptible / uninterruptible / timed variants.

## Common pitfalls
- Calling `wait()` without holding the lock — `IllegalMonitorStateException`.
- Calling `wait()` once instead of in a loop — spurious wakeup will surprise you.
- Calling `notify()` when multiple distinct predicates can be waiting → missed signals.
- Forgetting that `wait()` releases the monitor — assuming "I still hold it" can lead to invariant violations.

## Run
```bash
cd src
java Basics.Multithreading.WaitNotifyNotifyAll
```

## See also
- `ProducerConsumer.java` — a fuller example.
- `LocksInJava.java` — `Lock` + `Condition` API.
- `ReentrantLockDemo.java` — multiple condition variables.
