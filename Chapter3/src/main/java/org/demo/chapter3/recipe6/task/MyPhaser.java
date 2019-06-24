package org.demo.chapter3.recipe6.task;

import java.util.concurrent.Phaser;

/**
 * 创建 MyPhaser 类并继承 Phaser
 */
public class MyPhaser extends Phaser {

    /**
     * phaser 对象进行阶段切换的时候，在所有在 arriveAndAwaitAdvance() 方法里休眠的线程被唤醒之前，onAdvance() 方法将被自动调用
     *
     * @param phase             当前阶段序号
     * @param registeredParties 注册线程的数量
     * @return 返回 false 表示 phase 在继续进行，返回 true 表示 phase 已经完成执行并进入了终止态
     */
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
            case 0:
                return studentsArrived();
            case 1:
                return finishFirstExercise();
            case 2:
                return finishSecondExercise();
            case 3:
                return finishExam();
            default:
                return true;
        }
    }

    /**
     * phase 0 至 1 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean studentsArrived() {
        System.out.printf("Phaser: The exam are going to start. The students are ready.\n");
        System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
        return false;
    }

    /**
     * phase 1 至 2 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishFirstExercise() {
        System.out.printf("Phaser: All the students has finished the first exercise.\n");
        System.out.printf("Phaser: It's turn for the second one.\n");
        return false;
    }

    /**
     * phase 2 至 3 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishSecondExercise() {
        System.out.printf("Phaser: All the students has finished the second exercise.\n");
        System.out.printf("Phaser: It's turn for the third one.\n");
        return false;
    }

    /**
     * phase 3 至 4 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishExam() {
        System.out.printf("Phaser: All the students has finished the exam.\n");
        System.out.printf("Phaser: Thank you for your time.\n");
        return true;
    }
}
