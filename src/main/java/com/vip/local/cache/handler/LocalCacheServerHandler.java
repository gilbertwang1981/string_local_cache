package com.vip.local.cache.handler;

import java.net.InetAddress;
import java.util.HashMap;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand;
import com.vip.local.cache.worker.LocalCacheCommandWorker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LocalCacheServerHandler extends SimpleChannelInboundHandler<CacheCommand> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CacheCommand msg) throws Exception {
		if (msg.getMessageType() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode()) {
			LocalCacheParameter command = new LocalCacheParameter();
			command.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode());
			
			HashMap<String , Object> values = new HashMap<String , Object>();
			values.put("cache_value" , msg.getParameter());
			command.setParams(values);
			
			LocalCacheCommandWorker.getInstance().addCommand(command);
		} else if (msg.getMessageType() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode()) {			
			LocalCacheParameter command = new LocalCacheParameter();
			command.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode());

			HashMap<String , Object> values = new HashMap<String , Object>();
			values.put("cache_key", msg.getKey());
			values.put("cache_value" , msg.getValue());
						
			command.setParams(values);
			LocalCacheCommandWorker.getInstance().addCommand(command);
		} else if (msg.getMessageType() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode()) {
			LocalCacheParameter command = new LocalCacheParameter();
			command.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode());

			HashMap<String , Object> values = new HashMap<String , Object>();
			values.put("cache_key", msg.getKey());
			command.setParams(values);
			LocalCacheCommandWorker.getInstance().addCommand(command);
		} else if (msg.getMessageType() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_ERROR.getCode()) {			
			LocalCacheParameter command = new LocalCacheParameter();
			command.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_ERROR.getCode());

			HashMap<String , Object> values = new HashMap<String , Object>();
			values.put("cache_key", msg.getKey());
			values.put("cache_value" , msg.getValue());
			values.put("cache_param" , msg.getParameter());
						
			command.setParams(values);
			LocalCacheCommandWorker.getInstance().addCommand(command);
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