package org.demo.chapter2.recipe2;


import org.demo.chapter2.recipe2.task.Cinema;
import org.demo.chapter2.recipe2.task.TicketOffice1;
import org.demo.chapter2.recipe2.task.TicketOffice2;

/**
 * 使用非依赖属性实现同步
 */
public class Main {

    /**
     * 创建两个售票处线程同时操作电影院对象，两个线程操作互不影响
     */
    public static void main(String[] args) {
        // 创建电影对象
        Cinema cinema = new Cinema();

        // 创建售票处线程
        TicketOffice1 ticketOffice1 = new TicketOffice1(cinema);
        Thread thread1 = new Thread(ticketOffice1, "TicketOffice1");

        TicketOffice2 ticketOffice2 = new TicketOffice2(cinema);
        Thread thread2 = new Thread(ticketOffice2, "TicketOffice2");

        // 启动售票处线程
        thread1.start();
        thread2.start();

        try {
            // 等待线程执行完毕
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 打印电影院余票
        System.out.printf("Room 1 Vacancies: %d\n", cinema.getVacanciesCinema1());
        System.out.printf("Room 2 Vacancies: %d\n", cinema.getVacanciesCinema2());
    }

}
