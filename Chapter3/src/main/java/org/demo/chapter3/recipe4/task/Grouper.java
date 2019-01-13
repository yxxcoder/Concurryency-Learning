package org.demo.chapter3.recipe4.task;


import org.demo.chapter3.recipe4.utils.Results;

/**
 * 对每个搜索线程的结果进行求和汇总
 * 此线程在所有搜索线程完成时自动执行
 */
public class Grouper implements Runnable {

    /**
     * 存储在二维数组的每一行中查找的数字的出现次数
     */
    private Results results;

    /**
     * 初始化
     *
     * @param results 存储在二维数组的每一行中查找的数字的出现次数的对象
     */
    public Grouper(Results results) {
        this.results = results;
    }

    /**
     * 对结果对象中的数据求和汇总
     */
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: Processing results...\n");
        int data[] = results.getData();
        for (int number : data) {
            finalResult += number;
        }
        System.out.printf("Grouper: Total result: %d.\n", finalResult);
    }

}
