package com.vip.local.cache.handler;

import com.vip.local.cache.proto.CommonLocalCache.CacheCommand;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LocalCachePeerHandler extends SimpleChannelInboundHandler<CacheCommand>{

     @Override  
     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
         ctx.close();  
     }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CacheCommand msg) throws Exception {
	}
}
