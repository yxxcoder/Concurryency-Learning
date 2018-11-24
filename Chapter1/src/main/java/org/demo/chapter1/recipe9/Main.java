package org.demo.chapter1.recipe9;

import org.demo.chapter1.recipe9.task.UnsafeTask;
import java.util.concurrent.TimeUnit;

/**
 * 线程局部变量的使用
 */
public class Main {

    /**
     * 创建三个线程运行UnsafeTask任务
     */
    public static void main(String[] args) {
        // Creates the unsafe task
        UnsafeTask task=new UnsafeTask();

        // Throw three Thread objects
        for (int i=0; i<3; i++){
            Thread thread=new Thread(task);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

