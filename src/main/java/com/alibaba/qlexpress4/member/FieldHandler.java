package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.utils.QLAliasUtil;

import java.lang.reflect.Field;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class FieldHandler extends MemberHandler {
    public static class Preferred {

        public static Field gatherFieldRecursive(Class<?> baseClass, String propertyName) {
            Field[] fields = baseClass.getDeclaredFields();
            for (Field field : fields) {
                //优先使用本身的定义
                if (propertyName.equals(field.getName())) {
                    return field;
                }
                if (QLAliasUtil.findQLAliasFields(field, propertyName)) {
                    return field;
                }
            }
            Class<?> superclass = baseClass.getSuperclass();
            if (superclass != null) {
                return gatherFieldRecursive(superclass, propertyName);
            }
            return null;
        }
    }

    public static IField getFieldFromQLOption(QLOptions options, Class<?> clazz, Field field){
        if(field != null){
            return options.getMetaProtocol().getField(clazz, field.getName(), field);
        }else {
            return null;
        }
    }
}
