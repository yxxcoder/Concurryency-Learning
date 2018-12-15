package org.demo.chapter1.recipe11.task;

import java.util.Random;

/**
 * 一个会抛异常的任务
 */
public class Task implements Runnable {

    @Override
    public void run() {
        int result;
        // 创建一个随机数生成器
        Random random = new Random(Thread.currentThread().getId());
        while (true) {
            // 用1000除以一个随机数，当随机数为0时抛出异常
            result = 1000 / ((int) (random.nextDouble() * 1000));
            System.out.printf("%s : %f\n", Thread.currentThread().getId(), result);
            // 检查线程是否已被中断
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%d : Interrupted\n", Thread.currentThread().getId());
                return;
            }
        }
    }
}
