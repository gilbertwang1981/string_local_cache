package com.vip.local.cache.expire;

public class LocalCacheExpiredData {
	private Long expiredTime;
	private String expiredKey;
	
	public Long getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}
	public String getExpiredKey() {
		return expiredKey;
	}
	public void setExpiredKey(String expiredKey) {
		this.expiredKey = expiredKey;
	}
}
