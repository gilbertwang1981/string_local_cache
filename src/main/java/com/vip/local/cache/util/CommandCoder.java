package com.vip.local.cache.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.vip.local.cache.define.LocalCacheCmdType;

public class CommandCoder {
	public static LocalCacheCmdType decodeCommand(String request) {
		StringTokenizer tokenizer = new StringTokenizer(request);
		ArrayList<String> params = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			params.add(tokenizer.nextToken());
		}
		
		if (params.size() == 0) {
			return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_INV;
		}
		
		String cmd = params.get(0);
		if (cmd.equalsIgnoreCase(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCommand())) {
			return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH;
		} else if (cmd.equalsIgnoreCase(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCommand())) {
			return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET;
		} else if (cmd.equalsIgnoreCase(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCommand())) {
			return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL;
		} else if (cmd.equalsIgnoreCase(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_HB.getCommand())) {
			return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_HB;
		}
		
		return LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_INV;
	}
	
	public static String encodeCommand(boolean code , String reason) {
		if (code) {
			return "OK\n";
		} else {
			return "NOK " + reason + "\n";
		}
	}
}