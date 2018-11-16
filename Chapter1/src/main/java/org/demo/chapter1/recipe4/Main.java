package org.demo.chapter1.recipe4;

import org.demo.chapter1.recipe4.task.FileSearch;

import java.util.concurrent.TimeUnit;

/**
 * 线程中断的控制
 */
public class Main {

    /**
     * 启动FileSearch线程用于查找指定的文件，等待10秒后中断线程
     */
    public static void main(String[] args) {
        // Creates the Runnable object and the Thread to run it
        FileSearch searcher=new FileSearch("C:\\","autoexec.bat");
        Thread thread=new Thread(searcher);

        // Starts the Thread
        thread.start();

        // Wait for ten seconds
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrupts the thread
        thread.interrupt();
    }

}
