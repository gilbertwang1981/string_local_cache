package com.vip.local.cache.test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vip.local.cache.proto.SharedMemoryStruct;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject;
import com.vip.local.cache.proto.SharedMemoryStruct.SharedMemoryObject.Builder;

import junit.framework.TestCase;

public class SharedMemoryObjectTest extends TestCase {
	public void testSharedMemoryObject() {
		Builder builder = SharedMemoryStruct.SharedMemoryObject.newBuilder().setIp("127.0.0.1").setKey("test_key")
		.setTimestamp(System.currentTimeMillis()).setValue("this is a test!");
		
		SharedMemoryObject obj = builder.build();
		
		byte [] in = obj.toByteArray();
		
		SharedMemoryObject po;
		try {
			po = SharedMemoryObject.parseFrom(in);
			
			System.out.println(po.getIp() + "/" + po.getKey() + "/" + po.getValue());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
}
