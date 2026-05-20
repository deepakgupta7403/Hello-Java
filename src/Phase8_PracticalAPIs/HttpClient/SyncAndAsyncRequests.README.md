# Synchronous and Asynchronous HTTP Requests

| | Sync | Async |
|---|---|---|
| Call | `client.send(req, handler)` | `client.sendAsync(req, handler)` |
| Returns | `HttpResponse<T>` | `CompletableFuture<HttpResponse<T>>` |
| Blocks caller? | Yes | No |
| Cancelable? | Via thread interrupt | `future.cancel(true)` |

## When to pick which
| Use | Pick |
|---|---|
| Small script, one call | Sync |
| Lots of parallel calls | Async — compose with `thenCompose` / `thenApply` |
| Java 21 + virtual threads | Either is fine — sync inside a VT is cheap |

## Common patterns

**POST with JSON body:**
```java
HttpRequest.newBuilder(uri)
    .header("Content-Type", "application/json")
    .POST(BodyPublishers.ofString(json))
    .build();
```

**Save response to a file:**
```java
HttpResponse<Path> res = client.send(req, BodyHandlers.ofFile(targetPath));
```

**Fan-out / fan-in:**
```java
List<CompletableFuture<HttpResponse<String>>> futures = urls.stream()
        .map(u -> HttpRequest.newBuilder(URI.create(u)).GET().build())
        .map(r -> client.sendAsync(r, BodyHandlers.ofString()))
        .toList();
CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
```

**Error handling:**
```java
client.sendAsync(req, handler)
      .thenApply(HttpResponse::body)
      .exceptionally(ex -> fallbackValue);
```

**Per-request timeout:**
```java
HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(2)).GET().build();
```
Throws `HttpTimeoutException` (a subclass of `IOException`).

## Reuse the client
Treat `HttpClient` like a connection pool — **build one and reuse**. Building
per call creates a fresh pool every time.

## Streaming the response
For large downloads:
```java
HttpResponse<InputStream> res = client.send(req, BodyHandlers.ofInputStream());
try (var in = res.body()) {
    in.transferTo(outputStream);
}
```

## Sync in a virtual thread
With Java 21, "synchronous + many virtual threads" beats "async + few platform
threads" for I/O-bound workloads. Each VT carries one outstanding request;
when it blocks, the carrier is free.

## Run
```bash
cd src
java Basics.HttpClient.SyncAndAsyncRequests
```

Needs network access. Offline runs report the error and continue.

## See also
- `HttpClientIntroduction.java` — builder + body handlers.
- `WebSocketDemo.java` — full-duplex API.
- `Multithreading/VirtualThreads.java` — pair with virtual threads.
- `Multithreading/CompletableFutureDemo.java` — composing async stages.
