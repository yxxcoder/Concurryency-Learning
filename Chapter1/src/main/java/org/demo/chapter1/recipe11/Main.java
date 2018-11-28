package org.demo.chapter1.recipe11;

import org.demo.chapter1.recipe11.group.MyThreadGroup;
import org.demo.chapter1.recipe11.task.Task;

/**
 * 线程组中不可控异常的处理
 **/
public class Main {

    /**
     * 创建线程组并创建两个线程放入该线程组中
     * 当线程抛出未捕获异常时，该线程所在线程组的异常处理器将捕获该异常
     * @param args
     */
    public static void main(String[] args) {

        // 创建线程组
        MyThreadGroup threadGroup=new MyThreadGroup("MyThreadGroup");
        // 创建两个线程放入该线程组中
        Task task=new Task();
        for (int i=0; i<2; i++){
            Thread t=new Thread(threadGroup,task);
            t.start();
        }
    }

}
