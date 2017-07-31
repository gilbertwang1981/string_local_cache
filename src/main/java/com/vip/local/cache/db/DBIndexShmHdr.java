package com.vip.local.cache.db;

public class DBIndexShmHdr {
	private int total;
	private int currentWriteCtr;
	private int currentReadCtr;
	private int currentIndex;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCurrentWriteCtr() {
		return currentWriteCtr;
	}
	public void setCurrentWriteCtr(int currentWriteCtr) {
		this.currentWriteCtr = currentWriteCtr;
	}
	public int getCurrentReadCtr() {
		return currentReadCtr;
	}
	public void setCurrentReadCtr(int currentReadCtr) {
		this.currentReadCtr = currentReadCtr;
	}
	public int getCurrentIndex() {
		return currentIndex;
	}
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
}
