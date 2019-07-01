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

Java并发API提供了 CyclicBarrier 类，它也是一个同步辅助类。它允许两个或者多个线程在某个点上进行同步。与 CountDownLatch 类类似

CyclicBarrier 类使用一个整型数进行初始化，这个数是需要在某个点上同步的线程数。当一个线程到达指定的点后，它将调用`await()`方法等待其他的线程。当线程调用`await()`方法后，CyclicBarrier 类将阻塞这个线程并使之休眠直到所有其他线程到达。当最后一个线程调用 CyclicBarrier 类的 `await()`方法时， CyclicBarrier 对象将唤醒所有在等待的线程，然后这些线程将继续执行

CyclicBarrier 可以传入另一个 Runnable 对象作为初始化参数。当所有的线程都到达集合点后，CyclicBarrier 类将这个Runnable对象作为线程执行。这个特性使得这个类在并行任务上可以媲美分治编程技术（Divide and Conquer Programming Technigue）



范例中，将在数字矩阵中寻找一个数字（使用分治编程技术）。这个矩阵会被分成几个子集，然后每个线程在一个子集中查找。一旦所有线程都完成查找，最终的任务将统一这些结果

```java
public class Main {

    /**
     * 等待 5 个查询线程执行结束后执行汇总线程 grouper
     */
    public static void main(String[] args) {

        /*
         * 初始化二维数组
         * 		10000 行
         * 		1000 个数字每行
         * 		查询数字 5
         */
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;
        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);

        // 初始化结果对象 Results
        Results results = new Results(ROWS);

        // 初始化汇总线程
        Grouper grouper = new Grouper(results);

        // 创建CyclicBarrier对象，这个对象等待 5 个查询线程执行结束后执行汇总线程 grouper
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);

        // 创建并启动 5 个查询线程
        Searcher[] searchers = new Searcher[PARTICIPANTS];
        for (int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(i * LINES_PARTICIPANT, (i * LINES_PARTICIPANT) + LINES_PARTICIPANT, mock, results, 5, barrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main: The main thread has finished.\n");
    }
}
```



CyclicBarrier 类还提供了另一种 `await()`方法：

`await(long time, TimeUnit unit)`。这个方法被调用后，线程将一直休眠到被中断，或者 CyclicBarrier 的内部计数器到达 0，或者指定的时间已经过期。第二个参数是 TimeUnit 类型，它是一个常量枚举类型，它的值包含：DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS 和 SECONDS

CyclicBarrier 类还提供了 `getNumberWaiting()` 方法和 `getParties()` 方法，前者将返回在`await()`上阻塞的线程的数目，后者返回被 CyclicBarrier 对象同步的任务数

**重置CyclicBarrier对象**

虽然 CyclicBarrier 类和 CountDownLatch 类有很多共性，但是它们也有一定的差异。其中最重要的不同是，CyclicBarrier 对象可以被重置回初始状态，并把它的内部计数器重置成初始化时的值

CyclicBarrier 对象的重置，是通过 CyclicBarrier 类提供的`reset()`方法完成的。当重置发生后，在`await()`方法中等待的线程将收到一个 BrokenBarrierException 异常。本例是将这个异常打印出来，但是在更复杂的应用程序中，它可以用来执行其他的操作，比如重新执行或者将操作复原回被中断时的状态

**损坏的 CyclicBarrier 对象**

CyclicBarrier 对象有一种特殊的状态即损坏状态（Broken）。当很多线程在`await()`方法上等待的时候，如果其中一个线程被中断，这个线程将抛出 InterruptedException 异常，其他的等待线程将抛 BrokenBarrierException 异常，于是 CyclicBarrier 对象就处于损坏状态了

CyclicBarrier 类提供了`isBroken()`方法，如果处于损坏状态就返回true，否则返回false



## 5. 并发阶段任务的运行

Java 并发 API 还提供了一个更复杂、更强大的同步辅助类，即 Phaser，它允许执行并发多阶段任务。当我们有并发任务并且需要分解成几步执行时，这种机制就非常适用。Phaser 类机制是在每一步结束的位置对线程进行同步，当所有的线程都完成了这一步，才允许执行下一步

跟其他同步工具一样，必须对 Phaser 类中参与同步操作的任务数进行初始化，不同的是，我们可以动态地增加或者减少任务数



