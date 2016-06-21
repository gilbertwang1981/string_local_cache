package com.vip.local.cache.param;

import java.util.HashMap;

public class LocalCacheParameter {
	private int code;
	private HashMap<String , String> params = new HashMap<String , String>();
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public HashMap<String , String> getParams() {
		return params;
	}
	public void setParams(HashMap<String , String> params) {
		this.params = params;
	}
}
