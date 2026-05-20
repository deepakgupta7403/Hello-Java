package Basics.HttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket API — java.net.http.WebSocket (Java 11+)
 * --------------------------------------------------
 * Full-duplex messaging. Built on the same HttpClient.
 *
 *
 * The two roles
 * -------------
 *   WebSocket            - the connection.
 *   WebSocket.Listener   - callback interface for incoming events.
 *
 *
 * Listener methods
 * ----------------
 *   onOpen(WebSocket)
 *   onText(WebSocket, CharSequence, boolean last)
 *   onBinary(WebSocket, ByteBuffer, boolean last)
 *   onPing(WebSocket, ByteBuffer)
 *   onPong(WebSocket, ByteBuffer)
 *   onClose(WebSocket, int statusCode, String reason)
 *   onError(WebSocket, Throwable)
 *
 *   Each callback returns a CompletionStage<?>; return null to mean
 *   "done, ready for more." The runtime requests one frame at a time
 *   via webSocket.request(n).
 *
 *
 * Building & sending
 * ------------------
 *   HttpClient.newHttpClient()
 *             .newWebSocketBuilder()
 *             .buildAsync(uri, listener)            -> CompletableFuture<WebSocket>
 *
 *   webSocket.sendText("hello", boolean last)       -> CompletableFuture<WebSocket>
 *   webSocket.sendBinary(buf, boolean last)
 *   webSocket.sendPing / sendPong / sendClose
 *
 *
 * Common pitfalls
 * ---------------
 *   - The listener returns a CompletionStage — if you forget, the
 *     runtime stops feeding messages.
 *   - Call webSocket.request(1) (or larger) inside onText to keep
 *     receiving — the default Listener defaults to 1 per callback.
 *   - sendClose must be matched on both sides.
 *
 *
 * Example endpoint
 * ----------------
 * For a quick check use wss://echo.websocket.events/ — it echoes
 * whatever you send. Network required.
 */

public class WebSocketDemo {

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Listener listener = new EchoListener();

        section("1) Connect");
        CompletableFuture<WebSocket> connect = client
                .newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .buildAsync(URI.create("wss://echo.websocket.events/"), listener);

        WebSocket ws;
        try {
            ws = connect.get(8, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("(network error - offline? " + e.getClass().getSimpleName() + ")");
            return;
        }
        System.out.println("connected!");

        section("2) Send 3 messages");
        ws.sendText("hello", true).join();
        ws.sendText("how are you?", true).join();
        ws.sendText("goodbye", true).join();

        section("3) Wait a moment for echoes");
        Thread.sleep(1500);

        section("4) Close cleanly");
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye").get(5, TimeUnit.SECONDS);
        System.out.println("done");
    }

    static class EchoListener implements WebSocket.Listener {
        @Override public void onOpen(WebSocket ws) {
            System.out.println("  [listener] open");
            ws.request(Long.MAX_VALUE);          // request all subsequent frames
        }
        @Override public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            System.out.println("  [listener] <- " + data);
            return null;                          // null = done with this message
        }
        @Override public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            System.out.println("  [listener] closed " + statusCode + " " + reason);
            return null;
        }
        @Override public void onError(WebSocket ws, Throwable error) {
            System.out.println("  [listener] error " + error);
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
