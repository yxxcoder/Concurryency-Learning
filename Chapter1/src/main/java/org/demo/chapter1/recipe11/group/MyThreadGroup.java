package org.demo.chapter1.recipe11.group;

/**
 * 创建MyThreadGroup继承ThreadGroup并重写uncaughtException方法
 */
public class MyThreadGroup extends ThreadGroup {

    /**
     * 声明带参数的构造方法
     * @param name
     */
    public MyThreadGroup(String name) {
        super(name);
    }


    /**
     * 覆盖父类的处理uncaughtException方法，处理未捕获的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 打印异常线程的id
        System.out.printf("The thread %s has thrown an Exception\n", t.getId());
        // 打印异常堆栈信息
        e.printStackTrace(System.out);
        // 中断线程组的其余线程
        System.out.printf("Terminating the rest of the Threads\n");
        interrupt();
    }
}
