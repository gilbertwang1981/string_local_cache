package com.vip.local.cache.init;

import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.handler.LocalCacheServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class LocalCacheServerInitializer extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast("framer", 
				new DelimiterBasedFrameDecoder(
				new Integer(LocalCacheConst.LOCAL_CACHE_MAX_FRAME_SIZE.getDefinition()) ,
                Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        
        pipeline.addLast("handler", new LocalCacheServerHandler());
	}
}