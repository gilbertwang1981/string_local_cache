package com.vip.local.cache.init;

import com.vip.local.cache.handler.LocalCacheServerHandler;
import com.vip.local.cache.proto.CommonLocalCache;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class LocalCacheServerInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 5 , 0, 4, 0, 4));
		pipeline.addLast(new ProtobufDecoder(CommonLocalCache.CacheCommand.getDefaultInstance()));
		pipeline.addLast(new LengthFieldPrepender(4));
		pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new LocalCacheServerHandler());
	}
}