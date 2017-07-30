package com.vip.local.cache.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

public class LocalCacheSharedMemory {
	private int totalRecored = 16;
	private int pageFlag = 0;
	private int writeOffset = 0;
	private int writeCtr = 0;
	private int readOffset = 0;
	private int readCtr = 0;
	
	private RandomAccessFile ramFile = null;
	private FileChannel fileChannel = null;
	private MappedByteBuffer mapBuffer = null;
	
	public boolean initialize(String fileName) {
		try {
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
				mapBuffer.putInt(pageFlag);
				mapBuffer.putInt(writeOffset);
				mapBuffer.putInt(writeCtr);
				mapBuffer.putInt(readOffset);
				mapBuffer.putInt(readCtr);
			}
			
			mapBuffer.position(0);
			this.totalRecored = mapBuffer.getInt();
			this.pageFlag = mapBuffer.getInt();
			this.writeOffset = mapBuffer.getInt();
			this.writeCtr = mapBuffer.getInt();
			this.readOffset = mapBuffer.getInt();
			this.readCtr = mapBuffer.getInt();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public boolean write(SharedMemoryObject obj) throws IOException {
		byte [] in = obj.toBuilder().build().toByteArray();
		
		if (this.writeCtr >= this.totalRecored) {
			this.pageFlag = 1;
			this.writeCtr = 0;
			this.writeOffset = 0;
		}		
		
		mapBuffer.position(24 + this.writeOffset);
		
		mapBuffer.putInt(in.length);
		mapBuffer.put(in);
		
		this.writeCtr += 1;
		this.writeOffset += in.length + 4;
		
		mapBuffer.position(4);
		mapBuffer.putInt(pageFlag);
		mapBuffer.putInt(writeOffset);
		mapBuffer.putInt(writeCtr);
		
		return true;
	}
	
	public SharedMemoryObject read() throws IOException {
		
		if (this.pageFlag == 0 && this.readCtr >= this.writeCtr) {
			return null;
		}
		
		if (this.readCtr >= this.totalRecored) {
			this.readCtr = 0;
			this.readOffset = 0;
			this.pageFlag = 0;
		}
		
		mapBuffer.position(24 + this.readOffset);
		
		int length = mapBuffer.getInt();
		byte [] dst = new byte[length];
		mapBuffer.get(dst, 0 , length);
		
		this.readCtr ++;
		this.readOffset += dst.length + 4;
		
		mapBuffer.position(4);
		mapBuffer.putInt(this.pageFlag);
		
		mapBuffer.position(16);
		mapBuffer.putInt(this.readOffset);
		mapBuffer.putInt(this.readCtr);
		
		return SharedMemoryObject.parseFrom(dst);
	}
	
	public boolean destroy(){
		try {
			if (fileChannel != null) {
				fileChannel.close();
			}
			
			if (ramFile != null) {
				ramFile.close();
			}
			
			mapBuffer = null;
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
