package com.vip.local.cache.cmd;

import com.google.protobuf.ByteString;
import com.vip.local.cache.data.StringLocalCacheData;
import com.vip.local.cache.param.LocalCacheParameter;

public class SetLocalCacheCommand implements LocalCacheCommand{

	public boolean execute(LocalCacheParameter paramter) {
		try {
			StringLocalCacheData.getInstance().set((String) paramter.getParams().get("cache_key") , 
					((ByteString)paramter.getParams().get("cache_value")).toStringUtf8());
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
}