范例中，将使用 Phaser 类同步三个并发任务。这三个任务将在三个不同的文件夹及其子文件夹中查找过去 24 小时内修改过扩展名为 .log 的文件。这个任务分成以下三个步骤：

1. 在指定的文件夹及其子文件夹中获得扩展名为 .log 的文件

2. 对第一步的结果进行过滤，删除修改时间超过24小时的文件

3. 将结果打印到控制台

在第一步和第二步结束的时候，都会检查所查找到的结果列表是不是有元素存在。如果结果列表是空的，对应的线程将结束执行，并且从phaser中删除

```java
/**
 * 在一个文件夹及其子件夹中查找过去 24 小时内修改过的指定扩展名的文件
 */
public class FileSearch implements Runnable {

    /**
     * 查找的文件夹路径
     */
    private String initPath;

    /**
     * 查找的文件的扩展名
     */
    private String end;

    /**
     * 存储查找到的文件的完整路径
     */
    private List<String> results;

    /**
     * Phaser 对象用来同步多个 FileSearch 线程的查询操作，查询操作分为三个阶段：
     * 1st: 在文件夹及其子文件夹中查找具有指定扩展名的文件
     * 2nd: 筛选结果，只保留今天修改过的文件
     * 3rd: 打印结果
     */
    private Phaser phaser;


    /**
     * 初始化对象
     *
     * @param initPath 查找的文件夹路径
     * @param end      查找的文件的扩展名
     * @param phaser   用于同步的 Phaser 对象
     */
    public FileSearch(String initPath, String end, Phaser phaser) {
        this.initPath = initPath;
        this.end = end;
        this.phaser = phaser;
        results = new ArrayList<>();
    }

    /**
     * 查找文件，并利用 Phaser 同步多个 FileSearch 线程的并发操作
     */
    @Override
    public void run() {

        // 等待所有 FileSearch 线程被创建后再执行
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

        // 1st Phase: 在文件夹及其子文件夹中查找具有指定扩展名的文件
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }

        // 如果当前线程没有结果，则取消对 Phaser 的注册，结束任务
        if (!checkResults()) {
            return;
        }

        // 2nd Phase: 筛选结果，只保留今天修改过的文件
        filterResults();

        // 筛选结果后，如果当前线程没有结果则取消对 Phaser 的注册，结束任务
        if (!checkResults()) {
            return;
        }

        // 3rd Phase: 打印查询结果
        showInfo();
        // 取消对 Phaser 的注册，结束任务
        phaser.arriveAndDeregister();
        System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());
    }

    /**
     * 打印最后的查询结果
     */
    private void showInfo() {
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        // 等待所有注册 Phaser 的 FileSearch 线程运行结束
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * 检查当前线程在 1st 阶段执行后是否有结果。如果当前线程没有结果，则取消对 Phaser 的注册
     *
     * @return 有结果返回 true，没有则为 false
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
            // 没有结果， 通知 Phase 对象当前线程已经结束这个阶段，并且将不在参与接下来的阶段操作，取消对 Phaser 注册
            phaser.arriveAndDeregister();
            return false;
        } else {
            // 当前线程有结果的话，同步各线程等待进入下一阶段
            System.out.printf("%s: Phase %d: %d results.\n", Thread.currentThread().getName(), phaser.getPhase(), results.size());
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    /**
     * 筛选结果，只保留今天修改过的文件
     */
    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = System.currentTimeMillis();
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            long fileDate = file.lastModified();

            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(results.get(i));
            }
        }
        results = newResults;
    }

    /**
     * 获取一个文件夹里的所有文件及子文件夹，对于文件夹递归调用此方法，对于文件调用 fileProcess 方法处理
     *
     * @param file : 待处理的目录
     */
    private void directoryProcess(File file) {
        // 获取目录的内容
        File[] list = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // 当前为文件夹则递归调用 directoryProcess() 方法
                    directoryProcess(list[i]);
                } else {
                    // 如果是文件则检查其扩展名
                    fileProcess(list[i]);
                }
            }
        }
    }

    /**
     * 检查文件扩展名，如果是要查找的就暂时存放在 results 对象中
     *
     * @param file : 待检查的文件
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(end)) {
            results.add(file.getAbsolutePath());
        }
    }
}
```



一个Phaser对象有两种状态

- 活跃态（Active）：当存在参与同步的线程的时候，Phaser 就是活跃的，并且在每个阶段结束的时候进行同步

