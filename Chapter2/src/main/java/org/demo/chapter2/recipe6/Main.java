package org.demo.chapter2.recipe6;

import org.demo.chapter2.recipe6.task.Job;
import org.demo.chapter2.recipe6.task.PrintQueue;

/**
 * 修改锁的公平性
 */
public class Main {
	
	/**
	 * 启动10个进程同时将打印任务发送到打印队列
	 */
	public static void main (String args[]){
		// 创建打印队列
		PrintQueue printQueue = new PrintQueue();
		
		// 创建10个线程
		Thread thread[] = new Thread[10];
		for (int i = 0; i < 10; i++){
			thread[i] = new Thread(new Job(printQueue),"Thread " + i);
		}
		
		// 每隔0.1s启动一个线程
		for (int i = 0; i < 10; i++){
			thread[i].start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
