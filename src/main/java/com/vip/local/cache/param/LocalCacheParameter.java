package com.vip.local.cache.param;

import java.util.HashMap;

public class LocalCacheParameter {
	private int code;
	private HashMap<String , Object> params = new HashMap<String , Object>();
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public HashMap<String , Object> getParams() {
		return params;
	}
	public void setParams(HashMap<String , Object> params) {
		this.params = params;
	}
}
