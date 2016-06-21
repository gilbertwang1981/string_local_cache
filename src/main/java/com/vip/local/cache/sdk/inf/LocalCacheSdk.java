package com.vip.local.cache.sdk.inf;

import java.util.List;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.main.LocalCacheInitializer;
import com.vip.local.cache.util.LocalCachePeerUtil;
import com.vip.local.cache.util.LocalCacheUtil;

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
	
	public boolean flushCache() throws NumberFormatException, Exception{
		String hosts = System.getenv("VIP_LOCAL_CACHE_HOST_SET");
		
		List<String> params = LocalCacheUtil.tokenizer(hosts , ";");
		
		boolean ret = true;
		for (String host : params) {
			if (!LocalCachePeerUtil.replicate4Flush(host)){
				ret = false;
			}
		}
		
		return ret;
	}
}