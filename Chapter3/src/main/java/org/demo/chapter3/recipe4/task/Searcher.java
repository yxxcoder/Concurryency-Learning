package org.demo.chapter3.recipe4.task;

import org.demo.chapter3.recipe4.utils.MatrixMock;
import org.demo.chapter3.recipe4.utils.Results;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * 查找类，在二维数组MatrixMock中查询某一数字
 */
public class Searcher implements Runnable {

    /**
     * 控制多线程并发执行
     */
    private final CyclicBarrier barrier;
    /**
     * 开始查询的第一行的行序号
     */
    private int firstRow;
    /**
     * 查询的最后一行的行序号
     */
    private int lastRow;
    /**
     * 待查询的二维数组
     */
    private MatrixMock mock;
    /**
     * 用于存储结果的数组
     */
    private Results results;
    /**
     * 待查找的数字
     */
    private int number;

    /**
     * 初始化对象
     *
     * @param firstRow 开始查询的第一行的行序号
     * @param lastRow  查询的最后一行的行序号
     * @param mock     待查询的二维数组
     * @param results  用于存储结果的数组
     * @param number   待查找的数字
     * @param barrier  控制多线程并发执行的CyclicBarrier对象
     */
    public Searcher(int firstRow, int lastRow, MatrixMock mock, Results results, int number, CyclicBarrier barrier) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.mock = mock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }

    /**
     * 在指定的行中进行查找，对于每一列，将查询到的次数存储到results对象中的数组的相应位置
     */
    @Override
    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d.\n", Thread.currentThread().getName(), firstRow, lastRow);
        for (int i = firstRow; i < lastRow; i++) {
            int row[] = mock.getRow(i);
            counter = 0;
            for (int j = 0; j < row.length; j++) {
                if (row[j] == number) {
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());
        try {
            // 调用 await() 方法，等待其他查询线程执行结束
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
