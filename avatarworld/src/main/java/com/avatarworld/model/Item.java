package com.avatarworld.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Item {
    private int id;
    private String name;
    private String type;
    private String category;
    private int price;
    private String rarity;
    private String data;
    private boolean isFurniture;

    public Item() {}

    public Item(int id, String name, String type, int price, String rarity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.rarity = rarity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public int getRarityRank() {
        switch (rarity) {
            case "common": return 0;
            case "rare": return 1;
            case "vip": return 2;
            case "legendary": return 3;
            default: return 0;
        }
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public JsonObject getDataJson() {
        if (data == null || data.isEmpty()) return new JsonObject();
        return JsonParser.parseString(data).getAsJsonObject();
    }

    public boolean isFurniture() { return isFurniture; }
    public void setFurniture(boolean furniture) { isFurniture = furniture; }
}
