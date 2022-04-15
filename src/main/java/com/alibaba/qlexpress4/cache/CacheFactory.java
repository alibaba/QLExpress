package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class CacheFactory {
    public static ICache cacheBuilder(int size, boolean isUseCacheClear){
        if(isUseCacheClear){
            return new EdenCache(size);
        }else {
            return new LongTermCache(size);
        }
    }
}
