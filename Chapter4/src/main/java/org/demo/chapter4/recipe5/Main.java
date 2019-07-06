package org.demo.chapter4.recipe5;

import org.demo.chapter4.recipe5.task.Result;
import org.demo.chapter4.recipe5.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 运行多个任务并处理所有结果
 * 通过 ExecutorService 的 invokeAll() 方法实现
 * <p>
 * 发送一个任务列表给 ExecutorService 对象，并等待列表中所有任务执行完成
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        // 创建一个 ExecutorService 对象
        ExecutorService executor = Executors.newCachedThreadPool();

        // 创建三个任务并放入任务队列
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task-" + i);
            taskList.add(task);
        }

        // 执行任务列表中的任务
        List<Future<Result>> resultList = null;
        try {
            System.out.println("ddd");
            resultList = executor.invokeAll(taskList);
            System.out.println("ccc");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 结束 ExecutorService
        executor.shutdown();

        // 将结果输出到控制台
        System.out.printf("Core: Printing the results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Result> future = resultList.get(i);
            try {
                Result result = future.get();
                System.out.printf("%s: %s\n", result.getName(), result.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