- 终止态（Termination）：当所有参与同步的线程都取消注册的时候，Phaser 就处于终止状态，在这种状态下，Phaser 没有任何参与者。更具体地说，当Phaser 对象的`onAdvance()`方法返回 true 的时候，Phaser 对象就处于了终止态。通过覆盖这个方法可以改变默认的行为。当 Phaser 是终止态的时候，同步方法`arriveAndAwaitAdvance()`会立即返回，而且不会做任何同步的操作

Phaser类的一个重大特性就是不必对它的方法进行异常处理。不像其他的同步辅助类，被 Phaser 类置于休眠的线程不会响应中断事件，也不会抛出 InterruptedException 异常（只有一种特例会抛出异常，参见之后的部分）



Phaser类提供了一些其他改变 Phaser 对象的方法：

- `arrive()`：这个方法通知 phaser 对象一个参与者已经完成了当前阶段，但是它不应该等待其他参与者都完成当前阶段。必须小心使用这个方法，因为它不会与其他线程同步
- `awaitAdvance(int phase)`：如果传入的阶段参数与当前阶段一致，这个方法会将当前线程置于休眠，直到这个阶段的所有参与者都运行完成。如果传入的阶段参数与当前阶段不一致，这个方法将立即返回
- `awaitAdvanceInterruptibly(int phaser)`：这个方法跟`awaitAdvance(int phase)`一样，不同之处是，如果在这个方法中休眠的线程被中断，它将抛出 InterruptedException 异常



**将参与者注册到 Phaser 中**

创建一个 Phaser 对象时，需要指出有多少个参与者。Phaser 类提供了两种方法增加注册者的数量，这些方法如下：

- `register()`：这个方法将一个新的参与者注册到 Phaser 中，这个新的参与者将被当成没有执完本阶段的线程
- `bulkRegister(int Parties)`：这个方法将指定数目的参与者注册到Phaser中，所有这些新的参与者都将被当成没有执完本阶段的线程

Phaser 类只提供了一种方法减少注册者的数目，即 `arriveAndDeregister( )`方法。它通知phaser对象对应的线程已经完成了当前阶段，并且它不会参与到下一个阶段的操作中



**强制终止Phaser**

当一个phaser 对象没有参与线程的时候，它就处于终止状态。Phaser 类提供了`forceTermination()`方法来强制 phaser 进入终止态，这个方法不管 phaser 中是否存在注册的参与线程。当一个参与线程产生错误的时候，强制 phaser 终止是很有意义的

当一个 phaser 处于终止态的时候，`awaitAdvance()`和`arriveAndAwaitAdvance()`方法立即返回一个负数，而不再是一个正值了。如果知道phaser可能会被终止，就需要验证这些方法的返回值，以确定phaser是不是被终止了



## 6. 并发阶段任务中的阶段切换

Phaser 类提供了`onAdvance()`方法， 在 phaser 阶段改变的时候会被自动执行。`onAdvance()`方法需要两个 int  型的传入参数：当前的阶段数以及注册的参与者数量。返回的是 boolean 值，如果返回 false 表示 phaser 在继续执行，返回 true 表示 phaser 已经完成执行并且进入了终止态

这个方法默认实现如下：如果注册的参与者数量是 0 就返回 true，否则就返回 false。但是可以通过继承 Phaser 类覆盖这个方法。一般来说，当必须在从一个阶段到另一个阶段过渡的时候执行一些操作，那么就要这样做



范例将演示如何控制 phaser 中的阶段改变。范例将实现自己的 Phaser 类，并且覆盖`onAdvance()`方法在每个阶段改变的时候执行一些操作。范例模拟考试，考生必须做三道试题，只有当所有学生都完成一道试题的时候， 才能继续下一个

