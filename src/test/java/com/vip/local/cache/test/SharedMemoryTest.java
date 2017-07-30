package com.vip.local.cache.test;

import java.io.IOException;

import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;
import com.vip.local.cache.util.LocalCacheSharedMemory;

import junit.framework.TestCase;

public class SharedMemoryTest extends TestCase {
	public void testSharedMemory() throws IOException{
		LocalCacheSharedMemory shm = new LocalCacheSharedMemory();
		
		shm.initialize("test0.shm");
		
		for (int i = 0;i < 5;i ++) {
			SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp("127.0.0.1")
					.setKey("test-key").setValue("hello world" + i).setTimestamp(System.currentTimeMillis()).build();
			shm.write(obj);
		}
		
		while (true) {
			SharedMemoryObject out = shm.read();
			if (out == null) {
				break;
			}
			System.out.println("output:" + out.getIp() + " " + out.getKey() + " " + out.getValue() + " " + out.getTimestamp());
		}
			
		shm.destroy();
	}
}
