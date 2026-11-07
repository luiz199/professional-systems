package com.avatarworld.session;

import com.avatarworld.model.User;
import io.netty.channel.ChannelHandlerContext;

public class PlayerSession {
    private ChannelHandlerContext ctx;
    private User user;
    private int currentRoomId;
    private boolean authenticated;
    private String ipAddress;

    public PlayerSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.authenticated = false;
        this.currentRoomId = -1;
        this.ipAddress = ctx.channel().remoteAddress().toString();
    }

    public ChannelHandlerContext getCtx() { return ctx; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getUserId() { return user != null ? user.getId() : -1; }
    public String getUsername() { return user != null ? user.getUsername() : "???"; }

    public int getCurrentRoomId() { return currentRoomId; }
    public void setCurrentRoomId(int roomId) { this.currentRoomId = roomId; }
    public boolean isInRoom() { return currentRoomId > 0; }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public String getIpAddress() { return ipAddress; }

    public void send(String message) {
        if (ctx != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(message);
        }
    }

    public void disconnect() {
        if (ctx != null && ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
