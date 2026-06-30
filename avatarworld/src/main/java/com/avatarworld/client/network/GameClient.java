package com.avatarworld.client.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.*;

public class GameClient {
    private WebSocket websocket;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private volatile boolean connected = false;
    private String serverUri;
    private MessageListener listener;

    public interface MessageListener {
        void onMessage(JsonObject message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public GameClient(String host, int port) {
        this.serverUri = "ws://" + host + ":" + port + "/ws";
    }

    public void connect(MessageListener listener) {
        this.listener = listener;
        CompletableFuture<WebSocket> wsFuture = httpClient.newWebSocketBuilder()
            .buildAsync(URI.create(serverUri), new WebSocket.Listener() {
                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    String text = data.toString();
                    try {
                        JsonObject msg = JsonParser.parseString(text).getAsJsonObject();
                        if (listener != null) listener.onMessage(msg);
                    } catch (Exception e) {
                        System.err.println("[Client] Invalid message: " + text);
                    }
                    return WebSocket.Listener.super.onText(webSocket, data, last);
                }

                @Override
                public void onOpen(WebSocket webSocket) {
                    connected = true;
                    if (listener != null) listener.onConnected();
                }

                @Override
                public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                    connected = false;
                    if (listener != null) listener.onDisconnected();
                    return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                }

                @Override
                public void onError(WebSocket webSocket, Throwable error) {
                    connected = false;
                    if (listener != null) listener.onError(error.getMessage());
                }
            });

        try {
            websocket = wsFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            if (listener != null) listener.onError("Connection failed: " + e.getMessage());
        }
    }

    public void send(String message) {
        if (websocket != null && connected) {
            websocket.sendText(message, true);
        }
    }

    public void sendJson(JsonObject msg) {
        send(msg.toString());
    }

    public void login(String username, String password) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "LOGIN");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        send(msg.toString());
    }

    public void register(String username, String password) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "REGISTER");
        msg.addProperty("username", username);
        msg.addProperty("password", password);
        send(msg.toString());
    }

    public void listRooms() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "LIST_ROOMS");
        send(msg.toString());
    }

    public void createRoom(String name) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "CREATE_ROOM");
        msg.addProperty("name", name);
        send(msg.toString());
    }

    public void joinRoom(int roomId) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "JOIN_ROOM");
        msg.addProperty("roomId", roomId);
        send(msg.toString());
    }

    public void leaveRoom() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "LEAVE_ROOM");
        send(msg.toString());
    }

    public void sendChat(String message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "CHAT_SEND");
        msg.addProperty("message", message);
        send(msg.toString());
    }

    public void moveAvatar(int x, int y, String direction) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "AVATAR_MOVE");
        msg.addProperty("x", x);
        msg.addProperty("y", y);
        if (direction != null) msg.addProperty("direction", direction);
        send(msg.toString());
    }

    public void sendDance(String animation) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "AVATAR_DANCE");
        msg.addProperty("animation", animation);
        send(msg.toString());
    }

    public void updateAvatar(String avatarData) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "AVATAR_UPDATE");
        msg.addProperty("avatarData", avatarData);
        send(msg.toString());
    }

    public void requestShopList() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "SHOP_LIST");
        send(msg.toString());
    }

    public void requestFurnitureShopList() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "FURNITURE_SHOP_LIST");
        send(msg.toString());
    }

    public void buyItem(int itemId) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "SHOP_BUY");
        msg.addProperty("itemId", itemId);
        send(msg.toString());
    }

    public void requestInventory() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "INVENTORY_LIST");
        send(msg.toString());
    }

    public void equipItem(int itemId, boolean equip) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "INVENTORY_EQUIP");
        msg.addProperty("itemId", itemId);
        msg.addProperty("equip", equip);
        send(msg.toString());
    }

    public void claimDaily() {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "ECONOMY_DAILY");
        send(msg.toString());
    }

    public void giftCoins(int targetUserId, int amount) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "ECONOMY_GIFT");
        msg.addProperty("targetUserId", targetUserId);
        msg.addProperty("amount", amount);
        send(msg.toString());
    }

    public void placeFurniture(int itemId, int x, int y, int rotation) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "EDITOR_PLACE");
        msg.addProperty("itemId", itemId);
        msg.addProperty("x", x);
        msg.addProperty("y", y);
        msg.addProperty("rotation", rotation);
        send(msg.toString());
    }

    public void moveFurniture(int furnitureId, int x, int y, int rotation) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "EDITOR_MOVE");
        msg.addProperty("furnitureId", furnitureId);
        msg.addProperty("x", x);
        msg.addProperty("y", y);
        msg.addProperty("rotation", rotation);
        send(msg.toString());
    }

    public void removeFurniture(int furnitureId) {
        JsonObject msg = new JsonObject();
        msg.addProperty("type", "EDITOR_REMOVE");
        msg.addProperty("furnitureId", furnitureId);
        send(msg.toString());
    }

    public void disconnect() {
        if (websocket != null) {
            websocket.sendClose(1000, "Client closing");
        }
    }

    public boolean isConnected() { return connected; }
}
