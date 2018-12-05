package org.demo.chapter2.recipe1.task;

/**
 * 银行账户类
 *
 */
public class Account {

	/**
	 * 账户余额
	 */
	private double balance;

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	/**
	 * 转入
	 * @param amount 转入的金额
	 */
	public synchronized void addAmount(double amount) {
		double tmp=balance;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tmp+=amount;
		balance=tmp;
	}
	
	/**
	 * 转出
	 * @param amount 转出的金额
	 */
	public synchronized void subtractAmount(double amount) {
		double tmp=balance;
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tmp-=amount;
		balance=tmp;
	}
	
}
