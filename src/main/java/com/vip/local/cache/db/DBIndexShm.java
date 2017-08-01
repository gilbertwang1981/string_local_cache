package com.vip.local.cache.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DBIndexShm {
	private int total = DBShmConst.DB_SHM_SIZE_IN_DBS;
	private int currentWriteCtr = 0;
	private int currentReadCtr = 0;
	private int currentIndex = 0;
	
	private RandomAccessFile ramFile = null;
	private FileChannel fileChannel = null;
	private MappedByteBuffer mapBuffer = null;
	
	private DBFileLock localCacheFileLock = new DBFileLock();
	
	private String localCacheFileName = null;
	
	private String path = null;
	
	public DBIndexShmHdr getDbConfig() throws Exception{
		localCacheFileLock.lock(this.localCacheFileName);
		DBIndexShmHdr hdr = new DBIndexShmHdr();
		
		if (mapBuffer == null) {
			localCacheFileLock.unlock();
			
			return null;
		}
		
		mapBuffer.position(0);
		
		this.total = mapBuffer.getInt();
		this.currentWriteCtr = mapBuffer.getInt();
		this.currentReadCtr = mapBuffer.getInt();
		this.currentIndex = mapBuffer.getInt();
		
		hdr.setCurrentIndex(this.currentIndex);
		hdr.setCurrentReadCtr(this.currentReadCtr);
		hdr.setCurrentWriteCtr(this.currentWriteCtr);
		hdr.setTotal(this.total);
		
		localCacheFileLock.unlock();
		
		return hdr;
	}
	
	public boolean initialize(String fileName , String lockFile) throws Exception {
		
		try {
			path = System.getenv("DISTRIBUTED_STRING_LOCAL_CACHE_INDEX_PATH");
			if (path == null) {
				this.localCacheFileName = lockFile;
			} else {
				this.localCacheFileName = path + "/" + lockFile;
			}
			
			localCacheFileLock.lock(this.localCacheFileName);
			
			boolean isNew = false;
			File file = new File(fileName);
			if (!file.exists()) {
				isNew = true;
			}
			ramFile = new RandomAccessFile(fileName , "rw");
			
			fileChannel = ramFile.getChannel();
			
			mapBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE , 0 , 40);
			
			if (isNew) {
				mapBuffer.putInt(this.total);
				mapBuffer.putInt(this.currentWriteCtr);
				mapBuffer.putInt(this.currentReadCtr);
				mapBuffer.putInt(this.currentIndex);
			}
			
			localCacheFileLock.unlock();
		} catch (Exception e) {
			localCacheFileLock.unlock();
			return false;
		}
		
		return true;
	}
	
	public boolean setDbConfig4Write(DBIndexShmHdr obj) throws Exception {
		if (!localCacheFileLock.lock(this.localCacheFileName)){
			return false;
		}
		
		mapBuffer.position(4);
		mapBuffer.putInt(obj.getCurrentWriteCtr());
		mapBuffer.position(12);
		mapBuffer.putInt(obj.getCurrentIndex());
		
		localCacheFileLock.unlock();
		
		return true;
	}
	
	public boolean setDbConfig4Read(DBIndexShmHdr obj) throws Exception {
		if (!localCacheFileLock.lock(this.localCacheFileName)){
			return false;
		}
		
		mapBuffer.position(8);
		mapBuffer.putInt(obj.getCurrentReadCtr());
		
		localCacheFileLock.unlock();
		
		return true;
	}
	
	public boolean destroy() throws Exception{
		try {
			localCacheFileLock.lock(this.localCacheFileName);
			
			if (fileChannel != null) {
				fileChannel.close();
			}
			
			if (ramFile != null) {
				ramFile.close();
			}
			
			mapBuffer = null;
			
			localCacheFileLock.unlock();
			
			return true;
		} catch (Exception e) {
			localCacheFileLock.unlock();
			
			return false;
		}
	}
}
