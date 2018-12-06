package org.demo.chapter2.recipe2.task;

/**
 * 模拟电影院
 */
public class Cinema {
	
	/**
	 * 保存两个电影的剩余票数
	 */
	private long vacanciesCinema1;
	private long vacanciesCinema2;

	/**
	 * 用于并发控制属性
	 * controlCinema1用于控制对vacanciesCinema1属性的访问，同一时间只有一个线程可以访问vacanciesCinema1属性
	 * 同理，controlCinema2用于对vacanciesCinema1属性的访问控制
	 */
	private final Object controlCinema1, controlCinema2;
	
	/**
	 * 构造方法，初始化对象
	 */
	public Cinema(){
		controlCinema1=new Object();
		controlCinema2=new Object();
		vacanciesCinema1=20;
		vacanciesCinema2=20;
	}
	
	/**
	 * 该方法实现了电影院1的售票操作
	 * @param number 售票数量
	 * @return 如果门票售出则为true，如果没有则为false
	 */
	public boolean sellTickets1 (int number) {
		synchronized (controlCinema1) {
			if (number<vacanciesCinema1) {
				vacanciesCinema1-=number;
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 该方法实现了电影院2的售票操作
	 * @param number 售票数量
	 * @return 如果门票售出则为true，如果没有则为false
	 */
	public boolean sellTickets2 (int number){
		synchronized (controlCinema2) {
			if (number<vacanciesCinema2) {
				vacanciesCinema2-=number;
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 该方法实现了电影院1的回收票的操作
	 * @param number 回收的票数
	 * @return true
	 */
	public boolean returnTickets1 (int number) {
		synchronized (controlCinema1) {
			vacanciesCinema1+=number;
			return true;
		}
	}

	/**
	 * 该方法实现了电影院2的回收票的操作
	 * @param number 回收的票数
	 * @return true
	 */
	public boolean returnTickets2 (int number) {
		synchronized (controlCinema2) {
			vacanciesCinema2+=number;
			return true;
		}
	}

	/**
	 * 返回电影院1的余票
	 * @return 电影院1的余票
	 */
	public long getVacanciesCinema1() {
		return vacanciesCinema1;
	}

	/**
	 * 返回电影院2的余票
	 * @return 电影院2的余票
	 */
	public long getVacanciesCinema2() {
		return vacanciesCinema2;
	}

}
