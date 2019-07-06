package org.demo.chapter4.recipe5.task;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 模拟任务的执行，等待随机的一段时间，然后计算五个随机数的总和
 */
public class Task implements Callable<Result> {

    /**
     * 任务名
     */
    private String name;

    public Task(String name) {
        this.name = name;
    }

    /**
     * 等待随机的一段时间，然后计算五个随机数的总和
     */
    @Override
    public Result call() throws Exception {
        // 记录开始
        System.out.printf("%s: Staring\n", this.name);

        // 等待随机的一段时间
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 计算五个随机数的总和
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += (int) (Math.random() * 100);

        }

        // 组装任务结果
        Result result = new Result();
        result.setName(this.name);
        result.setValue(value);
        System.out.printf("%s: Ends\n", this.name);

        // 返回任务结果
        return result;
    }

}
