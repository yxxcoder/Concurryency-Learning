package org.demo.chapter2.recipe5;

import org.demo.chapter2.recipe5.task.PricesInfo;
import org.demo.chapter2.recipe5.task.Reader;
import org.demo.chapter2.recipe5.task.Writer;

/**
 * 使用读写锁实现同步数据访问
 */
public class Main {

	/**
	 * 启动5个Reader线程读取价格信息，启动1个Writer线程
	 * 读操作锁时可多个线程同时访问，写操作锁时只允许一个线程进行。一个线程写操作时，其他线程不能执行读操作
	 */
	public static void main(String[] args) {

		// 创建产品信息对象
		PricesInfo pricesInfo=new PricesInfo();
		
		Reader[]  readers = new Reader[5];
		Thread[]  threadsReader = new Thread[5];
		
		// 创建5个Reader线程读取价格
		for (int i = 0; i < 5; i++){
			readers[i] = new Reader(pricesInfo);
			threadsReader[i] = new Thread(readers[i]);
		}
		
		// 创建1个Writer线程修改价格
		Writer writer = new Writer(pricesInfo);
		Thread threadWriter = new Thread(writer);
		
		// 启动线程
		for (int i = 0; i < 5; i++){
			threadsReader[i].start();
		}
		threadWriter.start();
	}

}
