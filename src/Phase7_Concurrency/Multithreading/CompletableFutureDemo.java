package Phase7_Concurrency.Multithreading;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture — Composable Async (Java 8+)
 * ----------------------------------------------
 * Future is great for "submit and get later" — but every .get() BLOCKS.
 * CompletableFuture is composable: chain transformations, combine
 * multiple futures, handle errors inline, run async stages on the
 * executor of your choice.
 * <p>
 *
 * Creating
 * --------
 *   CompletableFuture.completedFuture(v)
 *   CompletableFuture.failedFuture(throwable)               (Java 9+)
 *   CompletableFuture.supplyAsync(Supplier<V>)              (uses commonPool)
 *   CompletableFuture.supplyAsync(Supplier<V>, Executor)
 *   CompletableFuture.runAsync(Runnable[, Executor])
 * <p>
 *
 * Transformations
 * ---------------
 *   thenApply(Function)        - synchronous map on the completing thread
 *   thenApplyAsync(Function)   - same, but on the default async executor
 *   thenAccept(Consumer)       - side-effect on the value
 *   thenRun(Runnable)          - side-effect, ignores the value
 *   thenCompose(F -> CF<V>)    - flatMap; chain another async stage
 * <p>
 *
 * Combinators
 * -----------
 *   thenCombine(otherCF, BiFn)        - wait for both, combine
 *   thenAcceptBoth / runAfterBoth     - side-effects
 *   applyToEither / acceptEither      - whichever completes first
 *   allOf(cf...)                       - wait for all (returns CF<Void>)
 *   anyOf(cf...)                       - first to finish (returns CF<Object>)
 * <p>
 *
 * Error handling
 * --------------
 *   exceptionally(Function<Throwable, V>)        - fallback value
 *   handle(BiFunction<V, Throwable, V>)           - see both result and error
 *   whenComplete(BiConsumer<V, Throwable>)        - side-effect on either
 * <p>
 *
 * Timeouts (Java 9+)
 * ------------------
 *   orTimeout(time, unit)                         - fail with TimeoutException
 *   completeOnTimeout(value, time, unit)          - fall back to value
 * <p>
 *
 * delayedExecutor (Java 9+)
 * -------------------------
 *   CompletableFuture.delayedExecutor(d, unit)   - returns an Executor that
 *                                                   schedules `delay` later.
 * <p>
 *
 * Threading note
 * --------------
 *   - The *Async variants run on the supplied executor (or commonPool).
 *   - The non-Async variants run on whichever thread completed the parent.
 *   - For CPU work you can use the commonPool. For I/O, USE YOUR OWN
 *     EXECUTOR (or virtual threads) — don't starve the common pool.
 */

public class CompletableFutureDemo {

    public static void main(String[] args) throws Exception {

        section("1) supplyAsync + thenApply + thenAccept");
        CompletableFuture<String> chain = CompletableFuture
                .supplyAsync(() -> { sleep(50); return "hello"; })
                .thenApply(String::toUpperCase)
                .thenApply(s -> s + " world");
        System.out.println(chain.get());

        section("2) thenCompose — chain async stages (flatMap)");
        CompletableFuture<String> composed = CompletableFuture
                .supplyAsync(() -> "user-42")
                .thenCompose(CompletableFutureDemo::loadProfile);
        System.out.println(composed.get());

        section("3) thenCombine — wait for both, combine");
        CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> { sleep(80); return 100; });
        CompletableFuture<Integer> tax   = CompletableFuture.supplyAsync(() -> { sleep(50); return 8;   });
        CompletableFuture<Integer> total = price.thenCombine(tax, Integer::sum);
        System.out.println("total = " + total.get());

        section("4) allOf — fan-out");
        List<CompletableFuture<Integer>> futures = List.of(
                CompletableFuture.supplyAsync(() -> { sleep(60); return 1; }),
                CompletableFuture.supplyAsync(() -> { sleep(40); return 2; }),
                CompletableFuture.supplyAsync(() -> { sleep(50); return 3; })
        );
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        all.get();
        int sum = futures.stream().map(CompletableFuture::join).mapToInt(Integer::intValue).sum();
        System.out.println("sum of all = " + sum);

        section("5) anyOf — first to finish");
        CompletableFuture<Object> first = CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(() -> { sleep(100); return "slow"; }),
                CompletableFuture.supplyAsync(() -> { sleep(50);  return "fast"; }),
                CompletableFuture.supplyAsync(() -> { sleep(200); return "slower"; })
        );
        System.out.println("winner = " + first.get());

        section("6) exceptionally — fallback");
        String resilient = CompletableFuture
                .<String>supplyAsync(() -> { throw new RuntimeException("network down"); })
                .exceptionally(ex -> "fallback (because " + ex.getMessage() + ")")
                .get();
        System.out.println(resilient);

        section("7) handle — see both value and exception");
        String h = CompletableFuture
                .supplyAsync(() -> { if (Math.random() < 2) throw new RuntimeException("X"); return "ok"; })
                .handle((v, ex) -> ex == null ? "value=" + v : "err=" + ex.getMessage())
                .get();
        System.out.println(h);

        section("8) orTimeout / completeOnTimeout — Java 9+");
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> { sleep(300); return "late"; });
        try {
            slow.orTimeout(50, TimeUnit.MILLISECONDS).get();
        } catch (Exception e) {
            System.out.println("orTimeout: " + e.getCause().getClass().getSimpleName());
        }
        String fallback = CompletableFuture
                .supplyAsync(() -> { sleep(300); return "late"; })
                .completeOnTimeout("default", 50, TimeUnit.MILLISECONDS)
                .get();
        System.out.println("completeOnTimeout = " + fallback);

        section("9) Run on a custom executor");
        ExecutorService io = Executors.newFixedThreadPool(4);
        CompletableFuture<String> ioStage = CompletableFuture.supplyAsync(() -> {
            sleep(50);
            return "thread=" + Thread.currentThread().getName();
        }, io);
        System.out.println(ioStage.get());
        io.shutdown();

        section("10) delayedExecutor");
        long t0 = System.currentTimeMillis();
        String afterDelay = CompletableFuture
                .supplyAsync(() -> "fired", CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS))
                .get();
        System.out.println(afterDelay + " at " + (System.currentTimeMillis() - t0) + "ms");

        section("done");
    }

    private static CompletableFuture<String> loadProfile(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(40);
            return "profile{" + userId + "}";
        });
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
