package com.avatarworld;

import com.avatarworld.client.network.GameClient;
import com.avatarworld.client.ui.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;

public class AvatarWorldClient {
    private GameClient client;
    private LoginFrame loginFrame;
    private MainFrame mainFrame;
    private ShopDialog shopDialog;
    private InventoryDialog inventoryDialog;
    private RoomEditorDialog roomEditorDialog;
    private boolean loggedIn = false;

    public void start() {
        SwingUtilities.invokeLater(() -> {
            loginFrame = new LoginFrame(this::handleLoginAction);
            loginFrame.setVisible(true);
        });
    }

    private void handleLoginAction(String[] data) {
        String action = data[0];
        String username = data[1];
        String password = data[2];

        client = new GameClient("localhost", 8080);
        client.connect(new GameClient.MessageListener() {
            @Override
            public void onMessage(JsonObject msg) {
                SwingUtilities.invokeLater(() -> handleMessage(msg));
            }

            @Override
            public void onConnected() {
                SwingUtilities.invokeLater(() -> {
                    if ("login".equals(action)) {
                        client.login(username, password);
                    } else {
                        client.register(username, password);
                    }
                });
            }

            @Override
            public void onDisconnected() {
                SwingUtilities.invokeLater(() -> {
                    if (!loggedIn) {
                        loginFrame.setEnabled(true);
                        loginFrame.setStatus("Connection lost", true);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Disconnected from server");
                    }
                });
            }

            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    if (!loggedIn) {
                        loginFrame.setEnabled(true);
                        loginFrame.setStatus("Error: " + error, true);
                    }
                });
            }
        });
    }

    private void handleMessage(JsonObject msg) {
        String type = msg.get("type").getAsString();

        switch (type) {
            case "LOGIN_RESULT":
                handleLoginResult(msg);
                break;
            case "REGISTER_RESULT":
                handleRegisterResult(msg);
                break;
            case "ROOM_LIST":
                handleRoomList(msg);
                break;
            case "ROOM_CREATED":
                handleRoomCreated(msg);
                break;
            case "ROOM_JOINED":
                handleRoomJoined(msg);
                break;
            case "ROOM_LEFT":
                handleRoomLeft();
                break;
            case "ROOM_USER_JOIN":
                handleRoomUserJoin(msg);
                break;
            case "ROOM_USER_LEAVE":
                handleRoomUserLeave(msg);
                break;
            case "AVATAR_MOVED":
                handleAvatarMoved(msg);
                break;
            case "AVATAR_ANIMATION":
                handleAvatarAnimation(msg);
                break;
            case "AVATAR_UPDATED":
                handleAvatarUpdated(msg);
                break;
            case "CHAT_RECEIVE":
                handleChatReceive(msg);
                break;
            case "SHOP_LIST_RESULT":
                handleShopList(msg);
                break;
            case "FURNITURE_SHOP_LIST_RESULT":
                handleFurnitureShopList(msg);
                break;
            case "SHOP_BUY_RESULT":
                handleShopBuy(msg);
                break;
            case "INVENTORY_LIST_RESULT":
                handleInventoryList(msg);
                break;
            case "INVENTORY_EQUIP_RESULT":
                handleEquipResult(msg);
                break;
            case "ECONOMY_BALANCE_RESULT":
                handleBalance(msg);
                break;
            case "ECONOMY_DAILY_RESULT":
                handleDailyResult(msg);
                break;
            case "ECONOMY_GIFT_RESULT":
                mainFrame.setStatus("Gift sent!");
                break;
            case "EDITOR_PLACED":
                if (mainFrame.getRoomPanel().isInRoom())
                    mainFrame.getRoomPanel().onFurniturePlaced(msg);
                break;
            case "EDITOR_MOVED":
                if (mainFrame.getRoomPanel().isInRoom())
                    mainFrame.getRoomPanel().onFurnitureMoved(msg);
                break;
            case "EDITOR_REMOVED":
                int fid = msg.get("furnitureId").getAsInt();
                if (mainFrame.getRoomPanel().isInRoom())
                    mainFrame.getRoomPanel().onFurnitureRemoved(fid);
                break;
            case "ADMIN_KICKED":
                JOptionPane.showMessageDialog(mainFrame, "You were kicked from the room by an admin");
                mainFrame.getRoomPanel().leaveRoom();
                break;
            case "ADMIN_BANNED":
                JOptionPane.showMessageDialog(mainFrame, "You have been banned!");
                System.exit(0);
                break;
            case "ADMIN_BROADCAST":
                JOptionPane.showMessageDialog(mainFrame, "[Broadcast] " + msg.get("message").getAsString());
                break;
            case "ERROR":
                handleError(msg);
                break;
        }
    }

    private void handleLoginResult(JsonObject msg) {
        if (msg.get("success").getAsBoolean()) {
            loggedIn = true;
            int userId = msg.get("userId").getAsInt();
            String username = msg.get("username").getAsString();
            int coins = msg.get("coins").getAsInt();
            boolean isAdmin = msg.get("isAdmin").getAsBoolean();
            String avatarData = msg.get("avatarData").getAsString();

            loginFrame.dispose();
            mainFrame = new MainFrame(client, userId, username, coins, isAdmin);
            mainFrame.setVisible(true);
        } else {
            loginFrame.setEnabled(true);
            loginFrame.setStatus("Login failed: Invalid credentials", true);
        }
    }

    private void handleRegisterResult(JsonObject msg) {
        loginFrame.setEnabled(true);
        if (msg.get("success").getAsBoolean()) {
            loginFrame.setStatus("Registration successful! Login now.", false);
        } else {
            loginFrame.setStatus("Registration failed", true);
        }
    }

    private void handleRoomList(JsonObject msg) {
        JsonArray rooms = msg.getAsJsonArray("rooms");
        mainFrame.updateRoomList(rooms);
    }

    private void handleRoomCreated(JsonObject msg) {
        mainFrame.setStatus("Room created: " + msg.get("name").getAsString());
        client.listRooms();
    }

    private void handleRoomJoined(JsonObject msg) {
        mainFrame.showRoom();
        mainFrame.getRoomPanel().joinRoom(msg);
    }

    private void handleRoomLeft() {
        // Already handled in RoomPanel.leaveRoom()
    }

    private void handleRoomUserJoin(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        String uname = msg.get("username").getAsString();
        String avatarJson = msg.get("avatarData").getAsString();
        mainFrame.getRoomPanel().onUserJoin(uid, uname, avatarJson);
    }

    private void handleRoomUserLeave(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        mainFrame.getRoomPanel().onUserLeave(uid);
    }

    private void handleAvatarMoved(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        int x = msg.get("x").getAsInt();
        int y = msg.get("y").getAsInt();
        String dir = msg.has("direction") ? msg.get("direction").getAsString() : null;
        mainFrame.getRoomPanel().onUserMove(uid, x, y, dir);
    }

    private void handleAvatarAnimation(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        String anim = msg.get("animation").getAsString();
        mainFrame.getRoomPanel().onUserAnimate(uid, anim);
    }

    private void handleAvatarUpdated(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        String avatarJson = msg.get("avatarData").getAsString();
        mainFrame.getRoomPanel().onAvatarUpdate(uid, avatarJson);
    }

    private void handleChatReceive(JsonObject msg) {
        int uid = msg.get("userId").getAsInt();
        String uname = msg.get("username").getAsString();
        String message = msg.get("message").getAsString();
        mainFrame.getRoomPanel().onChatMessage(uid, uname, message);
    }

    private void handleShopList(JsonObject msg) {
        JsonArray items = msg.getAsJsonArray("items");
        // Find open ShopDialog
        for (Window w : Window.getWindows()) {
            if (w instanceof ShopDialog) {
                ((ShopDialog) w).updateItems(items);
                return;
            }
        }
    }

    private void handleFurnitureShopList(JsonObject msg) {
        JsonArray items = msg.getAsJsonArray("items");
        for (Window w : Window.getWindows()) {
            if (w instanceof RoomEditorDialog) {
                ((RoomEditorDialog) w).updateFurniture(items);
                return;
            }
        }
    }

    private void handleShopBuy(JsonObject msg) {
        if (msg.get("success").getAsBoolean()) {
            mainFrame.setCoins(msg.get("coins").getAsInt());
            JOptionPane.showMessageDialog(mainFrame, "Item purchased!");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to buy item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleInventoryList(JsonObject msg) {
        JsonArray items = msg.getAsJsonArray("items");
        for (Window w : Window.getWindows()) {
            if (w instanceof InventoryDialog) {
                ((InventoryDialog) w).updateItems(items);
                return;
            }
        }
    }

    private void handleEquipResult(JsonObject msg) {
        if (msg.get("success").getAsBoolean()) {
            mainFrame.setStatus("Item equipped!");
            if (msg.has("avatarData")) {
                // Update local avatar info
            }
        } else {
            mainFrame.setStatus("Failed to equip item");
        }
    }

    private void handleBalance(JsonObject msg) {
        mainFrame.setCoins(msg.get("coins").getAsInt());
    }

    private void handleDailyResult(JsonObject msg) {
        if (msg.get("success").getAsBoolean()) {
            mainFrame.setCoins(msg.get("coins").getAsInt());
            JOptionPane.showMessageDialog(mainFrame, "Daily bonus claimed! +" + msg.get("amount").getAsInt() + " coins");
        } else {
            mainFrame.setStatus("Daily bonus already claimed today");
        }
    }

    private void handleError(JsonObject msg) {
        String error = msg.has("message") ? msg.get("message").getAsString() : "Unknown error";
        loginFrame.setEnabled(true);
        loginFrame.setStatus(error, true);
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        new AvatarWorldClient().start();
    }
}
