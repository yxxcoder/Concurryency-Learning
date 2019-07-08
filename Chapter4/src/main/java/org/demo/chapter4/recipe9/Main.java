package org.demo.chapter4.recipe9;

import org.demo.chapter4.recipe9.task.ExecutableTask;
import org.demo.chapter4.recipe9.task.ResultTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 在执行器中控制任务的完成
 * 通过覆盖 FutureTask 的 done() 方法实现
 * <p>
 * 创建并运行 5 个任务，5 秒 后取消所有任务，输出已经执行完成任务的结果
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();

        // 创建并运行 5 个任务
        ResultTask[] resultTasks = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(executableTask);
            executor.submit(resultTasks[i]);
        }

        // 等待 5 秒
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // 取消所有任务，在此刻已经完成的任务，本次取消没有任何效果
        for (int i = 0; i < resultTasks.length; i++) {
            resultTasks[i].cancel(true);
        }

        // 输出取消之前已经执行完成的任务的结果
        for (int i = 0; i < resultTasks.length; i++) {
            try {
                if (!resultTasks[i].isCancelled()) {
                    System.out.printf("%s\n", resultTasks[i].get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // 结束 executor.
        executor.shutdown();
    }

}
