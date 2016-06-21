package com.vip.local.cache.define;

public enum LocalCacheConst {
	LOCAL_CACHE_SERVER_PORT("10012" , "服务器端口"),
	LOCAL_CACHE_MAX_FRAME_SIZE("102400" , "最大针大小");
	
	private String definition;
	private String desc;
	
	private LocalCacheConst(String definition , String desc){
		this.setDefinition(definition);
		this.setDesc(desc);
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
