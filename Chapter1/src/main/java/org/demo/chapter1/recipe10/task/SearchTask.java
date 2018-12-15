package org.demo.chapter1.recipe10.task;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 模拟搜索操作
 */
public class SearchTask implements Runnable {

    /**
     * 如果此线程完成且未中断，则存储线程的名称
     */
    private Result result;

    public SearchTask(Result result) {
        this.result = result;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.printf("Thread %s: Start\n", name);
        try {
            doTask();
            // 任务执行完成，记录线程名称
            result.setName(name);
        } catch (InterruptedException e) {
            // 线程中断，打印线程信息
            System.out.printf("Thread %s: Interrupted\n", name);
            return;
        }
        // 线程执行结束，打印信息
        System.out.printf("Thread %s: End\n", name);
    }

    /**
     * 模拟搜索操作
     *
     * @throws InterruptedException 如果线程被中断抛出此异常
     */
    private void doTask() throws InterruptedException {
        Random random = new Random((new Date()).getTime());
        int value = (int) (random.nextDouble() * 100);
        System.out.printf("Thread %s: %d\n", Thread.currentThread().getName(), value);
        TimeUnit.SECONDS.sleep(value);
    }

}
