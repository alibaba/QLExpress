package com.ql.util.express;

import com.ql.util.express.exception.QLException;

import java.math.BigDecimal;

/**
 * 数字运行函数集合
 * @author qhlhl2010@gmail.com
 *
 */

interface NumberType{
	public int NUMBER_TYPE_BYTE = 1;
	public int NUMBER_TYPE_SHORT = 2;
	public int NUMBER_TYPE_INT = 3;
	public int NUMBER_TYPE_LONG = 4;
	public int NUMBER_TYPE_FLOAT = 5;
	public int NUMBER_TYPE_DOUBLE = 6;
	public int NUMBER_TYPE_BIGDECIMAL = 7;
}

public class OperatorOfNumber {
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获取数据类型精度顺序
	 * @param aClass
	 * @return
	 */
    public static int getSeq(Class<?> aClass){
    	if(aClass == Byte.class || aClass == byte.class) return NumberType.NUMBER_TYPE_BYTE;
    	if(aClass == Short.class || aClass == short.class) return NumberType.NUMBER_TYPE_SHORT;
    	if(aClass == Integer.class || aClass == int.class) return NumberType.NUMBER_TYPE_INT;
    	if(aClass == Long.class || aClass == long.class) return NumberType.NUMBER_TYPE_LONG;
    	if(aClass == Float.class || aClass == float.class) return NumberType.NUMBER_TYPE_FLOAT;
    	if(aClass == Double.class || aClass == double.class) return NumberType.NUMBER_TYPE_DOUBLE;
    	if(aClass == BigDecimal.class) return NumberType.NUMBER_TYPE_BIGDECIMAL;
    	throw new RuntimeException("不能处理的数据类型：" + aClass.getName());
    }
	/**
	 * 进行数据类型转换
	 * @param value
	 * @param type
	 * @return
	 */
	public static Number transfer(Number value,Class<?> type,boolean isForce){
		if (isForce == true || value instanceof BigDecimal == false) {
			if (type.equals(byte.class) || type.equals(Byte.class)) {
				return ((Number) value).byteValue();
			} else if (type.equals(short.class) || type.equals(Short.class)) {
				return ((Number) value).shortValue();
			} else if (type.equals(int.class) || type.equals(Integer.class)) {
				return ((Number) value).intValue();
			} else if (type.equals(long.class) || type.equals(Long.class)) {
				return ((Number) value).longValue();
			} else if (type.equals(float.class) || type.equals(Float.class)) {
				return ((Number) value).floatValue();
			} else if (type.equals(double.class)
					|| type.equals(Double.class)) {
				return ((Number) value).doubleValue();
			} else if (type.equals(BigDecimal.class)) {
				return new BigDecimal(value.toString());
			}else{
				throw new RuntimeException("没有处理的数据类型：" + type.getName());
			}
		} else {
			if (type.equals(byte.class) || type.equals(Byte.class)) {
				if(((BigDecimal)value).scale() >0 ){
					throw new RuntimeException("有小数位，不能转化为："+ type.getName());
				}
				return ((Number) value).byteValue();
			} else if (type.equals(short.class) || type.equals(Short.class)) {
				if(((BigDecimal)value).scale() >0 ){
					throw new RuntimeException("有小数位，不能转化为："+ type.getName());
				}
				return ((Number) value).shortValue();
			} else if (type.equals(int.class) || type.equals(Integer.class)) {
				if(((BigDecimal)value).scale() >0 ){
					throw new RuntimeException("有小数位，不能转化为："+ type.getName());
				}
				return ((Number) value).intValue();
			} else if (type.equals(long.class) || type.equals(Long.class)) {
				if(((BigDecimal)value).scale() >0 ){
					throw new RuntimeException("有小数位，不能转化为："+ type.getName());
				}
				return ((Number) value).longValue();
			} else if (type.equals(float.class) || type.equals(Float.class)) {
				return ((Number) value).floatValue();
			} else if (type.equals(double.class)
					|| type.equals(Double.class)) {
				return ((Number) value).doubleValue();
			}else{
				throw new RuntimeException("没有处理的数据类型：" + type.getName());
			}	
		}
	}
	public static int compareNumber(Number op1, Number op2){
		int type1 = OperatorOfNumber.getSeq(op1.getClass());
		int type2 = OperatorOfNumber.getSeq(op2.getClass());
		int type = type1 >  type2 ? type1:type2;
		if(type == 1)  {
			byte o1 =	((Number)op1).byteValue();
			byte o2 =  ((Number)op2).byteValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 2) {
			short o1 =	((Number)op1).shortValue();
			short o2 =  ((Number)op2).shortValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 3) {
			int o1 =	((Number)op1).intValue();
			int o2 =  ((Number)op2).intValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 4) {
			long o1 =	((Number)op1).longValue();
			long o2 =  ((Number)op2).longValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 5) {
			float o1 =	((Number)op1).floatValue();
			float o2 =  ((Number)op2).floatValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 6){
			double o1 =	((Number)op1).doubleValue();
			double o2 =  ((Number)op2).doubleValue();
			if(o1 == o2) return 0;
			if(o1 < o2) return -1;
			return 1;
		}
		if(type == 7){
			BigDecimal o1 =	new BigDecimal(op1.toString());
			BigDecimal o2 = new BigDecimal(op2.toString());
			return o1.compareTo(o2);
		}
		throw new RuntimeException("比较操作错误:op1=" + op1.toString() +",op2=" + op2.toString());

	}
	public static Object add(Object op1, Object op2,boolean isPrecise) throws Exception {
		if(op1 == null){
			op1 = "null";
		}
		if(op2 == null){
			op2 = "null";
		}
		if (op1 instanceof String || op2 instanceof String) {				
			return op1.toString() + op2.toString();
		}
		if(isPrecise==true){
			return PreciseNumberOperator.addPrecise((Number)op1,(Number)op2);
		}else{
			return NormalNumberOperator.addNormal((Number)op1,(Number)op2);
		}
	}
	public static Number subtract(Object op1, Object op2,boolean isPrecise) throws Exception {
		if(isPrecise==true){
			return PreciseNumberOperator.subtractPrecise((Number)op1,(Number)op2);
		}else{
			return NormalNumberOperator.subtractNormal((Number)op1,(Number)op2);
		}
	}
	public static Number multiply(Object op1, Object op2,boolean isPrecise) throws Exception {
		if(isPrecise==true){
			return PreciseNumberOperator.multiplyPrecise((Number)op1,(Number)op2);
		}else{
			return NormalNumberOperator.multiplyNormal((Number)op1,(Number)op2);
		}
	}
	public static Number divide(Object op1, Object op2,boolean isPrecise) throws Exception {
		if(isPrecise==true){
			return PreciseNumberOperator.dividePrecise((Number)op1,(Number)op2);
		}else{
			return NormalNumberOperator.divideNormal((Number)op1,(Number)op2);
		}
	}	
	  public static Object modulo(Object op1,Object op2) throws Exception{
		  return NormalNumberOperator.moduloNormal((Number)op1,(Number)op2);		  
	  }
}
class NormalNumberOperator {

	
	/**
	 * 普通的加法运算	
	 * @param op1
	 * @param op2
	 * @return
	 * @throws Exception
	 */
	public static Number addNormal(Number op1, Number op2) throws Exception {
		int type1 = OperatorOfNumber.getSeq(op1.getClass());
		int type2 = OperatorOfNumber.getSeq(op2.getClass());
		int type = type1 >  type2 ? type1:type2;
		if(type == NumberType.NUMBER_TYPE_BYTE) return op1.byteValue() + op2.byteValue();
		if(type == NumberType.NUMBER_TYPE_SHORT) return op1.shortValue() + op2.shortValue();
		if(type == NumberType.NUMBER_TYPE_INT) return op1.intValue() + op2.intValue();
		if(type == NumberType.NUMBER_TYPE_LONG) return op1.longValue() + op2.longValue();
		if(type == NumberType.NUMBER_TYPE_FLOAT) return op1.floatValue() + op2.floatValue();
		if(type == NumberType.NUMBER_TYPE_DOUBLE) return op1.doubleValue() + op2.doubleValue();
		if(type == NumberType.NUMBER_TYPE_BIGDECIMAL) return new BigDecimal(op1.toString()).add(new BigDecimal(op2.toString()));		
		throw new QLException("不支持的对象执行了\"+\"操作");
	}



