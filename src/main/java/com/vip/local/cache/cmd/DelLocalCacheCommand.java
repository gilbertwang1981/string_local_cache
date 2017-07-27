package com.vip.local.cache.cmd;

import com.vip.local.cache.data.StringLocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;

public class DelLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		try {
			StringLocalCacheData.getInstance().del((String) paramter.getParams().get("cache_key"));
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

}
