package com.vip.local.cache.worker;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.util.LocalCachePeerUtil;
import com.vip.local.cache.util.LocalCacheUtil;

public class LocalCacheReplicaWorker extends Thread{
	private static LocalCacheReplicaWorker instance = null;
	
	private String hosts = System.getenv("LOCAL_CACHE_HOST_SET");
	
	private BlockingQueue<LocalCacheParameter> queue = new ArrayBlockingQueue<LocalCacheParameter>(
			new Integer(LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_SIZE.getDefinition()));
	
	public static LocalCacheReplicaWorker getInstance(){
		if (instance == null) {
			instance = new LocalCacheReplicaWorker();
		}
		
		return instance;
	}
	
	public void addCommand(LocalCacheParameter command) {
		queue.add(command);
	}
	
	public boolean flushCache() throws NumberFormatException, Exception{
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Flush(host)){
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean delCache(String key) {
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Del(host , key)){
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean setCache(String key , String value) {
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Set(host , key , value)){
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
					continue;
				}
				
				if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode()){
					this.flushCache();
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode()) {
					this.delCache(value.getParams().get("cache_key"));
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode()) {
					this.setCache(value.getParams().get("cache_key") , 
							value.getParams().get("cache_value"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
