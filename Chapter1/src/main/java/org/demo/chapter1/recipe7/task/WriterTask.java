package org.demo.chapter1.recipe7.task;

import org.demo.chapter1.recipe7.event.Event;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;


/**
 * 每秒钟创建一个Event对象放入队列中，循环100次
 *
 */
public class WriterTask implements Runnable {

	/**
	 * 存放Event对象的双向队列
	 */
	Deque<Event> deque;

	/**
	 * 构造方法
	 * @param deque 存放Event对象的队列
	 */
	public WriterTask (Deque<Event> deque){
		this.deque=deque;
	}
	

	@Override
	public void run() {

		for (int i=1; i<100; i++) {
			// 创建Event对象并添加到队列中
			Event event=new Event();
			event.setDate(new Date());
			event.setEvent(String.format("The thread %s has generated an event",Thread.currentThread().getId()));

			deque.addFirst(event);
			try {
				// 休眠1秒
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Cleaner: OVER");
	}
}
