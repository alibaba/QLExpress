package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ValueInitializationTest {
    
    @Test
    public void nullValueDoesNotDependOnDataValue() {
        assertFalse(Value.NULL_VALUE instanceof DataValue);
        assertNull(Value.NULL_VALUE.get());
        assertEquals(Nothing.class, Value.NULL_VALUE.getType());
    }
    
    @Test
    public void valueAndDataValueCanInitializeConcurrently()
        throws Exception {
        URL classesUrl = new File(System.getProperty("user.dir"), "target/classes").toURI().toURL();
        try (URLClassLoader classLoader = new URLClassLoader(new URL[] {classesUrl}, null)) {
            ThreadFactory threadFactory = runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            };
            ExecutorService executorService = Executors.newFixedThreadPool(2, threadFactory);
            CyclicBarrier startBarrier = new CyclicBarrier(2);
            
            try {
                Future<?> valueFuture = executorService
                    .submit(() -> initialize("com.alibaba.qlexpress4.runtime.Value", classLoader, startBarrier));
                Future<?> dataValueFuture = executorService.submit(
                    () -> initialize("com.alibaba.qlexpress4.runtime.data.DataValue", classLoader, startBarrier));
                
                valueFuture.get(2, TimeUnit.SECONDS);
                dataValueFuture.get(2, TimeUnit.SECONDS);
            }
            finally {
                executorService.shutdownNow();
            }
        }
    }
    
    private static void initialize(String className, ClassLoader classLoader, CyclicBarrier startBarrier) {
        try {
            startBarrier.await();
            Class.forName(className, true, classLoader);
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
