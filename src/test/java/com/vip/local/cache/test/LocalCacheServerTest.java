package com.vip.local.cache.test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vip.local.cache.sdk.StringLocalCacheNotifyCallback;
import com.vip.local.cache.sdk.StringLocalCache;
import com.vip.local.cache.sdk.StringLocalCacheErrorCallback;

import junit.framework.TestCase;

class LocalCacheNotifyCallback implements StringLocalCacheNotifyCallback {

	public boolean onNotify(String param) {
		System.out.println("on notify:" + param);
		
		return true;
	}
}

class LocalCacheErrorCallback implements StringLocalCacheErrorCallback {

	public boolean onError(String nodeId , String key, String value) {
		System.out.println("on Error:" + nodeId + "/" + key + "/" + value);
		
		return false;
	}
	
}

class Getter extends Thread {
	public void run(){
		while (true) {
			try {
				Thread.sleep(500);
				
				System.out.println((String)StringLocalCache.getInstance().get("1" , "3"));
			} catch (InterruptedException e) {
			}
		}
	}
}

class Starter extends Thread {
	private LoadingCache<String , String> cahceBuilder = CacheBuilder.newBuilder()
			.refreshAfterWrite(1 , TimeUnit.SECONDS).expireAfterWrite(3 , TimeUnit.SECONDS)
            .build(new CacheLoader<String , String>() {  
                @Override
                public String load(String key) throws Exception {
                    return UUID.randomUUID().toString(); 
                }  
            });
	
	public void run(){
		try {
			StringLocalCache.getInstance().initialize(cahceBuilder , 
					new LocalCacheNotifyCallback() , new LocalCacheErrorCallback());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class LocalCacheServerTest extends TestCase {
	
	public void setUp() throws NumberFormatException, InterruptedException{
		new Starter().start();
		new Getter().start();
	}
	
	public void testLocalCacheServer00() throws InterruptedException, UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer();
		for (int i = 0;i < 256;i++) {
			sb.append("hello world");
		}
		
		while (true) {
			StringLocalCache.getInstance().set("1" , sb.toString());
			
			StringLocalCache.getInstance().notify("flush the cache.");
			
			if (System.currentTimeMillis() % 5 == 0) {
				StringLocalCache.getInstance().del("1");
			}
		
			Thread.sleep(100);
		}
	}
	
	public void tearDown(){
	}
}