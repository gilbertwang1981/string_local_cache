package com.vip.local.cache.util;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheClientInitializer;

public class LocalCachePeerUtil {
	public static boolean replicate4Flush(String host , String parameter) throws NumberFormatException, Exception {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCommand() + " " + 
				parameter + "\n");
	}
	
	public static boolean replicate4Del(String host , String key) {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCommand() + " " + key + "\n");
	}
	
	public static boolean replicate4Set(String host , String key , String value) {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCommand() + " " + key + " " + 
				value + "\n");
	}
}
