package org.demo.chapter3.recipe3.task;

import java.util.concurrent.CountDownLatch;

/**
 * 这个类模拟视频会议
 * <p>
 *     使用 CountDownLatch 控制所有的参会人员到齐后开始会议
 * </p>
 */
public class VideoConference implements Runnable {

    /**
     * 控制所有的参会人员到齐后开始会议
     */
    private final CountDownLatch controller;

    /**
     * 初始化参会人数
     *
     * @param number 参会人数
     */
    public VideoConference(int number) {
        controller = new CountDownLatch(number);
    }

    /**
     * 每个参会者在加入视频会议时都会调用此方法
     *
     * @param name 参会人姓名
     */
    public void arrive(String name) {
        System.out.printf("%s has arrived.\n", name);
        // 使用countDown方法递减CountDownLatch的内部计数器
        controller.countDown();
        System.out.printf("VideoConference: Waiting for %d participants.\n", controller.getCount());
    }

    /**
     * 等待所有参会人员，到齐后开始会议
     */
    @Override
    public void run() {
        System.out.printf("VideoConference: Initialization: %d participants.\n", controller.getCount());
        try {
            // 等待参会人员到齐
            controller.await();
            // 开始会议
            System.out.printf("VideoConference: All the participants have come\n");
            System.out.printf("VideoConference: Let's start...\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
