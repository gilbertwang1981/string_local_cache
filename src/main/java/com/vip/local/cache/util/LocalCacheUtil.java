package com.vip.local.cache.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LocalCacheUtil {
	
	public static List<String> tokenizer(String str , String del) {
		StringTokenizer tokenizer = null;
		if (del == null) {
			tokenizer = new StringTokenizer(str);
		} else {
			tokenizer = new StringTokenizer(str , del);
		}
		
		ArrayList<String> params = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			params.add(tokenizer.nextToken());
		}
		
		return params;
	}
}
