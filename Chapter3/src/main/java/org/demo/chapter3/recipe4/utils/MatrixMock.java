package org.demo.chapter3.recipe4.utils;

import java.util.Random;

/**
 * 此类生成1到10之间随机整数的矩阵
 */
public class MatrixMock {

    /**
     * 存放随机数的二维数组
     */
    private int data[][];

    /**
     * 传入矩阵行数，每行的长度，要寻找的数字，生成二维数组
     * 在生成数组时，同时记录下待寻找的数字在数组中的个数，用于检查查询线程是否得到正确的结果
     *
     * @param size 数组的行数
     * @param length 数组的列数
     * @param number 要寻找的数字
     */
    public MatrixMock(int size, int length, int number) {

        int counter = 0;
        data = new int[size][length];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                data[i][j] = random.nextInt(10);
                if (data[i][j] == number) {
                    counter++;
                }
            }
        }
        // 打印出待寻找的数字在数组中的个数，用于检查查询线程是否得到正确的结果
        System.out.printf("Mock: There are %d ocurrences of number in generated data.\n", counter, number);
    }

    /**
     * 此方法返回二维数组的一行
     *
     * @param row 要返回的行序号
     * @return 存在即返回不存在返回 null
     */
    public int[] getRow(int row) {
        if ((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }

}
