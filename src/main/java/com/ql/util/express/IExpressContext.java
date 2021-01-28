package com.ql.util.express;

/**
 * Data injection interface for expression calculation
 * @author qhlhl2010@gmail.com
 *
 */
public interface IExpressContext<K,V> {
    /**
     * Extract the attribute value from the attribute list based on the name. If the Spring object is used in the expression, it is also obtained through this method
     * @param key Attribute name
     * @return
     */
    public V get(Object key);
    /**
     * The result of expression calculation can be set back to the calling system, for example userId = 3 + 4
     * @param name Attribute name
     * @param object Attribute value
     */
    public V put(K name, V object);
}
