package com.vip.local.cache.data;

import java.util.Map;

public class LocalCacheData {
	private Map<String , String> cache = null;
	
	private static LocalCacheData instance = null;
	
	public static LocalCacheData getInstance(){
		if (instance == null) {
			instance = new LocalCacheData();
		}
		
		return instance;
	}
	
	public void switchCache(Map<String , String> cache) {
		this.cache = cache;
	}
	
	public String get(String key) {
		if (this.cache == null) {
			return null;
		}
		
		return this.cache.get(key);
	}
}
