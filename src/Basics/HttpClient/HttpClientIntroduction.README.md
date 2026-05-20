# Java HTTP Client (`java.net.http`)

Built into Java 11+. Modern API for **HTTP/1.1, HTTP/2, and WebSocket**.
Replaces the awkward `HttpURLConnection`.

## Three actors
| | What |
|---|---|
| `HttpClient` | Long-lived, thread-safe — build once, reuse. |
| `HttpRequest` | Immutable, built via `newBuilder`. |
| `HttpResponse<T>` | Immutable response. `T` is the body type returned by your handler. |

## Hello, world
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com"))
        .GET().build();
HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
System.out.println(res.statusCode() + " " + res.body());
```

## Body handlers (responses)
| Handler | Returns |
|---|---|
| `ofString()` / `ofString(charset)` | `String` body |
| `ofByteArray()` | `byte[]` |
| `ofInputStream()` | `InputStream` (you close it) |
| `ofFile(path)` | Dump to disk; returns `Path` |
| `ofLines()` | `Stream<String>` |
| `discarding()` | Drop body — return `Void` |

## Body publishers (requests)
| Publisher | Sends |
|---|---|
| `noBody()` | No body (GET / DELETE) |
| `ofString(text[, charset])` | Text |
| `ofByteArray(bytes)` | Binary |
| `ofFile(path)` | File contents |
| `ofInputStream(() -> in)` | Lazy stream |

## Client configuration
```java
HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(5))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .proxy(ProxySelector.getDefault())
    .authenticator(myAuthenticator)
    .executor(myExecutor)
    .build();
```

Reuse the **same** `HttpClient` instance — it pools connections.

## Sync vs async
| | |
|---|---|
| `client.send(req, handler)` | Blocks; throws `IOException` / `InterruptedException` |
| `client.sendAsync(req, handler)` | Returns `CompletableFuture<HttpResponse<T>>` |

## Common patterns
**JSON request body:**
```java
HttpRequest.newBuilder(URI.create(url))
    .header("Content-Type", "application/json")
    .POST(BodyPublishers.ofString(jsonBody))
    .build();
```

**Form data:**
```java
String form = URLEncoder.encode("name", UTF_8) + "=" + URLEncoder.encode(value, UTF_8);
HttpRequest.newBuilder(...)
    .header("Content-Type", "application/x-www-form-urlencoded")
    .POST(BodyPublishers.ofString(form))
    .build();
```

**Per-request timeout:**
```java
HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(2)).GET().build();
```

## Network requirement
Real examples hit a live URL. Offline → `IOException`. The shape of the code
remains informative.

## Run
```bash
cd src
java Basics.HttpClient.HttpClientIntroduction
```

## See also
- `SyncAndAsyncRequests.java` — `send` vs `sendAsync` patterns.
- `WebSocketDemo.java` — the WebSocket API.
- `Multithreading/CompletableFutureDemo.java` — composing async HTTP calls.
