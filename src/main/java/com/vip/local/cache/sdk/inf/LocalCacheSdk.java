package com.vip.local.cache.sdk.inf;

import java.util.HashMap;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheInitializer;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.worker.LocalCacheReplicaWorker;

public class LocalCacheSdk {
	private static LocalCacheSdk instance = null;
	
	public static LocalCacheSdk getInstance(){
		if (instance == null) {
			instance = new LocalCacheSdk();
		}
		
		return instance;
	}
	
	public void initialize(LocalCacheCallback callback) throws NumberFormatException, InterruptedException{
		LocalCacheInitializer.getInstance().initialize(null , callback);
	}
	
	public String get(String key) {
		return LocalCacheData.getInstance().get(key);
	}
	
	public void set(String key , String value) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode());
		HashMap<String , String> data = new HashMap<String , String>();
		data.put("cache_key", key);
		data.put("cache_value", value);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void del(String key) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode());
		HashMap<String , String> data = new HashMap<String , String>();
		data.put("cache_key", key);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void flush() throws NumberFormatException, Exception{
		LocalCacheParameter param = new LocalCacheParameter();
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode());
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
}