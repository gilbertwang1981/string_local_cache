package com.vip.local.cache.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class LocalCacheFileLock {
	private FileChannel fileChannel = null;
	private FileLock fileLock = null;
	private FileOutputStream fileOutputStream = null;
	
	public boolean lock(String fileName) throws IOException{
		try {
			fileOutputStream = new FileOutputStream(fileName);
			fileChannel = fileOutputStream.getChannel();
		} catch (FileNotFoundException e) {
			File file = new File(fileName);
			try {
				if (!file.createNewFile()) {
					return false;
				}
				fileOutputStream = new FileOutputStream(fileName);
				fileChannel = fileOutputStream.getChannel();
			} catch (IOException exc) {
				return false;
			}
		}
		
		fileLock = fileChannel.lock();
		
		return true;
		
	}
	
	public void unlock() throws Exception{
		if (fileLock != null) {
			fileLock.release();
			fileChannel.close();
			fileOutputStream.close();
		} else {
			throw new Exception("Null pointer Exception");
		}
	}
}