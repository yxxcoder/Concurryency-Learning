package org.demo.chapter4.recipe7;

import org.demo.chapter4.recipe7.task.Task;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * 在执行器中周期性执行任务
 * 通过 ScheduledThreadPoolExecutor 实现周期性执行任务
 * <p>
 * 每两秒执行一次任务，并获得下一次执行定时任务的剩余时间
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        System.out.printf("Main: Starting at: %s\n", new Date());

        Task task = new Task("Task");
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);

        // 获得下一次执行定时任务的剩余时间
        for (int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n", result.getDelay(TimeUnit.MILLISECONDS));
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        executor.shutdown();
        System.out.printf("Main: No more tasks at: %s\n", new Date());
        // 验证定时任务是否已停止
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 打印结束时间
        System.out.printf("Main: Finished at: %s\n", new Date());
    }

}
