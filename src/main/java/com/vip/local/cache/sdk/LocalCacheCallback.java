package com.vip.local.cache.sdk;

import java.util.Map;

public interface LocalCacheCallback {
	public Map<String , String> onFlush(String param);
}
