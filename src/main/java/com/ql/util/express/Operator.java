package com.ql.util.express;

import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;

import java.util.Date;

/**
 * Operator base class
 *
 * @author xuannan
 */
public abstract class Operator extends OperatorBase {
    
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        Object[] parameters = new Object[list.length];
        for (int i = 0; i < list.length; i++) {
            if(list.get(i)==null && QLExpressRunStrategy.isAvoidNullPointer()){
                parameters[i] = null;
            }else {
                parameters[i] = list.get(i).getObject(context);
            }
        }
        Object result = this.executeInner(parameters);
        if (result != null && result.getClass().equals(OperateData.class)) {
            throw new QLException("The return type defined by the operation symbol is wrong: " + this.getAliasName());
        }
        if (result == null) {
            //return new OperateData(null,null);
            return OperateDataCacheManager.fetchOperateData(null, null);
        } else {
            //return new OperateData(result,ExpressUtil.getSimpleDataType(result.getClass()));
            return OperateDataCacheManager.fetchOperateData(result, ExpressUtil.getSimpleDataType(result.getClass()));
        }
    }
    
    public abstract Object executeInner(Object[] list) throws Exception;
    
    /**
     * Compare whether objects are equal
     * @param op1
     * @param op2
     * @return
     * @throws Exception
     */
    public static boolean objectEquals(Object op1, Object op2) throws Exception{
        if (op1 == null && op2 == null) {
            return true;
        }
        if (op1 == null || op2 == null) {
            return false;
        }
        
        //Character-Value comparison
        if(op1 instanceof Character || op2 instanceof Character){
            int compareResult = 0;
            if (op1 instanceof Character && op2 instanceof Character) {
                return op1.equals(op2);
            }else if (op1 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((Number) op1, (int) (Character) op2);
                return compareResult==0;
            } else if (op2 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((int) (Character) op1, (Number) op2);
                return compareResult==0;
            }
        }
        //Numerical value comparison
        if (op1 instanceof Number && op2 instanceof Number) {
            //Number comparison
            int compareResult = OperatorOfNumber.compareNumber((Number) op1, (Number) op2);
            return compareResult==0;
        }
        //Call the comparison of the original Object
        return op1.equals(op2);
    }


    /** Compare objects
     * @param op1
     * @param op2
     * @return
     * @throws Exception
     */
    public static int compareData(Object op1, Object op2) throws Exception {
        
        if(op1 == op2){
            return 0;
        }
        
        int compareResult = -1;
        
        if (op1 instanceof String) {
            compareResult = ((String) op1).compareTo(op2.toString());
        } else if (op2 instanceof String) {
            compareResult = op1.toString().compareTo((String) op2);
        } else if (op1 instanceof Character || op2 instanceof Character) {
            if (op1 instanceof Character && op2 instanceof Character) {
                compareResult = ((Character) op1).compareTo((Character) op2);
            } else if (op1 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((Number) op1, (int) ((Character) op2).charValue());
            } else if (op2 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((int) ((Character) op1).charValue(), (Number) op2);
            } else {
                throw new QLException(op1 + ":" + op2 + " - Cannot perform compare operation ");
            }
        } else if (op1 instanceof Number && op2 instanceof Number) {
            //Number comparison
            compareResult = OperatorOfNumber.compareNumber((Number) op1, (Number) op2);
        } else if ((op1 instanceof Boolean) && (op2 instanceof Boolean)) {
            if (((Boolean) op1).booleanValue() == ((Boolean) op2).booleanValue())
                compareResult = 0;
            else
                compareResult = -1;
        } else if ((op1 instanceof Date) && (op2 instanceof Date)) {
            compareResult = ((Date) op1).compareTo((Date) op2);
        } else
            throw new QLException(op1 + ":" + op2 + " - Cannot perform compare operation ");
        return compareResult;
    }
    
}
