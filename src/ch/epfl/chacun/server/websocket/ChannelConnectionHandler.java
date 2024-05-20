package ch.epfl.chacun.server.websocket;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ChannelConnectionHandler<T> implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private final AsyncWebSocketServer<T> server;

    public ChannelConnectionHandler(AsyncWebSocketServer<T> server) {
        this.server = server;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel serverChannel) {
        // A connection is accepted, start to accept next connection
        serverChannel.accept(serverChannel, this);
        // Evolve the connection to a WebSocket channel
        WebSocketChannel<T> ws = new WebSocketChannel<>(channel, server);
        // Notify the server that a new connection is opened
        server.onOpen(ws);
        // Start to read message from the client
        server.startRead(ws);
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel channel) {
        System.out.println("Failed to accept a connection");
    }
}
