package org.demo.chapter2.recipe7.task;

import org.demo.chapter2.recipe7.utils.FileMock;


/**
 * 此类从文件中获取数据行，并将其写入在缓冲区中（如果有空间）
 */
public class Producer implements Runnable {

	/**
	 * 待读取的文件
	 */
	private FileMock mock;
	
	/**
	 * 缓冲区
	 */
	private Buffer buffer;
	
	/**
	 * 构造方法 初始化producer对象
	 * @param mock 待读取的文件
	 * @param buffer 缓冲区
	 */
	public Producer (FileMock mock, Buffer buffer){
		this.mock=mock;
		this.buffer=buffer;	
	}
	
	/**
	 * 读取文件中的数据，并写入缓冲区
	 */
	@Override
	public void run() {
		// Producer开始工作，结束前有一直有数据写入缓冲区
		buffer.setPendingLines(true);
		while (mock.hasMoreLines()){
			String line=mock.getLine();
			buffer.insert(line);
		}
		// Producer工作结束，不会再有数据写入
		buffer.setPendingLines(false);
	}

}