```java
/**
 * 创建 MyPhaser 类并继承 Phaser
 */
public class MyPhaser extends Phaser {

    /**
     * phaser 对象进行阶段切换的时候，在所有在 arriveAndAwaitAdvance() 方法里休眠的线程被唤醒之前，onAdvance() 方法将被自动调用
     *
     * @param phase             当前阶段序号
     * @param registeredParties 注册线程的数量
     * @return 返回 false 表示 phase 在继续进行，返回 true 表示 phase 已经完成执行并进入了终止态
     */
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
            case 0:
                return studentsArrived();
            case 1:
                return finishFirstExercise();
            case 2:
                return finishSecondExercise();
            case 3:
                return finishExam();
            default:
                return true;
        }
    }

    /**
     * phase 0 至 1 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean studentsArrived() {
        System.out.printf("Phaser: The exam are going to start. The students are ready.\n");
        System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
        return false;
    }

    /**
     * phase 1 至 2 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishFirstExercise() {
        System.out.printf("Phaser: All the students has finished the first exercise.\n");
        System.out.printf("Phaser: It's turn for the second one.\n");
        return false;
    }

    /**
     * phase 2 至 3 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishSecondExercise() {
        System.out.printf("Phaser: All the students has finished the second exercise.\n");
        System.out.printf("Phaser: It's turn for the third one.\n");
        return false;
    }

    /**
     * phase 3 至 4 阶段间调用
     *
     * @return 返回false表明 phaser 在继续执行中。
     */
    private boolean finishExam() {
        System.out.printf("Phaser: All the students has finished the exam.\n");
        System.out.printf("Phaser: Thank you for your time.\n");
        return true;
    }
}
```



在主类中，创建 MyPhaser 对象时，并没有指定 phaser 的参与者数目，但是每个学生对象都调用了 phaser 的`register()`方法，这将在 phaser 中注册。这个调用并没有建立学生对象或者它对应的执行线程与 phaser 之间的关联。实际上，phaser中的参与者数目只是一个数字，phaser 与参与者不存在任何关联



## 7. 并发任务间的数据交换

Java 并发 API 还提供了一个 Exchanger 同步辅助类，允许在并发任务之间交换数据。具体来说，Exchanger 类允许在两个线程之间定义同步点（Synchronization Point）。当两个线程都到达同步点时，它们交换数据结构，第一个线程的数据结构进入到第二个线程中，同时第二个线程的数据结构进入到第一个线程中

Exchanger 类在生产者消费者问题情境中很有用。这是一个经典的并发场景，包含一个数据缓冲区，一个或者多个数据生产者，一个或者多个数据消费者。Exchanger 类只能同步两个线程，如果有类似的只有一个生产者和消费者的问题，就可以使用Exchanger类



范例将使用 Exchanger 类来解决一对一的生产者消费者问题

```java
/**
 * 生产者
 */
public class Producer implements Runnable {

    /**
     * 用于数据同步
     */
    private final Exchanger<List<String>> exchanger;
    /**
     * 数据缓冲区
     */
    private List<String> buffer;


    public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * 生产者主要方法，一次创建 10 个 event，循环 10 次
     * 每产生 10 个 event 后，使用 Exchanger 对象与消费者同步数据，消费者获得 10 个 event，生产者获得一个空的 buffer
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Producer: Cycle %d\n", cycle);

            for (int j = 0; j < 10; j++) {
                String message = "Event " + ((i * 10) + j);
                System.out.printf("Producer: %s\n", message);
                buffer.add(message);
            }

            try {
                /*
                 * 与消费者交换 buffer
                 */
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Producer: %d\n", buffer.size());

            cycle++;
        }
    }
}


/**
 * 消费者
 */
public class Consumer implements Runnable {

    /**
     * 用于数据同步
     */
    private final Exchanger<List<String>> exchanger;
    /**
     * 数据缓冲区
     */
    private List<String> buffer;


    public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * 消费者的主要方法，消费生产者生成的所有 event
     * 每消费 10 个 event 后，使用 Exchanger 对象与生产者同步数据，发送给生产者空的 buffer 并获得有 10 个 event 的 buffer
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Consumer: Cycle %d\n", cycle);

            try {
                // 等待生产者生成的数据并将空的 buffer 发送给生产者
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Consumer: %d\n", buffer.size());

            for (int j = 0; j < 10; j++) {
                String message = buffer.get(0);
                System.out.printf("Consumer: %s\n", message);
                buffer.remove(0);
            }

            cycle++;
        }
    }
}

```



Exchanger 类还提供了另外的 exchange 方法，即 `exchange (V data, long time, TimeUnit unit)` 方法。其中第一个传入参数的类型是 V，即要交换的数据结构（本例中是List <String>）。这个方法被调用后，线程将休眠直到被中断，或者其他的线程到达，或者已耗费了指定的 time 值。第三个传入参数的类型是 TimeUnit，它是枚举类型，其值包含以下常量：DAYS、HOURS、MICROSECONDS、MIILLISECONDS、MIINUTES、NANOSECONDS 和 SECONDS



