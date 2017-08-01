package com.vip.local.cache.worker;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;
import com.vip.local.cache.db.DBShm;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.proto.CommonLocalCache;
import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand.Builder;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;
import com.vip.local.cache.util.LocalCachePeerUtil;
import com.vip.local.cache.util.LocalCacheUtil;

public final class LocalCacheReplicaWorker extends Thread{
	private static LocalCacheReplicaWorker instance = null;
	
	private String hosts = null;
	
	private BlockingQueue<LocalCacheParameter> queue = new ArrayBlockingQueue<LocalCacheParameter>(
			new Integer(LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_SIZE.getDefinition()));
	
	private DBShm shm = new DBShm();
	
	private LocalCacheReplicaWorker(){
		try {
			shm.initialize();
		} catch (Exception e) {
		}
	}
	
	public static LocalCacheReplicaWorker getInstance(){
		if (instance == null) {
			instance = new LocalCacheReplicaWorker();
		}
		
		return instance;
	}
	
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public boolean addCommand(LocalCacheParameter command) {
		try {
			return queue.add(command);
		} catch (IllegalStateException e) {
			// no space left to add more.
			return false;
		}
	}
	
	public boolean notify(String parameter) throws NumberFormatException, Exception{
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");

		Builder builder = CommonLocalCache.CacheCommand.newBuilder().setParameter(
				parameter).setMessageType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode());
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Notify(host , builder.build())){
				SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp(host).setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode())
						.setValue(parameter).setTimestamp(System.currentTimeMillis()).build();
				
				shm.write(obj);
				
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean healthCheck(){
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		Builder builder = CommonLocalCache.CacheCommand.newBuilder().setMessageType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_HB.getCode());
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.healthCheck(host , builder.build())){
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean delCache(String key) throws Exception {
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		Builder builder = CommonLocalCache.CacheCommand.newBuilder().setMessageType(
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode()).setKey(key);
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Del(host , builder.build())){
				SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp(host)
						.setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode())
						.setKey(key).setTimestamp(System.currentTimeMillis()).build();
				
				shm.write(obj);
				
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean setCache(String key , String value) throws Exception {
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		Builder builder = CommonLocalCache.CacheCommand.newBuilder().setMessageType(
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode()).setKey(key).setValue(ByteString.copyFrom((String) value , "UTF-8"));
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Set(host , builder.build())){
				SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp(host).setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode())
						.setKey(key).setTimestamp(System.currentTimeMillis())
						.setValue(value).build();
				
				shm.write(obj);
				
				ret = false;
			}
		}
		
		return ret;
	}
	
	public void run(){
		while (true) {
			LocalCacheParameter value = null;
			try {
				value = queue.poll(new Integer(
						LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_TMO.getDefinition()) , 
						TimeUnit.MILLISECONDS);
				if (value == null) {
					this.healthCheck();

					continue;
				}
				
				if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode()){
					this.notify((String)value.getParams().get("cache_value"));
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode()) {
					this.delCache((String)value.getParams().get("cache_key"));
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode()) {
					this.setCache((String)value.getParams().get("cache_key") , (String) value.getParams().get("cache_value"));
				}
			} catch (Exception e) {
				// nothing to do
			}
		}
	}
}
