package com.vip.local.cache.test;

import com.vip.local.cache.db.DBShm;
import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

import junit.framework.TestCase;

public class InterProcessShmTest extends TestCase {
	public void testProducer() throws Exception{
		DBShm shm = new DBShm();
		
		shm.initialize();
		
		for (int i = 0;i < 4096;i ++) {
			SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp("127.0.0.1").setOpType(2)
					.setKey("test-key").setValue("hello world" + i).setTimestamp(System.currentTimeMillis()).build();
			shm.write(obj);
			
			if (i % 10000 == 0) {
				Thread.sleep(100);
			}
		}
		
		shm.destory();
	}
}
