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
     * 1st: 在文件夹及其子文件夹中查找具有扩展名的文件
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
     * Main method of the class. See the comments inside to a better description of it
     */
    @Override
    public void run() {

        // 等待所有 FileSearch 线程被创建后再执行
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

        // 1st Phase: 查找文件
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }

        // If no results, deregister in the phaser and ends
        if (!checkResults()) {
            return;
        }

        // 2nd Phase: Filter the results
        filterResults();

        // If no results after the filter, deregister in the phaser and ends
        if (!checkResults()) {
            return;
        }

        // 3rd Phase: Show info
        showInfo();
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
     * This method checks if there are results after the execution of a phase. If there aren't
     * results, deregister the thread of the phaser.
     *
     * @return true if there are results, false if not
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
            // No results. Phase is completed but no more work to do. Deregister for the phaser
            phaser.arriveAndDeregister();
            return false;
        } else {
            // There are results. Phase is completed. Wait to continue with the next phase
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
     * @param file : Directory to process
     */
    private void directoryProcess(File file) {

        // Get the content of the directory
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // If is a directory, process it
                    directoryProcess(list[i]);
                } else {
                    // If is a file, process it
                    fileProcess(list[i]);
                }
            }
        }
    }

    /**
     * Method that process a File
     *
     * @param file : File to process
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(end)) {
            results.add(file.getAbsolutePath());
        }
    }

}
