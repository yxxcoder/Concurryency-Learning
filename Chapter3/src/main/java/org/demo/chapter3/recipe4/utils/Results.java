package org.demo.chapter3.recipe4.utils;

/**
 * 此类用于存储在二维数组的每一行中查找的数字的出现次数
 */
public class Results {

    /**
     * 用于存储数组每行中出现的次数
     */
    private int data[];

    /**
     * 初始化数组
     *
     * @param size 存储结果的数组长度
     */
    public Results(int size) {
        data = new int[size];
    }

    /**
     * 存储在二维数组的某一行中查找的数字的出现次数
     *
     * @param position 在二维数组的行序号
     * @param value    在二维数组中出现的次数
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
