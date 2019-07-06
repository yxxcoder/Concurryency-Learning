package org.demo.chapter4.recipe5.task;

/**
 * 存储任务生成结果
 */
public class Result {
    /**
     * 任务名
     */
    private String name;
    /**
     * 任务结果
     */
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
