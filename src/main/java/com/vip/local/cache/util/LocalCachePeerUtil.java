package com.vip.local.cache.util;

import java.io.UnsupportedEncodingException;
import com.vip.local.cache.main.LocalCacheClientInitializer;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand;

public class LocalCachePeerUtil {
	public static boolean replicate4Notify(String host , CacheCommand data) throws NumberFormatException, Exception {
		return LocalCacheClientInitializer.getInstance().replicate(host , data);
	}
	
	public static boolean replicate4Del(String host , CacheCommand data) {
		return LocalCacheClientInitializer.getInstance().replicate(host , data);
	}
	
	public static boolean replicate4Set(String host , CacheCommand data) throws UnsupportedEncodingException {
		return LocalCacheClientInitializer.getInstance().replicate(host , data);
	}
	
	public static boolean healthCheck(String host , CacheCommand data) {
		return LocalCacheClientInitializer.getInstance().replicate(host , data);
	}
}
