package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 用户自定义的服务函数操作
 *
 * @author qhlhl2010@gmail.com
 */
public class OperatorSelfDefineServiceFunction extends OperatorBase implements CanClone {
    Object serviceObject;
    String functionName;
    String[] parameterTypes;
    Class<?>[] parameterClasses;
    Method method;
    boolean isReturnVoid;
    boolean maybeDynamicParams;

    public OperatorSelfDefineServiceFunction(String aOperName,
        Object aServiceObject, String aFunctionName,
        Class<?>[] aParameterClassTypes, String[] aParameterDesc, String[] aParameterAnnotation, String aErrorInfo)
        throws Exception {
        if (errorInfo != null && errorInfo.trim().length() == 0) {
        }
        this.name = aOperName;
        this.errorInfo = aErrorInfo;
        this.serviceObject = aServiceObject;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.operDataDesc = aParameterDesc;
        this.operDataAnnotation = aParameterAnnotation;
        this.parameterTypes = new String[this.parameterClasses.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        Class<?> operClass = serviceObject.getClass();
        method = operClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineServiceFunction(String aOperName, Object aServiceObject, String aFunctionName,
        String[] aParameterTypes, String[] aParameterDesc, String[] aParameterAnnotation, String aErrorInfo)
        throws Exception {

        if (errorInfo != null && errorInfo.trim().length() == 0) {
        }
        this.name = aOperName;
        this.errorInfo = aErrorInfo;
        this.serviceObject = aServiceObject;
        this.functionName = aFunctionName;
        this.parameterTypes = aParameterTypes;
        this.operDataDesc = aParameterDesc;
        this.operDataAnnotation = aParameterAnnotation;
        this.parameterClasses = new Class[this.parameterTypes.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
        }
        Class<?> operClass = serviceObject.getClass();
        method = operClass.getMethod(functionName, parameterClasses);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorBase cloneMe(String opName, String errorInfo)
        throws Exception {
        OperatorBase result = new OperatorSelfDefineServiceFunction(opName,
            this.serviceObject, this.functionName, this.parameterClasses,
            this.operDataDesc, this.operDataAnnotation, errorInfo);
        return result;
    }

    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        Object[] parameres = DynamicParamsUtil.transferDynamicParams(context, list, parameterClasses,
            this.maybeDynamicParams);
        Object obj = this.method.invoke(this.serviceObject, ExpressUtil.transferArray(parameres, parameterClasses));
        if (obj != null) {
            return OperateDataCacheManager.fetchOperateData(obj, obj.getClass());
        }
        if (this.isReturnVoid) {
            return OperateDataCacheManager.fetchOperateDataAttr("null", void.class);
        } else {
            return OperateDataCacheManager.fetchOperateDataAttr("null", null);
        }
    }
}
