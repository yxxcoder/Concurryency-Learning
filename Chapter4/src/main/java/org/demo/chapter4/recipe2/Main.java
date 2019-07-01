package org.demo.chapter4.recipe2;

import org.demo.chapter4.recipe2.task.Server;
import org.demo.chapter4.recipe2.task.Task;

/**
 * 创建固定大小的线程执行器
 * 利用 Executors 的 newFixedThreadPool() 方法创建固定大小的线程执行器
 * <p>
 * 模拟一个 Web 服务器来应对来自不同客户端的请求
 * </p>
 */
public class Main {

    /**
     * 创建 Server 对象处理 100 个 Task 请求任务
     */
    public static void main(String[] args) {

        Server server = new Server();

        // 向服务器发送 100 个请求后关闭 Server
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }

        server.endServer();

    }

}
