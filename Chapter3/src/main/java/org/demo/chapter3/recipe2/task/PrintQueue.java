package org.demo.chapter3.recipe2.task;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 模拟打印队列，控制对三台打印机的访问
 * <p>
 * 使用信号量来控制对其中一台打印机的访问
 * 当有作业需要打印时，首先判断是否有一台或多台打印机状态是空闲的，有的话则
 * 可以访问到其中一个空闲的打印机，如果没有，它会一直等待出现一台空闲的打印机
 * </p>
 */
public class PrintQueue {

    /**
     * 控制对打印机的访问
     */
    private Semaphore semaphore;

    /**
     * 存放打印机的状态，空闲中或者正在打印
     */
    private boolean freePrinters[];

    /**
     * 锁对象用来保护对freePrinters数组的访问
     */
    private Lock lockPrinters;

    /**
     * 初始化三个打印机，当前都是空闲状态
     */
    public PrintQueue() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    /**
     * 尝试执行打印任务
     *
     * @param document 待打印的文档
     */
    public void printJob(Object document) {
        try {
            // 访问信号量，如果有一台或多台打印机空闲，则可以访问其中一台打印机
            semaphore.acquire();

            // 获取空闲状态的打印机的编号
            int assignedPrinter = getPrinter();

            // 模拟打印任务
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job in Printer %d during %d seconds\n", Thread.currentThread().getName(), assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);

            // 打印完成，修改对应打印机的状态
            freePrinters[assignedPrinter] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放信号量
            semaphore.release();
        }
    }

    /**
     * 获取空闲状态的打印机的编号
     *
     * @return 返回值为-1表示没有空闲的打印机
     */
    private int getPrinter() {
        int ret = -1;

        try {
            // 获取对freePrinters数组的锁
            lockPrinters.lock();
            // 查找第一台空闲中的打印机
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    // 修改打印机的状态
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放对freePrinters数组的锁
            lockPrinters.unlock();
        }
        return ret;
    }

}
