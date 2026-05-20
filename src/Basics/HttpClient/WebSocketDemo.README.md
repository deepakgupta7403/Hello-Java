# WebSocket API (`java.net.http.WebSocket`)

Built into Java 11+. Full-duplex messaging on top of the same `HttpClient`.

## Two roles
| | |
|---|---|
| `WebSocket` | The connection — `sendText`, `sendBinary`, `sendClose`, … |
| `WebSocket.Listener` | Receives events: `onOpen`, `onText`, `onBinary`, `onPing`, `onPong`, `onClose`, `onError` |

## Skeleton
```java
HttpClient client = HttpClient.newHttpClient();
WebSocket.Listener listener = new WebSocket.Listener() {
    @Override public void onOpen(WebSocket ws) { ws.request(Long.MAX_VALUE); }
    @Override public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        System.out.println("<- " + data);
        return null;     // done with this message
    }
    @Override public CompletionStage<?> onClose(WebSocket ws, int code, String reason) {
        return null;
    }
};
WebSocket ws = client.newWebSocketBuilder()
        .buildAsync(URI.create("wss://example.com/path"), listener)
        .get();

ws.sendText("hello", true).join();
// ... receive via listener.onText ...
ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();
```

## Flow control
Each `onText` / `onBinary` is for **one message** (or one fragment of one,
indicated by `last`). The runtime delivers messages as fast as the listener
returns from each callback **and** the listener requested more frames.

- Default request count: 1 per callback.
- Call `ws.request(n)` to ask for `n` more.
- For unbounded streaming: `ws.request(Long.MAX_VALUE)` in `onOpen`.

## Listener return values
Each callback returns a `CompletionStage<?>`. The runtime waits for it to
complete before delivering the next message. Use this for backpressure —
return a stage that completes only when your downstream is ready.

Return `null` to mean "done immediately."

## Sending
| Call | Sends |
|---|---|
| `sendText("...", boolean last)` | Text frame (or fragment) |
| `sendBinary(buf, boolean last)` | Binary frame |
| `sendPing(payload)` / `sendPong(payload)` | Liveness frames |
| `sendClose(statusCode, reason)` | Close handshake |

All return `CompletableFuture<WebSocket>`.

## Close codes
| Code | Meaning |
|---|---|
| `WebSocket.NORMAL_CLOSURE` (1000) | OK |
| 1001 | Going away |
| 1011 | Server error |
| 4000+ | App-defined |

## Example endpoint
`wss://echo.websocket.events/` echoes whatever you send — handy for tests.

## Run
```bash
cd src
java Basics.HttpClient.WebSocketDemo
```

Needs network access. Offline → "network error" notice.

## See also
- `HttpClientIntroduction.java`, `SyncAndAsyncRequests.java`.
- `Multithreading/CompletableFutureDemo.java` — orchestrate the futures.
