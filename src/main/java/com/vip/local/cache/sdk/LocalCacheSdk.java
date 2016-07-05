package com.vip.local.cache.sdk;

import java.net.URLEncoder;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;
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
	
	public void initialize(LocalCacheCallback callback , String hosts) throws NumberFormatException, InterruptedException{
		LocalCacheInitializer.getInstance().initialize(null , callback , hosts);
	}
	
	@SuppressWarnings("unchecked")
	public Object get(String key , @SuppressWarnings("rawtypes") Class type) {
		JSONObject obj = (JSONObject) LocalCacheData.getInstance().get(key);
		
		if (obj == null) {
			return null;
		}
		
		return obj.toJavaObject(type);
	}
	
	public void set(String key , Object value , Integer expire) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		if (expire == null) {
			expire = new Integer(0);
		}
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode());
		HashMap<String , Object> data = new HashMap<String , Object>();
		data.put("cache_key" , key);
		data.put("cache_value" , value);
		if (expire.intValue() != 0) {
			long expireTime = expire * 1000 + System.currentTimeMillis();
			data.put("cache_expire" , new Long(expireTime));
		} else {
			data.put("cache_expire" , new Long(0));
		}
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
	
	public void flush(String parameter) throws NumberFormatException, Exception{
		LocalCacheParameter param = new LocalCacheParameter();
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode());
		
		HashMap<String , Object> data = new HashMap<String , Object>();
		
		data.put("cache_key" , "flush_parameter_key");
		data.put("cache_value" , URLEncoder.encode(parameter , "UTF-8"));
		param.setParams(data);
		
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
}