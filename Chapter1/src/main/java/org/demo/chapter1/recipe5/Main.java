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
     * @param args
     */
    public static void main(String[] args) {
        // Creates a FileClock runnable object and a Thread to run it
        FileClock clock=new FileClock();
        Thread thread=new Thread(clock);

        // Starts the Thread
        thread.start();
        try {
            // Waits five seconds
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
        // Interrupts the Thread
        thread.interrupt();
    }
}
