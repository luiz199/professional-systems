package com.avatarworld.client.ui;

import com.avatarworld.client.network.GameClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InventoryDialog extends JDialog {
    private GameClient client;
    private JPanel itemPanel;
    private Map<Integer, JsonObject> items = new HashMap<>();

    public InventoryDialog(Frame owner, GameClient client) {
        super(owner, "Inventory", true);
        this.client = client;
        initUI();
        client.requestInventory();
    }

    private void initUI() {
        setSize(550, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x2C3E50));
        JLabel title = new JLabel("🎒 Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0x9B59B6));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0x3498DB));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> client.requestInventory());
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(new Color(0x34495E));
        JScrollPane scroll = new JScrollPane(itemPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(0xE74C3C));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }

    public void updateItems(JsonArray itemsArr) {
        itemPanel.removeAll();
        items.clear();
        for (int i = 0; i < itemsArr.size(); i++) {
            JsonObject item = itemsArr.get(i).getAsJsonObject();
            items.put(item.get("id").getAsInt(), item);

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(0x3D566E));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C3E50)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            card.setMaximumSize(new Dimension(500, 50));

            String type = item.get("type").getAsString();
            String emoji = type.equals("shirt") ? "👕" : type.equals("pants") ? "👖" :
                type.equals("hat") ? "🎩" : type.equals("shoes") ? "👟" :
                type.equals("accessory") ? "💎" : type.equals("hair") ? "💇" : "📦";
            JLabel nameLabel = new JLabel(emoji + " " + item.get("name").getAsString());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            card.add(nameLabel, BorderLayout.WEST);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            rightPanel.setBackground(new Color(0x3D566E));

            // Check if wearable (not furniture)
            boolean isFurniture = item.has("isFurniture") && item.get("isFurniture").getAsBoolean();
            if (!isFurniture) {
                JButton equipBtn = new JButton("Equip");
                equipBtn.setBackground(new Color(0x3498DB));
                equipBtn.setForeground(Color.WHITE);
                equipBtn.setFocusPainted(false);
                int itemId = item.get("id").getAsInt();
                equipBtn.addActionListener(e -> client.equipItem(itemId, true));
                rightPanel.add(equipBtn);

                JButton unequipBtn = new JButton("Unequip");
                unequipBtn.setBackground(new Color(0xE67E22));
                unequipBtn.setForeground(Color.WHITE);
                unequipBtn.setFocusPainted(false);
                unequipBtn.addActionListener(e -> client.equipItem(itemId, false));
                rightPanel.add(unequipBtn);
            }

            card.add(rightPanel, BorderLayout.EAST);
            itemPanel.add(card);
        }
        if (itemsArr.size() == 0) {
            JLabel empty = new JLabel("Your inventory is empty. Buy items from the shop!");
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            itemPanel.add(empty);
        }
        itemPanel.revalidate();
        itemPanel.repaint();
    }
}
