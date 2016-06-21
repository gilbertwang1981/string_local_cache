package com.vip.local.cache.define;

public enum LocalCacheCmdType {
	LOCAL_CACHE_CMD_TYPE_FLUSH(0 , "缓存刷新"),
	LOCAL_CACHE_CMD_TYPE_SET(1 , "设置缓存"),
	LOCAL_CACHE_CMD_TYPE_DEL(2 , "删除缓存");
	
	private LocalCacheCmdType(int code , String desc){
		this.code = code;
		this.desc = desc;
	}
	
	private int code;
	private String desc;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
