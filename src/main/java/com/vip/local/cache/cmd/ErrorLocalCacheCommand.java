package com.vip.local.cache.cmd;

import com.google.protobuf.ByteString;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.StringLocalCacheErrorCallback;

public class ErrorLocalCacheCommand implements LocalCacheCommand {
	
	private StringLocalCacheErrorCallback callback;

	public ErrorLocalCacheCommand(StringLocalCacheErrorCallback callback) {
		this.callback = callback;
	}
	
	public boolean execute(LocalCacheParameter paramter) {
		try {
			return callback.onError((String) paramter.getParams().get("cache_param") , 
					(String) paramter.getParams().get("cache_key") , 
					((ByteString) paramter.getParams().get("cache_value")).toStringUtf8());
		} catch (Exception e) {
			return false;
		}
	}

}
