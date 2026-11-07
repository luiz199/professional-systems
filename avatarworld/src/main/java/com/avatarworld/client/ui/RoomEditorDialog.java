package com.avatarworld.client.ui;

import com.avatarworld.client.network.GameClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RoomEditorDialog extends JDialog {
    private GameClient client;
    private JPanel furniturePanel;
    private Map<Integer, JsonObject> furnitureItems = new HashMap<>();

    public RoomEditorDialog(Frame owner, GameClient client) {
        super(owner, "Room Editor", true);
        this.client = client;
        initUI();
        client.requestFurnitureShopList();
    }

    private void initUI() {
        setSize(600, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x2C3E50));
        JLabel title = new JLabel("🏗️ Room Editor - Place Furniture", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0xE67E22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0x3498DB));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> client.requestFurnitureShopList());
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel instructions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructions.setBackground(new Color(0x1A252F));
        JLabel instrLabel = new JLabel("Click 'Place' to add furniture to your current room. Use arrows to move.");
        instrLabel.setForeground(new Color(0xBDC3C7));
        instrLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        instructions.add(instrLabel);
        add(instructions, BorderLayout.AFTER_LAST_LINE);

        furniturePanel = new JPanel();
        furniturePanel.setLayout(new BoxLayout(furniturePanel, BoxLayout.Y_AXIS));
        furniturePanel.setBackground(new Color(0x34495E));
        JScrollPane scroll = new JScrollPane(furniturePanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(0xE74C3C));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }

    public void updateFurniture(JsonArray itemsArr) {
        furniturePanel.removeAll();
        furnitureItems.clear();
        for (int i = 0; i < itemsArr.size(); i++) {
            JsonObject item = itemsArr.get(i).getAsJsonObject();
            furnitureItems.put(item.get("id").getAsInt(), item);

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(0x3D566E));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C3E50)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            card.setMaximumSize(new Dimension(550, 50));

            String rarity = item.get("rarity").getAsString();
            String emoji = "vip".equals(rarity) ? "👑" : "rare".equals(rarity) ? "✨" : "🪑";
            JLabel nameLabel = new JLabel(emoji + " " + item.get("name").getAsString() +
                " (💰" + item.get("price").getAsInt() + ")");
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            card.add(nameLabel, BorderLayout.WEST);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            rightPanel.setBackground(new Color(0x3D566E));

            // Place at different positions
            int itemId = item.get("id").getAsInt();
            JButton place5x5 = new JButton("Place 5,5");
            place5x5.setBackground(new Color(0x27AE60));
            place5x5.setForeground(Color.WHITE);
            place5x5.setFocusPainted(false);
            place5x5.addActionListener(e -> client.placeFurniture(itemId, 5, 5, 0));
            rightPanel.add(place5x5);

            JButton place8x8 = new JButton("Place 8,8");
            place8x8.setBackground(new Color(0x2ECC71));
            place8x8.setForeground(Color.WHITE);
            place8x8.setFocusPainted(false);
            place8x8.addActionListener(e -> client.placeFurniture(itemId, 8, 8, 0));
            rightPanel.add(place8x8);

            card.add(rightPanel, BorderLayout.EAST);
            furniturePanel.add(card);

            // Rotation buttons
            JPanel rotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            rotPanel.setBackground(new Color(0x3D566E));
            JLabel rotLabel = new JLabel("Rotation: ");
            rotLabel.setForeground(Color.LIGHT_GRAY);
            rotPanel.add(rotLabel);
            for (int rot : new int[]{0, 90, 180, 270}) {
                JButton rotBtn = new JButton(rot + "°");
                rotBtn.setBackground(new Color(0x7F8C8D));
                rotBtn.setForeground(Color.WHITE);
                rotBtn.setFocusPainted(false);
                int finalRot = rot;
                rotBtn.addActionListener(e -> client.placeFurniture(itemId, 10, 10, finalRot));
                rotPanel.add(rotBtn);
            }
            furniturePanel.add(rotPanel);
        }
        if (itemsArr.size() == 0) {
            JLabel empty = new JLabel("No furniture available in shop.");
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            furniturePanel.add(empty);
        }
        furniturePanel.revalidate();
        furniturePanel.repaint();
    }
}
