//package com.alibaba.qlexpress4.test.cache;
//
//import com.alibaba.qlexpress4.cache.LRUCache;
//import com.alibaba.qlexpress4.cache.LRUCache2;
//import com.alibaba.qlexpress4.cache.LRUCache3;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.IntStream;
//
//
///**
// * @Author TaoKan
// * @Date 2022/6/8 下午12:24
// */
//public class TestLRU {
//    @Test
//    public void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
//        final int size = 1000000;
//        final int maxSize = 1000;
//        final ExecutorService executorService = Executors.newFixedThreadPool(32);
//        LRUCache3<Integer, String> cache = new LRUCache3(32,maxSize);
//        CountDownLatch countDownLatch = new CountDownLatch(size);
//        try {
//            IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
//                cache.put(key, "value" + key);
//                countDownLatch.countDown();
//            }).forEach(executorService::submit);
//            countDownLatch.await(1, TimeUnit.SECONDS);
//        } finally {
//            executorService.shutdown();
//        }
////        Assert.assertEquals(cache.size(), size);
////        IntStream.range(0, size).forEach(i -> Assert.assertEquals("value" + i,cache.get(i)));
//    }
//
//
////    @Test
////    public void runMultiThreadTask_WhenPutDataInConcurrentToCache_LRUNum() throws Exception {
////        final int size = 4400;
////        final int exceedSize = 420000;
////        final ExecutorService executorService = Executors.newFixedThreadPool(8);
////        LRUCache3<Integer, String> cache = new LRUCache3(8,size);
////        CountDownLatch countDownLatch = new CountDownLatch(exceedSize);
////        try {
////            IntStream.range(0, exceedSize).<Runnable>mapToObj(key -> () -> {
////                cache.put(key, "value" + key);
////                countDownLatch.countDown();
////            }).forEach(executorService::submit);
////            countDownLatch.await(100, TimeUnit.SECONDS);
////        } finally {
////            executorService.shutdown();
////        }
////        final ExecutorService executorService2 = Executors.newFixedThreadPool(8);
////        CountDownLatch countDownLatch2= new CountDownLatch(exceedSize);
////        try {
////            IntStream.range(0, exceedSize).<Runnable>mapToObj(key -> () -> {
////                cache.get(key);
////                countDownLatch2.countDown();
////            }).forEach(executorService2::submit);
////            countDownLatch2.await(100, TimeUnit.SECONDS);
////        } finally {
////            executorService2.shutdown();
////        }
////        final ExecutorService executorService3 = Executors.newFixedThreadPool(8);
////        CountDownLatch countDownLatch3= new CountDownLatch(exceedSize);
////        try {
////            IntStream.range(0, exceedSize).<Runnable>mapToObj(key -> () -> {
////                cache.put(key, "value" + key);
////                countDownLatch3.countDown();
////            }).forEach(executorService3::submit);
////            countDownLatch3.await(100, TimeUnit.SECONDS);
////        } finally {
////            executorService3.shutdown();
////        }
////        Assert.assertEquals(cache.size(), size);
////    }
//
////
////
////    @Test
////    public void lately_LRU() throws Exception {
////        LRUCache3<Integer, String> cache = new LRUCache3(1,2);
////        cache.put(1,"1");
////        cache.put(2,"2");
////        cache.get(1);
////        cache.put(3,"3");
////        Assert.assertEquals(cache.size(), 2);
////        Assert.assertNull(cache.get(2), null);
////    }
//////
////    @Test
////    public void fullSize_LRU() throws Exception {
////        LRUCache3<Integer, String> cache = new LRUCache3(8, 50);
////        for(int i = 0; i < 70; i++){
////            cache.put(i,"value"+i);
//////            System.out.println(cache.totalSize);
//////            for(int j = 0; j < 70; j++){
//////                System.out.println(cache.get(j));
//////            }
////        }
//////        LRUCache3<Integer, String> cache = new LRUCache3(8, 50);
//////        for(int i = 0; i < 50; i++){
//////            cache.put(i,"value"+i);
//////            System.out.println(cache.totalSize);
//////        }
//////
//////        for(int i = 0; i < 50; i++){
//////            System.out.println(cache.get(i));
//////        }
////    }
//
//}