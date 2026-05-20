package Phase8_PracticalAPIs.HttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Java HTTP Client (java.net.http) — Introduction
 * -----------------------------------------------
 * Built into Java 11+. Modern API for HTTP/1.1, HTTP/2, and WebSocket.
 * Replaces the awkward HttpURLConnection.
 *
 *
 * The three actors
 * ----------------
 *   HttpClient        - long-lived, thread-safe; build once, reuse.
 *   HttpRequest       - immutable, built via a Builder.
 *   HttpResponse<T>   - immutable; T is the body-handler's payload type.
 *
 *
 * Hello, world
 * ------------
 *      HttpClient client = HttpClient.newHttpClient();
 *      HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com"))
 *                                    .GET()
 *                                    .build();
 *      HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
 *      System.out.println(res.statusCode() + " " + res.body());
 *
 *
 * Body handlers (built-in)
 * ------------------------
 *   ofString()              - response as String (UTF-8 by default)
 *   ofByteArray()           - response as byte[]
 *   ofInputStream()         - stream the response
 *   ofFile(path)            - dump straight to a file
 *   ofLines()               - Stream<String> of response lines
 *   discarding()            - throw away the body
 *
 *
 * Body publishers (for sending bodies)
 * ------------------------------------
 *   noBody()                - GET / DELETE without a body
 *   ofString("...")
 *   ofByteArray(byte[])
 *   ofFile(path)
 *   ofInputStream(supplier)
 *
 *
 * Sync vs async
 * -------------
 *   client.send(req, handler)              - blocks
 *   client.sendAsync(req, handler)         - returns CompletableFuture<HttpResponse>
 *
 *
 * Configuration
 * -------------
 *   HttpClient.newBuilder()
 *       .version(HttpClient.Version.HTTP_2)
 *       .connectTimeout(Duration.ofSeconds(5))
 *       .followRedirects(HttpClient.Redirect.NORMAL)
 *       .proxy(ProxySelector.getDefault())
 *       .authenticator(Authenticator.requestPasswordAuthentication...)
 *       .executor(myExecutor)               // for async callbacks
 *       .build();
 *
 *
 * Network requirement
 * -------------------
 * Examples here use httpbin.org. If you're offline they'll fail with
 * IOException — the SHAPE of the code is still informative.
 */

public class HttpClientIntroduction {

    public static void main(String[] args) {

        section("1) Build a reusable client");
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        System.out.println("client version  = " + client.version());
        System.out.println("connect timeout = " + client.connectTimeout().orElse(null));

        section("2) GET — synchronous, body as String");
        HttpRequest get = HttpRequest.newBuilder(URI.create("https://httpbin.org/get"))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> res = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println("status = " + res.statusCode());
            System.out.println("len    = " + res.body().length());
            System.out.println("first 80 chars: " + res.body().substring(0, Math.min(80, res.body().length())));
        } catch (Exception e) {
            System.out.println("network error (offline?) - " + e.getClass().getSimpleName());
        }

        section("3) Headers and URI");
        HttpRequest withHeaders = HttpRequest.newBuilder(URI.create("https://httpbin.org/headers"))
                .header("X-Demo", "hello")
                .header("X-Another", "world")
                .GET()
                .build();
        System.out.println("headers:");
        withHeaders.headers().map().forEach((k, v) -> System.out.println("  " + k + " = " + v));

        section("4) Response details — status, headers, uri, version");
        // (logging only; see sync demo above for a real call)
        System.out.println("response.statusCode(), .headers(), .uri(), .version() all available");

        section("done — see SyncAndAsyncRequests.java and WebSocketDemo.java");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