	 public  static  Number subtractNormal(Number op1,Number op2) throws Exception{
			int type1 = OperatorOfNumber.getSeq(op1.getClass());
			int type2 = OperatorOfNumber.getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == NumberType.NUMBER_TYPE_BYTE) return op1.byteValue() - op2.byteValue();
			if(type == NumberType.NUMBER_TYPE_SHORT) return op1.shortValue() - op2.shortValue();
			if(type == NumberType.NUMBER_TYPE_INT) return op1.intValue() - op2.intValue();
			if(type == NumberType.NUMBER_TYPE_LONG) return op1.longValue() - op2.longValue();
			if(type == NumberType.NUMBER_TYPE_FLOAT) return op1.floatValue() - op2.floatValue();
			if(type == NumberType.NUMBER_TYPE_DOUBLE) return op1.doubleValue() - op2.doubleValue();
			if(type == NumberType.NUMBER_TYPE_BIGDECIMAL) return new BigDecimal(op1.toString()).subtract(new BigDecimal(op2.toString()));
			throw new QLException("不支持的对象执行了\"-\"操作");
	    }

	    public static Number multiplyNormal(Number op1,Number op2) throws Exception {
			int type1 = OperatorOfNumber.getSeq(op1.getClass());
			int type2 = OperatorOfNumber.getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == NumberType.NUMBER_TYPE_BYTE) return op1.byteValue() * op2.byteValue();
			if(type == NumberType.NUMBER_TYPE_SHORT) return op1.shortValue() * op2.shortValue();
			if(type == NumberType.NUMBER_TYPE_INT) return op1.intValue() * op2.intValue();
			if(type == NumberType.NUMBER_TYPE_LONG) return op1.longValue() * op2.longValue();
			if(type == NumberType.NUMBER_TYPE_FLOAT) return op1.floatValue() * op2.floatValue();
			if(type == NumberType.NUMBER_TYPE_DOUBLE) return op1.doubleValue() * op2.doubleValue();
			if(type == NumberType.NUMBER_TYPE_BIGDECIMAL) return new BigDecimal(op1.toString()).multiply(new BigDecimal(op2.toString()));
			throw new QLException("不支持的对象执行了\"*\"操作");
	    }
	   public static Number divideNormal(Number op1,Number op2) throws Exception{
			int type1 = OperatorOfNumber.getSeq(op1.getClass());
			int type2 = OperatorOfNumber.getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == NumberType.NUMBER_TYPE_BYTE) return op1.byteValue() / op2.byteValue();
			if(type == NumberType.NUMBER_TYPE_SHORT) return op1.shortValue() / op2.shortValue();
			if(type == NumberType.NUMBER_TYPE_INT) return op1.intValue() / op2.intValue();
			if(type == NumberType.NUMBER_TYPE_LONG) return op1.longValue() / op2.longValue();
			if(type == NumberType.NUMBER_TYPE_FLOAT) return op1.floatValue() / op2.floatValue();
			if(type == NumberType.NUMBER_TYPE_DOUBLE) return op1.doubleValue() / op2.doubleValue();
			if(type == NumberType.NUMBER_TYPE_BIGDECIMAL) return new BigDecimal(op1.toString()).divide(new BigDecimal(op2.toString()), BigDecimal.ROUND_HALF_UP);
			throw new QLException("不支持的对象执行了\"/\"操作");
	    }


    public static Number moduloNormal(Number op1,Number op2) throws Exception{
			int type1 = OperatorOfNumber.getSeq(op1.getClass());
			int type2 = OperatorOfNumber.getSeq(op2.getClass());
			int type = type1 >  type2 ? type1:type2;
			if(type == NumberType.NUMBER_TYPE_BYTE) return op1.byteValue() % op2.byteValue();
			if(type == NumberType.NUMBER_TYPE_SHORT) return op1.shortValue() % op2.shortValue();
			if(type == NumberType.NUMBER_TYPE_INT) return op1.intValue() % op2.intValue();
			if(type == NumberType.NUMBER_TYPE_LONG) return op1.longValue() % op2.longValue();
			throw new QLException("不支持的对象执行了\"mod\"操作");
     }
}

