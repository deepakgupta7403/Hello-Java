# `ForkJoinPool` & `ForkJoinTask`

A specialised thread pool for **divide-and-conquer** algorithms. Workers use
**work-stealing**: when a worker's deque runs dry, it steals from the back of
a busier worker's deque. That's what makes recursive task decomposition fast.

## Two base classes
| Class | Returns | Method |
|---|---|---|
| `RecursiveAction` | nothing | `compute()` |
| `RecursiveTask<V>` | `V` | `compute(): V` |

## Inside `compute()` — the pattern
```java
@Override protected Long compute() {
    if (small enough) return computeDirectly();
    SubTask left  = new SubTask(...);
    SubTask right = new SubTask(...);
    ForkJoinTask<Long> lf = left.fork();   // async on the pool
    long rr = right.compute();             // run inline on this worker
    long lr = lf.join();
    return combine(lr, rr);
}
```

Why "fork one, compute the other"? You want **one** subtask to run on the
current worker so its deque stays busy; the other is stolen by another worker
if there's slack.

## The common pool
`ForkJoinPool.commonPool()` is shared by:
- Parallel streams (`.parallelStream()` / `.parallel()`)
- `CompletableFuture` default async stages
- Your own fork/join tasks if you don't supply a pool

Default parallelism = `availableProcessors() - 1`. Heavy work in one place
starves the others. For dedicated workloads, **create your own pool**.

## Don't do these
- **Don't block on I/O inside a fork/join task** — you stall the pool. If you must, use `ManagedBlocker`, or run the I/O on a separate executor.
- **Don't hold locks inside `compute()`** — work-stealing pulls tasks across workers; reentrancy assumptions can break.
- **Don't fork tiny tasks** — the overhead dominates. Pick a sensible `THRESHOLD`.

## Sizing parallelism
Override the common pool with a JVM flag:
```
-Djava.util.concurrent.ForkJoinPool.common.parallelism=8
```
Or create your own:
```java
ForkJoinPool pool = new ForkJoinPool(8);
long sum = pool.invoke(new Sum(0, n));
pool.shutdown();
```

## When to use ForkJoin vs other executors
| Workload | Pick |
|---|---|
| Recursive divide-and-conquer (sort, sum, tree walk) | `ForkJoinPool` |
| Lots of independent CPU tasks | `FixedThreadPool` |
| Lots of I/O-bound tasks | Virtual threads (Java 21) |
| Async chains | `CompletableFuture` |

## Run
```bash
cd src
java Basics.Multithreading.ForkJoinPoolDemo
```

## See also
- `ExecutorFramework.java`, `ThreadPools.java`.
- `CompletableFutureDemo.java`.
- `SequentialVsParallel.java` in `../LambdaAndStreams/` for parallel streams (which also use the common pool).
