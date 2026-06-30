package com.avatarworld.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());

        WebSocketServerProtocolConfig wsConfig = WebSocketServerProtocolConfig.newBuilder()
            .websocketPath("/ws")
            .subprotocols(null)
            .maxFramePayloadLength(65536)
            .build();
        pipeline.addLast(new WebSocketServerProtocolHandler(wsConfig));
        pipeline.addLast(new GameServerHandler());
    }
}
