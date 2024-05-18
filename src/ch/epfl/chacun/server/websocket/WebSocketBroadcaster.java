package ch.epfl.chacun.server.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WebSocketBroadcaster<T> extends WebSocketEventListener<T> {

    private final Map<String, List<WebSocketChannel<T>>> channels = new HashMap<>();

    protected void subscribeTo(String id, WebSocketChannel<T> channel) {
        channels.computeIfAbsent(id, _ -> new ArrayList<>()).add(channel);
    }

    protected void unsubscribeFrom(String id, WebSocketChannel<T> channel) {
        List<WebSocketChannel<T>> channelList = channels.get(id);
        if (channelList != null) {
            channelList.remove(channel);
            if (channelList.isEmpty())
                channels.remove(id);
        }
    }

    protected void broadcastTo(String id, String message) {
        if (channels.containsKey(id))
            channels.get(id).forEach(channel -> channel.sendText(message));
    }
}