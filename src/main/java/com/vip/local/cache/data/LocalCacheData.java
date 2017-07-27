package com.vip.local.cache.data;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;

public class LocalCacheData {
	private LoadingCache<String , String> cache = null;
	private static LocalCacheData instance = null;
		
	public static LocalCacheData getInstance(){
		if (instance == null) {
			instance = new LocalCacheData();
		}
		return instance;
	}
	
	public void setCache(LoadingCache<String , String> cache) {
		this.cache = cache;
	}
	
	public void del(String key) throws ExecutionException {
		if (cache == null) {
			throw new ExecutionException(new Error("the cache object is null"));
		}
		cache.invalidate(key);
	}
	
	public void set(String key , String value) throws ExecutionException {
		if (cache == null) {
			throw new ExecutionException(new Error("the cache object is null"));
		}
		cache.put(key , value);
	}
	
	public String get(String key) throws ExecutionException {
		if (cache == null) {
			throw new ExecutionException(new Error("the cache object is null"));
		}
		return cache.get(key);
	}
}
