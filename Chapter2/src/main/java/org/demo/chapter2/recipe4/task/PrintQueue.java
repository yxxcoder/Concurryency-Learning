package org.demo.chapter2.recipe4.task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 此类模拟打印队列
 */
public class PrintQueue {

    /**
     * 该锁用以控制对队列的访问
     */
    private final Lock queueLock = new ReentrantLock();

    /**
     * 打印文档
     *
     * @param document 打印任务
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }

        // 也可以通过tryLock获取锁
//		if (queueLock.tryLock()) {
//			System.out.println("Get Lock Succeed");
//		} else {
//			System.out.println("Get Lock Failed");
//		}
    }
}
