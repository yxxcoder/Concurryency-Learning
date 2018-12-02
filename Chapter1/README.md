# 第一章 线程管理

[TOC]

## 1. 线程的创建和运行
Java提供了两种方式创建线程；
- 继承Thread类并覆盖run()方法
- 实现Runnable接口，该类的对象作为Thread构造方法的参数创建Thread对象
```java
public class Main {
    public static void main(String[] args) {

        // 创建10个线程执行计算任务
        for (int i=1; i<=10; i++){
            Calculator calculator=new Calculator(i);
            Thread thread=new Thread(calculator);
            thread.start();
        }
    }
}

/**
 * 打印乘法表
 **/
public class Calculator implements Runnable {

    private int number;

    public Calculator(int number) {
        this.number=number;
    }

    @Override
    public void run() {
        for (int i=1; i<=10; i++){
            System.out.printf("%s: %d * %d = %d\n",Thread.currentThread().getName(),number,i,i*number);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```



## 2. 线程的创建和运行

Thread类有一些保存信息的属性，这些属性可以用来标识线程，显示线程的状态或者控制线程的优先级
- ID: 保存了线程的唯-标示符
- Name:保存了线程名称
- Priority:保存了线程对象的优先级。线程的优先级是从1到10,其中1是最低优先级:10是最高优先级。**并不推荐去改变线程的优先级**，然而，在需要的时候，也可以这么做
- Status:保存了线程的状态

在Java中，线程的状态有6种: new、 runnable、 blocked.waiting、time waiting或者terminated
```java
public class Main {
    public static void main(String[] args) {
        // 线程的优先级信息
        // 线程的优先级是从1到10 1为最低优先级
        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximun Priority: %s\n", Thread.MAX_PRIORITY);

        Thread thread = new Thread(new Calculator(0));
        // 设置优先级
        thread.setPriority(Thread.MAX_PRIORITY);
        // 设置线程名字
        thread.setName("A Thread");

        // 获取线程ID及Name
        System.out.printf("Main : Id %d - %s\n",thread.getId(), thread.getName());
        // 获取线程优先级
        System.out.printf("Main : Priority: %d\n", thread.getPriority());
        // 获取线程状态
        System.out.printf("Main : State: %s\n", thread.getState());
    }
}
```

Thread类的属性存储了线程的所有信息。JVM使用线程的priority属性来决定某一刻由哪个线程来使用CPU,并且根据线程的情景为它们设置实际状态

如果没有为线程指定一个名字，JVM将自动给它分配一个名字，格式是Thread-XX,其中XX是一组数字线程的ID和状态是不允许被修改的，线程类没有提供setld()和setStatus()方法来修改它们



## 3. 线程的中断  

如果一个Java程序有不止一个执行线程，**当所有线程都运行结束的时候，这个Java程序才能运行结束**；更确切地说应该是**所有的非守护线程运行结束时，或者其中一个线程调用了System.exit()方法时，这个Java程序才运行结束**。如果你想终止一个程序，或者程序的某个用户试图取消线程对象正在运行的任务，就需要结束这个线程

Java提供了中断机制，我们可以使用它来结束一个线程。这种机制要求线程检查它是否被中断了，然后决定是不是响应这个中断请求。线程允许忽略中断请求并且继续执行

```java
public class Main {
    public static void main(String[] args) {
        // 启动线程
        Thread task=new PrimeGenerator();
        task.start();

        // 设置线程中断状态
        task.interrupt();

        // 检查线程是否已被中断
        // isInterrupted只是简单的查询中断状态，不会对状态进行修改
        System.out.println("Task Thread is interrupted: " + task.isInterrupted());

        /**
         * interrupted是静态方法，返回的是当前线程的中断状态
         * 如果当前线程被中断（没有抛出中断异常，否则中断状态就会被清除），调用interrupted方法，第一次会返回true
         * 然后，当前线程的中断状态被方法内部清除了。第二次调用时就会返回false
         * 查询状态时更推荐使用isInterrupted
         */
        Thread.currentThread().interrupt();
        System.out.println("current Thread is interrupted: " + Thread.interrupted());
        System.out.println("current Thread is interrupted: " + Thread.interrupted());
    }
}
```



## 4. 线程中断的控制
上一节讲解了如何去中断执行中的线程，也学会了如何在线程对象中去控制这个中断。在上一节中使用的机制，可以使用在线程很容易被中断的情况下。但是，如果线程实现了复杂的算法并且分布在几个方法中，或者线程里有递归调用的方法，就得使用一个更好的机制来控制线程的中断为了达到这个目的，**Java 提供了InterruptedException异常。当检查到线程中断的时候，就抛出这个异常，然后在run()方法中捕获并处理这个异常**

具体实践请见org/demo/chapter1/recipe3



## 5. 线程的休眠和恢复

有些时候，你需要在某一个预期的时间中断线程的执行。例如，程序的一个线程每隔一分钟检查一次传感器状态，其余时间什么都不做。**在这段空闲时间，线程不占用计算机的任何资源**。当它继续执行的CPU时钟来临时，JVM会选中它继续执行。可以通过线程的sleep()方法来达到这个目标。sleep()方法接受整型数值作为参数，以表明线程挂起执行的毫秒数。**当线程休眠的时间结束了，JVM会分给它CPU时钟**，线程将继续执行它的指令

**sleep()方法的另一种使用方式是通过TimeUnit枚举类元素进行调用。**这个方法**也使用Thread类的sleep()方法来使当前线程休眠，**但是它接收的参数单位是秒，最后会被转化成毫秒

```java
public class Main {
    /**
     * 创建并启动FileClock线程用于每秒钟打印一次当前时间，等待5秒后中断线程
     * 当休眠中线程被中断会立即抛出InterruptedException异常，不需要等待线程休息时间结束
     * 最佳实践是，当线程被中断时，释放或者关闭线程正在使用的资源
     */
    public static void main(String[] args) {
        // 创建FileClock runnable对象并创建一个线程运行
        FileClock clock=new FileClock();
        Thread thread=new Thread(clock);

        // 启动线程
        thread.start();
        try {
            // 休眠5秒钟
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
        // 中断线程
        thread.interrupt();
    }
}
```

