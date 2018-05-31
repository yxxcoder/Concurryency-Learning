package org.demo.ch1_recipe1;

import org.demo.ch1_recipe1.task.Calculator;

/**
 * 线程的创建和运行
 * @author yuxuan
 * @create 2018-06-01 上午12:16
 **/
public class Main {
    public static void main(String[] args) {

        // 创建10个线程执行计算任务
        for (int i=1; i<=10; i++){
            Calculator calculator=new Calculator(i);
            Thread thread=new Thread(calculator);
            thread.start();
        }
    }
}
