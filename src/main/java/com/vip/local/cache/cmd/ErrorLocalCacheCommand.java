package com.vip.local.cache.cmd;

import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.StringLocalCacheErrorCallback;

public class ErrorLocalCacheCommand implements LocalCacheCommand {
	
	private StringLocalCacheErrorCallback callback;

	public ErrorLocalCacheCommand(StringLocalCacheErrorCallback callback) {
		this.callback = callback;
	}
	
	public boolean execute(LocalCacheParameter paramter) {
		return callback.onError((String) paramter.getParams().get("cache_key") , 
				(String) paramter.getParams().get("cache_value"));
	}

}
