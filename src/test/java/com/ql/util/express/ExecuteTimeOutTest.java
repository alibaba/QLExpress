package com.ql.util.express;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class ExecuteTimeOutTest {

    @Test
    public void noTimeoutTest() throws InterruptedException {
        assertFalse(ExecuteTimeOut.NO_TIMEOUT.isExpired());

        ExecuteTimeOut timeOut = new ExecuteTimeOut(20);
        Thread.sleep(15);
        assertFalse(timeOut.isExpired());
        Thread.sleep(6);
        assertTrue(timeOut.isExpired());
    }

}