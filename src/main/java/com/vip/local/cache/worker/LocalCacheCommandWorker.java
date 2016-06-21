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
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode() == value.getCode()) {
				LocalCacheCommandMgr.getInstance().execute(
						LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode() , value);
			} else if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() == value.getCode()) {
				LocalCacheCommandMgr.getInstance().execute(
						LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() , value);
			} else if (LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() == value.getCode()) {
				LocalCacheCommandMgr.getInstance().execute(
						LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() , value);
			}
		}
	}
	
	public void addCommand(LocalCacheParameter command) {
		queue.add(command);
	}
}
