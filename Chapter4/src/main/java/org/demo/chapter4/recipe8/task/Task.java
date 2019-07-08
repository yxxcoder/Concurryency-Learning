package org.demo.chapter4.recipe8.task;

import java.util.concurrent.Callable;

/**
 * 每隔 100 毫秒打印一条信息到控制台
 */
public class Task implements Callable<String> {

    @Override
    public String call() throws InterruptedException {
        while (true) {
            System.out.printf("Task: Test\n");
            Thread.sleep(100);
        }
    }
}
