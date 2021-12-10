package com.ql.util.express;

/**
 * 加载表达式资源接口
 *
 * @author xuannan
 */
public interface IExpressResourceLoader {
    /**
     * 根据表达式名称获取表达式的内容
     *
     * @param expressName
     * @return
     * @throws Exception
     */
    String loadExpress(String expressName) throws Exception;
}
