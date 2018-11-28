package org.demo.chapter1.recipe10;

import org.demo.chapter1.recipe10.task.Result;
import org.demo.chapter1.recipe10.task.SearchTask;

import java.util.concurrent.TimeUnit;

/**
 * 线程的分组
 **/
public class Main {

    /**
     * 创建10个线程去做同一件事，当其中一个线程执行成功后中断其他9个线程
     * @param args
     */
    public static void main(String[] args) {

        // 创建标识为Searcher的线程对象
        ThreadGroup threadGroup = new ThreadGroup("Searcher");
        Result result=new Result();

        // 创建SeachTask对象并作为参数创建10个线程
        SearchTask searchTask=new SearchTask(result);
        for (int i=0; i < 10; i++) {
            Thread thread=new Thread(threadGroup, searchTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 获取线程组包含的线程数目
        System.out.printf("Number of Threads: %d\n",threadGroup.activeCount());
        System.out.printf("Information about the Thread Group\n");
        // 打印线程组对象信息
        threadGroup.list();


        Thread[] threads=new Thread[threadGroup.activeCount()];
        // 获取线程组包含的线程列表
        threadGroup.enumerate(threads);
        // 打印线程组中各线程状态
        for (int i=0; i<threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n",threads[i].getName(),threads[i].getState());
        }

        // 等待线程组的第一个线程执行结束
        waitFinish(threadGroup);

        // 中断线程组的其余线程
        threadGroup.interrupt();
    }

    /**
     * 每隔一秒检测一次是否有线程执行完成
     * @param threadGroup
     */
    private static void waitFinish(ThreadGroup threadGroup) {
        while (threadGroup.activeCount() > 9) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
