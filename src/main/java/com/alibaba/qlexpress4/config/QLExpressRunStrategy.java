package com.alibaba.qlexpress4.config;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class QLExpressRunStrategy {

    /**
     * cache clear cutoff
     */
    private static boolean useCacheClear = false;


    public static boolean isUseCacheClear() {
        return useCacheClear;
    }

    public static void initUseCacheClear(boolean useCacheClear) {
        QLExpressRunStrategy.useCacheClear = useCacheClear;
    }
}
