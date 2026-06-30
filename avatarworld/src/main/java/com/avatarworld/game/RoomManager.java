package com.avatarworld.game;

import com.avatarworld.db.DatabaseManager;
import com.avatarworld.model.Furniture;
import com.avatarworld.model.Item;
import com.avatarworld.model.Room;
import com.avatarworld.model.Room.RoomUser;
import com.avatarworld.session.PlayerSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private static RoomManager instance;
    private Map<Integer, Room> activeRooms = new ConcurrentHashMap<>();
    private DatabaseManager db = DatabaseManager.getInstance();

    private RoomManager() {}

    public static synchronized RoomManager getInstance() {
        if (instance == null) instance = new RoomManager();
        return instance;
    }

    public Room createRoom(String name, int ownerId) {
        JsonObject layout = new JsonObject();
        layout.addProperty("width", 30);
        layout.addProperty("height", 20);
        Room room = db.createRoom(name, ownerId, layout.toString());
        if (room != null) {
            loadRoomFurniture(room);
            activeRooms.put(room.getId(), room);
            db.addLog("ROOM_CREATED", "Room '" + name + "' created by user " + ownerId, "system");
        }
        return room;
    }

    public Room getRoom(int roomId) {
        if (activeRooms.containsKey(roomId)) return activeRooms.get(roomId);
        Room room = db.getRoomById(roomId);
        if (room != null) {
            loadRoomFurniture(room);
            activeRooms.put(roomId, room);
        }
        return room;
    }

    private void loadRoomFurniture(Room room) {
        List<Furniture> furniture = db.getRoomFurniture(room.getId());
        room.setFurniture(furniture);
    }

    public boolean joinRoom(PlayerSession session, int roomId) {
        Room room = getRoom(roomId);
        if (room == null) return false;
        if (room.isFull()) return false;

        if (session.isInRoom()) leaveRoom(session);

        RoomUser ru = new RoomUser(session.getUserId(), session.getUsername(), session.getUser().getAvatarData());
        room.addUser(ru);
        session.setCurrentRoomId(roomId);

        // Notify room about new user
        JsonObject joinMsg = new JsonObject();
        joinMsg.addProperty("type", "ROOM_USER_JOIN");
        joinMsg.addProperty("userId", session.getUserId());
        joinMsg.addProperty("username", session.getUsername());
        joinMsg.addProperty("avatarData", session.getUser().getAvatarData() != null ? session.getUser().getAvatarData() : "{}");
        broadcastToRoom(roomId, joinMsg.toString(), session.getUserId());

        db.addLog("ROOM_JOIN", session.getUsername() + " joined room " + room.getName(), session.getIpAddress());
        return true;
    }

    public boolean joinRoom(PlayerSession session, int roomId, String password) {
        Room room = getRoom(roomId);
        if (room == null) return false;
        if (room.hasPassword() && !room.getPassword().equals(password)) return false;
        return joinRoom(session, roomId);
    }

    public void leaveRoom(PlayerSession session) {
        if (!session.isInRoom()) return;
        int roomId = session.getCurrentRoomId();
        Room room = activeRooms.get(roomId);
        if (room != null) {
            room.removeUser(session.getUserId());
            JsonObject leaveMsg = new JsonObject();
            leaveMsg.addProperty("type", "ROOM_USER_LEAVE");
            leaveMsg.addProperty("userId", session.getUserId());
            broadcastToRoom(roomId, leaveMsg.toString(), -1);
            if (room.getUserCount() == 0) activeRooms.remove(roomId);
        }
        session.setCurrentRoomId(-1);
    }

    public void moveUserInRoom(PlayerSession session, int x, int y, String direction) {
        if (!session.isInRoom()) return;
        Room room = activeRooms.get(session.getCurrentRoomId());
        if (room == null) return;
        RoomUser ru = room.getUser(session.getUserId());
        if (ru == null) return;
        ru.setPosition(x, y);
        if (direction != null) ru.setDirection(direction);

        JsonObject moveMsg = new JsonObject();
        moveMsg.addProperty("type", "AVATAR_MOVED");
        moveMsg.addProperty("userId", session.getUserId());
        moveMsg.addProperty("x", x);
        moveMsg.addProperty("y", y);
        if (direction != null) moveMsg.addProperty("direction", direction);
        broadcastToRoom(session.getCurrentRoomId(), moveMsg.toString(), session.getUserId());
    }

    public void setUserAnimation(PlayerSession session, String animation) {
        if (!session.isInRoom()) return;
        Room room = activeRooms.get(session.getCurrentRoomId());
        if (room == null) return;
        RoomUser ru = room.getUser(session.getUserId());
        if (ru == null) return;
        ru.setAnimation(animation);
        JsonObject animMsg = new JsonObject();
        animMsg.addProperty("type", "AVATAR_ANIMATION");
        animMsg.addProperty("userId", session.getUserId());
        animMsg.addProperty("animation", animation);
        broadcastToRoom(session.getCurrentRoomId(), animMsg.toString(), session.getUserId());
    }

    public void updateUserAvatar(PlayerSession session) {
        if (!session.isInRoom()) return;
        Room room = activeRooms.get(session.getCurrentRoomId());
        if (room == null) return;
        RoomUser ru = room.getUser(session.getUserId());
        if (ru == null) return;
        ru.setAvatarData(session.getUser().getAvatarData());
        JsonObject updateMsg = new JsonObject();
        updateMsg.addProperty("type", "AVATAR_UPDATED");
        updateMsg.addProperty("userId", session.getUserId());
        updateMsg.addProperty("avatarData", session.getUser().getAvatarData() != null ? session.getUser().getAvatarData() : "{}");
        broadcastToRoom(session.getCurrentRoomId(), updateMsg.toString(), session.getUserId());
    }

    public void broadcastToRoom(int roomId, String message, int excludeUserId) {
        Room room = activeRooms.get(roomId);
        if (room == null) return;
        for (RoomUser ru : room.getUsers()) {
            if (ru.getUserId() == excludeUserId) continue;
            PlayerSession target = SessionManager.getInstance().getSession(ru.getUserId());
            if (target != null) target.send(message);
        }
    }

    public List<Room> listRooms() {
        return db.getAllRooms();
    }
}
