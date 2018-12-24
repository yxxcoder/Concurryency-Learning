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

当使用synchronized关键字来保护代码块时，必须把对象引用作为传入参数。通常情况下，使用this关键字来引用执行方法所属的对象，**也可以使用其他的对象对其进行引用**。一般来说，这些对象就是为这个目的而创建的。例如，在类中有两个非依赖属性，它们被多个线程共享，你必须同步每一个变量的访问，但是同一时刻只允许一个线程访问一个属性变量，其他某个线程访问另一个属性变量

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

这个例子使用了一个对象来控制对vacanciesCinema1属性的访问，所以同一时间只有一个线程能够修改这个属性；使用了另一个对象来控制vacanciesCinema2属性的访问，所以同一时间只有一个线程能够修改这个属性。但是，这个例子允许同时运行两个线程：一个修改vacancesCinema1属性，另一 个修改vacanciesCinema2属性



## 3. 在同步代码中使用条件

在并发编程中一个典型的问题是生产者-消费者（Producer-Consumer）问题。我们有一个数据缓冲区，一个或者多个数据生产者将把数据存入这个缓冲区，一个或者多个数据消费者将数据从缓冲区中取走

这个缓冲区是一个共享数据结构，必须使用同步机制控制对它的访问，例如使用synchronized关键字，但是会受到更多的限制。如果缓冲区是满的，生产者就不能再放入数据，如果缓冲区是空的，消费者就不能读取数据

对于这些场景，Java在`Object`类中提供了`wait()`、`notify()`和 `notifyAll()`方法。线程可以在同步代码块中调用`wait()`方法。**如果在同步代码块之外调用`wait()`方法，JVM将抛出`llegalMonitorStateException`异常**。当一个线程调用`wait()`方法时，JVM将这个线程置入休眠，并且释放控制这个同步代码块的对象，同时允许其他线程执行这个对象控制的其他同步代码块。为了唤醒这个线程，必须在这个对象控制的某个**同步代码块中**调用`notify()`或者`notifyAll()`方法

```java
/**
 * 事件存储类
 */
public class EventStorage {

    /**
     * 最大存储大小
     */
    private int maxSize;
    /**
     * 存储的事件
     */
    private List<Date> storage;

    /**
     * 该类的构造函数，初始化属性
     */
    public EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    /**
     * 此方法创建并存储事件
     */
    public synchronized void set() {
        while (storage.size() == maxSize) {
            try {
                // 挂起线程，等待空余空间
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set: %d\n", storage.size());
        // 生产事件后唤醒等待事件的线程
        notify();
    }

    /**
     * 此方法消费链表的第一个事件
     */
    public synchronized void get() {
        while (storage.size() == 0) {
            try {
                // 挂起线程，事件的出现
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Get: %d: %s\n", storage.size(), ((LinkedList<?>) storage).poll());
        // 唤醒因调用wait()方法进入休眠的线程
        // 即生产者等待空间时挂起的线程
        notify();
    }
}
```

必须在while循环中调用`wait()`，并且不断查询while的条件，直到条件为真的时候才能继续



## 4. 使用锁实现同步

Java提供了同步代码块的另一种机制，这是一种比`synchronized`关键字更强大也更灵活的机制。这种机制基于`Lock`接口及其实现类（例如`ReentrantLock`），提供了更多的好处

- 支持更灵活的同步代码块结构。使用`synchronized` 关键字时，只能在同一个`synchronized`块结构中获取和释放控制。`Lock`接口允许实现更复杂的临界区结构（**即控制的获取和释放不出现在同一个块结构中**）
- 相比 `synchronized`关键字，`Lock`接口提供了更多的功能。其中一个新功能是`tryLock()`方法的实现。这个方法试图获取锁，如果锁已被其他线程获取，它将返回false并继续往下执行代码。使用`synchronized`关键字时，如果线程A试图执行一个同步代码块，而线程B已在执行这个同步代码块，则线程A就会被挂起直到线程B运行完这个同步代码块。**使用锁的`tryLock()`方法，通过返回值将得知是否有其他线程正在使用这个锁保护的代码块**
- `Lock`接口允许`分离读和写操作`，允许多个读线程和只有一个写线程
- 相比 synchronized关键字，Lock 接口**具有更好的性能**

