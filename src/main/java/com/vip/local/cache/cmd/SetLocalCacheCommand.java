package com.vip.local.cache.cmd;

import com.alibaba.fastjson.JSON;
import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;

public class SetLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		LocalCacheData.getInstance().set((String)paramter.getParams().get("cache_key") , 
				JSON.parseObject((String) paramter.getParams().get("cache_value") , Object.class));

		return true;
	}
	
}
