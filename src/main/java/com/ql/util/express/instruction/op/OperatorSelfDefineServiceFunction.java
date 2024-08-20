package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 用户自定义的对象方法操作
 *
 * @author qhlhl2010@gmail.com
 */
public class OperatorSelfDefineServiceFunction extends OperatorBase implements CanClone {
    private final Object serviceObject;
    private final String functionName;
    private final String[] parameterTypes;
    private final Class<?>[] parameterClasses;
    private final Method method;
    private boolean isReturnVoid;
    private final boolean maybeDynamicParams;

    public OperatorSelfDefineServiceFunction(String operatorName, Object serviceObject, String functionName,
        Class<?>[] parameterClassTypes, String[] parameterDesc, String[] parameterAnnotation, String errorInfo)
        throws Exception {
        this.name = operatorName;
        this.errorInfo = errorInfo;
        this.serviceObject = serviceObject;
        this.functionName = functionName;
        this.parameterClasses = parameterClassTypes;
        this.operateDataDesc = parameterDesc;
        this.operateDataAnnotation = parameterAnnotation;
        this.parameterTypes = new String[this.parameterClasses.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        Class<?> operatorClass = serviceObject.getClass();
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineServiceFunction(String operatorName, Object serviceObject, String functionName,
        String[] parameterTypes, String[] parameterDesc, String[] parameterAnnotation, String errorInfo)
        throws Exception {
        this.name = operatorName;
        this.errorInfo = errorInfo;
        this.serviceObject = serviceObject;
        this.functionName = functionName;
        this.parameterTypes = parameterTypes;
        this.operateDataDesc = parameterDesc;
        this.operateDataAnnotation = parameterAnnotation;
        this.parameterClasses = new Class[this.parameterTypes.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
        }
        Class<?> operatorClass = serviceObject.getClass();
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    @Override
    public OperatorBase cloneMe(String operatorName, String errorInfo) throws Exception {
        return new OperatorSelfDefineServiceFunction(operatorName, this.serviceObject, this.functionName,
            this.parameterClasses, this.operateDataDesc, this.operateDataAnnotation, errorInfo);
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Object[] parameters = DynamicParamsUtil.transferDynamicParams(parent, list, parameterClasses,
            this.maybeDynamicParams);
        Object obj = this.method.invoke(this.serviceObject, ExpressUtil.transferArray(parameters, parameterClasses));
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
