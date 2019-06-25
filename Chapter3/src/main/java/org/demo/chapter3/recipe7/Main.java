package org.demo.chapter3.recipe7;


import org.demo.chapter3.recipe7.task.Consumer;
import org.demo.chapter3.recipe7.task.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 并发任务间的数据交换
 * Exchanger 允许在两个线程之间定义同步点，当两个线程都到达同步点时，交换数据结构
 */
public class Main {

    /**
     * 一个消费者一个生产者情景
     */
    public static void main(String[] args) {

        // 创建两个数据缓冲区
        List<String> buffer1 = new ArrayList<>();
        List<String> buffer2 = new ArrayList<>();

        // 创建 Exchanger 用来同步生产者和消费者
        Exchanger<List<String>> exchanger = new Exchanger<>();

        // 创建生产者
        Producer producer = new Producer(buffer1, exchanger);
        // 创建消费者
        Consumer consumer = new Consumer(buffer2, exchanger);

        // 将生产者和消费者作为传入参数创建线程，并且启动线程
        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);

        threadProducer.start();
        threadConsumer.start();
    }

}
