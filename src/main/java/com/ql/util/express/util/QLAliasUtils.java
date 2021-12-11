package com.ql.util.express.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ql.util.express.annotation.QLAlias;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author tianqiao@come-future.com
 * 2021-11-15 6:31 下午
 */
public class QLAliasUtils {
    public static final Map<String, Object> FIELD_CACHE = new ConcurrentHashMap<>();

    public static Class<?> getPropertyClass(Object bean, String name) {
        Field f = findQLAliasFieldsWithCache(bean.getClass(), name);
        if (f != null) {
            f.setAccessible(true);
            return f.getType();
        }
        try {
            return PropertyUtils.getPropertyDescriptor(bean, name).getPropertyType();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getProperty(Object bean, String name) {
        try {
            Field f = findQLAliasFieldsWithCache(bean.getClass(), name);
            if (f != null) {
                f.setAccessible(true);
                return f.get(bean);
            }
            return PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            return null;
        }
    }

    private static Field findQLAliasFieldsWithCache(Class baseClass, String propertyName) {
        String key = baseClass + "#" + propertyName;
        Object result = FIELD_CACHE.get(key);
        if (result == null) {
            result = findQLAliasFields(baseClass, propertyName);
            if (result == null) {
                FIELD_CACHE.put(key, void.class);
            } else {
                FIELD_CACHE.put(key, result);
            }
        } else if (result == void.class) {
            result = null;
        }
        return (Field)result;
    }

    public static Field findQLAliasFields(Class baseClass, String propertyName) {
        Field[] fields = baseClass.getDeclaredFields();
        for (Field f : fields) {
            //优先使用本身的定义
            if (propertyName.equals(f.getName())) {
                return f;
            }
            //使用注解定义
            QLAlias[] qlAliases = f.getAnnotationsByType(QLAlias.class);
            if (qlAliases != null) {
                for (QLAlias alias : qlAliases) {
                    if (alias.value().length > 0) {
                        for (int i = 0; i < alias.value().length; i++) {
                            if (propertyName.equals(alias.value()[i])) {
                                return f;
                            }
                        }
                    }
                }
            }
        }
        Class<?> superclass = baseClass.getSuperclass();
        if (superclass != null) {
            return findQLAliasFields(superclass, propertyName);
        }
        return null;
    }
}
