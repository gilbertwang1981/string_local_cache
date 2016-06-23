package com.vip.local.cache.sdk;

import java.util.Map;

import com.vip.local.cache.param.LocalCacheParameter;

public interface LocalCacheCallback {
	public Map<String , String> onFlush(LocalCacheParameter param);
}
