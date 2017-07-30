package com.vip.local.cache.test;

import java.lang.management.ManagementFactory;

import com.vip.local.cache.util.LocalCacheFileLock;

import junit.framework.TestCase;

class Tester extends Thread {
	
	private LocalCacheFileLock localCacheFileLock = new LocalCacheFileLock();
	
	public void run(){
		while (true) {
			try {
				localCacheFileLock.lock("test.shm");
				System.out.println(ManagementFactory.getRuntimeMXBean().getName() + " obtain the file lock.");
				Thread.sleep(5000);				
				localCacheFileLock.unlock();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

public class FileLockTest extends TestCase {
	
	public void testFileLock00() throws InterruptedException{
		new Tester().start();
		
		Thread.sleep(30000);
	}
}
