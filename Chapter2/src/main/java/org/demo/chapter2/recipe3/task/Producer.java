package org.demo.chapter2.recipe3.task;

/**
 * 事件生产者
 */
public class Producer implements Runnable {

    /**
     * 需要操作的事件存储对象
     */
    private EventStorage storage;

    /**
     * 构造方法
     *
     * @param storage 事件存储对象
     */
    public Producer(EventStorage storage) {
        this.storage = storage;
    }

    /**
     * 生成100个事件
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.set();
        }
    }
}
