package org.demo.chapter3.recipe1;

import org.demo.chapter3.recipe1.task.Job;
import org.demo.chapter3.recipe1.task.PrintQueue;

/**
 * 资源的并发访问控制
 * 利用信号量Semaphore控制对资源的访问
 */
public class Main {

	/**
	 * 启动10个线程同时将打印任务发送到打印队列
	 */
	public static void main (String args[]){
		
		// 创建打印队列
		PrintQueue printQueue = new PrintQueue();
		
		// 创建10个打印任务线程
		Thread thread[] = new Thread[10];
		for (int i = 0; i < 10; i++){
			thread[i] = new Thread(new Job(printQueue),"Thread " + i);
		}
		
		// 启动线程
		for (int i = 0; i < 10; i++){
			thread[i].start();
		}
	}
}
