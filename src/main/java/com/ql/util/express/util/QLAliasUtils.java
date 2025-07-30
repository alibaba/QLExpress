package com.ql.util.express.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.annotation.QLAlias;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author tianqiao@come-future.com
 * 2021-11-15 6:31 下午
 */
public class QLAliasUtils {
    private static final Map<String, Object> FIELD_CACHE = new ConcurrentHashMap<>();

    private QLAliasUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Class<?> getPropertyClass(Object bean, String name) {
        Field field = findQLAliasFieldsWithCache(bean.getClass(), name);
        if (field != null) {
            name = field.getName();
        }
        try {
            return PropertyUtils.getPropertyDescriptor(bean, name).getPropertyType();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getProperty(Object bean, String name) {
        try {
            Field field = findQLAliasFieldsWithCache(bean.getClass(), name);
            if (field != null) {
                name = field.getName();
            }
            return PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setProperty(Object bean, Object name, Object value) {
        try {
            Field field = findQLAliasFieldsWithCache(bean.getClass(), name.toString());
            if (field != null) {
                name = field.getName();
            }
            Class<?> filedClass = PropertyUtils.getPropertyType(bean, name.toString());
            PropertyUtils.setProperty(bean, name.toString(), ExpressUtil.castObject(value, filedClass, false));
        } catch (Exception ignore) {
        }
    }

    private static Field findQLAliasFieldsWithCache(Class<?> baseClass, String propertyName) {
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

    public static Field findQLAliasFields(Class<?> baseClass, String propertyName) {
        Field[] fields = baseClass.getDeclaredFields();
        for (Field field : fields) {
            //优先使用本身的定义
            if (propertyName.equals(field.getName())) {
                return field;
            }
            //使用注解定义
            QLAlias[] qlAliases = field.getAnnotationsByType(QLAlias.class);
            for (QLAlias alias : qlAliases) {
                if (alias.value().length > 0) {
                    for (int i = 0; i < alias.value().length; i++) {
                        if (propertyName.equals(alias.value()[i])) {
                            return field;
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

    public static void cleanUp(){
        FIELD_CACHE.clear();
    }
}
