package org.demo.ch1_recipe1.task;

import java.util.concurrent.TimeUnit;

/**
 * 打印乘法表
 * @author yuxuan
 * @create 2018-06-01 上午12:13
 **/
public class Calculator implements Runnable {

    private int number;

    public Calculator(int number) {
        this.number=number;
    }

    public void run() {
        for (int i=1; i<=10; i++){
            System.out.printf("%s: %d * %d = %d\n",Thread.currentThread().getName(),number,i,i*number);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

