package com.avatarworld.client.ui;

import com.avatarworld.client.network.GameClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ShopDialog extends JDialog {
    private GameClient client;
    private JPanel itemPanel;
    private Map<Integer, JsonObject> items = new HashMap<>();

    public ShopDialog(Frame owner, GameClient client) {
        super(owner, "Item Shop", true);
        this.client = client;
        initUI();
        client.requestShopList();
    }

    private void initUI() {
        setSize(600, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x2C3E50));
        JLabel title = new JLabel("🛒 Item Shop", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0x3498DB));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0x3498DB));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> client.requestShopList());
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
            card.setMaximumSize(new Dimension(550, 50));

            String rarity = item.get("rarity").getAsString();
            String emoji = "common".equals(rarity) ? "⬜" : "rare".equals(rarity) ? "🟦" : "vip".equals(rarity) ? "🟪" : "⬜";
            JLabel nameLabel = new JLabel(emoji + " " + item.get("name").getAsString() +
                " (" + item.get("type").getAsString() + ")");
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            card.add(nameLabel, BorderLayout.WEST);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            rightPanel.setBackground(new Color(0x3D566E));

            JLabel priceLabel = new JLabel("💰 " + item.get("price").getAsInt());
            priceLabel.setForeground(new Color(0xFFD700));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 13));
            rightPanel.add(priceLabel);

            JLabel rarityLabel = new JLabel("[" + rarity.toUpperCase() + "]");
            rarityLabel.setForeground("vip".equals(rarity) ? new Color(0x9B59B6) :
                "rare".equals(rarity) ? new Color(0x3498DB) : Color.GRAY);
            rightPanel.add(rarityLabel);

            JButton buyBtn = new JButton("Buy");
            buyBtn.setBackground(new Color(0x27AE60));
            buyBtn.setForeground(Color.WHITE);
            buyBtn.setFocusPainted(false);
            int itemId = item.get("id").getAsInt();
            buyBtn.addActionListener(e -> client.buyItem(itemId));
            rightPanel.add(buyBtn);

            card.add(rightPanel, BorderLayout.EAST);
            itemPanel.add(card);
        }
        itemPanel.revalidate();
        itemPanel.repaint();
    }
}
