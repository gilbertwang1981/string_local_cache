package com.vip.local.cache.main;

import java.util.concurrent.ConcurrentHashMap;

import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.handler.LocalCachePeerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class LocalCacheClientInitializer extends Thread{
	private static LocalCacheClientInitializer instance = null;
	
	private Bootstrap bootstrap = null;
	
	private ConcurrentHashMap<String , Channel> channels = 
			new ConcurrentHashMap<String , Channel>();
	
	public static LocalCacheClientInitializer getInstance(){
		if (instance == null) {
			instance = new LocalCacheClientInitializer();
		}
		
		return instance;
	}
	
	public void initialize(){
		EventLoopGroup group = new NioEventLoopGroup();
		
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class);
		
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast("handler", new LocalCachePeerHandler());
			}
		});
		
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
	}
	
	private Channel getChannel(String host , int port){
		Channel channel = channels.get(host + ":" + port);
		if (channel == null) {
			try {
				channel = bootstrap.connect(host, port).sync().channel();
			} catch (Exception e) {
				return null;
			}
			
			channels.put(host + ":" + port , channel);
		}
		
		return channel;
	}
	
	public boolean sendMsg(String host , int port , String msg) throws Exception {
		Channel channel = this.getChannel(host, port);
		if(channel != null) {
			channel.writeAndFlush(msg).sync();
			
			return true;
		}else{
			return false;
		}
	}
	
	public void run(){
		while (true) {
			try {
				Thread.sleep(
					new Integer(
					LocalCacheConst.LOCAL_CACHE_HC_TMO.getDefinition()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}