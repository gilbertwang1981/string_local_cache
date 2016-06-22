package com.vip.local.cache.handler;

import java.net.InetAddress;
import java.util.List;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.util.CommandCoder;
import com.vip.local.cache.util.LocalCacheUtil;
import com.vip.local.cache.worker.LocalCacheCommandWorker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LocalCacheServerHandler extends SimpleChannelInboundHandler<String> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		LocalCacheCmdType type = CommandCoder.decodeCommand(msg);
		
		if (type.getCommand().contains(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCommand())) {
			LocalCacheParameter command = new LocalCacheParameter();
			command.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode());
			LocalCacheCommandWorker.getInstance().addCommand(command);
			ctx.writeAndFlush(CommandCoder.encodeCommand(true , "success"));
		} else if (type.getCommand().contains(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCommand())) {			
			List<String> params = LocalCacheUtil.tokenizer(msg , null);
			LocalCacheData.getInstance().set(params.get(1) , params.get(2));
			ctx.writeAndFlush(CommandCoder.encodeCommand(true , "success"));
		} else if (type.getCommand().contains(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCommand())) {
			List<String> params = LocalCacheUtil.tokenizer(msg , null);
			LocalCacheData.getInstance().del(params.get(1));
			ctx.writeAndFlush(CommandCoder.encodeCommand(true , "success"));
		} else {
			ctx.writeAndFlush(CommandCoder.encodeCommand(false , "invalid command"));
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
}