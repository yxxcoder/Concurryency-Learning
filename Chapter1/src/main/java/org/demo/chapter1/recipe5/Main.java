package org.demo.chapter1.recipe5;


import org.demo.chapter1.recipe5.task.FileClock;

import java.util.concurrent.TimeUnit;


/**
 * 线程的休眠和恢复
 */
public class Main {

    /**
     * 创建并启动FileClock线程用于每秒钟打印一次当前时间，等待5秒后中断线程
     * 当休眠中线程被中断会立即抛出InterruptedException异常，不需要等待线程休息时间结束
     * 最佳实践是，当线程被中断时，释放或者关闭线程正在使用的资源
     */
    public static void main(String[] args) {
        // 创建FileClock runnable对象并创建一个线程运行
        FileClock clock = new FileClock();
        Thread thread = new Thread(clock);

        // 启动线程
        thread.start();
        try {
            // 休眠5秒钟
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
        // 中断线程
        thread.interrupt();
    }
}
