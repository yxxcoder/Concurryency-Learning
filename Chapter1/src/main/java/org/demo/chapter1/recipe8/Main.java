package org.demo.chapter1.recipe8;

import org.demo.chapter1.recipe8.handler.ExceptionHandler;
import org.demo.chapter1.recipe8.task.Task;

/**
 * 线程中不可控异常的处理
 */
public class Main {

    public static void main(String[] args) {

        Thread thread=new Thread(new Task());

        // 设置线程的运行时异常处理器
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        // 启动线程
        thread.start();

        try {
            // 等待线程执行结束
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Thread has finished\n");

    }

}

