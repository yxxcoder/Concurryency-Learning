package org.demo.chapter2.recipe4.task;

/**
 * 此类模拟发送要打印的文档的作业
 */
public class Job implements Runnable {

	/**
	 * 打印队列
	 */
	private PrintQueue printQueue;
	
	/**
	 * 构造方法
	 * @param printQueue 打印队列
	 */
	public Job(PrintQueue printQueue){
		this.printQueue = printQueue;
	}
	
	/**
	 * 将打印任务发送到打印队列并等待执行完毕
	 */
	@Override
	public void run() {
		System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
		printQueue.printJob(new Object());
		System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
	}
}
