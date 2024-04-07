package com.ql.util.express;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class ExecuteTimeoutTest {

    @Test
    public void noTimeoutTest() throws InterruptedException {
        assertFalse(ExecuteTimeout.NO_TIMEOUT.isExpired());

        ExecuteTimeout timeOut = new ExecuteTimeout(20);
        Thread.sleep(15);
        assertFalse(timeOut.isExpired());
        Thread.sleep(6);
        assertTrue(timeOut.isExpired());
    }

}