package org.demo.chapter3.recipe2;


import org.demo.chapter3.recipe2.task.Job;
import org.demo.chapter3.recipe2.task.PrintQueue;

/**
 * 资源的多副本的并发访问控制
 * <p>
 * 利用信号量Semaphore控制对多个资源的访问
 * </p>
 */
public class Main {

    /**
     * 启动12个线程同时将打印任务发送到打印队列
     */
    public static void main(String args[]) {

        // 创建打印队列
        PrintQueue printQueue = new PrintQueue();

        // 创建12个打印任务线程
        Thread thread[] = new Thread[12];
        for (int i = 0; i < 12; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        // 启动线程
        for (int i = 0; i < 12; i++) {
            thread[i].start();
        }
    }

}
