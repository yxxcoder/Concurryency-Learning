package org.demo.chapter2.recipe1;


import org.demo.chapter2.recipe1.task.Account;
import org.demo.chapter2.recipe1.task.Bank;
import org.demo.chapter2.recipe1.task.Company;

/**
 * 使用synchronized实现同步方法
 */
public class Main {

    /**
     * 创建一个账户对象，银行与公司两个线程同时操作账户对象，以同步机制保证了账户余额是正确的
     */
    public static void main(String[] args) {
        // 创建账户对象
        Account account=new Account();
        // 将其余额初始化为1000
        account.setBalance(1000);

        // 创建银行与公司两个线程同时操作账户对象
        Company company=new Company(account);
        Thread companyThread=new Thread(company);

        Bank bank=new Bank(account);
        Thread bankThread=new Thread(bank);

        // 打印初始余额
        System.out.printf("Account : Initial Balance: %f\n",account.getBalance());

        // 启动银行与公司两个线程
        companyThread.start();
        bankThread.start();

        try {
            // 等待线程执行完毕
            companyThread.join();
            bankThread.join();
            // 打印最终的余额
            System.out.printf("Account : Final Balance: %f\n",account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
