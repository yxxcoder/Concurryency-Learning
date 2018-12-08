package org.demo.chapter2.recipe5.task;

/**
 * 这个类用于读取产品价格信息
 */
public class Reader implements Runnable {

	/**
	 * 保存价格信息对象
	 */
	private PricesInfo pricesInfo;
	
	/**
	 * 构造方法初始化价格信息对象
	 * @param pricesInfo 需要初始化的价格信息对象
	 */
	public Reader (PricesInfo pricesInfo){
		this.pricesInfo=pricesInfo;
	}
	
	/**
	 * 读取两个产品的价格并打印到控制台
	 */
	@Override
	public void run() {
		for (int i = 0; i < 10; i++){
			System.out.printf("%s: Price 1: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice1());
			System.out.printf("%s: Price 2: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice2());
		}
	}

}
