package org.demo.chapter2.recipe7;

import org.demo.chapter2.recipe7.task.Buffer;
import org.demo.chapter2.recipe7.task.Consumer;
import org.demo.chapter2.recipe7.task.Producer;
import org.demo.chapter2.recipe7.utils.FileMock;

/**
 * 在锁中使用多条件(Multiple Condition)
 */
public class Main {

    /**
     * Producer线程从FileMock读取数据写入Buffer，3个Consumer线程消费Buffer中的数据
     */
    public static void main(String[] args) {
        /**
         * 初始化文件
         */
        FileMock mock = new FileMock(101, 10);

        /**
         * 初始化缓冲区
         */
        Buffer buffer = new Buffer(20);

        /**
         * 创建Producer线程
         */
        Producer producer = new Producer(mock, buffer);
        Thread threadProducer = new Thread(producer, "Producer");

        /**
         * 创建Consumer线程
         */
        Consumer consumers[] = new Consumer[3];
        Thread threadConsumers[] = new Thread[3];

        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer(buffer);
            threadConsumers[i] = new Thread(consumers[i], "Consumer " + i);
        }

        /**
         * 启动Producer和Consumer线程
         */
        threadProducer.start();
        for (int i = 0; i < 3; i++) {
            threadConsumers[i].start();
        }
    }

}
