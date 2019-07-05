package org.demo.chapter4.recipe3;

import org.demo.chapter4.recipe3.task.FactorialCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 在执行器中执行任务并返回结果
 * 通过 Callable 和 Future 接口实现并发任务并返回结果
 * <p>
 * 并发执行 10 个阶乘计算任务，并输出计算结果
 * </p>
 */
public class Main {


    public static void main(String[] args) {

        // 创建一个固定大小的 ThreadPoolExecutor 最多有两个线程
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        // 存储控制任务执行的 Future 对象的列表，用于获取结果
        List<Future<Integer>> resultList = new ArrayList<>();

        // 创建一个随机数生成器
        Random random = new Random();
        // 向线程池发送 10 个任务
        for (int i = 0; i < 10; i++) {
            Integer number = new Integer(random.nextInt(10));
            FactorialCalculator calculator = new FactorialCalculator(number);
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }

        // 等待任务完成
        do {
            System.out.printf("Main: Number of Completed Tasks: %d\n", executor.getCompletedTaskCount());
            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> result = resultList.get(i);
                System.out.printf("Main: Task %d: %s\n", i, result.isDone());
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (executor.getCompletedTaskCount() < resultList.size());

        // 输出运行结果
        System.out.printf("Main: Results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            Integer number = null;
            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Core: Task %d: %d\n", i, number);
        }

        // 关闭线程池
        executor.shutdown();

    }

}
