package org.demo.chapter1.recipe12;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * 实现ThreadFactory接口创建一个简单的线程工厂类
 */
public class MyThreadFactory implements ThreadFactory {

    // 存放线程对象的数量
    private int counter;
    // 存放线程名称
    private String name;
    // 存放线程信息
    private List<String> stats;

    /**
     * 构造方法
     *
     * @param name 线程名称
     */
    public MyThreadFactory(String name) {
        counter = 0;
        this.name = name;
        stats = new ArrayList<String>();
    }

    /**
     * Runnable对象为参数创建线程对象
     *
     * @param r: Runnable对象
     */
    @Override
    public Thread newThread(Runnable r) {
        // 创建一个线程
        Thread t = new Thread(r, name + "-Thread_" + counter);
        // 数量加一
        counter++;
        // 保存统计数据
        stats.add(String.format("Created thread %d with name %s on %s\n", t.getId(), t.getName(), new Date()));
        // 返回创建的线程
        return t;
    }

    /**
     * 返回所有线程的统计数据
     *
     * @return String类型的统计数据
     */
    public String getStats() {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> it = stats.iterator();

        while (it.hasNext()) {
            buffer.append(it.next());
        }

        return buffer.toString();
    }

}

