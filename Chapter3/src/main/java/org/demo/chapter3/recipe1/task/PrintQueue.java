package org.demo.chapter3.recipe1.task;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 模拟文档打印
 * 利用信号量控制对打印机的访问
 */
public class PrintQueue {

    /**
     * 信号量控制对打印机的访问
     */
    private final Semaphore semaphore;

    /**
     * 初始化信号量
     */
    public PrintQueue() {
        semaphore = new Semaphore(1);
    }

    /**
     * 模拟打印文档的方法
     *
     * @param document 待打印的文档
     */
    public void printJob(Object document) {
        try {
            // 获取信号量的访问权限
            // 如果正在打印其他作业，则此线程将休眠，直到获得对信号量的访问权限
            semaphore.acquire();

            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(), duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放信号量
            // 如果有其他线程在等待此信号量，JVM会选择其中一个线程允许其访问临界区
            semaphore.release();
        }
    }

}
