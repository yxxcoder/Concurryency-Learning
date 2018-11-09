package org.demo.ch1_recipe2.task;

/**
 * @author
 * @create 2018-11-09 下午11:08
 **/
public class Calculator implements Runnable {

    /**
     *  The number
     */
    private int number;

    /**
     *  Constructor of the class
     * @param number : The number
     */
    public Calculator(int number) {
        this.number=number;
    }

    /**
     *  Method that do the calculations
     */
    @Override
    public void run() {
        for (int i=1; i<=10; i++){
            System.out.printf("%s: %d * %d = %d\n",Thread.currentThread().getName(),number,i,i*number);
        }
    }

}
