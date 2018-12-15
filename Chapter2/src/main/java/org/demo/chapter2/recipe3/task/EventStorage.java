package org.demo.chapter2.recipe3.task;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 事件存储类
 */
public class EventStorage {

    /**
     * 最大存储大小
     */
    private int maxSize;
    /**
     * 存储的事件
     */
    private List<Date> storage;

    /**
     * 该类的构造函数，初始化属性
     */
    public EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    /**
     * 此方法创建并存储事件
     */
    public synchronized void set() {
        while (storage.size() == maxSize) {
            try {
                // 挂起线程，等待空余空间
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set: %d\n", storage.size());
        // 生产事件后唤醒等待事件的线程
        notify();
    }

    /**
     * 此方法消费链表的第一个事件
     */
    public synchronized void get() {
        while (storage.size() == 0) {
            try {
                // 挂起线程，事件的出现
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Get: %d: %s\n", storage.size(), ((LinkedList<?>) storage).poll());
        // 唤醒因调用wait()方法进入休眠的线程
        // 即生产者等待空间时挂起的线程
        notify();
    }
}
