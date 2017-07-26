package com.vip.local.cache.test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vip.local.cache.sdk.LocalCacheNotifyCallback;
import com.vip.local.cache.sdk.LocalCacheSdk;

import junit.framework.TestCase;

class LocalCacheCallback implements LocalCacheNotifyCallback {

	public boolean onNotify(Object param) {
		System.out.println("no notify:" + param.toString());
		
		return true;
	}
}

class Getter extends Thread {
	public void run(){
		while (true) {
			try {
				Thread.sleep(500);
				
				System.out.println((String)LocalCacheSdk.getInstance().get("1" , "3"));
			} catch (InterruptedException e) {
			}
		}
	}
}

class Starter extends Thread {
	private LoadingCache<Object, Object> cahceBuilder = CacheBuilder.newBuilder()
			.refreshAfterWrite(1 , TimeUnit.SECONDS).expireAfterWrite(3 , TimeUnit.SECONDS)
            .build(new CacheLoader<Object, Object>() {  
                @Override
                public String load(Object key) throws Exception {
                    return UUID.randomUUID().toString(); 
                }  
            });
	
	public void run(){
		try {
			LocalCacheSdk.getInstance().initialize(cahceBuilder , 
					new LocalCacheCallback() , "127.0.0.1");
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
			LocalCacheSdk.getInstance().set("1" , sb.toString());
			
			LocalCacheSdk.getInstance().flush("flush the cache.");
		
			Thread.sleep(100);
		}
	}
	
	public void tearDown(){
	}
}