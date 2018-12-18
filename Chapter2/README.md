# 第二章 线程同步基础

[TOC]


为解决多线程并发读取数据导致的数据不一致的问题，人们引入了临界区(Critical Section)概念。临界区是一个用以访问共享资源的代码块，这个代码块在同一时间内只允许一个线程执行。为了帮助编程人员实现这个临界区，Java (以及大多数编程语言)提供了同步机制

当一个线程试图访问一个临界区时，它将使用一种同步机制来查看是不是已经有其他线程进入临界区。如果没有其他线程进入临界区，它就可以进入临界区；如果已经有线程进入了临界区，它就被同步机制挂起，直到进入的线程离开这个临界区。如果在等待进入临界区的线程不止一个，JVM会选择其中的一个，其余的将继续等待

本章将逐步讲解如何使用Java语言提供的两种基本同步机制：

- synchronized 关键字机制


- Lock 接口及其实现机制



## 1. 使用synchronized实现同步方法

synchronized关键字可以用来控制一个方法的并发访问。如果一个对象已用synchronized关键字声明，那么只有一个执行线程被允许访问它。如果其他某个线程试图访问这个对象的其他方法，它将被挂起，直到第一个线程执行完正在运行的方法

每一个用synchronized关键字声明的方法都是临界区。在Java中，同一个对象的临界区，在同一时间只有一个允许被访问

静态方法则有不同的行为。**用synchronized关键字声明的静态方法，同时只能够被一个执行线程访问，但是其他线程可以访问这个对象的非静态方法**。必须非常谨慎这一点，因为两个线程可以同时访问一个对象的两个不同的synchronized方法，即其中一个是静态方法，另一个是非静态方法。**如果两个方法都改变了相同的数据，将会出现数据不一致的错误**

```java
/**
 * 银行账户类
 * 同一时间只能有一个线程访问Account对象的addAmount方法或者subtractAmount方法
 */
public class Account {

    /**
     * 账户余额
     */
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * 转入
     *
     * @param amount 转入的金额
     */
    public synchronized void addAmount(double amount) {
        double tmp = balance;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tmp += amount;
        balance = tmp;
    }

    /**
     * 转出
     *
     * @param amount 转出的金额
     */
    public synchronized void subtractAmount(double amount) {
        double tmp = balance;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tmp -= amount;
        balance = tmp;
    }
}
```

一个对象的方法采用synchronized关键字进行声明，只能被一个线程访问。如果线程A正在执行一个同步方法syncMethodA()，线程B要执行这个对象的其他同步方法syncMethodB()，线程B将被阻塞直到线程A访问完。但如果线程B访问的是同一个类的不同对象，那么两个线程都不会被阻塞

**可以递归调用被synchronized声明的方法**。当线程访问一个对象的同步方法时，它还可以调用这个对象的其他的同步方法，也包含正在执行的方法，而不必再次去获取这个方法的访问权

**可以通过synchronized关键字来保护代码块（而不是整个方法）的访问**。应该这样利用synchronized关键字：方法的其余部分保持在synchronized代码块之外，以获取更好的性能。临界区（即同一时间只能被一个线程访问的代码块）的访问应该尽可能的短

例如在获取一幢楼人数的操作中，我们只使用synchronized关键字来保护对人数更新的指令，并让其他操作不使用共享数据。当这样使用synchronized关键字时，必须把对象引用作为传入参数。同一时间只有一个线程被允许访问这个synchronized代码。通常来说，我们使用this关键字来引用正在执行的方法所属的对象


```java
synchronized (this) {
    // Java code
}
```



## 2. 使用非依赖属性实现同步

当使用synchronized关键字来保护代码块时，必须把对象引用作为传入参数。通常情况下，使用this关键字来引用执行方法所属的对象，也可以使用其他的对象对其进行引用。一般来说，这些对象就是为这个目的而创建的。例如，在类中有两个非依赖属性，它们被多个线程共享，你必须同步每一个变量的访问，但是同一时刻只允许一个线程访问一个属性变量，其他某个线程访问另一个属性变量

```java
/**
 * 模拟电影院
 */
public class Cinema {

    /**
     * 用于并发控制属性
     * controlCinema1用于控制对vacanciesCinema1属性的访问，同一时间只有一个线程可以访问vacanciesCinema1属性
     * 同理，controlCinema2用于对vacanciesCinema1属性的访问控制
     */
    private final Object controlCinema1, controlCinema2;
    /**
     * 保存两个电影的剩余票数
     */
    private long vacanciesCinema1;
    private long vacanciesCinema2;

    /**
     * 构造方法，初始化对象
     */
    public Cinema() {
        controlCinema1 = new Object();
        controlCinema2 = new Object();
        vacanciesCinema1 = 20;
        vacanciesCinema2 = 20;
    }

    /**
     * 该方法实现了电影院1的售票操作
     *
     * @param number 售票数量
     * @return 如果门票售出则为true，如果没有则为false
     */
    public boolean sellTickets1(int number) {
        synchronized (controlCinema1) {
            if (number < vacanciesCinema1) {
                vacanciesCinema1 -= number;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 该方法实现了电影院2的售票操作
     *
     * @param number 售票数量
     * @return 如果门票售出则为true，如果没有则为false
     */
    public boolean sellTickets2(int number) {
        synchronized (controlCinema2) {
            if (number < vacanciesCinema2) {
                vacanciesCinema2 -= number;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 该方法实现了电影院1的回收票的操作
     *
     * @param number 回收的票数
     * @return true
     */
    public boolean returnTickets1(int number) {
        synchronized (controlCinema1) {
            vacanciesCinema1 += number;
            return true;
        }
    }

    /**
     * 该方法实现了电影院2的回收票的操作
     *
     * @param number 回收的票数
     * @return true
     */
    public boolean returnTickets2(int number) {
        synchronized (controlCinema2) {
            vacanciesCinema2 += number;
            return true;
        }
    }

    /**
     * 返回电影院1的余票
     *
     * @return 电影院1的余票
     */
    public long getVacanciesCinema1() {
        return vacanciesCinema1;
    }

    /**
     * 返回电影院2的余票
     *
     * @return 电影院2的余票
     */
    public long getVacanciesCinema2() {
        return vacanciesCinema2;
    }

}
```

用synchronized关键字保护代码块时，我们使用对象作为它的传入参数。JVM保证同一时间只有一个线程能够访问这个对象的代码保护块（注意我们一直谈论的是对象，不是类）

这个例子使用了一个对象来控制对vacanciesCinema1属性的访问，所以同一时间只有一个线程能够修改这个属性;使用了另一个对象来控制vacanciesCinema2属性的访问，所以同一时间只有一个线程能够修改这个属性。但是，这个例子允许同时运行两个线程：一个修改vacancesCinema1属性，另一 个修改vacanciesCinema2属性



## 3. 在同步代码中使用条件



## 4. 使用锁实现同步



## 5. 使用读写锁实现同步数据访问



## 6. 修改锁的公平性



## 7.在锁中使用多条件 

