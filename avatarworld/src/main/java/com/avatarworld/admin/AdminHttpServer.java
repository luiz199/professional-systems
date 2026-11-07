package com.avatarworld.admin;

import com.avatarworld.db.DatabaseManager;
import com.avatarworld.model.Item;
import com.avatarworld.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminHttpServer {
    private HttpServer server;
    private DatabaseManager db = DatabaseManager.getInstance();
    private Gson gson = new Gson();
    private int port;

    public AdminHttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/admin", new AdminAPIHandler());
        server.createContext("/api/stats", new StatsHandler());
        server.createContext("/api/users", new UsersAPIHandler());
        server.createContext("/api/items", new ItemsAPIHandler());
        server.createContext("/api/rooms", new RoomsAPIHandler());
        server.createContext("/api/logs", new LogsAPIHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("[Admin] HTTP server started on port " + port);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
            return bos.toString(StandardCharsets.UTF_8);
        }
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        JsonObject err = new JsonObject();
        err.addProperty("error", message);
        sendJson(exchange, code, err.toString());
    }

    class AdminAPIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }
            try {
                String body = readBody(exchange);
                JsonObject req = gson.fromJson(body, JsonObject.class);
                String action = req.get("action").getAsString();
                JsonObject resp = new JsonObject();

                switch (action) {
                    case "login": {
                        String username = req.get("username").getAsString();
                        String password = req.get("password").getAsString();
                        User user = db.authenticate(username, password);
                        if (user != null && user.isAdmin()) {
                            resp.addProperty("success", true);
                            resp.addProperty("token", "admin-" + user.getId() + "-" + System.currentTimeMillis());
                            resp.addProperty("username", user.getUsername());
                        } else {
                            resp.addProperty("success", false);
                            resp.addProperty("error", "Invalid admin credentials");
                        }
                        break;
                    }
                    default:
                        resp.addProperty("success", false);
                        resp.addProperty("error", "Unknown action");
                }
                sendJson(exchange, 200, resp.toString());
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                JsonObject stats = new JsonObject();
                stats.addProperty("totalUsers", db.getAllUsers().size());
                stats.addProperty("totalRooms", db.getAllRooms().size());
                stats.addProperty("totalItems", db.getAllItems().size());
                sendJson(exchange, 200, stats.toString());
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class UsersAPIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equals(method)) {
                    List<User> users = db.getAllUsers();
                    JsonArray arr = new JsonArray();
                    for (User u : users) {
                        JsonObject uj = new JsonObject();
                        uj.addProperty("id", u.getId());
                        uj.addProperty("username", u.getUsername());
                        uj.addProperty("coins", u.getCoins());
                        uj.addProperty("isAdmin", u.isAdmin());
                        uj.addProperty("isBanned", u.isBanned());
                        uj.addProperty("isMuted", u.isMuted());
                        arr.add(uj);
                    }
                    JsonObject resp = new JsonObject();
                    resp.add("users", arr);
                    sendJson(exchange, 200, resp.toString());
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    JsonObject req = gson.fromJson(body, JsonObject.class);
                    String action = req.get("action").getAsString();
                    int userId = req.get("userId").getAsInt();
                    boolean success = false;
                    switch (action) {
                        case "ban":
                            success = db.banUser(userId, true);
                            db.addLog("ADMIN_BAN", "User " + userId + " banned via API", "admin");
                            break;
                        case "unban":
                            success = db.banUser(userId, false);
                            break;
                        case "mute":
                            success = db.muteUser(userId, true);
                            break;
                        case "unmute":
                            success = db.muteUser(userId, false);
                            break;
                    }
                    JsonObject resp = new JsonObject();
                    resp.addProperty("success", success);
                    sendJson(exchange, 200, resp.toString());
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class ItemsAPIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equals(method)) {
                    List<Item> items = db.getAllItems();
                    JsonArray arr = new JsonArray();
                    for (Item item : items) {
                        JsonObject ij = new JsonObject();
                        ij.addProperty("id", item.getId());
                        ij.addProperty("name", item.getName());
                        ij.addProperty("type", item.getType());
                        ij.addProperty("category", item.getCategory());
                        ij.addProperty("price", item.getPrice());
                        ij.addProperty("rarity", item.getRarity());
                        ij.addProperty("isFurniture", item.isFurniture());
                        arr.add(ij);
                    }
                    JsonObject resp = new JsonObject();
                    resp.add("items", arr);
                    sendJson(exchange, 200, resp.toString());
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    JsonObject req = gson.fromJson(body, JsonObject.class);
                    String action = req.get("action").getAsString();
                    boolean success = false;
                    switch (action) {
                        case "create":
                            success = db.createItem(
                                req.get("name").getAsString(),
                                req.get("type").getAsString(),
                                req.get("category").getAsString(),
                                req.get("price").getAsInt(),
                                req.get("rarity").getAsString(),
                                req.has("data") ? req.get("data").getAsString() : null,
                                req.get("isFurniture").getAsBoolean()
                            );
                            break;
                        case "update":
                            success = db.updateItem(
                                req.get("id").getAsInt(),
                                req.get("name").getAsString(),
                                req.get("type").getAsString(),
                                req.get("category").getAsString(),
                                req.get("price").getAsInt(),
                                req.get("rarity").getAsString(),
                                req.has("data") ? req.get("data").getAsString() : null
                            );
                            break;
                        case "delete":
                            success = db.deleteItem(req.get("id").getAsInt());
                            break;
                    }
                    JsonObject resp = new JsonObject();
                    resp.addProperty("success", success);
                    sendJson(exchange, 200, resp.toString());
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class RoomsAPIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    List<com.avatarworld.model.Room> rooms = db.getAllRooms();
                    JsonArray arr = new JsonArray();
                    for (com.avatarworld.model.Room r : rooms) {
                        JsonObject rj = new JsonObject();
                        rj.addProperty("id", r.getId());
                        rj.addProperty("name", r.getName());
                        rj.addProperty("ownerId", r.getOwnerId());
                        rj.addProperty("maxUsers", r.getMaxUsers());
                        User owner = db.getUserById(r.getOwnerId());
                        rj.addProperty("ownerName", owner != null ? owner.getUsername() : "Unknown");
                        arr.add(rj);
                    }
                    JsonObject resp = new JsonObject();
                    resp.add("rooms", arr);
                    sendJson(exchange, 200, resp.toString());
                } else if ("POST".equals(exchange.getRequestMethod())) {
                    String body = readBody(exchange);
                    JsonObject req = gson.fromJson(body, JsonObject.class);
                    boolean success = false;
                    if (req.has("action") && req.get("action").getAsString().equals("delete")) {
                        success = db.deleteRoom(req.get("id").getAsInt());
                    }
                    JsonObject resp = new JsonObject();
                    resp.addProperty("success", success);
                    sendJson(exchange, 200, resp.toString());
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class LogsAPIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                List<String[]> logs = db.getLogs(100);
                JsonArray arr = new JsonArray();
                for (String[] log : logs) {
                    JsonObject lj = new JsonObject();
                    lj.addProperty("action", log[0]);
                    lj.addProperty("details", log[1]);
                    lj.addProperty("ip", log[2]);
                    lj.addProperty("date", log[3]);
                    arr.add(lj);
                }
                JsonObject resp = new JsonObject();
                resp.add("logs", arr);
                sendJson(exchange, 200, resp.toString());
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    class StaticFileHandler implements HttpHandler {
        private final String webRoot = "webadmin";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/admin.html";
            File file = new File(webRoot + path);
            if (file.exists() && !file.isDirectory()) {
                String mime = "text/html";
                if (path.endsWith(".css")) mime = "text/css";
                else if (path.endsWith(".js")) mime = "application/javascript";
                else if (path.endsWith(".png")) mime = "image/png";
                else if (path.endsWith(".ico")) mime = "image/x-icon";
                exchange.getResponseHeaders().set("Content-Type", mime);
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody();
                     FileInputStream fs = new FileInputStream(file)) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = fs.read(buf)) != -1) os.write(buf, 0, n);
                }
            } else {
                String resp = "{\"error\":\"Not found\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(404, resp.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(resp.getBytes());
                }
            }
        }
    }
}
