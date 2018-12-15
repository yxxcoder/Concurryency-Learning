package org.demo.chapter2.recipe1.task;

/**
 * 模拟银行从账户扣钱
 */
public class Bank implements Runnable {

    /**
     * 要操作的账户
     */
    private Account account;

    /**
     * 构造方法 初始化账户
     *
     * @param account 要操作的账户
     */
    public Bank(Account account) {
        this.account = account;
    }

    /**
     * 连续扣钱100次
     */
    public void run() {
        for (int i = 0; i < 100; i++) {
            account.subtractAmount(1000);
        }
    }

}
