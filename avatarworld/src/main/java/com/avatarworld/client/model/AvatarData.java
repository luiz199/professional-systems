package com.avatarworld.client.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AvatarData {
    public String headColor = "#FFD700";
    public String torsoColor = "#1E90FF";
    public String legsColor = "#000000";
    public String hairStyle = "short";
    public String hairColor = "#000000";
    public String accessory = "none";
    public String hat = "";

    public static AvatarData fromJson(String json) {
        AvatarData a = new AvatarData();
        if (json == null || json.isEmpty()) return a;
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("headColor")) a.headColor = obj.get("headColor").getAsString();
            if (obj.has("torsoColor")) a.torsoColor = obj.get("torsoColor").getAsString();
            if (obj.has("legsColor")) a.legsColor = obj.get("legsColor").getAsString();
            if (obj.has("hairStyle")) a.hairStyle = obj.get("hairStyle").getAsString();
            if (obj.has("hairColor")) a.hairColor = obj.get("hairColor").getAsString();
            if (obj.has("accessory")) a.accessory = obj.get("accessory").getAsString();
            if (obj.has("hat")) a.hat = obj.get("hat").getAsString();
        } catch (Exception ignored) {}
        return a;
    }

    public String toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("headColor", headColor);
        obj.addProperty("torsoColor", torsoColor);
        obj.addProperty("legsColor", legsColor);
        obj.addProperty("hairStyle", hairStyle);
        obj.addProperty("hairColor", hairColor);
        obj.addProperty("accessory", accessory);
        obj.addProperty("hat", hat);
        return obj.toString();
    }
}
