package com.vip.local.cache.db;

public class DBDataShmHdr {
	private int totalRecord;
	private int pages4Write;
	private int pages4Read;
	private int writeOffset;
	private int writeCtr;
	private int readOffset;
	private int readCtr;
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getPages4Write() {
		return pages4Write;
	}
	public void setPages4Write(int pages4Write) {
		this.pages4Write = pages4Write;
	}
	public int getWriteOffset() {
		return writeOffset;
	}
	public void setWriteOffset(int writeOffset) {
		this.writeOffset = writeOffset;
	}
	public int getPages4Read() {
		return pages4Read;
	}
	public void setPages4Read(int pages4Read) {
		this.pages4Read = pages4Read;
	}
	public int getWriteCtr() {
		return writeCtr;
	}
	public void setWriteCtr(int writeCtr) {
		this.writeCtr = writeCtr;
	}
	public int getReadOffset() {
		return readOffset;
	}
	public void setReadOffset(int readOffset) {
		this.readOffset = readOffset;
	}
	public int getReadCtr() {
		return readCtr;
	}
	public void setReadCtr(int readCtr) {
		this.readCtr = readCtr;
	}	
}
