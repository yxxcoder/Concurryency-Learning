package org.demo.chapter4.recipe9.task;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 等待随机一段时间
 */
public class ExecutableTask implements Callable<String> {

    private String name;

    public ExecutableTask(String name) {
        this.name = name;
    }

    @Override
    public String call() {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
        }
        return "Hello, world. I'm " + name;
    }

    public String getName() {
        return name;
    }
}
