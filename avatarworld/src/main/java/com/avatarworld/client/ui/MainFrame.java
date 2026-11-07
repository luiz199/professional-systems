package com.avatarworld.client.ui;

import com.avatarworld.client.network.GameClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class MainFrame extends JFrame {
    private GameClient client;
    private JPanel roomListPanel;
    private JPanel mainPanel;
    private JLabel coinsLabel;
    private JLabel userLabel;
    private CardLayout cardLayout;
    private RoomPanel roomPanel;
    private int userId;
    private String username;
    private int coins;
    private boolean isAdmin;

    public MainFrame(GameClient client, int userId, String username, int coins, boolean isAdmin) {
        this.client = client;
        this.userId = userId;
        this.username = username;
        this.coins = coins;
        this.isAdmin = isAdmin;
        initUI();
        client.listRooms();
    }

    private void initUI() {
        setTitle("AvatarWorld - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Room List Panel (default view)
        JPanel lobbyPanel = createLobbyPanel();
        mainPanel.add(lobbyPanel, "lobby");

        // Room Panel (in-game view)
        roomPanel = new RoomPanel(client, userId, username);
        mainPanel.add(roomPanel, "room");

        add(mainPanel);
    }

    private JPanel createLobbyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x2C3E50));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0x1A252F));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        userLabel = new JLabel("Welcome, " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        topBar.add(userLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(0x1A252F));

        coinsLabel = new JLabel("Coins: " + coins);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coinsLabel.setForeground(new Color(0xFFD700));
        rightPanel.add(coinsLabel);

        JButton dailyBtn = new JButton("Daily Bonus");
        dailyBtn.setBackground(new Color(0x2ECC71));
        dailyBtn.setForeground(Color.WHITE);
        dailyBtn.setFocusPainted(false);
        dailyBtn.addActionListener(e -> client.claimDaily());
        rightPanel.add(dailyBtn);

        JButton shopBtn = new JButton("Shop");
        shopBtn.setBackground(new Color(0x3498DB));
        shopBtn.setForeground(Color.WHITE);
        shopBtn.setFocusPainted(false);
        shopBtn.addActionListener(e -> new ShopDialog(this, client).setVisible(true));
        rightPanel.add(shopBtn);

        JButton invBtn = new JButton("Inventory");
        invBtn.setBackground(new Color(0x9B59B6));
        invBtn.setForeground(Color.WHITE);
        invBtn.setFocusPainted(false);
        invBtn.addActionListener(e -> new InventoryDialog(this, client).setVisible(true));
        rightPanel.add(invBtn);

        JButton roomEditorBtn = new JButton("Room Editor");
        roomEditorBtn.setBackground(new Color(0xE67E22));
        roomEditorBtn.setForeground(Color.WHITE);
        roomEditorBtn.setFocusPainted(false);
        roomEditorBtn.addActionListener(e -> new RoomEditorDialog(this, client).setVisible(true));
        rightPanel.add(roomEditorBtn);

        if (isAdmin) {
            JButton adminBtn = new JButton("Admin Panel");
            adminBtn.setBackground(new Color(0xE74C3C));
            adminBtn.setForeground(Color.WHITE);
            adminBtn.setFocusPainted(false);
            adminBtn.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("http://localhost:8081/api/admin"));
                } catch (Exception ignored) {}
            });
            rightPanel.add(adminBtn);
        }

        topBar.add(rightPanel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Room list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0x2C3E50));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0x3498DB), 2),
            "Available Rooms",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 16),
            new Color(0x3498DB)
        ));

        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setBackground(new Color(0x34495E));

        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Create room button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(0x2C3E50));
        JButton createRoomBtn = new JButton("+ Create Room");
        createRoomBtn.setFont(new Font("Arial", Font.BOLD, 14));
        createRoomBtn.setBackground(new Color(0x2ECC71));
        createRoomBtn.setForeground(Color.WHITE);
        createRoomBtn.setFocusPainted(false);
        createRoomBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Room name:");
            if (name != null && !name.trim().isEmpty()) {
                client.createRoom(name.trim());
            }
        });
        bottomPanel.add(createRoomBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(0x3498DB));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> client.listRooms());
        bottomPanel.add(refreshBtn);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    public void updateRoomList(JsonArray rooms) {
        roomListPanel.removeAll();
        for (int i = 0; i < rooms.size(); i++) {
            JsonObject room = rooms.get(i).getAsJsonObject();
            JPanel roomCard = new JPanel(new BorderLayout());
            roomCard.setBackground(new Color(0x3D566E));
            roomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x2C3E50)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            roomCard.setMaximumSize(new Dimension(900, 60));
            roomCard.setPreferredSize(new Dimension(900, 60));

            JLabel nameLabel = new JLabel("🏠 " + room.get("name").getAsString());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setForeground(Color.WHITE);
            roomCard.add(nameLabel, BorderLayout.WEST);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            infoPanel.setBackground(new Color(0x3D566E));

            String ownerName = room.has("ownerName") ? room.get("ownerName").getAsString() : "ID: " + room.get("ownerId").getAsInt();
            JLabel ownerLabel = new JLabel("Owner: " + ownerName);
            ownerLabel.setForeground(new Color(0xBDC3C7));
            infoPanel.add(ownerLabel);

            JLabel countLabel = new JLabel("👤 " + room.get("userCount").getAsInt() + "/" + room.get("maxUsers").getAsInt());
            countLabel.setForeground(new Color(0xBDC3C7));
            infoPanel.add(countLabel);

            if (room.get("hasPassword").getAsBoolean()) {
                JLabel lockLabel = new JLabel("🔒");
                lockLabel.setForeground(new Color(0xE74C3C));
                infoPanel.add(lockLabel);
            }

            JButton joinBtn = new JButton("Enter");
            joinBtn.setBackground(new Color(0x3498DB));
            joinBtn.setForeground(Color.WHITE);
            joinBtn.setFocusPainted(false);
            int roomId = room.get("id").getAsInt();
            boolean hasPass = room.get("hasPassword").getAsBoolean();
            joinBtn.addActionListener(e -> {
                if (hasPass) {
                    String pass = JOptionPane.showInputDialog(this, "Room password:");
                    if (pass == null) return;
                    // TODO: send password with join
                }
                client.joinRoom(roomId);
            });
            infoPanel.add(joinBtn);

            roomCard.add(infoPanel, BorderLayout.EAST);
            roomListPanel.add(roomCard);
        }
        if (rooms.size() == 0) {
            JLabel empty = new JLabel("No rooms available. Create one!");
            empty.setForeground(Color.GRAY);
            empty.setFont(new Font("Arial", Font.ITALIC, 14));
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            roomListPanel.add(empty);
        }
        roomListPanel.revalidate();
        roomListPanel.repaint();
    }

    public void showRoom() {
        cardLayout.show(mainPanel, "room");
        roomPanel.requestFocusInWindow();
    }

    public void showLobby() {
        cardLayout.show(mainPanel, "lobby");
        client.listRooms();
    }

    public void setCoins(int coins) {
        this.coins = coins;
        coinsLabel.setText("Coins: " + coins);
    }

    public RoomPanel getRoomPanel() { return roomPanel; }

    public void setStatus(String msg) {}
}
