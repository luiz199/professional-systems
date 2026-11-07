package com.avatarworld;

import com.avatarworld.admin.AdminHttpServer;
import com.avatarworld.config.ServerConfig;
import com.avatarworld.db.DatabaseManager;
import com.avatarworld.net.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AvatarWorldServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private AdminHttpServer adminServer;

    public void start() throws Exception {
        ServerConfig config = ServerConfig.getInstance();
        System.out.println("===================================");
        System.out.println("  AvatarWorld Server v1.0.0");
        System.out.println("===================================");

        // Initialize database
        DatabaseManager.getInstance();
        System.out.println("[OK] Database pool initialized");

        // Start game server (WebSocket)
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new NettyServerInitializer());

        int port = config.getInt("server.port");
        ChannelFuture future = bootstrap.bind(port).sync();
        System.out.println("[OK] Game server listening on port " + port);

        // Start admin HTTP server
        adminServer = new AdminHttpServer(config.getInt("admin.port"));
        adminServer.start();

        System.out.println("===================================");
        System.out.println("  Server is running!");
        System.out.println("  Game: ws://localhost:" + port + "/ws");
        System.out.println("  Admin: http://localhost:" + config.get("admin.port"));
        System.out.println("===================================");
    }

    public void stop() {
        System.out.println("\n[Shutdown] Stopping server...");
        if (adminServer != null) adminServer.stop();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
        DatabaseManager.getInstance().close();
        System.out.println("[OK] Server stopped");
    }

    public static void main(String[] args) {
        final AvatarWorldServer server = new AvatarWorldServer();
        try {
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        } catch (Exception e) {
            System.err.println("[FATAL] " + e.getMessage());
            e.printStackTrace();
            server.stop();
            System.exit(1);
        }
    }
}
