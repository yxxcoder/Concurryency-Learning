package org.demo.chapter1.recipe8.task;


public class Task implements Runnable {

    @Override
    public void run() {
        // 故意抛出异常
        int numero = Integer.parseInt("TTT");
    }

}
