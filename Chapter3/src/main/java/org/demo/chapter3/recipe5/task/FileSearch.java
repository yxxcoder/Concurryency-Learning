package org.demo.chapter3.recipe5.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 在一个文件夹及其子件夹中查找过去24小时内修改过的指定扩展名的文件
 */
public class FileSearch implements Runnable {

    /**
     * 查找的文件夹路径
     */
    private String initPath;

    /**
     * 查找的文件的扩展名
     */
    private String end;

    /**
     * 存储查找到的文件的完整路径
     */
    private List<String> results;

    /**
     * Phaser 对象用来同步多个 FileSearch 线程的查询操作，查询操作雰围三个阶段：
     * 1st: 在文件夹及其子文件夹中查找具有指定扩展名的文件
     * 2nd: 筛选结果，只保留今天修改过的文件
     * 3rd: 打印结果
     */
    private Phaser phaser;


    /**
     * 初始化对象
     *
     * @param initPath 查找的文件夹路径
     * @param end      查找的文件的扩展名
     * @param phaser   用于同步的 Phaser 对象
     */
    public FileSearch(String initPath, String end, Phaser phaser) {
        this.initPath = initPath;
        this.end = end;
        this.phaser = phaser;
        results = new ArrayList<>();
    }

    /**
     * 查找文件，并利用 Phaser 同步多个 FileSearch 线程的并发操作
     */
    @Override
    public void run() {

        // 等待所有 FileSearch 线程被创建后再执行
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

        // 1st Phase: 在文件夹及其子文件夹中查找具有指定扩展名的文件
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }

        // 如果当前线程没有结果，则取消对 Phaser 的注册，结束任务
        if (!checkResults()) {
            return;
        }

        // 2nd Phase: 筛选结果，只保留今天修改过的文件
        filterResults();

        // 筛选结果后，如果当前线程没有结果则取消对 Phaser 的注册，结束任务
        if (!checkResults()) {
            return;
        }

        // 3rd Phase: 打印查询结果
        showInfo();
        // 取消对 Phaser 的注册，结束任务
        phaser.arriveAndDeregister();
        System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());

    }

    /**
     * This method prints the final results of the search
     */
    private void showInfo() {
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        // Waits for the end of all the FileSearch threads that are registered in the phaser
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * 检查当前线程在 1st 阶段执行后是否有结果。如果当前线程没有结果，则取消对 Phaser 的注册
     *
     * @return 有结果返回 true，没有则为 false
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
            // 没有结果， 通知 Phase 对象当前线程已经结束这个阶段，并且将不在参与接下来的阶段操作，取消对 Phaser 注册
            phaser.arriveAndDeregister();
            return false;
        } else {
            // 当前线程有结果的话，同步各线程等待进入下一阶段
            System.out.printf("%s: Phase %d: %d results.\n", Thread.currentThread().getName(), phaser.getPhase(), results.size());
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    /**
     * Method that filter the results to delete the files modified more than a day before now
     */
    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = new Date().getTime();
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            long fileDate = file.lastModified();

            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(results.get(i));
            }
        }
        results = newResults;
    }

    /**
     * Method that process a directory
     *
     * @param file : 待处理的目录
     */
    private void directoryProcess(File file) {
        // 获取目录的内容
        File[] list = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // 当前为文件夹则递归调用 directoryProcess() 方法
                    directoryProcess(list[i]);
                } else {
                    // 如果是文件则检查其扩展名
                    fileProcess(list[i]);
                }
            }
        }
    }

    /**
     * 检查文件扩展名，如果是要查找的就暂时存放在 results 对象中
     *
     * @param file : 待检查的文件
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(end)) {
            results.add(file.getAbsolutePath());
        }
    }

}