```java
/**
 * 此类模拟打印队列
 */
public class PrintQueue {

    /**
     * 该锁用以控制对队列的访问
     */
    private final Lock queueLock = new ReentrantLock();

    /**
     * 打印文档
     *
     * @param document 打印任务
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }

        // 也可以通过tryLock获取锁
//		if (queueLock.tryLock()) {
//			System.out.println("Get Lock Succeed");
//		} else {
//			System.out.println("Get Lock Failed");
//		}
    }
}

```

在线程离开临界区的时候，我们必须使用`unlock()`方法来释放它持有的锁，以让其他线程来访问临界区。**如果在离开临界区的时候没有调用`unlock()`方法，其他线程将永久地等待，从而导致了死锁(Deadlock)情景**。如果在临界区使用了`try-catch`块，不要忘记将`unlock()`方法放入`finally`部分

`Lock`接口(和它的实现类`ReentrantLock` )还提供了另一个方法来获取锁,即`tryLock()`方法。跟`lock()`方法最大的不同是：**线程使用`tryLock()`不能够获取锁， tryLock()会立即返回，它不会将线程置入休眠**。`tryLock()`方法返回一个布尔值，true表示线程获取了锁，false表示没有获取锁

需要重视`tryLock()`方法的返回值及其对应的行为。如果这个方法返回false，则程序不会执行临界区代码。如果执行了，这个应用很可能会出现错误的结果

**`ReentrantLock`类也允许使用递归调用**。如果一个线程获取了锁并且进行了递归调用，它将继续持有这个锁，因此调用`lock()`方法后也将立即返回，并且线程将继续执行递归调用。再者，我们还可以调用其他的方法



## 5. 使用读写锁实现同步数据访问

`ReadWriteLock`接口的唯一实现类`ReentrantReadWriteLock`有两个锁，一个是读操作锁，另一个是写操作锁。使用读操作锁时可以允许多个线程同时访问，但是使用**写操作锁时只允许一个线程进行**。在一个线程执行写操作时，其他线程不能够执行读操作

```java
/**
 * 价格信息类，存放两个产品的价格
 * writer线程更改两个产品的价格，reader线程读取两个产品的价格
 */
public class PricesInfo {

    /**
     * 两个产品的价格
     */
    private double price1;
    private double price2;

    /**
     * 读写锁控制对价格的访问
     */
    private ReadWriteLock lock;

    /**
     * 构造方法初始化价格和读写锁
     */
    public PricesInfo() {
        price1 = 1.0;
        price2 = 2.0;
        lock = new ReentrantReadWriteLock();
    }

    /**
     * 返回第一个产品的价格
     *
     * @return 第一个产品的价格
     */
    public double getPrice1() {
        lock.readLock().lock();
        double value = price1;
        lock.readLock().unlock();
        return value;
    }

    /**
     * 返回第二个产品的价格
     *
     * @return 第二个产品的价格
     */
    public double getPrice2() {
        lock.readLock().lock();
        double value = price2;
        lock.readLock().unlock();
        return value;
    }

    /**
     * 更改产品的价格
     *
     * @param price1 第一个产品的价格
     * @param price2 第二个产品的价格
     */
    public void setPrices(double price1, double price2) {
        lock.writeLock().lock();
        this.price1 = price1;
        this.price2 = price2;
        lock.writeLock().unlock();
    }
}
```

读写锁均实现了`Lock`接口，所以我们可以使用`lock()`，`unlock()`和 `tryLock()`方法



## 6. 修改锁的公平性

`ReentrantLock`和`ReentrantReadWriteLock`类的构造器都含有一个布尔参数`fair`，它允许你控制这两个类的行为

默认`fair`值是`false`，它称为非公平模式(Non-Fair Mode)。在非公平模式下，当有很多线程在等待锁（`ReentrantLock` 和`ReentrantReadWriteLock`）时，锁将选择它们中的一个来访问临界区，这个选择是没有任何约束的。如果fair值是true，则称为公平模式(Fair Mode)。在公平模式下，当有很多线程在等待锁（ `ReentrantLock`和`ReentrantReadWriteLock`）时，**锁将选择它们中等待时间最长的一个来访问临界区**

这两种模式只适用于`lock()`和`unlock()`方法。**而Lock接口的`tryLock()`方法没有将线程置于休眠，`fair` 属性并不影响这个方法**

