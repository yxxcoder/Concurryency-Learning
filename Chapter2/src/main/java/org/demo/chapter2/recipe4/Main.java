package org.demo.chapter2.recipe4;


import org.demo.chapter2.recipe4.task.Job;
import org.demo.chapter2.recipe4.task.PrintQueue;

/**
 * 使用锁实现同步
 */
public class Main {

    /**
     * 启动10个线程同时将打印任务发送到打印队列
     */
    public static void main(String args[]) {

        // 创建打印任务
        PrintQueue printQueue = new PrintQueue();

        // 创建10个线程
        Thread thread[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        // 启动线程
        for (int i = 0; i < 10; i++) {
            thread[i].start();
        }
    }

}
