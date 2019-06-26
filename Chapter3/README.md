# 第三章 线程同步辅助类

[TOC]

在本章中，我们将学习如何使用更高级的同步机制来实现多线程间的同步
- 信号量(Semaphore): 是一种计数器，用来保护一个或者多个共享资源的访问。它是并发编程的一种基础工具，大多数编程语言都提供了这个机制
- CountDownLatch: 是Java语言提供的同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许线程一直等待
- CyclicBarrier: 也是Java语言提供的同步辅助类，它允许多个线程在某个集合点(common point)处进行相互等待
- Phaser: 也是Java语言提供的同步辅助类。它把并发任务分成多个阶段运行，在开始下一阶段之前，当前阶段中的所有线程都必须执行完成，这是Java7API中的新特性
- Exchanger: 也是Java语言提供的同步辅助类。它提供了两个线程之间的数据交换点

在应用程序中，任何时候都可以使用Semaphore来保护临界区，因为它是一个基础的同步机制。而其他的同步机制，则需要根据各自的上述特性来对其选择使用。所以我们需要根据应用程序的特性来选择合适的同步机制



## 1. 资源的并发访问控制

信号量（Semaphore）是一种计数器，用来保护一个或者多个共享资源的访问

如果线程要访问一个共享资源，它必须先获得信号量。如果信号量的内部计数器大于 0，信号量将减1，然后允许访问这个共享资源。计数器大于 0 意味着有可以使用的资源，因此线程将被允许使用其中一个资源

否则，如果信号量的计数器等于 0，信号量将会把线程置入休眠直至计数器大于 0。计数器等于 0 的时候意味着所有的共享资源已经被其他线程使用了，所以需要访问这个共享资源的线程必须等待

当线程使用完某个共享资源时，信号量必须被释放，以便其他线程能够访问共享资源。释放操作将使信号量的内部计数器增加 1

本节中，将介绍如何使用信号量类 Semaphore 来实现二进制信号量( Binary Semaphore)。二进制信号量是一种比较特殊的信号量，用来保护对唯一共享资源的访问，因而它的内部计数器只有 0 和 1 两个值



实现一个打印队列，并发任务将使用它来完成打印。这个打印队列受二进制信号量保护，因而同时只有一个线程可以执行打印

```java
/**
 * 模拟文档打印
 * 利用信号量控制对打印机的访问
 */
public class PrintQueue {

    /**
     * 信号量控制对打印机的访问
     */
    private final Semaphore semaphore;

    /**
     * 初始化信号量
     */
    public PrintQueue() {
        semaphore = new Semaphore(1);
    }

    /**
     * 模拟打印文档的方法
     *
     * @param document 待打印的文档
     */
    public void printJob(Object document) {
        try {
            // 获取信号量的访问权限
            // 如果正在打印其他作业，则此线程将休眠，直到获得对信号量的访问权限
            semaphore.acquire();

            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(), duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放信号量
            // 如果有其他线程在等待此信号量，JVM会选择其中一个线程允许其访问临界区
            semaphore.release();
        }
    }

}
```



Semaphore 类还有其他两种`acquire()`方法

- acquireUninterruptibly()： 它其实就是`acquire()`方法。当信号量的内部计数器变成 0 的时候，信号量将阻塞线程直到其被释放。线程在被阻塞的这段时间中，可能会被中断，从而导致`acquire()`方法抛出`InterruptedException`异常。而`acquireUninterruptibly()`方法会忽略线程的中断并且不会抛出任何异常
- tryAcquire()： 这个方法试图获得信号量。如果能获得就返回 true；如果不能，就返回 false，从而避开线程的阻塞和等待信号量的释放。我们可以根据返回值是 true 还是 flase 来做出恰当的处理



#### 信号量的公平性

在 Java 语言中，只要一个类可能出现多个线程被阻塞并且等待同步资源的释放（例如信号量），就会涉及公平性概念。默认的模式是非公平模式。在这种模式中，被同步的资源被释放后，所有等待的线程中会有一个被选中来使用共享资源，而这个选择是没有任何条件的。公平模式则不然，它选择的是等待共享资源时间最长的那个线程

跟其他的类一样，Semaphore 类的构造器也提供了第二个传入参数。这个参数是 boolean 型的。如果传入 false 值，那么创建的信号量就是非公平模式的，与不使用这个参数的效果一样。如果传入 true 值，那么创建的信号量是公平模式的



## 2. 资源的多副本的并发访问控制

Semaphore 二进制信号量可以保护对单一共享资源，或者单一临界区的访问，从而使得保护的资源在同一个时间内只能被一个线程访问。然而，信号量也可以用来保护一个资源的多个副本，或者被多个线程同时执行的临界区



实现一个打印队列，将被三个不同的打印机使用。最开始调用 acquire() 方法的 3 个线程将获得对临界区的访问，其余的线程将被阻塞。当一个线程完成了对临界区的访问，并且释放了信号量，另一个线程将获得这个信号量

