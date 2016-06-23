package com.vip.local.cache.cmd;

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
		Map<String , String> ret = callback.onFlush(paramter);
		
		if (ret == null) {
			return false;
		}
		
		LocalCacheData.getInstance().switchCache(ret);
		
		return true;
	}
}
