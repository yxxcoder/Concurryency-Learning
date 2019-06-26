package org.demo.chapter3.recipe1.task;

/**
 * 模拟打印任务
 */
public class Job implements Runnable {

    /**
     * 打印队列
     */
    private PrintQueue printQueue;

    /**
     * 初始化打印队列
     *
     * @param printQueue 打印队列
     */
    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    /**
     * 将打印任务发送到打印队列并等待完成
     */
    @Override
    public void run() {
        System.out.printf("%s: Going to print a job\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }
}
