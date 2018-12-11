package org.demo.chapter2.recipe6.task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 此类模拟打印队列
 */
public class PrintQueue {

	/**
	 * 创建锁用以控制对队列的访问
	 * 默认fair值为false，即为非公平锁，为true时为公平锁
	 * 如果一个锁是公平的，那么锁的获取顺序就应该符合请求上的绝对时间顺序，满足FIFO
	 * 公平锁每次都是从同步队列中的第一个节点获取到锁，而非公平性锁则不一定，有可能刚释放锁的线程能再次获取到锁
	 */
	private final Lock queueLock = new ReentrantLock(false);
	
	/**
	 * 打印作业的方法
	 * 打印分为两个阶段，体验公平属性如何影响线程选举的有锁控制权
	 * @param document 打印的作业
	 */
	public void printJob(Object document){
		queueLock.lock();
		
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.printf("%s: PrintQueue: First Printing a Job during %d seconds\n", Thread.currentThread().getName(),(duration / 1000));
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			queueLock.unlock();
		}


		queueLock.lock();
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.printf("%s: PrintQueue: Second Printing a Job during %d seconds\n", Thread.currentThread().getName(),(duration / 1000));
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			queueLock.unlock();
		}
	}
}