```java
/**
 * 模拟打印队列，控制对三台打印机的访问
 * <p>
 * 使用信号量来控制对其中一台打印机的访问
 * 当有作业需要打印时，首先判断是否有一台或多台打印机状态是空闲的，有的话则
 * 可以访问到其中一个空闲的打印机，如果没有，它会一直等待出现一台空闲的打印机
 * </p>
 */
public class PrintQueue {

    /**
     * 控制对打印机的访问
     */
    private Semaphore semaphore;

    /**
     * 存放打印机的状态，空闲中或者正在打印
     */
    private boolean freePrinters[];

    /**
     * 锁对象用来保护对freePrinters数组的访问
     */
    private Lock lockPrinters;

    /**
     * 初始化三个打印机，当前都是空闲状态
     */
    public PrintQueue() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    /**
     * 尝试执行打印任务
     *
     * @param document 待打印的文档
     */
    public void printJob(Object document) {
        try {
            // 访问信号量，如果有一台或多台打印机空闲，则可以访问其中一台打印机
            semaphore.acquire();

            // 获取空闲状态的打印机的编号
            int assignedPrinter = getPrinter();

            // 模拟打印任务
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job in Printer %d during %d seconds\n", Thread.currentThread().getName(), assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);

            // 打印完成，修改对应打印机的状态
            freePrinters[assignedPrinter] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放信号量
            semaphore.release();
        }
    }

    /**
     * 获取空闲状态的打印机的编号
     *
     * @return 返回值为-1表示没有空闲的打印机
     */
    private int getPrinter() {
        int ret = -1;

        try {
            // 获取对freePrinters数组的锁
            lockPrinters.lock();
            // 查找第一台空闲中的打印机
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    // 修改打印机的状态
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放对freePrinters数组的锁
            lockPrinters.unlock();
        }
        return ret;
    }

}
```



`acquire()`、`acquireUninterruptibl()`、`tryAcquire()`和`release()`方法都有另一种实现方式，即提供了一个 int 型的传入参数。这个参数声明了线程试图获取或者释放的共享资源数目，也就是这个线程想要在信号量内部计数器上删除或增加的数目。对于`acquire()`、`acquireUninterruptibly()`、`tryAcquire()`方法来讲， 如果计数器的值少于参数对应的值，那么线程将被阻塞直到计数器重新累加到或者超过这个值



## 3. 等待多个并发事件的完成

Java 并发 API 提供了 CountDownLatch 类，它是一个同步辅助类。在完成一组正在其他线程中执行的操作之前，它允许线程一直等待。CountDownLatch 使用一个整数进行初始化，这个整数就是线程要等待完成的操作的数目

当一个线程要等待某些操作先执行完时，需要调用`await()`方法，这个方法让线程进入休眠直到等待的所有操作都完成。当某一个操作完成后，它将调用`countDown()`方法将 CountDownLatch 类的内部计数器减 1 。当计数器变成 0 的时候，CountDownLatch 类将唤醒所有调用`await()`方法而进入休眠的线程



使用CountDownLatch类实现视频会议系统，这个视频会议系统将等待所有的参会者都到齐才开始

```java
/**
 * 这个类模拟视频会议
 * <p>
 *     使用 CountDownLatch 控制所有的参会人员到齐后开始会议
 * </p>
 */
public class VideoConference implements Runnable {

    /**
     * 控制所有的参会人员到齐后开始会议
     */
    private final CountDownLatch controller;

    /**
     * 初始化参会人数
     *
     * @param number 参会人数
     */
    public VideoConference(int number) {
        controller = new CountDownLatch(number);
    }

    /**
     * 每个参会者在加入视频会议时都会调用此方法
     *
     * @param name 参会人姓名
     */
    public void arrive(String name) {
        System.out.printf("%s has arrived.\n", name);
        // 使用countDown方法递减CountDownLatch的内部计数器
        controller.countDown();
        System.out.printf("VideoConference: Waiting for %d participants.\n", controller.getCount());
    }

    /**
     * 等待所有参会人员，到齐后开始会议
     */
    @Override
    public void run() {
        System.out.printf("VideoConference: Initialization: %d participants.\n", controller.getCount());
        try {
            // 等待参会人员到齐
            controller.await();
            // 开始会议
            System.out.printf("VideoConference: All the participants have come\n");
            System.out.printf("VideoConference: Let's start...\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```



CountDownLatch 类有三个基本元素：

- 一个初始值，即定义必须等待的先行完成的操作的数目

- `await()`方法，需要等待其他事件先完成的线程调用

- `countDown()`方法，每个被等待的事件在完成的时候调用


当创建 CountDownLatch 对象时，使用构造器来初始化内部计数器。当`countDown()`方法被调用后，计数器将减 1。当计数器到达 0 的时候，CountDownLatch 对象将唤起所有在`await()`方法上等待的线程

CountDownLatch 对象的内部计数器被初始化之后就不能被再次初始化或者修改。一旦计数器被初始化后，唯一能改变参数值的方法是`countDown()`方法。当计数器到达 0 时，所有因调用`await()`方法而等待的线程立刻被唤醒，再执行`countDown()`将不起任何作用



和其他同步方法相比，CountDownLatch 机制有下述不同

- CountDownLatch 机制不是用来保护共享资源或者临界区的，它是用来同步执行多个任务的一个或者多个线程
- CountDownLatch 只准许进入一次。 如同刚刚解释的，一 旦 CountDownLatch 的内部计数器到达0，再调用这个方法将不起作用。如果要做类似的同步，就必须创建一个新的 CountDownLatch 对象



CountDownLatch 类提供了另外一种`await()`方法，即`await(long time, TimeUnit unit)`。这个方法被调用后，线程将休眠直到被中断，或者 CountDownLatch 的内部计数器达到 0，或者指定的时间已经过期。第二个参数是 TimeUnit 类型，TimeUnit 类是以下常量的枚举:DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS 和 SECONDS



## 4. 在集合点的同步



## 5. 并发阶段任务的运行



## 6. 并发阶段任务中的阶段切换



## 7. 并发任务间的数据交换