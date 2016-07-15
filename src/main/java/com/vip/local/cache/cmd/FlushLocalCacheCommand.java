package com.vip.local.cache.cmd;

import java.net.URLDecoder;
import java.util.Map;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.LocalCacheCallback;

public class FlushLocalCacheCommand implements LocalCacheCommand{
	
	private LocalCacheCallback callback = null;
	
	public FlushLocalCacheCommand(LocalCacheCallback callback) {
		this.callback = callback;
	}

	public boolean execute(LocalCacheParameter paramter) {
		Map<String, Object> ret = null;
		try {
			ret = callback.onFlush(
					URLDecoder.decode((String)paramter.getParams().get("cache_value") , "UTF-8"));
		
			if (ret == null) {
				return false;
			}
			
			LocalCacheData.getInstance().switchCache(ret);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
	}
}
