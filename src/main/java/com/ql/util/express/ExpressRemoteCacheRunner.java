package com.ql.util.express;

import java.util.List;

/**
 * 远程缓存对象
 *
 * @author tianqiao
 */
public abstract class ExpressRemoteCacheRunner {
    public void loadCache(String expressName, String text) {
        InstructionSet instructionSet;
        try {
            instructionSet = getExpressRunner().parseInstructionSet(text);
            CacheObject cache = new CacheObject();
            cache.setExpressName(expressName);
            cache.setText(text);
            cache.setInstructionSet(instructionSet);
            this.putCache(expressName, cache);
        } catch (Exception e) {
            throw new RuntimeException("解析指令并缓存过程出现错误.", e);
        }
    }

    public Object execute(String name, IExpressContext<String, Object> context, List<String> errorList, boolean isTrace,
        boolean isCatchException) {
        try {
            CacheObject cache = (CacheObject)this.getCache(name);
            if (cache == null) {
                throw new RuntimeException("未获取到缓存对象.");
            }
            ExpressRunner expressRunner = getExpressRunner();
            return expressRunner.execute(cache.getInstructionSet(), context, errorList, isTrace, isCatchException);
        } catch (Exception e) {
            throw new RuntimeException("获取缓存信息，并且执行指令集出现错误.", e);
        }
    }

    /**
     * 获取执行器ExpressRunner
     *
     * @return
     */
    public abstract ExpressRunner getExpressRunner();

    /**
     * 获取缓存对象
     *
     * @param key
     * @return
     */
    public abstract Object getCache(String key);

    /**
     * 放置缓存的对象
     *
     * @param key
     * @param object
     */
    public abstract void putCache(String key, Object object);
}
