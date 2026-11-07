package com.avatarworld.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private int coins;
    private String avatarData;
    private boolean admin;
    private boolean banned;
    private boolean muted;

    public User() {}

    public User(int id, String username, String password, int coins) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.coins = coins;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public void addCoins(int amount) { this.coins += amount; }
    public boolean removeCoins(int amount) {
        if (this.coins < amount) return false;
        this.coins -= amount;
        return true;
    }

    public String getAvatarData() { return avatarData; }
    public void setAvatarData(String avatarData) { this.avatarData = avatarData; }

    public JsonObject getAvatarJson() {
        if (avatarData == null || avatarData.isEmpty()) {
            JsonObject def = new JsonObject();
            def.addProperty("headColor", "#FFD700");
            def.addProperty("torsoColor", "#1E90FF");
            def.addProperty("legsColor", "#000000");
            def.addProperty("hairStyle", "short");
            def.addProperty("accessory", "none");
            return def;
        }
        return JsonParser.parseString(avatarData).getAsJsonObject();
    }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }

    private java.sql.Timestamp lastDaily;
    public java.sql.Timestamp getLastDaily() { return lastDaily; }
    public void setLastDaily(java.sql.Timestamp lastDaily) { this.lastDaily = lastDaily; }
}
