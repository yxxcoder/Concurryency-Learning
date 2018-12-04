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



## 6. 等待线程的终止

在一些情形下，我们必须等待线程的终止。例如，我们的程序在执行其他的任务时，必须先初始化一些必须的资源。可以使用线程来完成这些初始化任务，等待线程终止，再执行程序的其他任务

为了达到这个目的，我们使用Thread类的join()方法。当一个线程对象的join()方法被调用时，**调用它的线程将被挂起**，直到这个线程对象完成它的任务

```java
public class Main {
    /**
     * 创建并启动两个线程，等待他们终止
     */
    public static void main(String[] args) {
        // 创建并启动DataSourceLoader线程对象
        DataSourcesLoader dsLoader = new DataSourcesLoader();
        Thread thread1 = new Thread(dsLoader,"DataSourceThread");
        thread1.start();
        // 创建并启动NetworkConnectionsLoader线程
        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread thread2 = new Thread(ncLoader,"NetworkConnectionLoader");
        thread2.start();
      
        // 等待两个线程终止
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 主线程执行结束
        System.out.printf("Main: Configuration has been loaded: %s\n",new Date());
    }
}
```



Java提供了另外两种形式的join()方法:

```java
join (long milliseconds)
join (long milliseconds, long nanos)
```

当一个线程调用其他某个线程的join()方法时，如果使用的是第一种join()方式，那么它不必等到被调用线程运行终止，**如果参数指定的毫秒时钟已经到达，它将继续运行**。例如，thread1中有这样的代码thread2.join(1000)，thread1 将挂起运行，直到满足下面两个条件之一:

- thread2运行已经完成;
- 时钟已经过去1000毫秒。

当两个条件中的任何一条成立时，join()方法将返回

第二种join()方法跟第一种相似，只是需要接受毫秒和纳秒两个参数



## 7. 守护线程的创建和运行

Java里有一种特殊的线程叫做守护(Daemon)线程。这种线程的优先级很低，通常来说，当同一个应用程序里没有其他的线程运行的时候，守护线程才运行。当守护线程是程序中唯一运行的线程时， 守护线程执行结束后，JVM也就结束了这个程序

因为这种特性，守护线程通常被用来做为同一程序中普通线程(也称为用户线程)的服务提供者。它们通常是无限循环的，以等待服务请求或者执行线程的任务。它们不能做重要的工作，因为我们不可能知道守护线程什么时候能够获取CPU时钟，并且，在没有其他线程运行的时候，守护线程随时可能结束。一个典型的守护线程是Java的垃圾回收器(Garbage Collector)

```java
public class ATask extends Thread {
	public CleanerTask() {
		// 把当前线程设置为守护线程
		setDaemon(true);
	}
    @Override
	public void run() {
		// do something
	}
}
```



**setDaemon()方法只能在start()方法被调用之前设置**。一旦线程开始运行，将不能再修改守护状态

isDaemon()方法被用来检查一个线程是不是守护线程，返回值true 表示这个线程是守护线程，false 表示这个线程是用户线程



## 8. 线程中不可控异常的处理

在Java中有两种异常：

- 非运行时异常(Checked Exception)：这种异常必须在方法声明的throws语句指定，或者在方法体内捕获。例如:IOException 和ClassNotFoundException
- 运行时异常(Unchecked Exception)：这种异常不必在方法声明中指定，也不需要在方法体中捕获。例如: NumberFormatException

因为run()方法不支持throws语句，所以当线程对象的run()方法抛出非运行异常时，我们必须捕获并且处理它们。当运行时异常从run()方法中抛出时，默认行为是在控制台输出堆栈记录并且退出程序

好在，Java提供给一种在线程对象里捕获和处理运行时异常的一种机制

```java
public class Main {
    public static void main(String[] args) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                // 故意抛出异常
                int numero=Integer.parseInt("TTT");
            }
        });
        // 设置线程的运行时异常处理器
        thread.setUncaughtExceptionHandler(new ExceptionHandler());

        // 也可以为所有线程对象设置默认异常处理器
        // 未查找到线程对象的异常处理器时会查找线程对象所在的线程组的异常处理器，还找不到时就会查找默认异常处理器
        // Thread.setDefaultUncaughtExceptionHandler();

        // 启动线程
        thread.start();

        try {
            // 等待线程执行结束
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread has finished\n");
    }
}

/**
 * 用于处理线程中的运行时异常(Unchecked Exception)
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
    /**
     * 处理线程中的运行时异常
     * @param t 抛出异常的线程对象
     * @param e 线程抛出的运行时异常对象
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s\n",t.getId());
        System.out.printf("Exception: %s: %s\n",e.getClass().getName(),e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status: %s\n",t.getState());
    }
}
```

Thread类的静态方法`setDefaultUncaughtExceptionHandler()`也可以处理未捕获到的异常。这个方法在应用程序中为所有的线程对象创建了一个异常处理器

当线程抛出一个未捕获到的异常时，JVM将为异常寻找以下三种可能的处理器：

​	首先，它查找线程对象的未捕获异常处理器；如果找不到，JVM继续查找线程对象所在的线程组(ThreadGroup)的未捕获异常处理器（后面会用到）；如果还是找不到，JVM将继续查找默认的未捕获异常处理器；如果没有一个处理器存在，JVM则将堆栈异常记录打印到控制台，并退出程序



## 9. 线程局部变量的使用

## 10.线程的分组

## 11. 线程组中不可控异常的处理

## 12. 使用工程类创建线程


