package com.vip.local.cache.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheClientInitializer;

public class LocalCachePeerUtil {
	public static boolean replicate4Flush(String host , String parameter) throws NumberFormatException, Exception {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCommand() + " " + 
				URLEncoder.encode(parameter , "UTF-8") + "\n");
	}
	
	public static boolean replicate4Del(String host , String key) {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCommand() + " " + key + "\n");
	}
	
	public static boolean replicate4Set(String host , String key , Object value) throws UnsupportedEncodingException {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCommand() + " " + key + " " + 
				URLEncoder.encode((String)value , "UTF-8") + "\n");
	}
	
	public static boolean healthCheck(String host) {
		return LocalCacheClientInitializer.getInstance().replicate(host , 
				LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_HB.getCommand() + " " + 
				host +  "\n");
	}
}
