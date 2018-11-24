package org.demo.chapter1.recipe9;

import org.demo.chapter1.recipe9.task.SafeTask;
import java.util.concurrent.TimeUnit;

/**
 * 线程局部变量的使用
 */
public class SafeMain {

    /**
     * 创建三个线程运行SafeTask任务
     */
    public static void main(String[] args) {
        // Creates a task
        SafeTask task=new SafeTask();

        // Creates and start three Thread objects for that Task
        for (int i=0; i<3; i++){
            Thread thread=new Thread(task);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.start();
        }

    }

}

