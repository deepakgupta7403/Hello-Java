package Phase7_Concurrency.Multithreading;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Structured Concurrency
 * ----------------------
 * Java 21 ships StructuredTaskScope as a PREVIEW API (JEP 453, evolved
 * further in 22/23). It treats a GROUP of concurrent subtasks as a
 * SINGLE UNIT OF WORK with a SCOPED LIFETIME:
 * <p>
 *
 *      try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
 *          Subtask<String> user  = scope.fork(() -> loadUser(id));
 *          Subtask<Cart>   cart  = scope.fork(() -> loadCart(id));
 *          scope.join();             // wait for both / either failure
 *          scope.throwIfFailed();    // rethrow first error if any
 *          return new Bundle(user.get(), cart.get());
 *      }
 * <p>
 *
 * The scope GUARANTEES that all forked tasks finish (succeed, fail, or
 * are cancelled) before the try-with-resources exits. Errors propagate
 * naturally; cancellation propagates to every child.
 * <p>
 *
 * Why it matters
 * --------------
 *   - Replaces the LIFETIME-LEAKING world of bare ExecutorService and
 *     dangling CompletableFuture chains.
 *   - Treats "fan out N concurrent tasks, get all/any of their results"
 *     as a structured statement — just like try-with-resources made
 *     resource handling structured.
 * <p>
 *
 * Two built-in policies (Java 21)
 * -------------------------------
 *   ShutdownOnFailure  - cancel all on first failure (the most common).
 *   ShutdownOnSuccess  - cancel all on first success (parallel race).
 * <p>
 *
 * You can subclass StructuredTaskScope for custom policies.
 * <p>
 *
 * Preview API
 * -----------
 * StructuredTaskScope is in `java.util.concurrent` but PREVIEW. To
 * compile/run this file:
 * <p>
 *
 *      javac --release 21 --enable-preview StructuredConcurrency.java
 *      java  --enable-preview  Basics.Multithreading.StructuredConcurrency
 * <p>
 *
 * Implementation note for this repo
 * ---------------------------------
 * To avoid making the project depend on preview flags, this file shows
 * the SHAPE of the API in comments AND demonstrates a portable
 * equivalent using virtual threads + CompletableFuture so it compiles
 * cleanly without --enable-preview.
 */

public class StructuredConcurrency {

    public static void main(String[] args) throws Exception {

        section("1) Portable fan-out (no preview flag): virtual threads + CompletableFuture");
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> user = CompletableFuture.supplyAsync(() -> loadUser("u-1"), vts);
            CompletableFuture<String> cart = CompletableFuture.supplyAsync(() -> loadCart("u-1"), vts);
            CompletableFuture<String> bundle = user.thenCombine(cart, (u, c) -> "{user=" + u + ", cart=" + c + "}");
            System.out.println(bundle.get());
        }

        section("2) Same in JEP 453 shape (commented)");
        // try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        //     Subtask<String> user = scope.fork(() -> loadUser("u-1"));
        //     Subtask<String> cart = scope.fork(() -> loadCart("u-1"));
        //     scope.join();
        //     scope.throwIfFailed();
        //     System.out.println("{user=" + user.get() + ", cart=" + cart.get() + "}");
        // }
        System.out.println("(see comment block above for the JEP 453 form)");

        section("3) Cancel-the-rest race — first success wins");
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> attempts = List.of(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(120); return "primary";
                    }, vts),
                    CompletableFuture.supplyAsync(() -> {
                        sleep(50);  return "backup";
                    }, vts)
            );
            @SuppressWarnings("unchecked")
            CompletableFuture<Object> race = (CompletableFuture<Object>) CompletableFuture
                    .anyOf(attempts.toArray(new CompletableFuture[0]));
            String winner = (String) race.get();
            System.out.println("winner = " + winner);
            // cancel the losers
            for (CompletableFuture<?> f : attempts) f.cancel(true);
        }

        section("4) Failure propagates and cancels siblings");
        try (ExecutorService vts = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<String> ok    = CompletableFuture.supplyAsync(() -> { sleep(200); return "ok"; }, vts);
            CompletableFuture<String> bad   = CompletableFuture.supplyAsync(() -> { sleep(30);  throw new RuntimeException("boom"); }, vts);
            CompletableFuture<Void> both = CompletableFuture.allOf(ok, bad);
            try { both.get(); }
            catch (Exception ee) {
                System.out.println("first failure: " + ee.getCause());
                ok.cancel(true);
            }
        }

        section("done");
    }

    private static String loadUser(String id) { sleep(40); return "user-" + id; }
    private static String loadCart(String id) { sleep(60); return "cart(" + id + ")"; }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
