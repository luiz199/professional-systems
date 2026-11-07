package com.avatarworld.client.ui;

import com.avatarworld.client.model.AvatarData;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class AvatarRenderer {
    private static final int AVATAR_WIDTH = 40;
    private static final int AVATAR_HEIGHT = 60;

    public static void drawAvatar(Graphics2D g, AvatarData data, int x, int y, String direction, String animation) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = x - AVATAR_WIDTH / 2;
        int cy = y - AVATAR_HEIGHT;

        // Shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(cx + 2, y - 6, AVATAR_WIDTH - 4, 8);

        // Legs
        int legW = 10, legH = 18;
        Color legColor = parseColor(data.legsColor);
        g.setColor(legColor);
        g.fillRect(cx + 4, cy + 30, legW, legH);
        g.fillRect(cx + AVATAR_WIDTH - 14, cy + 30, legW, legH);

        if ("dance".equals(animation)) {
            g.fillRect(cx + 4, cy + 32, legW, legH - 4);
            g.fillRect(cx + AVATAR_WIDTH - 14, cy + 28, legW, legH - 4);
        }

        // Shoes
        g.setColor(Color.DARK_GRAY);
        g.fillOval(cx + 3, cy + 46, 12, 6);
        g.fillOval(cx + AVATAR_WIDTH - 15, cy + 46, 12, 6);

        // Torso
        Color torsoColor = parseColor(data.torsoColor);
        g.setColor(torsoColor);
        g.fillRoundRect(cx + 3, cy + 16, AVATAR_WIDTH - 6, 18, 4, 4);

        // Torso detail - collar
        g.setColor(torsoColor.darker());
        g.fillRect(cx + 12, cy + 16, 4, 3);
        g.fillRect(cx + AVATAR_WIDTH - 16, cy + 16, 4, 3);

        // Arms
        g.setColor(torsoColor);
        g.fillRoundRect(cx - 4, cy + 18, 8, 14, 3, 3);
        g.fillRoundRect(cx + AVATAR_WIDTH - 4, cy + 18, 8, 14, 3, 3);

        // Head
        Color headColor = parseColor(data.headColor);
        g.setColor(headColor);
        g.fillOval(cx + 5, cy + 1, AVATAR_WIDTH - 10, 18);

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx + 11, cy + 6, 7, 7);
        g.fillOval(cx + 22, cy + 6, 7, 7);
        g.setColor(new Color(0x4169E1));
        g.fillOval(cx + 13, cy + 8, 4, 4);
        g.fillOval(cx + 24, cy + 8, 4, 4);

        // Mouth
        g.setColor(new Color(0xCC6666));
        g.drawArc(cx + 14, cy + 13, 12, 5, 0, -180);

        // Hair
        if ("short".equals(data.hairStyle)) {
            g.setColor(parseColor(data.hairColor));
            g.fillArc(cx + 4, cy, AVATAR_WIDTH - 8, 12, 0, 180);
        } else if ("long".equals(data.hairStyle)) {
            g.setColor(parseColor(data.hairColor));
            g.fillArc(cx + 4, cy - 1, AVATAR_WIDTH - 8, 14, 0, 180);
            g.fillRect(cx + 4, cy + 8, 6, 12);
            g.fillRect(cx + AVATAR_WIDTH - 10, cy + 8, 6, 12);
        } else if ("mohawk".equals(data.hairStyle)) {
            g.setColor(parseColor(data.hairColor));
            g.fillRect(cx + 16, cy - 5, 8, 18);
        }

        // Hat
        if (!data.hat.isEmpty()) {
            g.setColor(new Color(0x8B4513));
            g.fillRect(cx + 4, cy - 3, AVATAR_WIDTH - 8, 6);
            g.fillRect(cx + 7, cy - 6, AVATAR_WIDTH - 14, 4);
        }

        // Accessory - glasses
        if ("glasses".equals(data.accessory) || "Óculos Escuros".equals(data.accessory)) {
            g.setColor(Color.BLACK);
            g.drawOval(cx + 9, cy + 5, 10, 8);
            g.drawOval(cx + 21, cy + 5, 10, 8);
            g.drawLine(cx + 19, cy + 9, cx + 21, cy + 9);
        } else if ("crown".equals(data.accessory) || "Coroa VIP".equals(data.accessory)) {
            g.setColor(new Color(0xFFD700));
            int[] xPoints = {cx + 8, cx + 12, cx + 15, cx + 18, cx + 22, cx + 26, cx + 30, cx + 32};
            int[] yPoints = {cy - 2, cy + 4, cy, cy + 4, cy - 2, cy + 4, cy - 2, cy + 2};
            g.fillPolygon(xPoints, yPoints, 8);
            g.setColor(new Color(0xFF0000));
            g.fillOval(cx + 14, cy - 1, 4, 4);
            g.fillOval(cx + 22, cy - 1, 4, 4);
            g.fillOval(cx + 18, cy - 3, 4, 4);
        }

        // Username label
        g.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        String name = "";  // Name is drawn externally
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(cx - 10, y + 2, AVATAR_WIDTH + 20, 14, 4, 4);
        g.setColor(Color.WHITE);
    }

    public static Dimension getAvatarSize() {
        return new Dimension(AVATAR_WIDTH + 20, AVATAR_HEIGHT + 20);
    }

    private static Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) return Color.GRAY;
        try {
            return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }
}
