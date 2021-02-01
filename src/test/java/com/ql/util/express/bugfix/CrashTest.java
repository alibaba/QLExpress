package com.ql.util.express.bugfix;

import com.ql.util.express.parse.WordSplit;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by tianqiao on 16/11/10.
 */
public class CrashTest {


    @Test
    public void helloworld()
    {

    }


    public static String[] splitWord={
            "~","&","|","<<", ">>",//Bit manipulation
            "+", "-","*", "/", "%","++", "--",//Arithmetic：
            ".",",",":",";","(", ")", "{", "}", "[", "]","?",//Delimiter
            "!","<", ">", "<=", ">=", "==","!=","&&","||",//Boolean operation symbol
            "=","/**","**/"
    };

    /**
     * There is a multi-threaded initialization problem below version 3.0.9, this class is used as an example
     */
    public void testCrash() throws InterruptedException, BrokenBarrierException {
        System.out.println(Arrays.asList(splitWord));
        for (int j = 0; j < 1000; j++) {
            CyclicBarrier barrier = new CyclicBarrier(11, null);
            for (int i = 0; i < 10; i++) {
                Thread t = new Thread(new Worker(barrier), "t" + i);
                t.start();
            }
            Thread.sleep(500);
            barrier.await();
            while (barrier.getNumberWaiting() > 0) {
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
            System.out.println(Arrays.asList(splitWord));
        }
    }

    static class Worker implements Runnable {

        private CyclicBarrier barrier;

        public Worker(CyclicBarrier b){
            this.barrier = b;
        }

        public void run() {
            try {

                barrier.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            WordSplit.sortSplitWord(splitWord);
        }
    }
}
