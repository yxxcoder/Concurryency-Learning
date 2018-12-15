package org.demo.chapter1.recipe2;

import org.demo.chapter1.recipe2.task.Calculator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

/**
 * 线程信息的获取和设置
 **/
public class Main {

    public static void main(String[] args) {

        // 线程的优先级信息
        // 线程的优先级是从1到10 1为最低优先级
        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximun Priority: %s\n", Thread.MAX_PRIORITY);


        Thread[] threads = new Thread[10];
        Thread.State[] status = new Thread.State[10];
        // 5个线程的优先级最高 5个线程优先级最低
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator(i));
            // 被乘数是偶数的线程优先级高
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread " + i);
        }


        // 将线程的状态演变写入文件中
        try (FileWriter file = new FileWriter("log.txt"); PrintWriter pw = new PrintWriter(file);) {

            // 线程执行前的状态
            for (int i = 0; i < 10; i++) {
                pw.println("Main : Status of Thread " + i + " : " + threads[i].getState());
                status[i] = threads[i].getState();
            }

            // 启动所有线程
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }

            // 等待线程全部执行结束
            boolean finish = false;
            while (!finish) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != status[i]) {
                        writeThreadInfo(pw, threads[i], status[i]);
                        status[i] = threads[i].getState();
                    }
                }

                finish = true;
                for (int i = 0; i < 10; i++) {
                    finish = finish && (threads[i].getState() == State.TERMINATED);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将线程的状态信息写入到文件中
     *
     * @param pw     : PrintWriter to write the data
     * @param thread : Thread whose information will be written
     * @param state  : Old state of the thread
     */
    private static void writeThreadInfo(PrintWriter pw, Thread thread, State state) {
        pw.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        pw.printf("Main : Priority: %d\n", thread.getPriority());
        pw.printf("Main : Old State: %s\n", state);
        pw.printf("Main : New State: %s\n", thread.getState());
        pw.printf("Main : ************************************\n");
    }

}

