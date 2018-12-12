package org.demo.chapter2.recipe7.task;

import java.util.Random;

/**
 * 该类从缓冲区读取数据行并处理它
 */
public class Consumer implements Runnable {

	/**
	 * 缓冲区
	 */
	private Buffer buffer;
	
	/**
	 * 构造方法 初始化缓冲区
	 * @param buffer 缓冲区
	 */
	public Consumer (Buffer buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * 缓冲区中有待处理的数据行，尝试读取并处理
	 */
	@Override
	public void run() {
		while (buffer.hasPendingLines()) {
			String line=buffer.get();
			processLine(line);
		}
	}

	/**
	 * 模拟处理一行数据的方法，需要花费10毫秒
	 * @param line 待处理的数据
	 */
	private void processLine(String line) {
		try {
			Random random = new Random();
			Thread.sleep(random.nextInt(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

}
