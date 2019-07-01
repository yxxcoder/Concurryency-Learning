package org.demo.chapter3.recipe6;


import org.demo.chapter3.recipe6.task.MyPhaser;
import org.demo.chapter3.recipe6.task.Student;

/**
 * 并发阶段任务中的阶段切换
 * 重写 Phaser 的 onAdvance() 方法实现并发任务的阶段同步
 * <p>
 *     实现自己的 Phaser 类，并且覆盖 onAdvance() 方法在每个阶段改变的时候执行一些操作
 * </p>
 */
public class Main {

    /**
     * 模拟有三道试题的考试过程，所有的同学必须做完第一道题才可以开始做第二道
     *
     * @param args
     */
    public static void main(String[] args) {

        // 创建 MyPhaser 对象
        MyPhaser phaser = new MyPhaser();

        // 创建 5 个学生对象，并将他们注册道 phaser 对象
        Student[] students = new Student[5];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(phaser);
            phaser.register();
        }

        // 将学生参数作为参数创建线程，并启动
        Thread[] threads = new Thread[students.length];
        for (int i = 0; i < students.length; i++) {
            threads[i] = new Thread(students[i], "Student " + i);
            threads[i].start();
        }

        // 等待线程执行完成
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 检查 phaser 对象是否处于终止状态
        System.out.printf("Main: The phaser has finished: %s.\n", phaser.isTerminated());
    }

}
