package com.vip.local.cache.data;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalCacheData {
	private Map<String , String> cache = null;
	
	private static LocalCacheData instance = null;
	
	private ReadWriteLock readwritelock = new ReentrantReadWriteLock();
	
	public static LocalCacheData getInstance(){
		if (instance == null) {
			instance = new LocalCacheData();
		}
		
		return instance;
	}
	
	public void switchCache(Map<String , String> cache) {
		readwritelock.writeLock().lock();
		this.cache = cache;
		readwritelock.writeLock().unlock();
	}
	
	public String get(String key) {
		readwritelock.readLock().lock();
		if (this.cache == null) {
			readwritelock.readLock().unlock();
			return null;
		}
		
		String ret = this.cache.get(key);
		readwritelock.readLock().unlock();
		
		return ret;
	}
}
