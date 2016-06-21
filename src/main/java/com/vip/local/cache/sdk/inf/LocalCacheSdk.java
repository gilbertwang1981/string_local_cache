package com.vip.local.cache.sdk.inf;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.main.LocalCacheInitializer;

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
	
	public void flushCache(){
		
	}
}