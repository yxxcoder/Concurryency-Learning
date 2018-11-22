package org.demo.chapter1.recipe8.handler;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 用于处理线程中的运行时异常(Unchecked Exception)
 */
public class ExceptionHandler implements UncaughtExceptionHandler {


    /**
     * 处理线程中的运行时异常
     * @param t 抛出异常的线程对象
     * @param e 线程抛出的运行时异常对象
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s\n",t.getId());
        System.out.printf("Exception: %s: %s\n",e.getClass().getName(),e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n",t.getState());
    }

}
