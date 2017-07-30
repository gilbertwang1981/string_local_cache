package com.vip.local.cache.test;

import java.io.UnsupportedEncodingException;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.vip.local.cache.proto.CommonLocalCache;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand.Builder;

import junit.framework.TestCase;

public class CommonLocalCacheTest extends TestCase {
	public void testCommonLocalCacheObject() throws UnsupportedEncodingException{
		Builder builder = CommonLocalCache.CacheCommand.newBuilder().setMessageType(5).setKey("test-key").setParameter(
				"this is a parameter").setValue(ByteString.copyFromUtf8("this is a test!"));
		
		byte [] in = builder.build().toByteArray();
		
		try {
			CacheCommand cc = CacheCommand.parseFrom(in);
			
			System.out.println(cc.getMessageType() + "/" + cc.getKey() + "/" + cc.getParameter() + "/" + cc.getValue().toString("utf-8"));
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
}
