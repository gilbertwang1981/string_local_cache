package com.vip.local.cache.db;

import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

public class DBShm {
	private DBIndexShm indexShm = new DBIndexShm();
	private DBDataShm dataShm = null;
	
	public boolean initialize() throws Exception {
		if (!indexShm.initialize("index.shm" , "index.lock")) {
			return false;
		}
		
		DBIndexShmHdr index = indexShm.getDbConfig();
		if (index == null) {
			indexShm.destroy();
			
			return false;
		}
		
		if (index.getCurrentIndex() == index.getTotal()) {
			indexShm.destroy();
			
			return false;
		}
		
		dataShm = new DBDataShm();
		if (!dataShm.initialize("data." + index.getCurrentIndex() + ".shm" , "data." + index.getCurrentIndex() + ".lock")){
			indexShm.destroy();
		}
		
		return true;
	}
	
	public SharedMemoryObject read() throws Exception{
		DBIndexShmHdr index = indexShm.getDbConfig();
		if (index == null) {
			return null;
		}
		
		if (dataShm == null) {
			return null;
		}
		
		if (dataShm.getShmConfig().getReadCtr() >= DBShmConst.DB_SHM_SIZE_IN_EACH_DB) {
			dataShm.destroy();
			
			if (index.getCurrentReadCtr() == index.getCurrentIndex()) {
				return null;
			}
			
			DBIndexShmHdr newHdr = new DBIndexShmHdr();
			if (index.getCurrentReadCtr() >= (index.getTotal() - 1)) {
				newHdr.setCurrentReadCtr(0);
			} else {
				newHdr.setCurrentReadCtr(index.getCurrentReadCtr() + 1);
			}
			indexShm.setDbConfig4Read(newHdr);
			
			dataShm = new DBDataShm();
			if (!dataShm.initialize("data." + newHdr.getCurrentReadCtr() + ".shm" , "data." + newHdr.getCurrentReadCtr() + ".lock")) {
				return null;
			}
		}
		
		return dataShm.read();
	}
	
	public boolean write(SharedMemoryObject obj) throws Exception {
		DBIndexShmHdr index = indexShm.getDbConfig();
		if (index == null) {
			return false;
		}
		
		if (dataShm == null) {
			return false;
		}
		
		if (dataShm.getShmConfig().getWriteCtr() >= DBShmConst.DB_SHM_SIZE_IN_EACH_DB) {
			dataShm.destroy();
			
			DBIndexShmHdr newHdr = new DBIndexShmHdr();
			if (index.getCurrentIndex() >= (index.getTotal() - 1)) {	
				newHdr.setCurrentIndex(0);
				newHdr.setCurrentWriteCtr(0);
			} else {
				newHdr.setCurrentIndex(index.getCurrentIndex() + 1);
				newHdr.setCurrentWriteCtr(index.getCurrentWriteCtr() + 1);
			}			
			indexShm.setDbConfig4Write(newHdr);
			
			dataShm = new DBDataShm();
			if (!dataShm.initialize("data." + newHdr.getCurrentIndex() + ".shm" , 
					"data." + newHdr.getCurrentIndex() + ".lock" , true)) {
				return false;
			}
		}
	
		return dataShm.write(obj);
	}
	
	public void destory() throws Exception{
		indexShm.destroy();
		
		if (dataShm != null) {
			dataShm.destroy();
		}
	}
}