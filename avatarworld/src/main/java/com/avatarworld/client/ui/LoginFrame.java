package com.avatarworld.client.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private Consumer<String[]> callback;
    private boolean dragging;
    private int dragX, dragY;

    public LoginFrame(Consumer<String[]> callback) {
        this.callback = callback;
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 440, 520, 20, 20));
        initUI();
        setSize(440, 520);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        JPanel main = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1a0a2e), w, h, new Color(0x16213e));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
                // decorative circles
                g2.setColor(new Color(79, 195, 247, 30));
                g2.fillOval(-80, -80, 200, 200);
                g2.setColor(new Color(171, 71, 188, 25));
                g2.fillOval(w - 120, h - 120, 200, 200);
                g2.setColor(new Color(79, 195, 247, 15));
                g2.fillOval(280, -40, 120, 120);
            }
        };
        main.setLayout(new BorderLayout());
        main.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(79, 195, 247, 80), 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Drag support
        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragging = true; dragX = e.getX(); dragY = e.getY(); }
            public void mouseReleased(MouseEvent e) { dragging = false; }
            public void mouseDragged(MouseEvent e) {
                if (dragging) setLocation(getX() + e.getX() - dragX, getY() + e.getY() - dragY);
            }
        };
        main.addMouseListener(ma);
        main.addMouseMotionListener(ma);

        // === TOP: Title + Close ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 10));

        JLabel closeBtn = new JLabel("✕", SwingConstants.CENTER);
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeBtn.setForeground(new Color(0x8888aa));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(new Color(0xef5350)); }
            public void mouseExited(MouseEvent e) { closeBtn.setForeground(new Color(0x8888aa)); }
        });
        topBar.add(closeBtn, BorderLayout.EAST);

        main.add(topBar, BorderLayout.NORTH);

        // === CENTER: Logo + Form ===
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // Logo
        JLabel icon = new JLabel("🌍", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(icon);

        JLabel title = new JLabel("AvatarWorld", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0x4fc3f7));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        center.add(title);

        JLabel subtitle = new JLabel("Faça login para entrar no mundo", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(0x8888aa));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        center.add(subtitle);

        // Username field
        usernameField = createStyledField("Nome de usuário");
        center.add(usernameField);
        center.add(Box.createVerticalStrut(12));

        // Password field
        passwordField = createStyledPasswordField("Senha");
        center.add(passwordField);
        center.add(Box.createVerticalStrut(8));

        // Status
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        center.add(statusLabel);

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(400, 42));

        loginButton = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setPaint(new GradientPaint(0,0,new Color(0x4fc3f7),getWidth(),0,new Color(0x29b6f6)));
                else g2.setPaint(new GradientPaint(0,0,new Color(0x29b6f6),getWidth(),0,new Color(0x0288d1)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth()-fm.stringWidth(t))/2, (getHeight()+fm.getAscent())/2-2);
            }
        };
        loginButton.setPreferredSize(new Dimension(120, 42));
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        btnRow.add(loginButton);

        registerButton = new JButton("Criar Conta") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setPaint(new GradientPaint(0,0,new Color(0xab47bc),getWidth(),0,new Color(0x9c27b0)));
                else g2.setPaint(new GradientPaint(0,0,new Color(0x7b1fa2),getWidth(),0,new Color(0x6a1b9a)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth()-fm.stringWidth(t))/2, (getHeight()+fm.getAscent())/2-2);
            }
        };
        registerButton.setPreferredSize(new Dimension(120, 42));
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> doRegister());
        btnRow.add(registerButton);

        center.add(btnRow);
        center.add(Box.createVerticalStrut(10));

        // Version
        JLabel ver = new JLabel("v1.0.0 - Conecte-se e explore!", SwingConstants.CENTER);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(new Color(0x666688));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(ver);

        main.add(center, BorderLayout.CENTER);

        add(main);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a1a3e));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x2a2a5e));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0x4fc3f7));
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setMaximumSize(new Dimension(340, 42));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a1a3e));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x2a2a5e));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(0x4fc3f7));
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setMaximumSize(new Dimension(340, 42));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setEchoChar('●');
        return field;
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setForeground(new Color(0xef5350));
            statusLabel.setText("Preencha todos os campos!");
            return;
        }
        statusLabel.setForeground(new Color(0x66bb6a));
        statusLabel.setText("Conectando...");
        setEnabled(false);
        callback.accept(new String[]{"login", user, pass});
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setForeground(new Color(0xef5350));
            statusLabel.setText("Preencha todos os campos!");
            return;
        }
        if (pass.length() < 4) {
            statusLabel.setForeground(new Color(0xef5350));
            statusLabel.setText("A senha deve ter pelo menos 4 caracteres!");
            return;
        }
        statusLabel.setForeground(new Color(0x66bb6a));
        statusLabel.setText("Conectando...");
        setEnabled(false);
        callback.accept(new String[]{"register", user, pass});
    }

    public void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setForeground(error ? new Color(0xef5350) : new Color(0x66bb6a));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }
}
