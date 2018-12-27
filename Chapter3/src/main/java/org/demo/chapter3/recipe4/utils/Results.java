package org.demo.chapter3.recipe4.utils;

/**
 * 此类用于存储我们在二维数组的每一行中查找的数字的出现次数
 */
public class Results {

    /**
     * 用于存储数组每行中出现次数的数组
     */
    private int data[];

    /**
     * 初始化数组
     *
     * @param size 存储结果的数组大小
     */
    public Results(int size) {
        data = new int[size];
    }

    /**
     * 设置结果数组中一个位置的值
     *
     * @param position 在阵列中的位置
     * @param value    要在该位置设置的值
     */
    public void setData(int position, int value) {
        data[position] = value;
    }

    /**
     * 返回结果数组
     *
     * @return 结果数组
     */
    public int[] getData() {
        return data;
    }
}
