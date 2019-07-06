package org.demo.chapter4.recipe4.task;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 模拟用户验证系统
 * 随机的 sleep 一段时间，然后返回一个随机的布尔值
 */
public class UserValidator {

    /**
     * 验证系统的名称
     */
    private String name;

    public UserValidator(String name) {
        this.name = name;
    }

    /**
     * 验证方法
     *
     * @param name     用户名
     * @param password 用户密码
     * @return 如果用户验证通过，为true，否则为false
     */
    public boolean validate(String name, String password) {
        Random random = new Random();

        // Sleep 随机的时间
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("Validator %s: Validating a user during %d seconds\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }

        // 返回一个随机布尔值
        return random.nextBoolean();
    }

    /**
     * 返回验证系统的名称
     *
     * @return 验证系统的名称
     */
    public String getName() {
        return name;
    }

}
