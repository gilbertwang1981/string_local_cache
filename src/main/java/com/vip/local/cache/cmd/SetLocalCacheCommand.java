package com.vip.local.cache.cmd;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;

public class SetLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		LocalCacheData.getInstance().set(paramter.getParams().get("cache_key") , 
				paramter.getParams().get("cache_value"));
		
		return true;
	}
	
}
