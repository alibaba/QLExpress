package com.ql.util.express;

import java.lang.reflect.Array;

import com.ql.util.express.exception.QLException;

/**
 * Created by tianqiao on 16/9/12.
 */
public class DynamicParamsUtil {
    public static boolean supportDynamicParams = false;

    private DynamicParamsUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Object[] transferDynamicParams(InstructionSetContext context, ArraySwap list,
        Class<?>[] declaredParamsClasses, boolean maybeDynamicParams) throws Exception {

        Object[] params;
        // 参数定义不符合动态参数形式 || 用户自定义不支持 || 用户传入的参数不符合
        if (!maybeDynamicParams || !supportDynamicParams || !maybeDynamicParams(context, list, declaredParamsClasses)) {
            if (declaredParamsClasses.length != list.length) {
                throw new QLException("定义的参数长度与运行期传入的参数长度不一致");
            }
            params = new Object[list.length];
            for (int i = 0; i < list.length; i++) {
                params[i] = list.get(i).getObject(context);
            }
            return params;
        }

        //支持不定参数的使用 function(arg1,arg2,arg3...)
        //list -> parameters[]
        // arg1,arg2 -> arg1,arg2,[]
        // arg1,arg2,arg3,arg4,arg5   ->  arg1,arg2,[arg3,arg4,arg5]
        int paramLength = declaredParamsClasses.length;
        int beforeCount = paramLength - 1;
        int paramsCount = list.length - beforeCount;

        if (beforeCount >= 0 && declaredParamsClasses[beforeCount].isArray() && paramsCount >= 0) {
            Class<?> componentType = declaredParamsClasses[beforeCount].getComponentType();
            params = new Object[beforeCount + 1];
            Object[] lastParameters = (Object[])Array.newInstance(componentType, paramsCount);
            params[beforeCount] = lastParameters;
            for (int i = 0; i < list.length; i++) {
                if (i < beforeCount) {
                    params[i] = list.get(i).getObject(context);
                } else {
                    lastParameters[i - beforeCount] = list.get(i).getObject(context);
                }
            }
        } else {
            throw new QLException("定义的参数长度与运行期传入的参数长度不一致");
        }
        return params;
    }

    public static boolean maybeDynamicParams(Class<?>[] declaredParamsClasses) {
        int length = declaredParamsClasses.length;
        return length > 0 && declaredParamsClasses[length - 1].isArray();
    }

    private static boolean maybeDynamicParams(InstructionSetContext context, ArraySwap list,
        Class<?>[] declaredParamsClasses) throws Exception {

        //长度不一致,有可能
        if (declaredParamsClasses.length != list.length) {
            return true;
        }
        //长度一致的不定参数:不定参数的数组,只输入了一个参数并且为array,有可能
        int length = list.length;
        Object lastParam = list.get(length - 1).getObject(context);
        return lastParam != null && !lastParam.getClass().isArray();
    }
}
