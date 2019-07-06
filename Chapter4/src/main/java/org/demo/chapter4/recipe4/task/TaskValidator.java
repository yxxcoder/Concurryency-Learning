package org.demo.chapter4.recipe4.task;

import java.util.concurrent.Callable;

/**
 * 执行用户验证任务
 * 如果验证通过，则返回验证系统的名称，否则抛出异常
 */
public class TaskValidator implements Callable<String> {

    /**
     * 用户验证系统，用于验证用户
     */
    private UserValidator validator;
    /**
     * 用户名
     */
    private String user;
    /**
     * 用户密码
     */
    private String password;


    public TaskValidator(UserValidator validator, String user, String password) {
        this.validator = validator;
        this.user = user;
        this.password = password;
    }

    /**
     * Callable 接口核心方法，如果验证通过，则返回验证系统的名称，否则抛出异常
     */
    @Override
    public String call() throws Exception {
        if (!validator.validate(user, password)) {
            System.out.printf("%s: The user has not been found\n", validator.getName());
            throw new Exception("Error validating user");
        }
        System.out.printf("%s: The user has been found\n", validator.getName());
        return validator.getName();
    }

}
