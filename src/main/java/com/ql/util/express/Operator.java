package com.ql.util.express;

import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;

import java.util.Date;

/**
 * 操作符的基类
 * @author xuannan
 *
 */
public abstract class Operator extends  OperatorBase{
	
	public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception{
		Object[] parameters = new Object[list.length];
		for(int i = 0;i <list.length;i++){
			parameters[i] = list.get(i).getObject(context);
		}
		Object result = this.executeInner(parameters);
		if(result != null && result.getClass().equals(OperateData.class)){
			throw new Exception("操作符号定义的返回类型错误：" + this.getAliasName());
		}
		if(result == null){
			//return new OperateData(null,null);
			return OperateDataCacheManager.fetchOperateData(null, null);
		}else{
			//return new OperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
			return OperateDataCacheManager.fetchOperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
		}
	}
	public abstract Object executeInner(Object[] list) throws Exception;

    /**
     * 进行对象比较
     * @param op1
     * @param op2
     * @return 0--等于 ，负数 < , 正数 >
     * @throws Exception
     */
    public  static int compareData(Object op1,Object op2) throws Exception{
     int compareResult = -1;

     if(op1 instanceof String){
        compareResult = ((String)op1).compareTo(op2.toString());
     }else if(op2 instanceof String){
    	compareResult = op1.toString().compareTo((String)op2);
     }else if(op1 instanceof Number && op2 instanceof Number){
   	  //数字比较
   	  compareResult =  OperatorOfNumber.compareNumber((Number)op1, (Number)op2);
     }
     else if (op1 instanceof Character && op2 instanceof Character) {
         compareResult = ((Character) op1).compareTo((Character) op2);
     }
     else if ((op1 instanceof Boolean) && (op2 instanceof Boolean))
     {
         if (((Boolean)op1).booleanValue() ==((Boolean)op2).booleanValue())
            compareResult =0;
         else
            compareResult =-1;
      }else if ((op1 instanceof Date) && (op2 instanceof Date))
     {
         compareResult = ((Date) op1).compareTo((Date) op2);
     }
     else
        throw new Exception(op1 + "和" + op2 +"不能执行compare 操作");
     return compareResult;
   }

}
