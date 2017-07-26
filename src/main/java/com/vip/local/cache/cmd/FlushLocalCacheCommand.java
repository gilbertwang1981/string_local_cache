package com.vip.local.cache.cmd;

import java.net.URLDecoder;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.LocalCacheNotifyCallback;

public class FlushLocalCacheCommand implements LocalCacheCommand{
	
	private LocalCacheNotifyCallback callback = null;
	
	public FlushLocalCacheCommand(LocalCacheNotifyCallback callback) {
		this.callback = callback;
	}

	public boolean execute(LocalCacheParameter paramter) {
		try {
			return callback.onNotify(URLDecoder.decode((String) paramter.getParams().get("cache_value") , "UTF-8"));
		} catch (Exception e) {			
			return false;
		}
	}
}
