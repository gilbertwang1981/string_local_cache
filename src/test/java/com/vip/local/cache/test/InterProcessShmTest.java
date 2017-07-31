package com.vip.local.cache.test;

import com.vip.local.cache.db.DBShm;
import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;

import junit.framework.TestCase;

public class InterProcessShmTest extends TestCase {
	public void testProducer() throws Exception{
		DBShm shm = new DBShm();
		
		shm.initialize();
		
		for (int i = 0;i < 50000;i ++) {
			SharedMemoryObject obj = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp("127.0.0.1")
					.setKey("test-key").setValue("hello world" + i).setTimestamp(System.currentTimeMillis()).build();
			shm.write(obj);
			
			if (i % 5000 == 0) {
				Thread.sleep(100);
			}
		}
		
		shm.destory();
	}

	public void testConsumer() throws Exception {
		DBShm shm = new DBShm();
		
		shm.initialize();
		
		while (true) {
			SharedMemoryObject out = shm.read();
			if (out == null) {
				Thread.sleep(500);
				continue;
			}
			
			System.out.println("output:" + out.getIp() + " " + out.getKey() + " " + out.getValue() + " " + out.getTimestamp());
		}
	}
}
