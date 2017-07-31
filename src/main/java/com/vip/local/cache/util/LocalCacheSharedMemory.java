package com.vip.local.cache.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

public class LocalCacheSharedMemory {
	private int totalRecored = 1024;
	private int rPage = 0;
	private int wPage = 0;
	private int writeOffset = 0;
	private int writeCtr = 0;
	private int readOffset = 0;
	private int readCtr = 0;
	
	private RandomAccessFile ramFile = null;
	private FileChannel fileChannel = null;
	private MappedByteBuffer mapBuffer = null;
	
	private LocalCacheFileLock localCacheFileLock = new LocalCacheFileLock();
	
	private static final String LOCAL_CACHE_FILE_LOCK_NAME = "localcache.lock";
	
	public LocalCacheShmHdr getShmConfig() throws Exception{
		localCacheFileLock.lock(LOCAL_CACHE_FILE_LOCK_NAME);
		LocalCacheShmHdr hdr = new LocalCacheShmHdr();
		
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
	
	public boolean initialize(String fileName) throws Exception {
		try {
			localCacheFileLock.lock(LOCAL_CACHE_FILE_LOCK_NAME);
			
			boolean isNew = false;
			File file = new File(fileName);
			if (!file.exists()) {
				isNew = true;
			}
			ramFile = new RandomAccessFile(fileName , "rw");
			
			fileChannel = ramFile.getChannel();
			
			mapBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE , 0 , totalRecored * 1024 * 10);
			
			if (isNew) {
				mapBuffer.putInt(totalRecored);
				mapBuffer.putInt(wPage);
				mapBuffer.putInt(rPage);
				mapBuffer.putInt(writeOffset);
				mapBuffer.putInt(writeCtr);
				mapBuffer.putInt(readOffset);
				mapBuffer.putInt(readCtr);
			}
			
			mapBuffer.position(0);
			this.totalRecored = mapBuffer.getInt();
			this.wPage = mapBuffer.getInt();
			this.rPage = mapBuffer.getInt();
			this.writeOffset = mapBuffer.getInt();
			this.writeCtr = mapBuffer.getInt();
			this.readOffset = mapBuffer.getInt();
			this.readCtr = mapBuffer.getInt();
			
			localCacheFileLock.unlock();
		} catch (Exception e) {
			localCacheFileLock.unlock();
			return false;
		}
		
		return true;
	}
	
	public boolean write(SharedMemoryObject obj) throws Exception {
		localCacheFileLock.lock(LOCAL_CACHE_FILE_LOCK_NAME);
		
		byte [] in = obj.toBuilder().build().toByteArray();
		
		if (this.writeCtr >= this.totalRecored) {
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
	
	public SharedMemoryObject read() throws Exception {
		
		localCacheFileLock.lock(LOCAL_CACHE_FILE_LOCK_NAME);
		
		mapBuffer.position(0);
		this.totalRecored = mapBuffer.getInt();
		this.wPage = mapBuffer.getInt();
		this.rPage = mapBuffer.getInt();
		this.writeOffset = mapBuffer.getInt();
		this.writeCtr = mapBuffer.getInt();
		this.readOffset = mapBuffer.getInt();
		this.readCtr = mapBuffer.getInt();
		
		if (this.rPage == this.wPage && this.readCtr == this.writeCtr) {
			localCacheFileLock.unlock();
			
			return null;
		}
		
		if (this.rPage < this.wPage && this.readCtr == this.totalRecored) {
				this.readCtr = 0;
				this.readOffset = 0;
				this.rPage ++;
		}
		
		mapBuffer.position(28 + this.readOffset);
		
		int length = mapBuffer.getInt();
		byte [] dst = new byte[length];
		mapBuffer.get(dst, 0 , length);
		
		this.readCtr ++;
		this.readOffset += dst.length + 4;
		
		mapBuffer.position(8);
		mapBuffer.putInt(this.rPage);
		
		mapBuffer.position(20);
		mapBuffer.putInt(this.readOffset);
		mapBuffer.putInt(this.readCtr);
		
		localCacheFileLock.unlock();
		
		return SharedMemoryObject.parseFrom(dst);
	}
	
	public boolean destroy() throws Exception{
		try {
			localCacheFileLock.lock(LOCAL_CACHE_FILE_LOCK_NAME);
			
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
