package org.demo.ch1_recipe3;

import org.demo.ch1_recipe3.task.PrimeGenerator;
import java.util.concurrent.TimeUnit;

/**
 *  线程的中断
 */
public class Main {

    /**
     * 运行PrimeGenerator 线程，运行5秒后通过中断机制使其终止
     * @param args
     */
    public static void main(String[] args) {

        // 启动线程
        Thread task=new PrimeGenerator();
        task.start();

        // 等待5秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断线程
        task.interrupt();

        // 检查线程是否已被中断
        System.out.println("Thread is interrupted: " + task.isInterrupted());
    }

}
