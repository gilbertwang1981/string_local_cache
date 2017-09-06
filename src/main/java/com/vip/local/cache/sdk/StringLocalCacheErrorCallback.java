package com.vip.local.cache.sdk;

public interface StringLocalCacheErrorCallback {
	public boolean onError(String nodeId , String key , String value);
}
