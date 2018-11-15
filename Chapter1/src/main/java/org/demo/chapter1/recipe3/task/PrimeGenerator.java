package org.demo.chapter1.recipe3.task;


/**
 *  处理从1开始的数字，如果是质数就打印出来，直到线程被中断
 *  @author yxxcoder
 */
public class PrimeGenerator extends Thread{


    @Override
    public void run() {
        long number=1L;

        // 无限循环，直到线程被中断
        while (true) {
            if (isPrime(number)) {
                System.out.printf("Number %d is Prime\n",number);
            }

            // 检查线程是否被中断
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }
            number++;
        }
    }

    /**
     *  计算数字是否是质数
     * @param number : 需要判断的数字
     * @return 返回boolean类型的值. 该数字是质数返回true 否则返回false
     */
    private boolean isPrime(long number) {
        if (number <=2) {
            return true;
        }
        for (long i=2; i<number; i++){
            if ((number % i)==0) {
                return false;
            }
        }
        return true;
    }

}
