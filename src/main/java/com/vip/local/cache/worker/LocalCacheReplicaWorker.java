package com.vip.local.cache.worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vip.local.cache.db.DBShm;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

public final class LocalCacheReplicaWorker extends Thread{
	private static LocalCacheReplicaWorker instance = null;
	
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
	
	public boolean addCommand(LocalCacheParameter command) {
		try {
			return queue.add(command);
		} catch (IllegalStateException e) {
			// no space left to add more.
			return false;
		}
	}
	
	public boolean notify(String parameter) throws NumberFormatException, Exception{
		
		SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode())
				.setValue(parameter).setTimestamp(System.currentTimeMillis()).build();
		
		return shm.write(obj);
	}
	
	public boolean delCache(String key) throws Exception {
		SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder()
				.setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode())
				.setKey(key).setTimestamp(System.currentTimeMillis()).build();
		
		return shm.write(obj);
	}
	
	public boolean setCache(String key , String value) throws Exception {
		SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setOpType(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode())
				.setKey(key).setTimestamp(System.currentTimeMillis())
				.setValue(value).build();
		
		return shm.write(obj);
	}
	
	public void run(){
		while (true) {
			LocalCacheParameter value = null;
			try {
				value = queue.poll(new Integer(
						LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_TMO.getDefinition()) , 
						TimeUnit.MILLISECONDS);
				if (value == null) {
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
