package com.vip.local.cache.worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vip.local.cache.cmd.LocalCacheCommandMgr;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.param.LocalCacheParameter;

public class LocalCacheCommandWorker extends Thread{
	private static LocalCacheCommandWorker instance = null;
	
	public static LocalCacheCommandWorker getInstance(){
		if (instance == null) {
			instance = new LocalCacheCommandWorker();
		}
		
		return instance;
	}
	
	private BlockingQueue<LocalCacheParameter> queue = new ArrayBlockingQueue<LocalCacheParameter>(
			new Integer(LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_SIZE.getDefinition()));
	
	public void run(){
		while (true) {
			LocalCacheParameter value = null;
			try {
				value = queue.poll(new Integer(
						LocalCacheConst.LOCAL_CACHE_CMD_QUEUE_TMO.getDefinition()) , 
						TimeUnit.MILLISECONDS);
				if (value == null) {
					continue;
				}
	
				if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode() == value.getCode()) {
					if (!LocalCacheCommandMgr.getInstance().execute(
							LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_NOTIFY.getCode() , value)){
						throw new Exception("execute failed," + value);
					}
				} else if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() == value.getCode()) {
					if (!LocalCacheCommandMgr.getInstance().execute(
							LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() , value)){
						throw new Exception("execute failed," + value);
					}
				} else if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() == value.getCode()) {
					if (!LocalCacheCommandMgr.getInstance().execute(
							LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() , value)){
						throw new Exception("execute failed," + value);
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addCommand(LocalCacheParameter command) {
		queue.add(command);
	}
}
