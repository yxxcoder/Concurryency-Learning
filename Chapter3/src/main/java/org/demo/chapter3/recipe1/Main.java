package org.demo.chapter3.recipe1;

import org.demo.chapter3.recipe1.task.Job;
import org.demo.chapter3.recipe1.task.PrintQueue;

/**
 * 资源的并发访问控制
 * 利用信号量 Semaphore 控制对资源的访问
 *
 * <p>
 *     实现一个打印队列，并发任务将使用它来完成打印。这个打印队列受二进制信号量保护，
 *     因而同时只有一个线程可以执行打印
 * </p>
 */
public class Main {

    /**
     * 启动10个线程同时将打印任务发送到打印队列
     */
    public static void main(String args[]) {

        // 创建打印队列
        PrintQueue printQueue = new PrintQueue();

        // 创建10个打印任务线程
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
