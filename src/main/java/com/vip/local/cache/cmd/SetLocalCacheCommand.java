package com.vip.local.cache.cmd;

import com.alibaba.fastjson.JSON;
import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.expire.LocalCacheExpirer;
import com.vip.local.cache.param.LocalCacheParameter;

public class SetLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		LocalCacheData.getInstance().set((String)paramter.getParams().get("cache_key") , 
				JSON.parseObject((String) paramter.getParams().get("cache_value") , Object.class));

		Long expired = new Long((String)paramter.getParams().get("cache_expire"));
		if (expired != 0) {
			LocalCacheExpirer.getInstance().expire((String)paramter.getParams().get("cache_key") , expired);
		}
		
		return true;
	}
	
}
