package org.demo.chapter4.recipe6;

import org.demo.chapter4.recipe6.task.Task;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 在执行器中延时执行任务
 * 通过 ScheduledThreadPoolExecutor 延时执行任务
 * <p>
 * 发送 5 个任务给 ScheduledExecutorService 延时执行
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        System.out.printf("Main: Starting at: %s\n", new Date());

        // 发送任务到 ScheduledExecutorService 并设置延时时间
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task " + i);
            executor.schedule(task, i + 1, TimeUnit.SECONDS);
        }

        // 结束 ScheduledExecutorService
        executor.shutdown();

        // 等待结束
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 打印结束时间
        System.out.printf("Core: Ends at: %s\n", new Date());
    }
}
