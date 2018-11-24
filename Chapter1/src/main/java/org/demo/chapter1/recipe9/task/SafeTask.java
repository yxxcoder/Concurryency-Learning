package org.demo.chapter1.recipe9.task;


import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Class that shows the usage of ThreadLocal variables to share
 * data between Thread objects
 *
 */
public class SafeTask implements Runnable {

    /**
     * ThreadLocal存储的数据可在线程间共享
     */
    private static ThreadLocal<Date> startDate= new ThreadLocal<Date>() {
        @Override
        protected Date initialValue(){
            return new Date();
        }
    };


    /**
     * 打印ThreadLocal保存的时间到控制台，休息若干秒后再次打印ThreadLocal中已保存的时间对象
     */
    @Override
    public void run() {
        System.out.printf("Starting Thread: %s : %s\n",Thread.currentThread().getId(),startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n",Thread.currentThread().getId(),startDate.get());
    }

}

