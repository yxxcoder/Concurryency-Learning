package org.demo.chapter3.recipe4;

import org.demo.chapter3.recipe4.task.Grouper;
import org.demo.chapter3.recipe4.task.Searcher;
import org.demo.chapter3.recipe4.utils.MatrixMock;
import org.demo.chapter3.recipe4.utils.Results;

import java.util.concurrent.CyclicBarrier;

/**
 * 在集合点的同步
 * 利用 CyclicBarrier 实现分治编程技术
 * <p>
 *     将在数字矩阵中寻找一个数字（使用分治编程技术）。这个矩阵会被分成几个子集，然后
 *     每个线程在一个子集中查找。一旦所有线程都完成查找，最终的任务将统一这些结果
 * </p>
 */
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

        // 初始化结果对象Results
        Results results = new Results(ROWS);

        // 初始化汇总线程
        Grouper grouper = new Grouper(results);

        // 创建CyclicBarrier对象，这个对象等待 5 个查询线程执行结束后执行汇总线程grouper
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
