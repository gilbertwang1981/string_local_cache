package com.vip.local.cache.test;

import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;
import com.vip.local.cache.util.LocalCacheSharedMemory;
import com.vip.local.cache.util.LocalCacheShmHdr;

import junit.framework.TestCase;

public class InterProcessShmTest extends TestCase {
	public void testProducer() throws Exception{
		LocalCacheSharedMemory shm = new LocalCacheSharedMemory();
		
		shm.initialize("test1.shm");
		
		for (int i = 0;i < 60;i ++) {
			SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp("127.0.0.1")
					.setKey("test-key").setValue("hello world" + i).setTimestamp(System.currentTimeMillis()).build();
			shm.write(obj);
			
			Thread.sleep(1000);
		}
		
		shm.destroy();
	}

	public void testConsumer() throws Exception {
		LocalCacheSharedMemory shm = new LocalCacheSharedMemory();
		
		shm.initialize("test1.shm");
		
		while (true) {
			SharedMemoryObject out = shm.read();
			if (out == null) {
				
				LocalCacheShmHdr hdr = shm.getShmConfig();
				System.out.println(hdr.getTotalRecord() + "/" + hdr.getReadCtr() + "/" + hdr.getWriteCtr() + "/" + hdr.getPages4Read() + "/" + hdr.getPages4Write() + 
						"/" + hdr.getReadOffset() + "/" + hdr.getWriteOffset());
				
				Thread.sleep(3000);
				continue;
			}
			
			System.out.println("output:" + out.getIp() + " " + out.getKey() + " " + out.getValue() + " " + out.getTimestamp());
		}
	}
}
