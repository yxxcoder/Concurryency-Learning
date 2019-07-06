package org.demo.chapter4.recipe4;

import org.demo.chapter4.recipe4.task.TaskValidator;
import org.demo.chapter4.recipe4.task.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 运行多个任务并处理第一个结果
 * 通过 ExecutorService 的 invokeAny() 方法实现
 * <p>
 * 用户可以通过两种验证机制进行验证，只要有一种机制验证成功，那么这个用户就被验证通过了
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        // 初始化用户的参数
        String username = "test";
        String password = "test";

        // 创建两个用户验证对象
        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");

        // 创建两个用户验证任务
        TaskValidator ldapTask = new TaskValidator(ldapValidator, username, password);
        TaskValidator dbTask = new TaskValidator(dbValidator, username, password);

        // 将两个任务添加到任务列表中
        List<TaskValidator> taskList = new ArrayList<>();
        taskList.add(ldapTask);
        taskList.add(dbTask);

        // 创建一个 Executor
        ExecutorService executor = Executors.newCachedThreadPool();
        String result;
        try {
            // 将任务列表发送给 Executor 对象，并等待第一个任务的结果
            // 如果所有任务抛出异常，则该方法抛出 ExecutionException
            result = executor.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 关闭 Executor 对象
        executor.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }

}
