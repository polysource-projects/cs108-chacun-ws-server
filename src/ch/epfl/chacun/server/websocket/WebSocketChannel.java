package ch.epfl.chacun.server.websocket;

import ch.epfl.chacun.server.rfc6455.CloseStatusCode;
import ch.epfl.chacun.server.rfc6455.RFC6455;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WebSocketChannel<T> {

    private final SocketChannel channel;
    private final WebSocketEventListener<T> listener;
    private final SelectionKey key;

    public WebSocketChannel(SocketChannel channel, SelectionKey key, WebSocketEventListener<T> listener) {
        this.channel = channel;
        this.key = key;
        this.listener = listener;
    }

    public void attachContext(T context) {
        key.attach(context);
    }

    /**
     * Returns the underlying SocketChannel.
     * @return The underlying SocketChannel.
     */
    public SocketChannel getChannel() {
        return channel;
    }

    @SuppressWarnings("unchecked")
    public T getContext() {
        return (T) key.attachment();
    }

    public boolean sendText(String message) {
        return sendBytes(RFC6455.encodeTextFrame(message));
    }

    public boolean sendPing() {
        return sendBytes(RFC6455.PING);
    }

    public boolean sendPong() {
        return sendBytes(RFC6455.PONG);
    }

    public boolean close(CloseStatusCode code, String reason) {
        return sendBytes(RFC6455.encodeCloseFrame(code, reason));
    }

    public boolean sendBytes(byte[] bytes) {
        try {
            channel.write(ByteBuffer.wrap(bytes));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WebSocketChannel ws) {
            return ws.channel.equals(channel);
        }
        return false;
    }

}
