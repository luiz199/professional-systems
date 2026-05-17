package com.avatarworld.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    private int id;
    private String name;
    private int ownerId;
    private String password;
    private int maxUsers;
    private String layoutData;
    private String wallpaper;
    private String floor;
    private List<RoomUser> users = new CopyOnWriteArrayList<>();
    private List<Furniture> furniture = new CopyOnWriteArrayList<>();

    public Room() {}

    public Room(int id, String name, int ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.maxUsers = 50;
        JsonObject layout = new JsonObject();
        layout.addProperty("width", 30);
        layout.addProperty("height", 20);
        this.layoutData = layout.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean hasPassword() { return password != null && !password.isEmpty(); }

    public int getMaxUsers() { return maxUsers; }
    public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }

    public String getLayoutData() { return layoutData; }
    public void setLayoutData(String layoutData) { this.layoutData = layoutData; }
    public JsonObject getLayoutJson() {
        if (layoutData == null || layoutData.isEmpty()) {
            JsonObject def = new JsonObject();
            def.addProperty("width", 30);
            def.addProperty("height", 20);
            return def;
        }
        return JsonParser.parseString(layoutData).getAsJsonObject();
    }

    public String getWallpaper() { return wallpaper; }
    public void setWallpaper(String wallpaper) { this.wallpaper = wallpaper; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public List<RoomUser> getUsers() { return users; }
    public void addUser(RoomUser user) { users.add(user); }
    public void removeUser(int userId) { users.removeIf(u -> u.getUserId() == userId); }
    public RoomUser getUser(int userId) {
        return users.stream().filter(u -> u.getUserId() == userId).findFirst().orElse(null);
    }
    public int getUserCount() { return users.size(); }
    public boolean isFull() { return users.size() >= maxUsers; }

    public List<Furniture> getFurniture() { return furniture; }
    public void setFurniture(List<Furniture> furniture) { this.furniture = furniture; }
    public void addFurniture(Furniture f) { furniture.add(f); }
    public void removeFurniture(int id) { furniture.removeIf(f -> f.getId() == id); }

    public static class RoomUser {
        private int userId;
        private String username;
        private int x, y;
        private String direction;
        private String animation;
        private String avatarData;

        public RoomUser(int userId, String username, String avatarData) {
            this.userId = userId;
            this.username = username;
            this.x = 5;
            this.y = 5;
            this.direction = "down";
            this.animation = "idle";
            this.avatarData = avatarData;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public int getX() { return x; }
        public int getY() { return y; }
        public void setPosition(int x, int y) { this.x = x; this.y = y; }
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }
        public String getAnimation() { return animation; }
        public void setAnimation(String animation) { this.animation = animation; }
        public String getAvatarData() { return avatarData; }
        public void setAvatarData(String avatarData) { this.avatarData = avatarData; }
    }
}
