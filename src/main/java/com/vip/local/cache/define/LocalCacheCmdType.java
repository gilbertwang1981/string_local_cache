package com.vip.local.cache.define;

public enum LocalCacheCmdType {
	LOCAL_CACHE_CMD_TYPE_INV(-1 , "无效命令" , "inv"),
	LOCAL_CACHE_CMD_TYPE_NOTIFY(0 , "缓存刷新" , "flush"),
	LOCAL_CACHE_CMD_TYPE_SET(1 , "设置缓存" , "set"),
	LOCAL_CACHE_CMD_TYPE_DEL(2 , "删除缓存" , "del"),
	LOCAL_CACHE_CMD_TYPE_ERROR(3 , "错误回调" , "error");
	
	private LocalCacheCmdType(int code , String desc , String command){
		this.code = code;
		this.desc = desc;
		this.command = command;
	}
	
	private int code;
	private String desc;
	private String command;
	
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
