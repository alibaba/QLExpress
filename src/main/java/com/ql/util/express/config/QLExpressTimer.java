package com.ql.util.express.config;

import com.ql.util.express.exception.QLTimeOutException;

import java.sql.SQLTimeoutException;

/**
 * @author tianqiao@taobao.com
 * @since 2019/6/17 4:12 PM
 */
public class QLExpressTimer {

    private static ThreadLocal<Boolean> NEED_TIMER = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    private static ThreadLocal<Long> TIME_OUT_MILLIS = new ThreadLocal<Long>(){};
    private static ThreadLocal<Long> START_TIME = new ThreadLocal<Long>(){};
    private static ThreadLocal<Long> END_TIME = new ThreadLocal<Long>(){};



    /**
     * Set timer
     * @param timeoutMillis overtime time
     */
    public static void setTimer(long timeoutMillis)
    {
        NEED_TIMER.set(true);
        TIME_OUT_MILLIS.set(timeoutMillis);
    }

    /**
     * start the timer
     */
    public static void startTimer()
    {
        if(NEED_TIMER.get()) {
            long t = System.currentTimeMillis();
            START_TIME.set(t);
            END_TIME.set(t+TIME_OUT_MILLIS.get());
        }
    }


    /**
     * Whether the assertion timed out
     * @throws QLTimeOutException
     */
    public static void assertTimeOut() throws QLTimeOutException {

        if(NEED_TIMER.get() && System.currentTimeMillis()>END_TIME.get()){
            throw new QLTimeOutException("The next instruction to run the QLExpress script will exceed the time limit:" + TIME_OUT_MILLIS.get() + "ms");
        }
    }

    public static boolean hasExpired()
    {
        if(NEED_TIMER.get() && System.currentTimeMillis()>END_TIME.get()){
            return true;
        }
        return false;
    }

    public static void reset()
    {
        if(NEED_TIMER.get()) {
            START_TIME.remove();
            END_TIME.remove();
            NEED_TIMER.remove();
            TIME_OUT_MILLIS.remove();
        }
    }
}
