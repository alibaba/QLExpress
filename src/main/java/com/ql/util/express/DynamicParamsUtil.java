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
        //The parameter definition does not conform to the dynamic parameter form || User-defined does not support || The parameter passed in by the user does not conform
        if(!maybeDynamicParams || !supportDynamicParams || !maybeDynamicParams(context,list,delaredParamsClasses)){
            if(delaredParamsClasses.length != list.length){
                throw new QLException("The defined parameter length is inconsistent with the parameter length passed in during runtime");
            }
            params = new Object[list.length];
            for (int i = 0; i < list.length; i++) {
                params[i] = list.get(i).getObject(context);
            }
            return params;
        }

        //Support the use of indefinite parameters function(arg1,arg2,arg3...)
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
            throw new QLException("The defined parameter length is inconsistent with the parameter length passed in during runtime");
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

        //Inconsistent length, it is possible
        if(delaredParamsClasses.length != list.length) {
            return true;
        }
        //Indefinite parameters with the same length: an array of indefinite parameters, only one parameter is input and it is an array, it is possible
        int length = list.length;
        Object lastParam = list.get(length-1).getObject(context);
        if(lastParam!=null && !lastParam.getClass().isArray())
        {
            return true;
        }
        return false;
    }
}
