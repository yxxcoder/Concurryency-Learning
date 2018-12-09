package org.demo.chapter2.recipe6.task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PrintQueue {

	/**
	 * 创建锁用以控制对队列的访问
	 */
	private final Lock queueLock = new ReentrantLock(false);
	
	/**
	 * 打印作业的方法
	 * 打印分为两个阶段，显示公平属性如何影响线程的选举有锁的控制权
	 * @param document 打印作业的方法
	 */
	public void printJob(Object document){
		queueLock.lock();
		
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(),(duration / 1000));
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			queueLock.unlock();
		}
		
		
		queueLock.lock();
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(),(duration / 1000));
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			queueLock.unlock();
		}
	}
}
