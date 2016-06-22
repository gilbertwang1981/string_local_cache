package com.vip.local.cache.util;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheClientInitializer;

public class LocalCachePeerUtil {
	public static boolean replicate4Flush(String host) throws NumberFormatException, Exception {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCommand() + "\n");
	}
	
	public static boolean replicate4Del(String host , String key) {
		return true;
	}
	
	public static boolean replicate4Set(String host , String key , String value) {
		return true;
	}
}
