package org.demo.chapter3.recipe3.task;

import java.util.concurrent.TimeUnit;

/**
 * 这个类模拟视频会议的参与者
 */
public class Participant implements Runnable {

    /**
     * 参与者将参加的视频会议
     */
    private Videoconference conference;

    /**
     * 参与者的姓名
     */
    private String name;

    /**
     * 初始化参与者及将参加的视频会议
     *
     * @param conference 参与者将参加的视频会议
     * @param name       参与者的姓名
     */
    public Participant(Videoconference conference, String name) {
        this.conference = conference;
        this.name = name;
    }

    /**
     * 等待随机一段时间后加入视频会议
     */
    public void run() {
        Long duration = (long) (Math.random() * 10);
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 加入视频会议
        conference.arrive(name);
    }
}
