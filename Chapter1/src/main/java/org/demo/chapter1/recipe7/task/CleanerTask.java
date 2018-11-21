package org.demo.chapter1.recipe7.task;

import org.demo.chapter1.recipe7.event.Event;

import java.util.Date;
import java.util.Deque;

/**
 * 检查队列中的Event对象，删除存活超过10秒的Event对象
 *
 */
public class CleanerTask extends Thread {

	/**
	 * 存放Event对象的队列
	 */
	private Deque<Event> deque;

	/**
	 * 构造方法
	 * @param deque 存放Event对象的队列
	 */
	public CleanerTask(Deque<Event> deque) {
		this.deque = deque;
		// 把当前线程设置为守护线程
		setDaemon(true);
	}


	/**
	 * 无限循环，每次获取当前时间并调用clean方法
	 */
	@Override
	public void run() {
		while (true) {
			Date date = new Date();
			clean(date);
		}
	}

	/**
	 * Method that review the Events data structure and delete
	 * the events older than ten seconds
	 * @param date
	 */
	private void clean(Date date) {
		long difference;
		boolean delete;
		
		if (deque.size()==0) {
			System.out.println("deque is empty");
			return;
		}
		
		delete=false;
		do {
			Event e = deque.getLast();
			difference = date.getTime() - e.getDate().getTime();
			if (difference > 10000) {
				System.out.printf("Cleaner: %s\n",e.getEvent());
				deque.removeLast();
				delete=true;
			}	
		} while (difference > 10000);
		if (delete){
			System.out.printf("Cleaner: Size of the queue: %d\n",deque.size());
		}
	}
}
