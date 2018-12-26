package org.demo.chapter3.recipe3;

import org.demo.chapter3.recipe3.task.Participant;
import org.demo.chapter3.recipe3.task.Videoconference;

/**
 * 等待多个并发事件的完成
 */
public class Main {

    /**
     * 视频会议Videoconference等待所有的参会人员到齐后开始会议
     */
    public static void main(String[] args) {

        // 创建一个10人参会的视频会议VideoConference.
        Videoconference conference = new Videoconference(10);
        // 启动视频会议线程
        Thread threadConference = new Thread(conference);
        threadConference.start();

        // 创建10个参会人员
        for (int i = 0; i < 10; i++) {
            Participant p = new Participant(conference, "Participant " + i);
            Thread t = new Thread(p);
            t.start();
        }
    }
}
