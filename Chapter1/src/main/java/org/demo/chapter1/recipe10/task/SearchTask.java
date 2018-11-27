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
        this.result=result;
    }

    @Override
    public void run() {
        String name=Thread.currentThread().getName();
        System.out.printf("Thread %s: Start\n",name);
        try {
            doTask();
            result.setName(name);
        } catch (InterruptedException e) {
            System.out.printf("Thread %s: Interrupted\n",name);
            return;
        }
        System.out.printf("Thread %s: End\n",name);
    }

    /**
     * Method that simulates the search operation
     * @throws InterruptedException Throws this exception if the Thread is interrupted
     */
    private void doTask() throws InterruptedException {
        Random random=new Random((new Date()).getTime());
        int value=(int)(random.nextDouble()*100);
        System.out.printf("Thread %s: %d\n",Thread.currentThread().getName(),value);
        TimeUnit.SECONDS.sleep(value);
    }

}

