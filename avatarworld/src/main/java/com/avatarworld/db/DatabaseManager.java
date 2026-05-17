package com.avatarworld.db;

import com.avatarworld.config.ServerConfig;
import com.avatarworld.model.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;

    private DatabaseManager() {
        ServerConfig cfg = ServerConfig.getInstance();
        HikariConfig hcfg = new HikariConfig();
        hcfg.setJdbcUrl(cfg.getDbUrl());
        hcfg.setUsername(cfg.get("db.user"));
        hcfg.setPassword(cfg.get("db.password"));
        hcfg.setMaximumPoolSize(cfg.getInt("db.pool.size"));
        hcfg.setMinimumIdle(3);
        hcfg.setConnectionTimeout(10000);
        hcfg.setIdleTimeout(30000);
        hcfg.setMaxLifetime(600000);
        hcfg.addDataSourceProperty("cachePrepStmts", "true");
        hcfg.addDataSourceProperty("prepStmtCacheSize", "250");
        hcfg.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource = new HikariDataSource(hcfg);
        System.out.println("[DB] Connection pool initialized: " + cfg.getDbUrl());
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }

    // === USERS ===

    public User authenticate(String username, String passwordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_banned = FALSE";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean createUser(String username, String passwordHash, String email) {
        String sql = "INSERT INTO users (username, password, email, coins, avatar_data) VALUES (?, ?, ?, 1000, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, email);
            ps.setString(4, "{\"headColor\":\"#FFD700\",\"torsoColor\":\"#1E90FF\",\"legsColor\":\"#000000\",\"hairStyle\":\"short\",\"accessory\":\"none\"}");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUserCoins(int userId, int coins) {
        String sql = "UPDATE users SET coins = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coins);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateAvatarData(int userId, String avatarData) {
        String sql = "UPDATE users SET avatar_data = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarData);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateLastDaily(int userId) {
        String sql = "UPDATE users SET last_daily = NOW() WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean banUser(int userId, boolean banned) {
        String sql = "UPDATE users SET is_banned = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, banned);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean muteUser(int userId, boolean muted) {
        String sql = "UPDATE users SET is_muted = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, muted);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // === ITEMS ===

    public Item getItemById(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapItem(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY type, price";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapItem(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Item> getShopItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE is_furniture = FALSE ORDER BY type, price";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapItem(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Item> getFurnitureShopItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE is_furniture = TRUE ORDER BY price";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapItem(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean createItem(String name, String type, String category, int price, String rarity, String data, boolean isFurniture) {
        String sql = "INSERT INTO items (name, type, category, price, rarity, data, is_furniture) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, category);
            ps.setInt(4, price);
            ps.setString(5, rarity);
            ps.setString(6, data);
            ps.setBoolean(7, isFurniture);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateItem(int id, String name, String type, String category, int price, String rarity, String data) {
        String sql = "UPDATE items SET name=?, type=?, category=?, price=?, rarity=?, data=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, category);
            ps.setInt(4, price);
            ps.setString(5, rarity);
            ps.setString(6, data);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteItem(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // === INVENTORY ===

    public List<Item> getUserInventory(int userId) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.*, inv.is_equipped FROM inventory inv JOIN items i ON inv.item_id = i.id WHERE inv.user_id = ? ORDER BY i.type";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapItem(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean hasItem(int userId, int itemId) {
        String sql = "SELECT COUNT(*) FROM inventory WHERE user_id = ? AND item_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean addItemToInventory(int userId, int itemId) {
        String sql = "INSERT INTO inventory (user_id, item_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean equipItem(int userId, int itemId, boolean equip) {
        String sql = "UPDATE inventory SET is_equipped = ? WHERE user_id = ? AND item_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, equip);
            ps.setInt(2, userId);
            ps.setInt(3, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Item> getEquippedItems(int userId) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT i.* FROM inventory inv JOIN items i ON inv.item_id = i.id WHERE inv.user_id = ? AND inv.is_equipped = TRUE";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapItem(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // === ROOMS ===

    public Room createRoom(String name, int ownerId, String layoutData) {
        String sql = "INSERT INTO rooms (name, owner_id, layout_data) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, ownerId);
            ps.setString(3, layoutData);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Room room = new Room(rs.getInt(1), name, ownerId);
                    room.setLayoutData(layoutData);
                    return room;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRoom(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY id";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRoom(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // === ROOM FURNITURE ===

    public List<Furniture> getRoomFurniture(int roomId) {
        List<Furniture> list = new ArrayList<>();
        String sql = "SELECT rf.*, i.name as item_name, i.data as item_data FROM room_furniture rf JOIN items i ON rf.item_id = i.id WHERE rf.room_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFurniture(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Furniture addFurniture(int roomId, int itemId, int x, int y, int rotation) {
        String sql = "INSERT INTO room_furniture (room_id, item_id, x, y, rotation) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, roomId);
            ps.setInt(2, itemId);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setInt(5, rotation);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Furniture f = new Furniture(roomId, itemId, x, y);
                    f.setId(rs.getInt(1));
                    f.setRotation(rotation);
                    Item item = getItemById(itemId);
                    if (item != null) {
                        f.setItemName(item.getName());
                        f.setItemData(item.getData());
                    }
                    return f;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateFurniture(int id, int x, int y, int rotation) {
        String sql = "UPDATE room_furniture SET x=?, y=?, rotation=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, x); ps.setInt(2, y); ps.setInt(3, rotation); ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removeFurniture(int id) {
        String sql = "DELETE FROM room_furniture WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // === TRANSACTIONS ===

    public boolean addTransaction(Integer fromId, Integer toId, Integer itemId, int amount, String type) {
        String sql = "INSERT INTO transactions (from_user_id, to_user_id, item_id, amount, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (fromId != null) ps.setInt(1, fromId); else ps.setNull(1, Types.INTEGER);
            if (toId != null) ps.setInt(2, toId); else ps.setNull(2, Types.INTEGER);
            if (itemId != null) ps.setInt(3, itemId); else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, amount);
            ps.setString(5, type);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<com.avatarworld.model.Transaction> getUserTransactions(int userId) {
        List<com.avatarworld.model.Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_user_id = ? OR to_user_id = ? ORDER BY created_at DESC LIMIT 50";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.avatarworld.model.Transaction t = new com.avatarworld.model.Transaction();
                    t.setId(rs.getInt("id"));
                    t.setFromUserId(rs.getObject("from_user_id") != null ? rs.getInt("from_user_id") : null);
                    t.setToUserId(rs.getObject("to_user_id") != null ? rs.getInt("to_user_id") : null);
                    t.setItemId(rs.getObject("item_id") != null ? rs.getInt("item_id") : null);
                    t.setAmount(rs.getInt("amount"));
                    t.setType(rs.getString("type"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // === LOGS ===

    public void addLog(String action, String details, String ip) {
        String sql = "INSERT INTO server_logs (action, details, ip_address) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, details);
            ps.setString(3, ip);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String[]> getLogs(int limit) {
        List<String[]> logs = new ArrayList<>();
        String sql = "SELECT * FROM server_logs ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[]{
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getString("ip_address"),
                        rs.getTimestamp("created_at").toString()
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    // === MAPPER HELPERS ===

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setCoins(rs.getInt("coins"));
        u.setAvatarData(rs.getString("avatar_data"));
        u.setAdmin(rs.getBoolean("is_admin"));
        u.setBanned(rs.getBoolean("is_banned"));
        u.setMuted(rs.getBoolean("is_muted"));
        u.setLastDaily(rs.getTimestamp("last_daily"));
        return u;
    }

    private Item mapItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setType(rs.getString("type"));
        item.setCategory(rs.getString("category"));
        item.setPrice(rs.getInt("price"));
        item.setRarity(rs.getString("rarity"));
        item.setData(rs.getString("data"));
        item.setFurniture(rs.getBoolean("is_furniture"));
        return item;
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setName(rs.getString("name"));
        room.setOwnerId(rs.getInt("owner_id"));
        room.setPassword(rs.getString("password"));
        room.setMaxUsers(rs.getInt("max_users"));
        room.setLayoutData(rs.getString("layout_data"));
        room.setWallpaper(rs.getString("wallpaper"));
        room.setFloor(rs.getString("floor"));
        return room;
    }

    private Furniture mapFurniture(ResultSet rs) throws SQLException {
        Furniture f = new Furniture();
        f.setId(rs.getInt("id"));
        f.setRoomId(rs.getInt("room_id"));
        f.setItemId(rs.getInt("item_id"));
        f.setX(rs.getInt("x"));
        f.setY(rs.getInt("y"));
        f.setRotation(rs.getInt("rotation"));
        f.setItemName(rs.getString("item_name"));
        f.setItemData(rs.getString("item_data"));
        return f;
    }
}
