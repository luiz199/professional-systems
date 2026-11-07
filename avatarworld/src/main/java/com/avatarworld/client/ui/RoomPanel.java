package com.avatarworld.client.ui;

import com.avatarworld.client.model.AvatarData;
import com.avatarworld.client.network.GameClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RoomPanel extends JPanel {
    private GameClient client;
    private int userId;
    private String username;
    private int roomId = -1;
    private int gridWidth = 30;
    private int gridHeight = 20;
    private int tileSize = 32;

    private Map<Integer, RoomAvatar> avatars = new ConcurrentHashMap<>();
    private Map<Integer, RoomFurnitureItem> furnitureItems = new ConcurrentHashMap<>();
    private List<ChatBubble> chatBubbles = new ArrayList<>();

    private int myX = 5, myY = 5;
    private String myDirection = "down";
    private boolean isDancing = false;
    private javax.swing.Timer danceTimer;
    private JTextField chatInput;
    private JPanel chatPanel;
    private JTextArea chatLog;
    private boolean inRoom = false;

    public RoomPanel(GameClient client, int userId, String username) {
        this.client = client;
        this.userId = userId;
        this.username = username;
        setLayout(new BorderLayout());
        setBackground(new Color(0x1a1a2e));
        setFocusable(true);

        // Chat panel at bottom
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(0x16213E));
        chatPanel.setPreferredSize(new Dimension(300, 150));

        chatLog = new JTextArea();
        chatLog.setEditable(false);
        chatLog.setBackground(new Color(0x0F3460));
        chatLog.setForeground(Color.WHITE);
        chatLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatLog.setLineWrap(true);
        chatLog.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatLog);
        chatScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0x3498DB)),
            "Chat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12), new Color(0x3498DB)
        ));

        chatInput = new JTextField();
        chatInput.setBackground(new Color(0x16213E));
        chatInput.setForeground(Color.WHITE);
        chatInput.setCaretColor(Color.WHITE);
        chatInput.setFont(new Font("Arial", Font.PLAIN, 13));
        chatInput.addActionListener(e -> sendChat());
        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    moveAvatar(0, -1, "up");
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveAvatar(0, 1, "down");
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveAvatar(-1, 0, "left");
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveAvatar(1, 0, "right");
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    toggleDance();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    leaveRoom();
                }
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);

        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(0x3498DB));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(e -> sendChat());
        inputPanel.add(sendBtn, BorderLayout.EAST);

        JButton leaveBtn = new JButton("Leave");
        leaveBtn.setBackground(new Color(0xE74C3C));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.addActionListener(e -> leaveRoom());
        inputPanel.add(leaveBtn, BorderLayout.WEST);

        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.EAST);

        // Timer for repainting
        new javax.swing.Timer(50, e -> repaint()).start();

        // Click listener for movement
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int gx = e.getX() / tileSize;
                int gy = e.getY() / tileSize;
                if (gx >= 0 && gx < gridWidth && gy >= 0 && gy < gridHeight) {
                    moveAvatarTo(gx, gy);
                }
            }
        });
    }

    public void joinRoom(JsonObject data) {
        roomId = data.get("roomId").getAsInt();
        inRoom = true;
        avatars.clear();
        furnitureItems.clear();
        chatBubbles.clear();
        chatLog.setText("");

        // Parse layout
        if (data.has("layoutData")) {
            try {
                JsonObject layout = JsonParser.parseString(data.get("layoutData").getAsString()).getAsJsonObject();
                if (layout.has("width")) gridWidth = layout.get("width").getAsInt();
                if (layout.has("height")) gridHeight = layout.get("height").getAsInt();
            } catch (Exception ignored) {}
        }

        // Add users
        if (data.has("users")) {
            JsonArray users = data.getAsJsonArray("users");
            for (int i = 0; i < users.size(); i++) {
                JsonObject u = users.get(i).getAsJsonObject();
                RoomAvatar ra = new RoomAvatar();
                ra.userId = u.get("userId").getAsInt();
                ra.username = u.get("username").getAsString();
                ra.x = u.get("x").getAsInt();
                ra.y = u.get("y").getAsInt();
                ra.direction = u.get("direction").getAsString();
                ra.animation = u.get("animation").getAsString();
                ra.avatarData = AvatarData.fromJson(u.get("avatarData").getAsString());
                avatars.put(ra.userId, ra);
                if (ra.userId == userId) {
                    myX = ra.x;
                    myY = ra.y;
                }
            }
        }

        // Add furniture
        if (data.has("furniture")) {
            JsonArray furn = data.getAsJsonArray("furniture");
            for (int i = 0; i < furn.size(); i++) {
                JsonObject f = furn.get(i).getAsJsonObject();
                RoomFurnitureItem rfi = new RoomFurnitureItem();
                rfi.id = f.get("id").getAsInt();
                rfi.itemId = f.get("itemId").getAsInt();
                rfi.name = f.get("itemName").getAsString();
                rfi.itemData = f.has("itemData") ? f.get("itemData").getAsString() : "{}";
                rfi.x = f.get("x").getAsInt();
                rfi.y = f.get("y").getAsInt();
                rfi.rotation = f.get("rotation").getAsInt();
                furnitureItems.put(rfi.id, rfi);
            }
        }

        addChatMessage("SYSTEM", "You entered the room", new Color(0x2ECC71));
        repaint();
    }

    public void leaveRoom() {
        if (!inRoom) return;
        inRoom = false;
        client.leaveRoom();
        avatars.clear();
        furnitureItems.clear();
        chatBubbles.clear();
        roomId = -1;

        // Go back to lobby
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).showLobby();
        }
    }

    public void onUserJoin(int uid, String uname, String avatarJson) {
        RoomAvatar ra = new RoomAvatar();
        ra.userId = uid;
        ra.username = uname;
        ra.x = 5;
        ra.y = 5;
        ra.avatarData = AvatarData.fromJson(avatarJson);
        avatars.put(uid, ra);
        addChatMessage("SYSTEM", uname + " entered the room", new Color(0x3498DB));
    }

    public void onUserLeave(int uid) {
        RoomAvatar ra = avatars.remove(uid);
        if (ra != null) {
            addChatMessage("SYSTEM", ra.username + " left the room", new Color(0xE74C3C));
        }
    }

    public void onUserMove(int uid, int x, int y, String direction) {
        RoomAvatar ra = avatars.get(uid);
        if (ra != null) {
            ra.x = x;
            ra.y = y;
            if (direction != null) ra.direction = direction;
        }
    }

    public void onUserAnimate(int uid, String animation) {
        RoomAvatar ra = avatars.get(uid);
        if (ra != null) {
            ra.animation = animation;
        }
    }

    public void onAvatarUpdate(int uid, String avatarJson) {
        RoomAvatar ra = avatars.get(uid);
        if (ra != null) {
            ra.avatarData = AvatarData.fromJson(avatarJson);
        }
    }

    public void onChatMessage(int uid, String uname, String message) {
        addChatMessage(uname, message, Color.WHITE);
        // Add chat bubble
        RoomAvatar ra = avatars.get(uid);
        if (ra != null) {
            ChatBubble bubble = new ChatBubble(message, System.currentTimeMillis());
            chatBubbles.add(bubble);
            ra.chatBubble = bubble;
        }
    }

    public void onFurniturePlaced(JsonObject data) {
        RoomFurnitureItem rfi = new RoomFurnitureItem();
        rfi.id = data.get("id").getAsInt();
        rfi.itemId = data.get("itemId").getAsInt();
        rfi.x = data.get("x").getAsInt();
        rfi.y = data.get("y").getAsInt();
        rfi.rotation = data.get("rotation").getAsInt();
        // Try to find name from existing items
        rfi.name = "Furniture";
        furnitureItems.put(rfi.id, rfi);
    }

    public void onFurnitureMoved(JsonObject data) {
        int fid = data.get("furnitureId").getAsInt();
        RoomFurnitureItem rfi = furnitureItems.get(fid);
        if (rfi != null) {
            rfi.x = data.get("x").getAsInt();
            rfi.y = data.get("y").getAsInt();
            rfi.rotation = data.get("rotation").getAsInt();
        }
    }

    public void onFurnitureRemoved(int fid) {
        furnitureItems.remove(fid);
    }

    private void addChatMessage(String sender, String message, Color color) {
        String line = "[" + sender + "] " + message;
        chatLog.append(line + "\n");
        chatLog.setCaretPosition(chatLog.getDocument().getLength());
        // Limit chat lines
        if (chatLog.getLineCount() > 100) {
            try {
                int end = chatLog.getLineEndOffset(0);
                chatLog.replaceRange("", 0, end);
            } catch (Exception ignored) {}
        }
    }

    private void sendChat() {
        String text = chatInput.getText().trim();
        if (!text.isEmpty() && inRoom) {
            client.sendChat(text);
            chatInput.setText("");
        }
    }

    private void moveAvatar(int dx, int dy, String dir) {
        if (!inRoom) return;
        int nx = myX + dx;
        int ny = myY + dy;
        if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight) {
            myX = nx;
            myY = ny;
            myDirection = dir;
            client.moveAvatar(nx, ny, dir);
        }
    }

    private void moveAvatarTo(int gx, int gy) {
        if (!inRoom) return;
        client.moveAvatar(gx, gy, myDirection);
        // Smooth movement will be handled by server response
    }

    private void toggleDance() {
        if (!inRoom) return;
        isDancing = !isDancing;
        if (isDancing) {
            client.sendDance("dance");
            if (danceTimer == null) {
                danceTimer = new javax.swing.Timer(500, e -> repaint());
                danceTimer.start();
            }
        } else {
            client.sendDance("idle");
            if (danceTimer != null) {
                danceTimer.stop();
                danceTimer = null;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!inRoom) {
            g2d.setColor(new Color(0x1a1a2e));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String msg = "Click a room to enter";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            return;
        }

        drawRoom(g2d);
        drawGrid(g2d);
        drawFurniture(g2d);
        drawAvatars(g2d);
        drawChatBubbles(g2d);
    }

    private void drawRoom(Graphics2D g) {
        // Floor
        g.setColor(new Color(0x2d2d44));
        g.fillRect(0, 0, gridWidth * tileSize, gridHeight * tileSize);

        // Floor pattern
        g.setColor(new Color(0x3d3d55));
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if ((x + y) % 2 == 0) {
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 25));
        for (int x = 0; x <= gridWidth; x++) {
            g.drawLine(x * tileSize, 0, x * tileSize, gridHeight * tileSize);
        }
        for (int y = 0; y <= gridHeight; y++) {
            g.drawLine(0, y * tileSize, gridWidth * tileSize, y * tileSize);
        }
    }

    private void drawFurniture(Graphics2D g) {
        for (RoomFurnitureItem f : furnitureItems.values()) {
            int px = f.x * tileSize;
            int py = f.y * tileSize;
            int w = tileSize * 2;
            int h = tileSize;

            // Parse color from itemData
            Color furnColor = new Color(0x8B4513);
            try {
                JsonObject d = JsonParser.parseString(f.itemData).getAsJsonObject();
                if (d.has("color")) furnColor = Color.decode(d.get("color").getAsString());
                if (d.has("width")) w = tileSize * d.get("width").getAsInt();
                if (d.has("height")) h = tileSize * d.get("height").getAsInt();
            } catch (Exception ignored) {}

            // Draw rotated
            if (f.rotation == 90 || f.rotation == 270) {
                int tmp = w; w = h; h = tmp;
            }

            g.setColor(furnColor);
            g.fillRoundRect(px, py, w, h, 6, 6);
            g.setColor(furnColor.darker());
            g.drawRoundRect(px, py, w, h, 6, 6);

            // Highlight
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRoundRect(px + 2, py + 2, w - 4, h / 3, 4, 4);

            // Label
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            FontMetrics fm = g.getFontMetrics();
            String label = f.name.length() > 12 ? f.name.substring(0, 12) + ".." : f.name;
            int lw = fm.stringWidth(label);
            g.drawString(label, px + (w - lw) / 2, py + h / 2 + 4);
        }
    }

    private void drawAvatars(Graphics2D g) {
        List<RoomAvatar> sorted = new ArrayList<>(avatars.values());
        sorted.sort((a, b) -> Integer.compare(a.y, b.y));

        for (RoomAvatar ra : sorted) {
            int ax = ra.x * tileSize + tileSize / 2;
            int ay = ra.y * tileSize + tileSize;

            // Draw name
            g.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();
            String name = ra.username;
            int nw = fm.stringWidth(name);
            g.setColor(new Color(0, 0, 0, 140));
            g.fillRoundRect(ax - nw / 2 - 3, ay - 68, nw + 6, 14, 4, 4);
            g.setColor(ra.userId == userId ? new Color(0x2ECC71) : Color.WHITE);
            g.drawString(name, ax - nw / 2, ay - 57);

            // Draw avatar
            AvatarRenderer.drawAvatar(g, ra.avatarData, ax, ay - 8, ra.direction, ra.animation);
        }
    }

    private void drawChatBubbles(Graphics2D g) {
        long now = System.currentTimeMillis();
        chatBubbles.removeIf(cb -> now - cb.timestamp > 5000);

        for (RoomAvatar ra : avatars.values()) {
            if (ra.chatBubble != null) {
                int ax = ra.x * tileSize + tileSize / 2;
                int ay = ra.y * tileSize - 10;
                String text = ra.chatBubble.message;
                g.setFont(new Font("Arial", Font.PLAIN, 11));
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth(text);
                int bw = Math.max(tw + 20, 40);

                // Bubble background
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRoundRect(ax - bw / 2, ay - 90, bw, 22, 8, 8);
                g.setColor(Color.BLACK);
                g.drawRoundRect(ax - bw / 2, ay - 90, bw, 22, 8, 8);

                // Text
                g.setColor(Color.BLACK);
                g.drawString(text, ax - tw / 2, ay - 73);

                if (now - ra.chatBubble.timestamp > 4000) {
                    ra.chatBubble = null;
                }
            }
        }
    }

    public boolean isInRoom() { return inRoom; }

    static class RoomAvatar {
        int userId;
        String username;
        int x, y;
        String direction = "down";
        String animation = "idle";
        AvatarData avatarData = new AvatarData();
        ChatBubble chatBubble;
    }

    static class ChatBubble {
        String message;
        long timestamp;
        ChatBubble(String msg, long ts) { message = msg; timestamp = ts; }
    }

    static class RoomFurnitureItem {
        int id, itemId, x, y, rotation;
        String name, itemData;
    }
}
