package com.ql.util.express;

import com.ql.util.express.exception.QLException;

import java.lang.reflect.Array;

/**
 * Created by tianqiao on 16/9/12.
 */
public class DynamicParamsUtil {

    public static boolean supportDynamicParams = false;

    public static Object[] transferDynamicParams(InstructionSetContext context, ArraySwap list, Class<?>[] delaredParamsClasses,boolean maybeDynamicParams) throws Exception {

        Object[] params = null;
        //参数定义不符合动态参数形式 || 用户自定义不支持 || 用户传入的参数不符合
        if(!maybeDynamicParams || !supportDynamicParams || !maybeDynamicParams(context,list,delaredParamsClasses)){
            if(delaredParamsClasses.length != list.length){
                throw new QLException("定义的参数长度与运行期传入的参数长度不一致");
            }
            params = new Object[list.length];
            for (int i = 0; i < list.length; i++) {
                params[i] = list.get(i).getObject(context);
            }
            return params;
        }

        //支持不定参数的使用 function(arg1,arg2,arg3...)
        //list -> parameres[]
        // arg1,arg2 -> arg1,arg2,[]
        // arg1,arg2,arg3,arg4,arg5   ->  arg1,arg2,[arg3,arg4,arg5]
        int paramLength = delaredParamsClasses.length;
        int beforeCount = paramLength-1;
        int paramsCount = list.length - beforeCount;

        if(beforeCount>=0 && ((Class<?>)(delaredParamsClasses[beforeCount])).isArray() && paramsCount>=0){
            Class<?>componentType = delaredParamsClasses[beforeCount].getComponentType();
            params = new Object[beforeCount+1];
            Object[] lastParameres = (Object[]) Array.newInstance(componentType,paramsCount);
            params[beforeCount] = lastParameres;
            for (int i = 0; i < list.length; i++) {
                if(i<beforeCount) {
                    params[i] = list.get(i).getObject(context);
                }else{
                    lastParameres[i-beforeCount] = list.get(i).getObject(context);
                }
            }
        }else {
            throw new QLException("定义的参数长度与运行期传入的参数长度不一致");
        }
        return params;

    }

    public static boolean maybeDynamicParams(Class<?>[] delaredParamsClasses)
    {
        int length = delaredParamsClasses.length;
        if(length>0 && delaredParamsClasses[length-1].isArray())
        {
            return true;
        }
        return false;
    }

    private static boolean maybeDynamicParams(InstructionSetContext context, ArraySwap list, Class<?>[] delaredParamsClasses) throws Exception {

        //长度不一致,有可能
        if(delaredParamsClasses.length != list.length) {
            return true;
        }
        //长度一致的不定参数:不定参数的数组,只输入了一个参数并且为array,有可能
        int length = list.length;
        Object lastParam = list.get(length-1).getObject(context);
        if(lastParam!=null && !lastParam.getClass().isArray())
        {
            return true;
        }
        return false;
    }
}
