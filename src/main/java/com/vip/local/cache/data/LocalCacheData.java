package com.vip.local.cache.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalCacheData {
	private Map<String , String> cache = new HashMap<String , String>();
	
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
	
	public void del(String key) {
		readwritelock.writeLock().lock();
		if (this.cache == null) {
			readwritelock.writeLock().unlock();
			return;
		}
		
		cache.remove(key);
		
		readwritelock.writeLock().unlock();
	}
	
	public void set(String key , String value) {
		readwritelock.writeLock().lock();
		if (this.cache == null) {
			readwritelock.writeLock().unlock();
			return;
		}
		
		cache.put(key , value);
		
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
