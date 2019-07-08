package org.demo.chapter4.recipe9.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 此类用来管理 ExecutableTask 任务的执行，覆盖的 done() 方法在任务结束后调用
 */
public class ResultTask extends FutureTask<String> {

    private String name;

    public ResultTask(Callable<String> callable) {
        super(callable);
        this.name = ((ExecutableTask) callable).getName();
    }

    /**
     * 任务完成时调用此方法
     */
    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s: Has been cancelled\n", name);
        } else {
            System.out.printf("%s: Has finished\n", name);
        }
    }

}
