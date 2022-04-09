package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.config.QLExpressRunStrategy;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class CacheFactory {
    public static ICache cacheBuilder(int size){
        if(QLExpressRunStrategy.isUseCacheClear()){
            return new EdenCache(size);
        }else {
            return new LongTermCache(size);
        }
    }
}
