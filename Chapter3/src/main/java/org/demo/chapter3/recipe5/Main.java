package org.demo.chapter3.recipe5;

import org.demo.chapter3.recipe5.task.FileSearch;

import java.util.concurrent.Phaser;

/**
 * 并发阶段任务的运行
 * 使用 Phaser 同步并发任务
 * <p>
 *     使用 Phaser 类同步三个并发任务。这三个任务将在三个不同的文件夹
 *     及其子文件夹中查找过去 24 小时内修改过扩展名为 .log 的文件
 * </p>
 */
public class Main {

    /**
     * 创建三个 FileSearch 线程在不同的文件夹查找指定扩展名的文件，并用 Phaser 同步线程间执行进度
     *
     * @param args
     */
    public static void main(String[] args) {

        // 创建一个有三个参与者的 Phaser
        Phaser phaser = new Phaser(3);

        // 创建三个查找线程 FileSearch，并分别指定一个查询目录
        FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
        FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
        FileSearch documents = new FileSearch("C:\\Documents And Settings", "log", phaser);

        // 启动线程
        Thread systemThread = new Thread(system, "System");
        systemThread.start();

        // 启动线程
        Thread appsThread = new Thread(apps, "Apps");
        appsThread.start();

        // 启动线程
        Thread documentsThread = new Thread(documents, "Documents");
        documentsThread.start();

        // 等待三个线程执行结束
        try {
            systemThread.join();
            appsThread.join();
            documentsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 查看 Phaser 对象是否终止
        System.out.printf("Terminated: %s\n", phaser.isTerminated());

    }

}
