package org.demo.chapter2.recipe5.task;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 价格信息类，存放两个产品的价格
 * writer线程更改两个产品的价格，reader线程读取两个产品的价格
 */
public class PricesInfo {
	
	/**
	 * 两个产品的价格
	 */
	private double price1;
	private double price2;
	
	/**
	 * 读写锁控制对价格的访问
	 */
	private ReadWriteLock lock;
	
	/**
	 * 构造方法初始化价格和读写锁
	 */
	public PricesInfo(){
		price1=1.0;
		price2=2.0;
		lock=new ReentrantReadWriteLock();
	}

	/**
	 * 返回第一个产品的价格
	 * @return 第一个产品的价格
	 */
	public double getPrice1() {
		lock.readLock().lock();
		double value=price1;
		lock.readLock().unlock();
		return value;
	}

	/**
	 * 返回第二个产品的价格
	 * @return 第二个产品的价格
	 */
	public double getPrice2() {
		lock.readLock().lock();
		double value=price2;
		lock.readLock().unlock();
		return value;
	}

	/**
	 * 更改产品的价格
	 * @param price1 第一个产品的价格
	 * @param price2 第二个产品的价格
	 */
	public void setPrices(double price1, double price2) {
		lock.writeLock().lock();
		this.price1=price1;
		this.price2=price2;
		lock.writeLock().unlock();
	}
}
