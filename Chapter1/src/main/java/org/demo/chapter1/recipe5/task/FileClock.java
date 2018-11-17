package org.demo.chapter1.recipe5.task;


import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 每秒钟打印一次当前时间，循环10次
 */
public class FileClock implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                // Sleep during one second
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.printf("The FileClock has been interrupted");
            }
        }
    }
}