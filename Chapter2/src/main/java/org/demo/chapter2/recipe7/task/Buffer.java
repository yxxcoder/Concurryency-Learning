package org.demo.chapter2.recipe7.task;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数据缓冲区
 * 生产者读取数据写入缓冲区，消费者将缓冲区中的数据消费掉
 */
public class Buffer {

	/**
	 * 存放共享数据
	 */
	private LinkedList<String> buffer;

	/**
	 * 缓冲区大小
	 */
	private int maxSize;

	/**
	 * 控制对缓冲区的访问
	 */
	private ReentrantLock lock;

	/**
	 * Conditions to control that the buffer has lines and has empty space
	 */
	private Condition lines;
	private Condition space;

	/**
	 * 表明缓冲区中是否还有数据
	 */
	private boolean pendingLines;

	/**
	 * 构造方法 初始化缓冲区
	 * @param maxSize 缓冲区大小
	 */
	public Buffer(int maxSize) {
		this.maxSize = maxSize;
		buffer = new LinkedList<>();
		lock = new ReentrantLock();
		lines = lock.newCondition();
		space = lock.newCondition();
		pendingLines = true;
	}

	/**
	 * 写入一行数据到缓冲区
	 * @param line 要写入缓冲区的行
	 */
	public void insert(String line) {
		lock.lock();
		try {
			while (buffer.size() == maxSize) {
				space.await();
			}
			buffer.offer(line);
			System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread()
					.getName(), buffer.size());
			lines.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 从缓冲区返回一行数据
	 * @return 缓冲区中的一行
	 */
	public String get() {
		String line=null;
		lock.lock();		
		try {
			while ((buffer.size() == 0) &&(hasPendingLines())) {
				lines.await();
			}
			
			if (hasPendingLines()) {
				line = buffer.poll();
				System.out.printf("%s: Line Readed: %d\n", Thread.currentThread().getName(),buffer.size());
				space.signalAll();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return line;
	}

	/**
	 * 设置pendingLines变量的值
	 * @param pendingLines 改变后的值
	 */
	public void setPendingLines(boolean pendingLines) {
		this.pendingLines = pendingLines;
	}

	/**
	 * 有可以处理的行时返回true，否则返回false
	 * @return 是否有数据行可以处理
	 */
	public boolean hasPendingLines() {
		return pendingLines || buffer.size() > 0;
	}

}
