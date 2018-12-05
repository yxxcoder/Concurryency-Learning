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

当某个对象的属性不需要被所有的线程共享，可以使用Java并发API提供的线程局部变量( Thread-Local Variable)

```java
public class SafeMain {
    /**
     * 创建三个线程运行SafeTask任务
     */
    public static void main(String[] args) {
        SafeTask task=new SafeTask();
        for (int i=0; i<3; i++){
            Thread thread=new Thread(task);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.start();
        }
    }
}

public class SafeTask implements Runnable {
    /**
     * ThreadLocal存储的数据可在线程间共享
     */
    private static ThreadLocal<Date> startDate= new ThreadLocal<Date>() {
        @Override
        protected Date initialValue(){
            return new Date();
        }
    };

    /**
     * 打印ThreadLocal保存的时间到控制台，休息若干秒后再次打印ThreadLocal中已保存的时间对象
     */
    @Override
    public void run() {
        System.out.printf("Starting Thread: %s : %s\n",Thread.currentThread().getId(),startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int)Math.rint(Math.random()*10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n",Thread.currentThread().getId(),startDate.get());
    }
}

```

线程局部变量也提供了remove()方法， 用来为访问这个变量的线程删除已经存储的值。Java并发API包含了`InheritableThreadLocal` 类，**如果一个线程是从其他某个线程中创建的，这个类将提供继承的值**。如果一个线程A在线程局部变量已有值，当它创建其他某个线程B时，线程B的线程局部变量将跟线程A是一样的。你可以覆盖`childValue()`方法，这个方法用来初始化子线程在线程局部变量中的值。它使用父线程在线程局部变量中的值作为传入参数



## 10.线程的分组

Java并发API提供线程分组的能力。这允许我们把一个组的线程当成一个单一的单元，对组内线程对象进行访问并操作它们。例如，对于一些执行同样任务的线程，想控制它们，不管多少线程在运行，只需要一个单一的调用，所有这些线程的运行都会被中断

Java提供ThreadGroup类表示一组线程。 线程组可以包含线程对象，也可以包含其他的线程组对象，它是一个树形结构

```java
// 创建标识为Searcher的线程对象
ThreadGroup threadGroup = new ThreadGroup("Searcher");
// 创建线程，第一个参数是ThreadGroup对象，第二个参数是Runnable对象
Thread thread=new Thread(threadGroup, searchTask);

// 获取线程组包含的线程数目
System.out.printf("Number of Threads: %d\n",threadGroup.activeCount());
// 打印线程组对象信息
threadGroup.list();

Thread[] threads=new Thread[threadGroup.activeCount()];
// 获取线程组包含的线程列表
threadGroup.enumerate(threads);

// 中断线程组中为执行完成的线程
threadGroup.interrupt();
```



## 11. 线程组中不可控异常的处理

建立一个方法来捕获线程组中的任何线程对象抛出的非捕获异常

```java
/**
 * 创建MyThreadGroup继承ThreadGroup并重写uncaughtException方法
 */
public class MyThreadGroup extends ThreadGroup {
    /**
     * 声明带参数的构造方法
     * @param name
     */
    public MyThreadGroup(String name) {
        super(name);
    }

    /**
     * 覆盖父类的处理uncaughtException方法，处理未捕获的异常
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 打印异常线程的id
        System.out.printf("The thread %s has thrown an Exception\n", t.getId());
        // 打印异常堆栈信息
        e.printStackTrace(System.out);
        // 中断线程组的其余线程
        System.out.printf("Terminating the rest of the Threads\n");
        interrupt();
    }
}
/**
 * 创建线程组并创建两个线程放入该线程组中
 * 当线程抛出未捕获异常时，该线程所在线程组的异常处理器将捕获该异常
 */
public class Main {
    public static void main(String[] args) {

        // 创建线程组
        MyThreadGroup threadGroup=new MyThreadGroup("MyThreadGroup");
        // 创建两个线程放入该线程组中
        Task task=new Task();
        for (int i=0; i<2; i++){
            Thread t=new Thread(threadGroup,task);
            t.start();
        }
    }
}
```

