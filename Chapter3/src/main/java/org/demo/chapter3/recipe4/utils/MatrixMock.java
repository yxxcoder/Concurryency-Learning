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
     * Constructor of the class. Generates the bi-dimensional array of numbers.
     * While generates the array, it counts the times that appears the number we are going to look for so we can check that the CiclycBarrier class does a good job
     *
     * @param size   Number of rows of the array
     * @param length Number of columns of the array
     * @param number Number we are going to look for
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
        System.out.printf("Mock: There are %d ocurrences of number in generated data.\n", counter, number);
    }

    /**
     * 此方法返回二维数组的一行
     * This methods returns a row of the bi-dimensional array
     *
     * @param row 要返回的行
     * @return 选定的行的数组
     */
    public int[] getRow(int row) {
        if ((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }

}
