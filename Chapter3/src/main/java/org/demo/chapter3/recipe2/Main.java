package org.demo.chapter3.recipe2;


import org.demo.chapter3.recipe2.task.Job;
import org.demo.chapter3.recipe2.task.PrintQueue;

/**
 * 资源的多副本的并发访问控制
 * 利用信号量 Semaphore 控制对多个资源的访问
 * <p>
 *     实现一个打印队列，将被三个不同的打印机使用。最开始调用 acquire() 方法的 3 个线程
 *     将获得对临界区的访问，其余的线程将被阻塞。当一个线程完成了对临界区的访问，并且释放了
 *     信号量，另一个线程将获得这个信号量
 * </p>
 */
public class Main {

    /**
     * 启动 12 个线程同时将打印任务发送到打印队列
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
