package org.demo.chapter1.recipe7;

import org.demo.chapter1.recipe7.event.Event;
import org.demo.chapter1.recipe7.task.CleanerTask;
import org.demo.chapter1.recipe7.task.WriterTask;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 守护线程的创建和运行
 */
public class Main {

    /**
     * 创建并启动3个WriterTask线程，然后创建并启动CleanerTask
     */
    public static void main(String[] args) {

        // 创建存放Event对象的双向队列
        Deque<Event> deque=new ArrayDeque<Event>();

        // 创建并启动3个WriterTask线程
        WriterTask writer=new WriterTask(deque);
        for (int i=0; i<3; i++){
            Thread thread=new Thread(writer);
            thread.start();
        }

        // 创建并启动CleanerTask
        CleanerTask cleaner=new CleanerTask(deque);
        cleaner.start();

    }

}

