package com.vip.local.cache.cmd;

import java.util.concurrent.ConcurrentHashMap;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.sdk.LocalCacheNotifyCallback;

public class LocalCacheCommandMgr {
	private static LocalCacheCommandMgr instance = null;
	private ConcurrentHashMap<Integer , LocalCacheCommand> commands = new 
			ConcurrentHashMap<Integer , LocalCacheCommand>();
	
	private LocalCacheNotifyCallback callback = null;
	
	public static LocalCacheCommandMgr getInstance() {
		if (instance == null) {
			instance = new LocalCacheCommandMgr();
		}
		
		return instance;
	}
	
	public void initialize(LocalCacheNotifyCallback callback) {
		this.callback = callback;
		
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() , 
				new DelLocalCacheCommand());
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode() , 
				new FlushLocalCacheCommand(this.callback));
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() , 
				new SetLocalCacheCommand());
	}
	
	public boolean execute(int code , LocalCacheParameter paramter) {
		LocalCacheCommand localCacheCommand = commands.get(code);
		if (localCacheCommand == null) {
			return false;
		}
		
		return localCacheCommand.execute(paramter);
	}
}
