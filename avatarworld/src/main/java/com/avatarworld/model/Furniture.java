package com.avatarworld.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Furniture {
    private int id;
    private int roomId;
    private int itemId;
    private String itemName;
    private String itemData;
    private int x;
    private int y;
    private int rotation;

    public Furniture() {}

    public Furniture(int roomId, int itemId, int x, int y) {
        this.roomId = roomId;
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.rotation = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemData() { return itemData; }
    public void setItemData(String itemData) { this.itemData = itemData; }

    public JsonObject getItemDataJson() {
        if (itemData == null || itemData.isEmpty()) return new JsonObject();
        return JsonParser.parseString(itemData).getAsJsonObject();
    }

    public int getWidth() {
        JsonObject d = getItemDataJson();
        return d.has("width") ? d.get("width").getAsInt() : 1;
    }

    public int getHeight() {
        JsonObject d = getItemDataJson();
        return d.has("height") ? d.get("height").getAsInt() : 1;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation % 360; }
}
