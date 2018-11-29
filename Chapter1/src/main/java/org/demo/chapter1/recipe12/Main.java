package org.demo.chapter1.recipe12;

/**
 * 使用工程类创建线程
 */
public class Main {

    /**
     * 实现线程工厂类并使用工厂类创建10个线程
     * @param args
     */
    public static void main(String[] args) {
        // 创建线程工厂对象
        MyThreadFactory factory=new MyThreadFactory("MyThreadFactory");

        Task task=new Task();
        Thread thread;

        // 创建10个线程并启动
        System.out.printf("Starting the Threads\n");
        for (int i=0; i<10; i++){
            thread=factory.newThread(task);
            thread.start();
        }
        // 将线程工厂的统计打印到控制台
        System.out.printf("Factory stats:\n");
        System.out.printf("%s\n",factory.getStats());
    }

}
