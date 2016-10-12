package com.vip.local.cache.worker;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.util.LocalCachePeerUtil;
import com.vip.local.cache.util.LocalCacheUtil;

public final class LocalCacheReplicaWorker extends Thread{
	private static LocalCacheReplicaWorker instance = null;
	
	private String hosts = null;
	
	private BlockingQueue<LocalCacheParameter> queue = new ArrayBlockingQueue<LocalCacheParameter>(
			new Integer(LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_SIZE.getDefinition()));
	
	private BlockingQueue<LocalCacheParameter> retransmitQueue = new ArrayBlockingQueue<LocalCacheParameter>(
			new Integer(LocalCacheConst.LOCAL_CACHE_RETRANS_QUEUE_SIZE.getDefinition()));
	
	public static LocalCacheReplicaWorker getInstance(){
		if (instance == null) {
			instance = new LocalCacheReplicaWorker();
		}
		
		return instance;
	}
	
	private LocalCacheReplicaWorker(){
		this.hosts = System.getenv("LOCAL_CACHE_HOST_SET");
	}
	
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public void addCommand(LocalCacheParameter command) {
		queue.add(command);
	}
	
	private boolean retransmit(String host , String parameter) throws UnsupportedEncodingException{
		LocalCacheParameter lcParameter = new LocalCacheParameter();
		HashMap<String , Object> data = new HashMap<String , Object>();
		
		data.put("cache_key" , "flush_parameter_key");
		data.put("cache_value" , parameter);
		data.put("cache_expire" , System.currentTimeMillis());
		data.put("cache_host" , host);
		
		lcParameter.setParams(data);
		
		return retransmitQueue.add(lcParameter);
	}
	
	private boolean doRetransmit() {
		try {
			LocalCacheParameter value = retransmitQueue.poll(new Integer(
					LocalCacheConst.LOCAL_CACHE_RETRANS_QUEUE_TMO.getDefinition()) , 
					TimeUnit.MILLISECONDS);
			if (value == null) {
				return false;
			}
			
			long expire = (Long)value.getParams().get("cache_expire");
			if (expire > (System.currentTimeMillis() - 
					new Long(LocalCacheConst.LOCAL_CACHE_RETRANS_TMO.getDefinition()).longValue())) {
				
				retransmitQueue.add(value);
				
				return false;
			} else {
				return LocalCachePeerUtil.replicate4Flush(value.getParams().get("cache_host").toString() , 
					value.getParams().get("cache_value").toString());
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean flushCache(String parameter) throws NumberFormatException, Exception{
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Flush(host , parameter)){
				ret = false;
				if (!this.retransmit(host, parameter)) {
					// TODO:
				}
			}
		}
		
		return ret;
	}
	
	public boolean healthCheck(){
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.healthCheck(host)){
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
	
	public boolean setCache(String key , Object value , Long expire) {
		if (hosts == null) {
			return false;
		}
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Set(host , key , value , expire)){
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
					if (!this.doRetransmit()){
						this.healthCheck();
					}
					
					continue;
				}
				
				if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode()){
					this.flushCache((String)value.getParams().get("cache_value"));
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode()) {
					this.delCache((String)value.getParams().get("cache_key"));
				} else if (value.getCode() == LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode()) {
					this.setCache((String)value.getParams().get("cache_key") , 
							value.getParams().get("cache_value") , 
							(Long)value.getParams().get("cache_expire"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
