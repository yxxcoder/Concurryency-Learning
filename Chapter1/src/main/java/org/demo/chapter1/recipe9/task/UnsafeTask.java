package org.demo.chapter1.recipe9.task;


import java.util.Date;
import java.util.concurrent.TimeUnit;


public class UnsafeTask implements Runnable {

    private Date startDate;

    /**
     * 保存当前时间并打印到控制台，休息若干秒后再次打印已保存的时间对象
     */
    @Override
    public void run() {
        startDate = new Date();
        System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate);
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate);
    }

}

