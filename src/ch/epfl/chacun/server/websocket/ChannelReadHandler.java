package ch.epfl.chacun.server.websocket;

import ch.epfl.chacun.server.rfc6455.OpCode;
import ch.epfl.chacun.server.rfc6455.PayloadData;
import ch.epfl.chacun.server.rfc6455.RFC6455;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ChannelReadHandler<T> implements CompletionHandler<Integer, WebSocketChannel<T>> {

    private final AsyncWebSocketServer<T> server;
    private final ByteBuffer payload;

    public ChannelReadHandler(AsyncWebSocketServer<T> server, ByteBuffer payload) {
        this.server = server;
        this.payload = payload;
    }

    @Override
    public void completed(Integer result, WebSocketChannel<T> channel) {
        // If the client has disconnected
        if (result == -1) {
            failed(new IllegalArgumentException("Client disconnected"), channel);
            return;
        }

        String content = new String(payload.array());
        // Check for any incoming HTTP upgrade request
        if (RFC6455.isUpgradeRequest(content)) {
            // Send the upgrade response
            try {
                String upgradeResponse = RFC6455.upgradeToWebsocket(content);
                server.startWrite(channel, ByteBuffer.wrap(upgradeResponse.getBytes()));
            } catch (IllegalArgumentException e) {
                failed(e, channel);
            }
            // Start to read next message again
            server.startRead(channel);
            return;
        }

        // Decode the payload
        PayloadData payloadData = RFC6455.parsePayload(payload);
        if (payloadData == null) {
            // The payload is invalid
            failed(new IllegalArgumentException("Invalid RFC6455 payload"), channel);
            return;
        }

        // Fire the event corresponding to the payload
        server.dispatch(payloadData, channel);
        // If the payload is not a close message
        if (payloadData.opCode() != OpCode.CLOSE) {
            // Start to read next message again
            server.startRead(channel);
        }
    }

    @Override
    public void failed(Throwable exc, WebSocketChannel<T> ws) {
        System.out.println("Failed to read message from client... closing channel");
        ws.terminate();
    }
}
