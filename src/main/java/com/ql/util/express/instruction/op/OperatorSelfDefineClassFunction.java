package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.ql.util.express.*;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 用户自定义的函数操作
 * @author qhlhl2010@gmail.com
 *
 */
public class OperatorSelfDefineClassFunction extends OperatorBase implements CanClone{
  String functionName;
  String[] parameterTypes;
  Class<?>[] parameterClasses ;
  Class<?> operClass;
  Object operInstance;
  Method method;
  boolean isReturnVoid;
  boolean maybeDynamicParams;
    
  public OperatorSelfDefineClassFunction(String aOperName,Class<?> aOperClass, String aFunctionName,
                                           Class<?>[] aParameterClassTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
        if (errorInfo != null && errorInfo.trim().length() == 0) {
            errorInfo = null;
        }
        this.name = aOperName;
        this.errorInfo = aErrorInfo;
        this.functionName = aFunctionName;
        this.parameterClasses = aParameterClassTypes;
        this.parameterTypes = new String[aParameterClassTypes.length];
        this.operDataDesc = aParameterDesc;
        this.operDataAnnotation = aParameterAnnotation;
        for(int i=0;i<this.parameterClasses.length;i++){
            this.parameterTypes[i] = this.parameterClasses[i].getName();
        }
        operClass = aOperClass;
        method = operClass.getMethod(functionName,parameterClasses);
        this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
    }

  public OperatorSelfDefineClassFunction(String aOperName,String aClassName, String aFunctionName,
          Class<?>[] aParameterClassTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
		if (errorInfo != null && errorInfo.trim().length() == 0) {
			errorInfo = null;
		} 
		this.name = aOperName;
	    this.errorInfo = aErrorInfo;
	    this.functionName = aFunctionName;
	    this.parameterClasses = aParameterClassTypes;
	    this.parameterTypes = new String[aParameterClassTypes.length];
	    this.operDataDesc = aParameterDesc;
	    this.operDataAnnotation = aParameterAnnotation;
	    for(int i=0;i<this.parameterClasses.length;i++){
	      this.parameterTypes[i] = this.parameterClasses[i].getName();
	    }
	    operClass = ExpressUtil.getJavaClass(aClassName);
	    method = operClass.getMethod(functionName,parameterClasses);
	    this.isReturnVoid = method.getReturnType().equals(void.class);
        this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
  }

  public OperatorSelfDefineClassFunction(String aOperName,String aClassName, String aFunctionName,
                         String[] aParameterTypes,String[] aParameterDesc,String[] aParameterAnnotation,String aErrorInfo) throws Exception {
	if (errorInfo != null && errorInfo.trim().length() == 0) {
			errorInfo = null;
	} 
	this.name = aOperName;
    this.errorInfo = aErrorInfo;
    this.functionName = aFunctionName;
    this.parameterTypes = aParameterTypes;
    this.operDataDesc = aParameterDesc;
    this.operDataAnnotation = aParameterAnnotation;
    this.parameterClasses = new Class[this.parameterTypes.length];
    for(int i=0;i<this.parameterClasses.length;i++){
      this.parameterClasses[i] = ExpressUtil.getJavaClass(this.parameterTypes[i]);
    }
    operClass = ExpressUtil.getJavaClass(aClassName);
    method = operClass.getMethod(functionName,parameterClasses);
    this.maybeDynamicParams = DynamicParamsUtil.maybeDynamicParams(parameterClasses);
  }

	public OperatorBase cloneMe(String opName, String errorInfo)
			throws Exception {
		OperatorBase result = new OperatorSelfDefineClassFunction(opName,
				this.operClass.getName(), this.functionName,
				this.parameterClasses, this.operDataDesc,
				this.operDataAnnotation, errorInfo);
		return result;
	}
  public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
      Object[] parameres = DynamicParamsUtil.transferDynamicParams(context, list, parameterClasses,this.maybeDynamicParams);
      Object obj = null;
      if( Modifier.isStatic(this.method.getModifiers())){
         obj = this.method.invoke(null,ExpressUtil.transferArray(parameres,parameterClasses));
      }else{
		  if(operInstance==null){
			  operInstance =  operClass.newInstance();
		  }
    	 obj = this.method.invoke(operInstance,ExpressUtil.transferArray(parameres,parameterClasses));
      }

      if(obj != null){
          return OperateDataCacheManager.fetchOperateData(obj,obj.getClass());
       }
      if(this.isReturnVoid == true){
    	  return OperateDataCacheManager.fetchOperateDataAttr("null", void.class);
      }else{
    	  return OperateDataCacheManager.fetchOperateDataAttr("null", null);  
      }
  }


}
