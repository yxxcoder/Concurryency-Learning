package org.demo.chapter3.recipe7.task;

import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 消费者
 */
public class Consumer implements Runnable {

    /**
     * 用于数据同步
     */
    private final Exchanger<List<String>> exchanger;
    /**
     * 数据缓冲区
     */
    private List<String> buffer;


    public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * 消费者的主要方法，消费生产者生成的所有 event
     * 每消费 10 个 event 后，使用 Exchanger 对象与生产者同步数据，发送给生产者空的 buffer 并获得有 10 个 event 的 buffer
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Consumer: Cycle %d\n", cycle);

            try {
                // 等待生产者生成的数据并将空的 buffer 发送给生产者
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Consumer: %d\n", buffer.size());

            for (int j = 0; j < 10; j++) {
                String message = buffer.get(0);
                System.out.printf("Consumer: %s\n", message);
                buffer.remove(0);
            }

            cycle++;
        }
    }

}
