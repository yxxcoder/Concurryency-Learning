package org.demo.chapter2.recipe5.task;

/**
 * 这个类用于更新产品价格信息
 */
public class Writer implements Runnable {

	/**
	 * 保存价格信息对象
	 */
	private PricesInfo pricesInfo;

	/**
	 * 构造方法初始化价格信息对象
	 * @param pricesInfo 需要初始化的价格信息对象
	 */
	public Writer(PricesInfo pricesInfo){
		this.pricesInfo=pricesInfo;
	}
	
	/**
	 * 更新两个产品的价格
	 */
	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			System.out.print("Writer: Attempt to modify the prices.\n");
			pricesInfo.setPrices(Math.random() * 10, Math.random() * 8);
			System.out.print("Writer: Prices have been modified.\n");
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
