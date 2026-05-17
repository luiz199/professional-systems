package com.avatarworld.config;

import java.io.*;
import java.util.Properties;

public class ServerConfig {
    private static ServerConfig instance;
    private Properties props;

    private ServerConfig() {
        props = new Properties();
        props.setProperty("server.host", "0.0.0.0");
        props.setProperty("server.port", "8080");
        props.setProperty("admin.port", "8081");
        props.setProperty("db.host", "localhost");
        props.setProperty("db.port", "3306");
        props.setProperty("db.name", "avatarworld");
        props.setProperty("db.user", "root");
        props.setProperty("db.password", "");
        props.setProperty("db.pool.size", "10");
        props.setProperty("daily.coins", "200");
        props.setProperty("chat.filter.enabled", "true");
        load();
    }

    public static synchronized ServerConfig getInstance() {
        if (instance == null) instance = new ServerConfig();
        return instance;
    }

    private void load() {
        try (InputStream in = new FileInputStream("server.properties")) {
            props.load(in);
            System.out.println("[Config] Loaded server.properties");
        } catch (IOException e) {
            System.out.println("[Config] No server.properties found, using defaults");
            save();
        }
    }

    public void save() {
        try (OutputStream out = new FileOutputStream("server.properties")) {
            props.store(out, "AvatarWorld Server Configuration");
        } catch (IOException e) {
            System.err.println("[Config] Failed to save: " + e.getMessage());
        }
    }

    public String get(String key) { return props.getProperty(key); }
    public int getInt(String key) { return Integer.parseInt(props.getProperty(key)); }

    public String getDbUrl() {
        return "jdbc:mysql://" + get("db.host") + ":" + get("db.port") + "/" + get("db.name")
                + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    }
}
