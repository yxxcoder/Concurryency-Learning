package org.demo.chapter2.recipe2.task;

/**
 * 模拟售票处
 * 出售或回收电影票
 */
public class TicketOffice2 implements Runnable {

    /**
     * 电影院
     */
    private Cinema cinema;

    /**
     * 构造方法
     *
     * @param cinema 电影院
     */
    public TicketOffice2(Cinema cinema) {
        this.cinema = cinema;
    }

    /**
     * 模拟出售或回收电影票
     */
    public void run() {
        cinema.sellTickets2(2);
        cinema.sellTickets2(4);
        cinema.sellTickets1(2);
        cinema.sellTickets1(1);
        cinema.returnTickets2(2);
        cinema.sellTickets1(3);
        cinema.sellTickets2(2);
        cinema.sellTickets1(2);
    }

}
