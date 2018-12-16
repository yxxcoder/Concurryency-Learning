package org.demo.chapter2.recipe1.task;

/**
 * 模拟公司支付薪水
 */
public class Company implements Runnable {
    /**
     * 要操作的账户
     */
    private Account account;

    /**
     * 构造方法 初始化账户
     *
     * @param account 要操作的账户
     */
    public Company(Account account) {
        this.account = account;
    }

    /**
     * 连续支付薪水100次
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            account.addAmount(1000);
        }
    }

}
