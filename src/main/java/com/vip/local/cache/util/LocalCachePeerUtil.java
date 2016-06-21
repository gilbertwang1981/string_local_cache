package com.vip.local.cache.util;

import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.main.LocalCacheClientInitializer;

public class LocalCachePeerUtil {
	public static boolean replicate4Flush(String host) throws NumberFormatException, Exception {
		return LocalCacheClientInitializer.getInstance().sendMsg(host, 
				new Integer(LocalCacheConst.LOCAL_CACHE_PEER_PORT.getDefinition()), 
				"flush\n");
	}
}