当线程抛出一个未捕获到的异常时，JVM将为异常寻找以下三种可能的处理器：

​	首先，它查找线程对象的未捕获异常处理器；如果找不到，JVM继续查找线程对象所在的线程组(ThreadGroup)的未捕获异常处理器（如本节所示）；如果还是找不到，JVM将继续查找默认的未捕获异常处理器；如果没有一个处理器存在，JVM则将堆栈异常记录打印到控制台，并退出程序



## 12. 使用工程类创建线程

工厂模式是面向对象编程中最常使用的模式之一。它是一个创建者模式，使用一个类为其他的一个或者多个类创建对象。当我们要为这些类创建对象时，不需再使用new构造器，而使用工厂类

使用工厂类，可以将对象的创建集中化，这样做有以下的好处：

- 更容易修改类，或者改变创建对象的方式

- 更容易为有限资源限制创建对象的数目。例如，我们可以限制一个类型的对象不多于n个

- 更容易为创建的对象生成统计数据

Java提供了ThreadFactory 接口，这个接口实现了线程对象工厂。Java并发API的高级工具类也使用了线程工厂创建线程

```java
/**
 * 实现ThreadFactory接口创建一个简单的线程工厂类
 */
public class MyThreadFactory implements ThreadFactory {

    // 存放线程对象的数量
    private int counter;
    // 存放线程名称
    private String name;
    // 存放线程信息
    private List<String> stats;

    /**
     * 构造方法
     * @param name 线程名称
     */
    public MyThreadFactory(String name){
        counter=0;
        this.name=name;
        stats=new ArrayList<String>();
    }

    /**
     * Runnable对象为参数创建线程对象
     * @param r: Runnable对象
     */
    @Override
    public Thread newThread(Runnable r) {
        // 创建一个线程
        Thread t=new Thread(r,name+"-Thread_"+counter);
        // 数量加一
        counter++;
        // 保存统计数据
        stats.add(String.format("Created thread %d with name %s on %s\n",t.getId(),t.getName(),new Date()));
        // 返回创建的线程
        return t;
    }

    /**
     * 返回所有线程的统计数据
     * @return String类型的统计数据
     */
    public String getStats(){
        StringBuffer buffer=new StringBuffer();
        Iterator<String> it=stats.iterator();

        while (it.hasNext()) {
            buffer.append(it.next());
        }
        return buffer.toString();
    }
}

/**
 * 使用工厂类创建线程
 */
public class Main {
    /**
     * 实现线程工厂类并使用工厂类创建10个线程
     * @param args
     */
    public static void main(String[] args) {
        // 创建线程工厂对象
        MyThreadFactory factory=new MyThreadFactory("MyThreadFactory");

        // 创建10个线程并启动
        System.out.printf("Starting the Threads\n");
        for (int i=0; i<10; i++){
            Thread thread=factory.newThread(new Task());
            thread.start();
        }
        // 将线程工厂的统计打印到控制台
        System.out.printf("Factory stats:\n");
        System.out.printf("%s\n",factory.getStats());
    }
}
```



ThreadFactory接口只有一个方法，即`newThread`,它以`Runnable`接口对象作为传入参数并且返回一个线程对象。当实现ThreadFactory接口时，必须实现覆盖这个方法。大多数基本的线程工厂类只有一行，即:

```
return new Thread(r) ;
```



可以通过增加一些变化来强化实现方法覆盖：

- 创建一个个性化线程， 如本例使用一个特殊的格式作为线程名，或者通过继承Thread类来创建自己的线程类

- 保存新创建的线程的统计数据，如本例这样

- 限制创建的线程的数量

- 对生成的线程进行验证

使用工厂设计模式是一个很好的编程实践，但是，如果是通过实现ThreadFactory接口来创建线程，你必须检查代码，以保证所有的线程都是使用这个工厂创建的