/**
 * 高精度计算
 * @author xuannan
 */
class PreciseNumberOperator {
	
	public static int DIVIDE_PRECISION = 10;
	
	public static Number addPrecise(Number op1, Number op2) throws Exception {
		BigDecimal result =  null;
		if(op1 instanceof BigDecimal){
			if(op2 instanceof BigDecimal){
				result =  ((BigDecimal)op1).add((BigDecimal)op2);
			}else{
				result =  ((BigDecimal)op1).add(new BigDecimal(op2.toString()));
			}
		}else{
			if(op2 instanceof BigDecimal){
				result =  new BigDecimal(op1.toString()).add((BigDecimal)op2);
			}else{
				result =  new BigDecimal(op1.toString()).add(new BigDecimal(op2.toString()));
			}
		}
		if(result.scale() ==0){
			long tempLong =  result.longValue();
			if(tempLong <= Integer.MAX_VALUE && tempLong >= Integer.MIN_VALUE){
				return (int)tempLong;
			}else{
				return tempLong;
			}
		}else{
			return result;
		}
		
	}
	public static Number subtractPrecise(Number op1, Number op2) throws Exception {
		BigDecimal result =  null;
		if(op1 instanceof BigDecimal){
			if(op2 instanceof BigDecimal){
				result = ((BigDecimal)op1).subtract((BigDecimal)op2);
			}else{
				result = ((BigDecimal)op1).subtract(new BigDecimal(op2.toString()));
			}
		}else{
			if(op2 instanceof BigDecimal){
				result = new BigDecimal(op1.toString()).subtract((BigDecimal)op2);
			}else{
				result = new BigDecimal(op1.toString()).subtract(new BigDecimal(op2.toString()));
			}
		}
		if(result.scale() ==0){
			long tempLong =  result.longValue();
			if(tempLong <= Integer.MAX_VALUE && tempLong >= Integer.MIN_VALUE){
				return (int)tempLong;
			}else{
				return tempLong;
			}
		}else{
			return result;
		}
	}
	public static Number multiplyPrecise(Number op1, Number op2) throws Exception {
		BigDecimal result =  null;
		if(op1 instanceof BigDecimal){
			if(op2 instanceof BigDecimal){
				result = ((BigDecimal)op1).multiply((BigDecimal)op2);
			}else{
				result = ((BigDecimal)op1).multiply(new BigDecimal(op2.toString()));
			}
		}else{
			if(op2 instanceof BigDecimal){
				result = new BigDecimal(op1.toString()).multiply((BigDecimal)op2);
			}else{
				result = new BigDecimal(op1.toString()).multiply(new BigDecimal(op2.toString()));
			}
		}
		if(result.scale() ==0){
			long tempLong =  result.longValue();
			if(tempLong <= Integer.MAX_VALUE && tempLong >= Integer.MIN_VALUE){
				return (int)tempLong;
			}else{
				return tempLong;
			}
		}else{
			return result;
		}
	}
	public static Number dividePrecise(Number op1, Number op2) throws Exception {
		BigDecimal result =  null;
		if(op1 instanceof BigDecimal){
			if(op2 instanceof BigDecimal){
				result = ((BigDecimal)op1).divide((BigDecimal)op2, DIVIDE_PRECISION, BigDecimal.ROUND_HALF_UP);
			}else{
				result = ((BigDecimal)op1).divide(new BigDecimal(op2.toString()), DIVIDE_PRECISION, BigDecimal.ROUND_HALF_UP);
			}
		}else{
			if(op2 instanceof BigDecimal){
				result = new BigDecimal(op1.toString()).divide((BigDecimal)op2, DIVIDE_PRECISION, BigDecimal.ROUND_HALF_UP);
			}else{
				result = new BigDecimal(op1.toString()).divide(new BigDecimal(op2.toString()), DIVIDE_PRECISION, BigDecimal.ROUND_HALF_UP);
			}
		}
		if(result.scale() ==0){
			long tempLong =  result.longValue();
			if(tempLong <= Integer.MAX_VALUE && tempLong >= Integer.MIN_VALUE){
				return (int)tempLong;
			}else{
				return tempLong;
			}
		}else{
			return result;
		}
	}
}
