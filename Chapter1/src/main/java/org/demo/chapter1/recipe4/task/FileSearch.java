package org.demo.chapter1.recipe4.task;


import java.io.File;

/**
 * 在一个文件夹中寻找一个指定的文件
 */
public class FileSearch implements Runnable {

    /**
     * 用于查找的初始文件夹
     */
    private String initPath;
    /**
     * 要查找的文件名
     */
    private String fileName;

    public FileSearch(String initPath, String fileName) {
        this.initPath = initPath;
        this.fileName = fileName;
    }

    /**
     * 检查当前file是不是一个目录，是的话就调用directoryProcess方法
     */
    @Override
    public void run() {
        File file = new File(initPath);
        if (file.isDirectory()) {
            try {
                directoryProcess(file);
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
                cleanResources();
            }
        }
    }

    /**
     * Method for cleaning the resources. In this case, is empty
     */
    private void cleanResources() {

    }

    /**
     * 获取一个文件夹里的所有文件及子文件夹，对于文件夹递归调用，对于文件调用fileProcess方法
     *
     * @param file : Directory to process
     * @throws InterruptedException : If the thread is interrupted
     */
    private void directoryProcess(File file) throws InterruptedException {

        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // 当前为文件夹则递归调用
                    directoryProcess(list[i]);
                } else {
                    // 当前为文件则调用fileProcess方法
                    fileProcess(list[i]);
                }
            }
        }
        // Check the interruption
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * 输入文件名与要查找的文件名相同则打印到控制台
     * 做完比较后检查线程是否已被中断
     *
     * @param file : File to process
     * @throws InterruptedException : If the thread is interrupted
     */
    private void fileProcess(File file) throws InterruptedException {
        // Check the name
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }

        // Check the interruption
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

}
