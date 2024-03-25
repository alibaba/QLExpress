package com.ql.util.express.issue;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue312Test {
    @Test
    public void test() throws Exception {
        while (true) {
            exeucte();
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                while (true) {
                    exeucte();
                }
            });
        }

        // 信号量
        Semaphore semaphore = new Semaphore(0);
        semaphore.acquire();
    }

    private static void exeucte() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner();
        expressRunner.setIgnoreConstChar(true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("input", "bingo");
        String express = "input.equals(\"" + UUID.randomUUID() + "\")";
        Object execute = expressRunner.execute(express, context, null, true, false);
        //System.out.println(execute);
    }
}