```java
/**
 * 此类模拟打印队列
 */
public class PrintQueue {

    /**
     * 创建锁用以控制对队列的访问
     * 默认fair值为false，即为非公平锁，为true时为公平锁
     * 如果一个锁是公平的，那么锁的获取顺序就应该符合请求上的绝对时间顺序，满足FIFO
     * 公平锁每次都是从同步队列中的第一个节点获取到锁，而非公平性锁则不一定，有可能刚释放锁的线程能再次获取到锁
     */
    private final Lock queueLock = new ReentrantLock(false);

    /**
     * 打印作业的方法
     * 打印分为两个阶段，体验公平属性如何影响线程选举的有锁控制权
     *
     * @param document 打印的作业
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: First Printing a Job during %d seconds\n", Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }


        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.printf("%s: PrintQueue: Second Printing a Job during %d seconds\n", Thread.currentThread().getName(), (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }
}
```



## 7.在锁中使用多条件 

一个锁可能关联一个或者**多个条件**，这些条件通过`Condition`接口声明。目的是允许线程获取锁并且查看等待的某一个条件是否满足，如果不满足就挂起直到某个线程唤醒它们。`Condition`接口提供了挂起线程和唤起线程的机制

```java
/**
 * 数据缓冲区
 * 生产者读取数据写入缓冲区，消费者将缓冲区中的数据消费掉
 */
public class Buffer {

    /**
     * 存放共享数据
     */
    private LinkedList<String> buffer;

    /**
     * 缓冲区大小
     */
    private int maxSize;

    /**
     * 控制对缓冲区的访问
     */
    private ReentrantLock lock;

    /**
     * 控制是否有数据可供读取
     */
    private Condition lines;

    /**
     * 控制是否有空间写入新的一行数据
     */
    private Condition space;

    /**
     * 表明缓冲区中是否还会有数据，当Producer不在工作时该值为false
     * （当前例子只有一个Producer线程，多个Producer时会有问题）
     */
    private boolean pendingLines;

    /**
     * 构造方法 初始化缓冲区
     *
     * @param maxSize 缓冲区大小
     */
    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
    }

    /**
     * 写入一行数据到缓冲区
     *
     * @param line 要写入缓冲区的行
     */
    public void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread()
                    .getName(), buffer.size());
            lines.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从缓冲区返回一行数据
     *
     * @return 缓冲区中的一行
     */
    public String get() {
        String line = null;
        lock.lock();
        try {
            while ((buffer.size() == 0) && (hasPendingLines())) {
                lines.await();
            }

            if (hasPendingLines()) {
                line = buffer.poll();
                System.out.printf("%s: Line Readed: %d\n", Thread.currentThread().getName(), buffer.size());
                space.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return line;
    }

    /**
     * 设置pendingLines变量的值
     *
     * @param pendingLines 改变后的值
     */
    public void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    /**
     * 有可以处理的行时返回true，否则返回false
     *
     * @return 是否有数据行可以处理
     */
    public boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}
```

与锁绑定的所有条件对象都是通过Lock接口声明的`newCondition()`方法创建的。**在使用条件的时候，必须获取这个条件绑定的锁，所以带条件的代码必须在调用`Lock`对象的`lock()`方法和`unlock()`方法之间**

当一个线程调用了条件对象的`signal()`或者`signallAll()`方法后， 一个或者多个在该条件上挂起的线程将被唤醒，但这并不能保证让它们挂起的条件已经满足，所以**必须在`while`循环中调用`await()`，在条件成立之前不能离开这个循环**。如果条件不成立，将再次调用`await()`

因调用`await()`方法进入休眠的线程**可能会被中断**，所以必须处理`InterruptedException`异常



Condition接口还提供了await()方法的其他形式：

**`await(long time, TimeUnit unit)`**，直到发生以下情况之一之前，线程将一直处于休眠状态

- 其他某个线程中断当前线程
- 其他某个线程调用了将当前线程挂起的条件的`singal()`或`signalAll()`方法
- 指定的等待时间已经过去
- 通过TimeUnit 类的常量DAYS、 HOURS、MICROSECONDS、 MILLISECONDS、MINUTES、ANOSECONDS和SECONDS指定的等待时间已经过去

**`awaitUninterruptibly()`**，是不可中断的。这个线程将休眠直到其他某个线程调用了将它挂起的条件的`singal()`或`signalAll()`方法
**`awaitUntil(Date date)`** ，直到发生以下情况之一之前，线程将一直处于休眠状态

- 其他某个线程中断当前线程
- 其他某个线程调用了将它挂起的条件的`singal()`或`signalAll()`方法
- 指定的最后期限到了

**也可以将条件与读写锁ReadLock和WriteLock一起使用**

