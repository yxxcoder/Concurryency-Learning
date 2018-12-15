package org.demo.chapter1.recipe6;

import org.demo.chapter1.recipe6.task.DataSourcesLoader;
import org.demo.chapter1.recipe6.task.NetworkConnectionsLoader;

import java.util.Date;

/**
 * 等待线程的终止
 * 可以使用Thread类的join()方法达到目的
 */
public class Main {

    /**
     * 创建并启动两个线程，等待他们终止
     */
    public static void main(String[] args) {

        // 创建并启动DataSourceLoader线程对象
        DataSourcesLoader dsLoader = new DataSourcesLoader();
        Thread thread1 = new Thread(dsLoader, "DataSourceThread");
        thread1.start();

        // 创建并启动NetworkConnectionsLoader线程
        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread thread2 = new Thread(ncLoader, "NetworkConnectionLoader");
        thread2.start();

        // 等待两个线程终止
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 主线程执行结束
        System.out.printf("Main: Configuration has been loaded: %s\n", new Date());
    }
}

