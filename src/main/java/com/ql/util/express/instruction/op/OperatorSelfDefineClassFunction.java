package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 用户自定义的函数操作
 *
 * @author qhlhl2010@gmail.com
 */
public class OperatorSelfDefineClassFunction extends OperatorBase implements CanClone {
    String functionName;
    String[] parameterTypes;
    Class<?>[] parameterClasses;
    Class<?> operatorClass;
    Object operatorInstance;
    Method method;
    boolean isReturnVoid;
    boolean maybeDynamicParams;

    public OperatorSelfDefineClassFunction(String aOperatorName, Class<?> aOperatorClass, String aFunctionName,
        Class<?>[] aParameterClassTypes, String[] aParameterDesc, String[] aParameterAnnotation, String aErrorInfo)
        throws Exception {
        this.name = aOperatorName;
        this.errorInfo = aErrorInfo;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.parameterTypes = new String[aParameterClassTypes.length];
        this.operatorDataDesc = aParameterDesc;
        this.operatorDataAnnotation = aParameterAnnotation;
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        operatorClass = aOperatorClass;
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineClassFunction(String aOperatorName, String aClassName, String aFunctionName,
        Class<?>[] aParameterClassTypes, String[] aParameterDesc, String[] aParameterAnnotation, String aErrorInfo)
        throws Exception {
        this.name = aOperatorName;
        this.errorInfo = aErrorInfo;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.parameterTypes = new String[aParameterClassTypes.length];
        this.operatorDataDesc = aParameterDesc;
        this.operatorDataAnnotation = aParameterAnnotation;
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        operatorClass = ExpressUtil.getJavaClass(aClassName);
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    public OperatorSelfDefineClassFunction(String aOperatorName, String aClassName, String aFunctionName,
        String[] aParameterTypes, String[] aParameterDesc, String[] aParameterAnnotation, String aErrorInfo)
        throws Exception {
        this.name = aOperatorName;
        this.errorInfo = aErrorInfo;
        this.functionName = aFunctionName;
        this.parameterTypes = aParameterTypes;
        this.operatorDataDesc = aParameterDesc;
        this.operatorDataAnnotation = aParameterAnnotation;
        this.parameterClasses = new Class[this.parameterTypes.length];
        for (int i = 0; i < this.parameterClasses.length; i++) {
            this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
        }
        operatorClass = ExpressUtil.getJavaClass(aClassName);
        method = operatorClass.getMethod(functionName, parameterClasses);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

    @Override
    public OperatorBase cloneMe(String opName, String errorInfo) throws Exception {
        return new OperatorSelfDefineClassFunction(opName, this.operatorClass.getName(), this.functionName,
            this.parameterClasses, this.operatorDataDesc, this.operatorDataAnnotation, errorInfo);
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        Object[] parameters = DynamicParamsUtil.transferDynamicParams(context, list, parameterClasses,
            this.maybeDynamicParams);
        Object obj;
        if (Modifier.isStatic(this.method.getModifiers())) {
            obj = this.method.invoke(null, ExpressUtil.transferArray(parameters, parameterClasses));
        } else {
            if (operatorInstance == null) {
                operatorInstance = operatorClass.newInstance();
            }
            obj = this.method.invoke(operatorInstance, ExpressUtil.transferArray(parameters, parameterClasses));
        }

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
