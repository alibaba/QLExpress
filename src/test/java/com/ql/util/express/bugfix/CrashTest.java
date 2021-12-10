package com.ql.util.express.bugfix;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.ql.util.express.parse.WordSplit;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tianqiao on 16/11/10.
 */
public class CrashTest {
    public static final String[] splitWord = {
        "~", "&", "|", "<<", ">>",//位操作
        "+", "-", "*", "/", "%", "++", "--",//四则运算：
        ".", ",", ":", ";", "(", ")", "{", "}", "[", "]", "?",//分隔符号
        "!", "<", ">", "<=", ">=", "==", "!=", "&&", "||",//Boolean运算符号
        "=", "/**", "**/"
    };

    /**
     * 版本3.0.9以下存在多线程初始化问题，这个类作为一个样例
     */
    @Test
    @Ignore
    public void test_crash() throws InterruptedException, BrokenBarrierException {
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
        private final CyclicBarrier barrier;

        public Worker(CyclicBarrier b) {
            this.barrier = b;
        }

        public void run() {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            WordSplit.sortSplitWord(splitWord);
        }
    }
}
