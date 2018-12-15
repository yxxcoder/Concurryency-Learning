package org.demo.chapter2.recipe3;

import org.demo.chapter2.recipe3.task.Consumer;
import org.demo.chapter2.recipe3.task.EventStorage;
import org.demo.chapter2.recipe3.task.Producer;

/**
 * 在同步代码中使用条件
 */
public class Main {

    /**
     * 生产者-消费者模型
     * wait(), notify()实现
     */
    public static void main(String[] args) {

        // 创建事件存储对象
        EventStorage storage = new EventStorage();

        // 创建生产者线程
        Producer producer = new Producer(storage);
        Thread thread1 = new Thread(producer);

        // 创建消费者线程
        Consumer consumer = new Consumer(storage);
        Thread thread2 = new Thread(consumer);

        // 启动线程
        thread2.start();
        thread1.start();
    }

}
