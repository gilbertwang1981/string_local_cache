package com.vip.local.cache.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

public class DBDataShm {
	private int totalRecored = DBShmConst.DB_SHM_SIZE_IN_EACH_DB;
	private int rPage = 0;
	private int wPage = 0;
	private int writeOffset = 0;
	private int writeCtr = 0;
	private int readOffset = 0;
	private int readCtr = 0;
	
	private RandomAccessFile ramFile = null;
	private FileChannel fileChannel = null;
	private MappedByteBuffer mapBuffer = null;
	
	private DBFileLock localCacheFileLock = new DBFileLock();
	
	private String localCacheFileName = null;
	
	private String path = null;
	
	public DBDataShmHdr getShmConfig() throws Exception{
		localCacheFileLock.lock(this.localCacheFileName);
		DBDataShmHdr hdr = new DBDataShmHdr();
		
		if (mapBuffer == null) {
			localCacheFileLock.unlock();
			
			return null;
		}
		
		mapBuffer.position(0);
		
		this.totalRecored = mapBuffer.getInt();
		this.wPage = mapBuffer.getInt();
		this.rPage = mapBuffer.getInt();
		this.writeOffset = mapBuffer.getInt();
		this.writeCtr = mapBuffer.getInt();
		this.readOffset = mapBuffer.getInt();
		this.readCtr = mapBuffer.getInt();
		
		hdr.setPages4Read(this.rPage);
		hdr.setPages4Write(this.wPage);
		hdr.setReadCtr(this.readCtr);
		hdr.setReadOffset(this.readOffset);
		hdr.setTotalRecord(this.totalRecored);
		hdr.setWriteCtr(this.writeCtr);
		hdr.setWriteOffset(this.writeOffset);
		
		localCacheFileLock.unlock();
		
		return hdr;
	}
	
	public boolean initialize(String fileName , String lockFile) throws Exception {
		return this.initialize(fileName , lockFile , false);
	}
	
	public boolean initialize(String fileName , String lockFile , boolean force) throws Exception {
		
		try {
			String absolutedFileName = null;
			path = System.getenv("DISTRIBUTED_STRING_LOCAL_CACHE_DATA_PATH");
			if (path == null) {
				this.localCacheFileName = lockFile;
				absolutedFileName = fileName;
			} else {
				this.localCacheFileName = path + "/" + lockFile;
				absolutedFileName = path + "/" + fileName;
			}
			
			localCacheFileLock.lock(this.localCacheFileName);
			
			boolean isNew = false;
			File file = new File(absolutedFileName);
			if (force) {
				file.delete();
				isNew = true;
			} else if (!file.exists()) {
				isNew = true;
			}
			ramFile = new RandomAccessFile(absolutedFileName , "rw");
			
			fileChannel = ramFile.getChannel();
			
			mapBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE , 0 , totalRecored * DBShmConst.DB_SHM_MAX_SIZE_IN_EACH_ELEM);
			
			if (isNew) {
				mapBuffer.putInt(totalRecored);
				mapBuffer.putInt(wPage);
				mapBuffer.putInt(rPage);
				mapBuffer.putInt(writeOffset);
				mapBuffer.putInt(writeCtr);
				mapBuffer.putInt(readOffset);
				mapBuffer.putInt(readCtr);
			}
			
			localCacheFileLock.unlock();
		} catch (Exception e) {
			localCacheFileLock.unlock();
			return false;
		}
		
		return true;
	}
	
	public boolean write(SharedMemoryObject obj) throws Exception {
		if (!localCacheFileLock.lock(this.localCacheFileName)){
			return false;
		}
		
		mapBuffer.position(0);
		this.totalRecored = mapBuffer.getInt();
		this.wPage = mapBuffer.getInt();
		this.rPage = mapBuffer.getInt();
		this.writeOffset = mapBuffer.getInt();
		this.writeCtr = mapBuffer.getInt();
		this.readOffset = mapBuffer.getInt();
		this.readCtr = mapBuffer.getInt();
		
		byte [] in = obj.toBuilder().build().toByteArray();
		if (in.length > DBShmConst.DB_SHM_MAX_SIZE_IN_EACH_ELEM) {
			localCacheFileLock.unlock();
			
			return false;
		}
		
		if (this.writeCtr > this.totalRecored) {
			this.wPage ++;
			this.writeCtr = 0;
			this.writeOffset = 0;
		}		
		
		mapBuffer.position(28 + this.writeOffset);
		
		mapBuffer.putInt(in.length);
		mapBuffer.put(in);
		
		this.writeCtr += 1;
		this.writeOffset += in.length + 4;
		
		mapBuffer.position(4);
		mapBuffer.putInt(wPage);
		
		mapBuffer.position(12);
		mapBuffer.putInt(writeOffset);
		mapBuffer.putInt(writeCtr);
		
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
