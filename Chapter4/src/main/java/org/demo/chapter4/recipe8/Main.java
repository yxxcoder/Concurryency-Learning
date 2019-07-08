package org.demo.chapter4.recipe8;

import org.demo.chapter4.recipe8.task.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 在执行器中取消任务
 * 利用 Future 接口的 cancel() 方法取消任务
 * <p>
 * 通过执行器执行一个任务，等待两秒后取消这个任务
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        Task task = new Task();

        System.out.printf("Main: Executing the Task\n");

        // 执行任务
        Future<String> result = executor.submit(task);

        // 等待 2 秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 取消任务
        System.out.printf("Main: Cancelling the Task\n");
        result.cancel(true);
        // 验证任务是否已取消
        System.out.printf("Main: Cancelled: %s\n", result.isCancelled());
        System.out.printf("Main: Done: %s\n", result.isDone());

        // 关闭执行器
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");
    }

}
