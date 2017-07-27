package com.vip.local.cache.sdk;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;
import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheInitializer;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.worker.LocalCacheReplicaWorker;

public class StringLocalCache {
	private static StringLocalCache instance = null;
	
	public static StringLocalCache getInstance(){
		if (instance == null) {
			instance = new StringLocalCache();
		}
		
		return instance;
	}
	
	public void initialize(LoadingCache<String , String> cache , 
			LocalCacheNotifyCallback callback , String hosts) throws NumberFormatException, InterruptedException {
		LocalCacheData.getInstance().setCache(cache);
		LocalCacheInitializer.getInstance().initialize(null , callback , hosts);
	}
	
	public String get(String key , String defaultValue) {
		try {
			return LocalCacheData.getInstance().get(key);
		} catch (ExecutionException e) {
			return defaultValue;
		}
	}
	
	public void set(String key , String value) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode());
		HashMap<String , Object> data = new HashMap<String , Object>();
		data.put("cache_key" , key);
		data.put("cache_value" , value);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void del(String key) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode());
		HashMap<String , Object> data = new HashMap<String , Object>();
		data.put("cache_key", key);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void notify(String parameter) throws UnsupportedEncodingException {
		LocalCacheParameter param = new LocalCacheParameter();
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode());
		
		HashMap<String , Object> data = new HashMap<String , Object>();
		
		data.put("cache_value" , parameter);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
}