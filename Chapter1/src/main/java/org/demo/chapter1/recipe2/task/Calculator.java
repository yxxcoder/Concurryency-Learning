package org.demo.chapter1.recipe2.task;

/**
 * 打印乘法表
 * @author
 * @create 2018-11-09 下午11:08
 **/
public class Calculator implements Runnable {

    /**
     * number表示乘法表中的被乘数 number * n
     */
    private int number;

    /**
     *  构造方法
     * @param number : 被乘数
     */
    public Calculator(int number) {
        this.number=number;
    }

    /**
     * 打印乘法表
     */
    @Override
    public void run() {
        for (int i=1; i<=10; i++){
            System.out.printf("%s: %d * %d = %d\n",Thread.currentThread().getName(),number,i,i*number);
        }
    }

}
