package org.demo.chapter3.recipe7.task;

import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 生产者
 */
public class Producer implements Runnable {

    /**
     * 用于数据同步
     */
    private final Exchanger<List<String>> exchanger;
    /**
     * 数据缓冲区
     */
    private List<String> buffer;


    public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * 生产者主要方法，一次创建 10 个 event，循环 10 次
     * 每产生 10 个 event 后，使用 Exchanger 对象与消费者同步数据，消费者获得 10 个 event，生产者获得一个空的 buffer
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Producer: Cycle %d\n", cycle);

            for (int j = 0; j < 10; j++) {
                String message = "Event " + ((i * 10) + j);
                System.out.printf("Producer: %s\n", message);
                buffer.add(message);
            }

            try {
                /*
                 * 与消费者交换 buffer
                 */
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Producer: %d\n", buffer.size());

            cycle++;
        }
    }

}
