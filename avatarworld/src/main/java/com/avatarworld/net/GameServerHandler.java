package com.avatarworld.net;

import com.avatarworld.config.ServerConfig;
import com.avatarworld.db.DatabaseManager;
import com.avatarworld.game.*;
import com.avatarworld.model.*;
import com.avatarworld.session.PlayerSession;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class GameServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Gson gson = new Gson();
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final RoomManager roomManager = RoomManager.getInstance();
    private final EconomyManager economy = EconomyManager.getInstance();
    private final ChatFilter chatFilter = ChatFilter.getInstance();
    private PlayerSession session;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        session = SessionManager.getInstance().createSession(ctx);
        System.out.println("[+] New connection: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("[-] Disconnected: " + (session != null ? session.getUsername() : "unknown"));
        SessionManager.getInstance().removeSession(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            handleMessage(ctx, text);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, String text) {
        try {
            JsonObject msg = JsonParser.parseString(text).getAsJsonObject();
            String type = msg.get("type").getAsString();

            switch (type) {
                case "LOGIN": handleLogin(msg); break;
                case "REGISTER": handleRegister(msg); break;
                case "LIST_ROOMS": handleListRooms(); break;
                case "CREATE_ROOM": handleCreateRoom(msg); break;
                case "JOIN_ROOM": handleJoinRoom(msg); break;
                case "LEAVE_ROOM": handleLeaveRoom(); break;
                case "CHAT_SEND": handleChat(msg); break;
                case "AVATAR_MOVE": handleMove(msg); break;
                case "AVATAR_DANCE": handleDance(msg); break;
                case "AVATAR_UPDATE": handleAvatarUpdate(msg); break;
                case "SHOP_LIST": handleShopList(); break;
                case "FURNITURE_SHOP_LIST": handleFurnitureShopList(); break;
                case "SHOP_BUY": handleShopBuy(msg); break;
                case "INVENTORY_LIST": handleInventoryList(); break;
                case "INVENTORY_EQUIP": handleEquip(msg); break;
                case "ECONOMY_BALANCE": handleBalance(); break;
                case "ECONOMY_DAILY": handleDaily(); break;
                case "ECONOMY_GIFT": handleGift(msg); break;
                case "ROOM_USERS": handleRoomUsers(); break;
                case "EDITOR_PLACE": handleEditorPlace(msg); break;
                case "EDITOR_MOVE": handleEditorMove(msg); break;
                case "EDITOR_REMOVE": handleEditorRemove(msg); break;
                case "EDITOR_SAVE": handleEditorSave(); break;
                case "ADMIN_KICK": handleAdminKick(msg); break;
                case "ADMIN_BAN": handleAdminBan(msg); break;
                case "ADMIN_MUTE": handleAdminMute(msg); break;
                case "ADMIN_BROADCAST": handleAdminBroadcast(msg); break;
                default: sendError("Unknown message type: " + type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Invalid message format");
        }
    }

    // === AUTH ===
    private void handleLogin(JsonObject msg) {
        if (session.isAuthenticated()) { sendError("Already logged in"); return; }
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        User user = db.authenticate(username, password);
        if (user == null) {
            sendError("Invalid credentials or user is banned");
            return;
        }
        session.setUser(user);
        session.setAuthenticated(true);
        SessionManager.getInstance().authenticate(session, user.getId());
        db.updateUserCoins(user.getId(), user.getCoins());

        JsonObject resp = new JsonObject();
        resp.addProperty("type", "LOGIN_RESULT");
        resp.addProperty("success", true);
        resp.addProperty("userId", user.getId());
        resp.addProperty("username", user.getUsername());
        resp.addProperty("coins", user.getCoins());
        resp.addProperty("avatarData", user.getAvatarData() != null ? user.getAvatarData() : "{}");
        resp.addProperty("isAdmin", user.isAdmin());
        send(resp.toString());
        db.addLog("LOGIN", username + " logged in", session.getIpAddress());
    }

    private void handleRegister(JsonObject msg) {
        String username = msg.get("username").getAsString();
        String password = msg.get("password").getAsString();
        String email = msg.has("email") ? msg.get("email").getAsString() : null;

        if (username.length() < 3 || username.length() > 20) {
            sendError("Username must be 3-20 characters");
            return;
        }
        if (password.length() < 4) {
            sendError("Password must be at least 4 characters");
            return;
        }
        if (db.getUserByUsername(username) != null) {
            sendError("Username already taken");
            return;
        }
        if (db.createUser(username, password, email)) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "REGISTER_RESULT");
            resp.addProperty("success", true);
            send(resp.toString());
            db.addLog("REGISTER", "New user: " + username, session.getIpAddress());
        } else {
            sendError("Registration failed");
        }
    }

    // === ROOMS ===
    private void handleListRooms() {
        checkAuth();
        List<Room> rooms = roomManager.listRooms();
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "ROOM_LIST");
        JsonArray arr = new JsonArray();
        for (Room r : rooms) {
            JsonObject rj = new JsonObject();
            rj.addProperty("id", r.getId());
            rj.addProperty("name", r.getName());
            rj.addProperty("ownerId", r.getOwnerId());
            rj.addProperty("maxUsers", r.getMaxUsers());
            rj.addProperty("hasPassword", r.hasPassword());
            rj.addProperty("userCount", roomManager.getRoom(r.getId()) != null ? roomManager.getRoom(r.getId()).getUserCount() : 0);
            arr.add(rj);
        }
        resp.add("rooms", arr);
        send(resp.toString());
    }

    private void handleCreateRoom(JsonObject msg) {
        checkAuth();
        String name = msg.get("name").getAsString();
        if (name.length() < 2 || name.length() > 50) {
            sendError("Room name must be 2-50 characters");
            return;
        }
        Room room = roomManager.createRoom(name, session.getUserId());
        if (room != null) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "ROOM_CREATED");
            resp.addProperty("roomId", room.getId());
            resp.addProperty("name", room.getName());
            send(resp.toString());
        } else {
            sendError("Failed to create room");
        }
    }

    private void handleJoinRoom(JsonObject msg) {
        checkAuth();
        int roomId = msg.get("roomId").getAsInt();
        String password = msg.has("password") ? msg.get("password").getAsString() : null;

        boolean joined;
        if (password != null && !password.isEmpty()) {
            joined = roomManager.joinRoom(session, roomId, password);
        } else {
            joined = roomManager.joinRoom(session, roomId);
        }

        if (joined) {
            Room room = roomManager.getRoom(roomId);
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "ROOM_JOINED");
            resp.addProperty("roomId", roomId);
            resp.addProperty("name", room.getName());
            resp.addProperty("ownerId", room.getOwnerId());
            resp.addProperty("layoutData", room.getLayoutData());
            resp.addProperty("wallpaper", room.getWallpaper());
            resp.addProperty("floor", room.getFloor());

            JsonArray usersArr = new JsonArray();
            for (Room.RoomUser ru : room.getUsers()) {
                JsonObject uj = new JsonObject();
                uj.addProperty("userId", ru.getUserId());
                uj.addProperty("username", ru.getUsername());
                uj.addProperty("x", ru.getX());
                uj.addProperty("y", ru.getY());
                uj.addProperty("direction", ru.getDirection());
                uj.addProperty("animation", ru.getAnimation());
                uj.addProperty("avatarData", ru.getAvatarData() != null ? ru.getAvatarData() : "{}");
                usersArr.add(uj);
            }
            resp.add("users", usersArr);

            JsonArray furnArr = new JsonArray();
            for (Furniture f : room.getFurniture()) {
                JsonObject fj = new JsonObject();
                fj.addProperty("id", f.getId());
                fj.addProperty("itemId", f.getItemId());
                fj.addProperty("itemName", f.getItemName());
                fj.addProperty("itemData", f.getItemData());
                fj.addProperty("x", f.getX());
                fj.addProperty("y", f.getY());
                fj.addProperty("rotation", f.getRotation());
                furnArr.add(fj);
            }
            resp.add("furniture", furnArr);
            send(resp.toString());
        } else {
            sendError("Failed to join room (full, not found, or wrong password)");
        }
    }

    private void handleLeaveRoom() {
        checkAuth();
        roomManager.leaveRoom(session);
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "ROOM_LEFT");
        resp.addProperty("success", true);
        send(resp.toString());
    }

    private void handleRoomUsers() {
        checkAuth();
        if (!session.isInRoom()) { sendError("Not in a room"); return; }
        Room room = roomManager.getRoom(session.getCurrentRoomId());
        if (room == null) { sendError("Room not found"); return; }
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "ROOM_USERS_LIST");
        JsonArray arr = new JsonArray();
        for (Room.RoomUser ru : room.getUsers()) {
            JsonObject uj = new JsonObject();
            uj.addProperty("userId", ru.getUserId());
            uj.addProperty("username", ru.getUsername());
            arr.add(uj);
        }
        resp.add("users", arr);
        send(resp.toString());
    }

    // === CHAT ===
    private void handleChat(JsonObject msg) {
        checkAuth();
        String message = msg.get("message").getAsString();
        if (message.isEmpty() || message.length() > 500) return;
        String filtered = chatFilter.filter(message);

        JsonObject chatMsg = new JsonObject();
        chatMsg.addProperty("type", "CHAT_RECEIVE");
        chatMsg.addProperty("userId", session.getUserId());
        chatMsg.addProperty("username", session.getUsername());
        chatMsg.addProperty("message", filtered);

        if (session.isInRoom()) {
            roomManager.broadcastToRoom(session.getCurrentRoomId(), chatMsg.toString(), -1);
        }
        send(chatMsg.toString());

        db.addLog("CHAT", session.getUsername() + ": " + filtered, session.getIpAddress());
    }

    // === AVATAR ===
    private void handleMove(JsonObject msg) {
        checkAuth();
        int x = msg.get("x").getAsInt();
        int y = msg.get("y").getAsInt();
        String dir = msg.has("direction") ? msg.get("direction").getAsString() : null;
        roomManager.moveUserInRoom(session, x, y, dir);
    }

    private void handleDance(JsonObject msg) {
        checkAuth();
        String anim = msg.get("animation").getAsString();
        roomManager.setUserAnimation(session, anim);
    }

    private void handleAvatarUpdate(JsonObject msg) {
        checkAuth();
        String avatarData = msg.get("avatarData").getAsString();
        session.getUser().setAvatarData(avatarData);
        db.updateAvatarData(session.getUserId(), avatarData);
        roomManager.updateUserAvatar(session);
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "AVATAR_UPDATE_RESULT");
        resp.addProperty("success", true);
        send(resp.toString());
    }

    // === SHOP ===
    private void handleShopList() {
        checkAuth();
        List<Item> items = db.getShopItems();
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "SHOP_LIST_RESULT");
        resp.add("items", itemsToJson(items));
        send(resp.toString());
    }

    private void handleFurnitureShopList() {
        checkAuth();
        List<Item> items = db.getFurnitureShopItems();
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "FURNITURE_SHOP_LIST_RESULT");
        resp.add("items", itemsToJson(items));
        send(resp.toString());
    }

    private void handleShopBuy(JsonObject msg) {
        checkAuth();
        int itemId = msg.get("itemId").getAsInt();
        if (economy.buyItem(session.getUser(), itemId)) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "SHOP_BUY_RESULT");
            resp.addProperty("success", true);
            resp.addProperty("coins", session.getUser().getCoins());
            send(resp.toString());
        } else {
            sendError("Failed to buy item (insufficient coins, already owned, or not found)");
        }
    }

    // === INVENTORY ===
    private void handleInventoryList() {
        checkAuth();
        List<Item> items = db.getUserInventory(session.getUserId());
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "INVENTORY_LIST_RESULT");
        resp.add("items", itemsToJson(items));
        send(resp.toString());
    }

    private void handleEquip(JsonObject msg) {
        checkAuth();
        int itemId = msg.get("itemId").getAsInt();
        boolean equip = msg.get("equip").getAsBoolean();
        if (db.equipItem(session.getUserId(), itemId, equip)) {
            // Update avatar data based on equipped items
            List<Item> equipped = db.getEquippedItems(session.getUserId());
            JsonObject avatarJson = session.getUser().getAvatarJson();
            for (Item item : equipped) {
                switch (item.getType()) {
                    case "shirt":
                        if (item.getDataJson().has("color"))
                            avatarJson.addProperty("torsoColor", item.getDataJson().get("color").getAsString());
                        break;
                    case "pants":
                        if (item.getDataJson().has("color"))
                            avatarJson.addProperty("legsColor", item.getDataJson().get("color").getAsString());
                        break;
                    case "hat":
                        avatarJson.addProperty("hat", item.getName());
                        break;
                    case "accessory":
                        avatarJson.addProperty("accessory", item.getName());
                        break;
                    case "hair":
                        if (item.getDataJson().has("color"))
                            avatarJson.addProperty("hairColor", item.getDataJson().get("color").getAsString());
                        if (item.getDataJson().has("style"))
                            avatarJson.addProperty("hairStyle", item.getDataJson().get("style").getAsString());
                        break;
                }
            }
            session.getUser().setAvatarData(avatarJson.toString());
            db.updateAvatarData(session.getUserId(), avatarJson.toString());
            roomManager.updateUserAvatar(session);

            JsonObject resp = new JsonObject();
            resp.addProperty("type", "INVENTORY_EQUIP_RESULT");
            resp.addProperty("success", true);
            resp.addProperty("avatarData", avatarJson.toString());
            send(resp.toString());
        } else {
            sendError("Failed to equip item");
        }
    }

    // === ECONOMY ===
    private void handleBalance() {
        checkAuth();
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "ECONOMY_BALANCE_RESULT");
        resp.addProperty("coins", session.getUser().getCoins());
        send(resp.toString());
    }

    private void handleDaily() {
        checkAuth();
        if (economy.claimDaily(session.getUser())) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "ECONOMY_DAILY_RESULT");
            resp.addProperty("success", true);
            resp.addProperty("coins", session.getUser().getCoins());
            resp.addProperty("amount", 200);
            send(resp.toString());
        } else {
            sendError("Daily bonus already claimed today");
        }
    }

    private void handleGift(JsonObject msg) {
        checkAuth();
        int targetId = msg.get("targetUserId").getAsInt();
        int amount = msg.get("amount").getAsInt();
        User target = db.getUserById(targetId);
        if (target == null) { sendError("Target user not found"); return; }
        if (economy.giftCoins(session.getUser(), target, amount)) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "ECONOMY_GIFT_RESULT");
            resp.addProperty("success", true);
            resp.addProperty("coins", session.getUser().getCoins());
            send(resp.toString());
        } else {
            sendError("Failed to gift coins (insufficient balance)");
        }
    }

    // === EDITOR ===
    private void handleEditorPlace(JsonObject msg) {
        checkAuth();
        if (!session.isInRoom()) { sendError("Not in a room"); return; }
        int roomId = session.getCurrentRoomId();
        int itemId = msg.get("itemId").getAsInt();
        int x = msg.get("x").getAsInt();
        int y = msg.get("y").getAsInt();
        int rot = msg.has("rotation") ? msg.get("rotation").getAsInt() : 0;

        // Check if user owns the item
        if (!db.hasItem(session.getUserId(), itemId)) {
            sendError("You don't own this item");
            return;
        }

        Furniture f = db.addFurniture(roomId, itemId, x, y, rot);
        if (f != null) {
            Room room = roomManager.getRoom(roomId);
            if (room != null) room.addFurniture(f);
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "EDITOR_PLACED");
            resp.addProperty("id", f.getId());
            resp.addProperty("itemId", itemId);
            resp.addProperty("x", x);
            resp.addProperty("y", y);
            resp.addProperty("rotation", rot);
            send(resp.toString());
            roomManager.broadcastToRoom(roomId, resp.toString(), session.getUserId());
        } else {
            sendError("Failed to place furniture");
        }
    }

    private void handleEditorMove(JsonObject msg) {
        checkAuth();
        int furnId = msg.get("furnitureId").getAsInt();
        int x = msg.get("x").getAsInt();
        int y = msg.get("y").getAsInt();
        int rot = msg.has("rotation") ? msg.get("rotation").getAsInt() : -1;
        if (rot < 0) rot = 0;
        if (db.updateFurniture(furnId, x, y, rot)) {
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "EDITOR_MOVED");
            resp.addProperty("furnitureId", furnId);
            resp.addProperty("x", x);
            resp.addProperty("y", y);
            resp.addProperty("rotation", rot);
            send(resp.toString());
            if (session.isInRoom()) roomManager.broadcastToRoom(session.getCurrentRoomId(), resp.toString(), session.getUserId());
        }
    }

    private void handleEditorRemove(JsonObject msg) {
        checkAuth();
        int furnId = msg.get("furnitureId").getAsInt();
        if (db.removeFurniture(furnId)) {
            if (session.isInRoom()) {
                Room room = roomManager.getRoom(session.getCurrentRoomId());
                if (room != null) room.removeFurniture(furnId);
            }
            JsonObject resp = new JsonObject();
            resp.addProperty("type", "EDITOR_REMOVED");
            resp.addProperty("furnitureId", furnId);
            send(resp.toString());
            if (session.isInRoom()) roomManager.broadcastToRoom(session.getCurrentRoomId(), resp.toString(), session.getUserId());
        }
    }

    private void handleEditorSave() {
        checkAuth();
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "EDITOR_SAVED");
        resp.addProperty("success", true);
        send(resp.toString());
    }

    // === ADMIN ===
    private void checkAdmin() {
        if (session == null || !session.isAuthenticated() || !session.getUser().isAdmin()) {
            sendError("Admin access required");
            throw new RuntimeException("Unauthorized admin access");
        }
    }

    private void handleAdminKick(JsonObject msg) {
        checkAdmin();
        int targetId = msg.get("targetUserId").getAsInt();
        PlayerSession target = SessionManager.getInstance().getSession(targetId);
        if (target != null) {
            roomManager.leaveRoom(target);
            JsonObject kickMsg = new JsonObject();
            kickMsg.addProperty("type", "ADMIN_KICKED");
            target.send(kickMsg.toString());
            sendSuccess("ADMIN_ACTION_RESULT", "User kicked");
            db.addLog("ADMIN_KICK", session.getUsername() + " kicked user " + targetId, session.getIpAddress());
        }
    }

    private void handleAdminBan(JsonObject msg) {
        checkAdmin();
        int targetId = msg.get("targetUserId").getAsInt();
        boolean ban = msg.get("ban").getAsBoolean();
        db.banUser(targetId, ban);
        if (ban) {
            PlayerSession target = SessionManager.getInstance().getSession(targetId);
            if (target != null) {
                target.send("{\"type\":\"ADMIN_BANNED\"}");
                target.disconnect();
            }
        }
        sendSuccess("ADMIN_ACTION_RESULT", ban ? "User banned" : "User unbanned");
        db.addLog("ADMIN_BAN", session.getUsername() + " " + (ban ? "banned" : "unbanned") + " user " + targetId, session.getIpAddress());
    }

    private void handleAdminMute(JsonObject msg) {
        checkAdmin();
        int targetId = msg.get("targetUserId").getAsInt();
        boolean mute = msg.get("mute").getAsBoolean();
        db.muteUser(targetId, mute);
        sendSuccess("ADMIN_ACTION_RESULT", mute ? "User muted" : "User unmuted");
        db.addLog("ADMIN_MUTE", session.getUsername() + " " + (mute ? "muted" : "unmuted") + " user " + targetId, session.getIpAddress());
    }

    private void handleAdminBroadcast(JsonObject msg) {
        checkAdmin();
        String message = msg.get("message").getAsString();
        JsonObject bc = new JsonObject();
        bc.addProperty("type", "ADMIN_BROADCAST");
        bc.addProperty("message", message);
        // Send to all connected sessions
        sendSuccess("ADMIN_ACTION_RESULT", "Broadcast sent");
        db.addLog("ADMIN_BROADCAST", session.getUsername() + " broadcast: " + message, session.getIpAddress());
    }

    // === HELPERS ===
    private void checkAuth() {
        if (session == null || !session.isAuthenticated()) {
            sendError("Not authenticated");
            throw new RuntimeException("Unauthorized");
        }
    }

    private void send(String message) {
        if (session != null) session.send(message);
    }

    private void sendError(String error) {
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "ERROR");
        resp.addProperty("message", error);
        send(resp.toString());
    }

    private void sendSuccess(String type, String message) {
        JsonObject resp = new JsonObject();
        resp.addProperty("type", type);
        resp.addProperty("success", true);
        resp.addProperty("message", message);
        send(resp.toString());
    }

    private JsonArray itemsToJson(List<Item> items) {
        JsonArray arr = new JsonArray();
        for (Item item : items) {
            JsonObject ij = new JsonObject();
            ij.addProperty("id", item.getId());
            ij.addProperty("name", item.getName());
            ij.addProperty("type", item.getType());
            ij.addProperty("category", item.getCategory());
            ij.addProperty("price", item.getPrice());
            ij.addProperty("rarity", item.getRarity());
            ij.addProperty("data", item.getData());
            ij.addProperty("isFurniture", item.isFurniture());
            arr.add(ij);
        }
        return arr;
    }
}
