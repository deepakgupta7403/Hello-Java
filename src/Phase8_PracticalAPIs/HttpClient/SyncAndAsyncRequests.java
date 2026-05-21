package Phase8_PracticalAPIs.HttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Synchronous and Asynchronous HTTP Requests
 * ------------------------------------------
 * <p>
 *
 *   client.send(req, handler)         - synchronous; blocks the caller.
 *   client.sendAsync(req, handler)    - asynchronous; returns
 *                                       CompletableFuture<HttpResponse<T>>.
 * <p>
 *
 * Sync vs async — when to pick which
 * ----------------------------------
 *   SYNC  - simple scripts, single call, blocking thread is fine.
 *   ASYNC - many parallel requests; compose with thenCompose / thenApply.
 * <p>
 *
 * With Java 21 virtual threads, you can call send() from many VTs and
 * the cost of blocking is trivial — async is no longer the only path to
 * concurrency.
 * <p>
 *
 * Examples in this file
 * ---------------------
 *   1. Simple sync GET.
 *   2. POST with a JSON body.
 *   3. Save the body straight to a file.
 *   4. Async with thenApply.
 *   5. Fan-out: 3 parallel requests, combine results.
 *   6. Error handling with .exceptionally / .handle.
 * <p>
 *
 * Hit URLs are example endpoints (httpbin.org). If you're offline the
 * code still compiles cleanly; the calls will throw IOException at run.
 */

public class SyncAndAsyncRequests {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void main(String[] args) throws Exception {

        section("1) Synchronous GET");
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://httpbin.org/get?demo=1"))
                .GET().timeout(Duration.ofSeconds(5)).build();
        try {
            HttpResponse<String> res = CLIENT.send(req, BodyHandlers.ofString());
            System.out.println("status = " + res.statusCode());
            System.out.println("first 80: " + slice(res.body(), 80));
        } catch (Exception e) { offlineNote(e); }

        section("2) POST with JSON body");
        String json = "{ \"name\": \"alice\", \"role\": \"engineer\" }";
        HttpRequest post = HttpRequest.newBuilder(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> res = CLIENT.send(post, BodyHandlers.ofString());
            System.out.println("status = " + res.statusCode());
            System.out.println("body length = " + res.body().length());
        } catch (Exception e) { offlineNote(e); }

        section("3) Save body straight to a file");
        Path tmp = Files.createTempFile("http-", ".json");
        HttpRequest toFile = HttpRequest.newBuilder(URI.create("https://httpbin.org/json")).GET().build();
        try {
            HttpResponse<Path> res = CLIENT.send(toFile, BodyHandlers.ofFile(tmp));
            System.out.println("downloaded to " + res.body() + " (" + Files.size(res.body()) + " bytes)");
            Files.deleteIfExists(tmp);
        } catch (Exception e) { offlineNote(e); }

        section("4) Async — sendAsync + thenApply");
        try {
            String body = CLIENT.sendAsync(req, BodyHandlers.ofString())
                                .thenApply(HttpResponse::body)
                                .thenApply(s -> "got " + s.length() + " chars")
                                .get(10, java.util.concurrent.TimeUnit.SECONDS);
            System.out.println(body);
        } catch (Exception e) { offlineNote(e); }

        section("5) Fan-out — 3 parallel requests, combine results");
        List<String> urls = List.of(
                "https://httpbin.org/uuid",
                "https://httpbin.org/uuid",
                "https://httpbin.org/uuid");
        List<CompletableFuture<HttpResponse<String>>> futures = urls.stream()
                .map(u -> HttpRequest.newBuilder(URI.create(u)).GET().build())
                .map(r -> CLIENT.sendAsync(r, BodyHandlers.ofString()))
                .toList();
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.get(10, java.util.concurrent.TimeUnit.SECONDS);
            futures.stream().map(CompletableFuture::join)
                    .map(HttpResponse::body)
                    .forEach(b -> System.out.println("  " + slice(b, 60)));
        } catch (Exception e) { offlineNote(e); }

        section("6) Error handling — .exceptionally / .handle");
        CompletableFuture<String> safe = CLIENT
                .sendAsync(HttpRequest.newBuilder(URI.create("https://httpbin.org/status/500")).GET().build(),
                           BodyHandlers.ofString())
                .thenApply(r -> "status " + r.statusCode())
                .exceptionally(ex -> "caught: " + ex.getClass().getSimpleName());
        try { System.out.println(safe.get(10, java.util.concurrent.TimeUnit.SECONDS)); }
        catch (Exception e) { offlineNote(e); }

        section("done");
    }

    private static String slice(String s, int n) {
        return s.substring(0, Math.min(n, s.length())).replace("\n", " ");
    }

    private static void offlineNote(Throwable t) {
        System.out.println("(network error - offline? " + t.getClass().getSimpleName() + ")");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
