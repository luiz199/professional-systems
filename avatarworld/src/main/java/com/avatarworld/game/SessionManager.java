package com.avatarworld.game;

import com.avatarworld.session.PlayerSession;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static SessionManager instance;
    private Map<Integer, PlayerSession> sessionsByUserId = new ConcurrentHashMap<>();
    private Map<ChannelHandlerContext, PlayerSession> sessionsByCtx = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public PlayerSession createSession(ChannelHandlerContext ctx) {
        PlayerSession session = new PlayerSession(ctx);
        sessionsByCtx.put(ctx, session);
        return session;
    }

    public PlayerSession getSession(ChannelHandlerContext ctx) {
        return sessionsByCtx.get(ctx);
    }

    public PlayerSession getSession(int userId) {
        return sessionsByUserId.get(userId);
    }

    public void authenticate(PlayerSession session, int userId) {
        sessionsByUserId.put(userId, session);
    }

    public void removeSession(ChannelHandlerContext ctx) {
        PlayerSession session = sessionsByCtx.remove(ctx);
        if (session != null && session.isAuthenticated()) {
            sessionsByUserId.remove(session.getUserId());
            RoomManager.getInstance().leaveRoom(session);
        }
    }
}
