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

        // 也可以为所有线程对象设置默认异常处理器
        // 未查找到线程对象的异常处理器时会查找线程对象所在的线程组的异常处理器，还找不到时就会查找默认异常处理器
        // Thread.setDefaultUncaughtExceptionHandler();

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

