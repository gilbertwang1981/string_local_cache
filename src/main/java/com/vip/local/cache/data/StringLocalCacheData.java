package com.vip.local.cache.data;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.LoadingCache;

public class StringLocalCacheData {
	private LoadingCache<String , String> cache = null;
	private static StringLocalCacheData instance = null;
		
	public static StringLocalCacheData getInstance(){
		if (instance == null) {
			instance = new StringLocalCacheData();
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
