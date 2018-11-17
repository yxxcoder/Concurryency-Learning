package org.demo.chapter1.recipe3;

import org.demo.chapter1.recipe3.task.PrimeGenerator;
import java.util.concurrent.TimeUnit;

/**
 * 线程的中断
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

        // 设置线程中断状态
        task.interrupt();


        // 检查线程是否已被中断
        // isInterrupted只是简单的查询中断状态，不会对状态进行修改
        System.out.println("Task Thread is interrupted: " + task.isInterrupted());

        /**
         * interrupted是静态方法，返回的是当前线程的中断状态
         * 如果当前线程被中断（没有抛出中断异常，否则中断状态就会被清除），调用interrupted方法，第一次会返回true
         * 然后，当前线程的中断状态被方法内部清除了。第二次调用时就会返回false
         * 查询状态时更推荐使用isInterrupted
         */
        Thread.currentThread().interrupt();
        System.out.println("current Thread is interrupted: " + Thread.interrupted());
        System.out.println("current Thread is interrupted: " + Thread.interrupted());
    }

}
