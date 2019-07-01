package org.demo.chapter4.recipe1.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 模拟 Web 服务器，接收请求并使用 ThreadPoolExecutor 执行这些请求
 */
public class Server {

    private ThreadPoolExecutor executor;


    public Server() {
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }


    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);
        System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n", executor.getActiveCount());
        System.out.printf("Server: Completed Tasks: %d\n", executor.getCompletedTaskCount());
    }

    public void endServer() {
        executor.shutdown();
    }

}
