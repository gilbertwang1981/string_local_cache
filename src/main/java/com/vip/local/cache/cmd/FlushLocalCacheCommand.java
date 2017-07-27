package com.vip.local.cache.cmd;

import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.StringLocalCacheNotifyCallback;

public class FlushLocalCacheCommand implements LocalCacheCommand{
	
	private StringLocalCacheNotifyCallback callback = null;
	
	public FlushLocalCacheCommand(StringLocalCacheNotifyCallback callback) {
		this.callback = callback;
	}

	public boolean execute(LocalCacheParameter paramter) {
		try {
			return callback.onNotify((String) paramter.getParams().get("cache_value"));
		} catch (Exception e) {			
			return false;
		}
	}
}
