package com.vip.local.cache.cmd;

import java.net.URLDecoder;
import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;

public class SetLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		try {
			LocalCacheData.getInstance().set(paramter.getParams().get("cache_key") , 
					URLDecoder.decode((String) paramter.getParams().get("cache_value") , "UTF-8"));
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
}
