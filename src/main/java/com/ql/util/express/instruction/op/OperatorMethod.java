package com.ql.util.express.instruction.op;

import java.lang.reflect.Method;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateClass;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;
import com.ql.util.express.parse.AppendingClassMethodManager;

public class OperatorMethod extends OperatorBase {
	String methodName;
	public OperatorMethod() {
		this.name ="MethodCall";
	}
	public OperatorMethod(String aMethodName) {
		this.name ="MethodCall";
		this.methodName = aMethodName;
	}
	static Class<?> ArrayClass = (new Object[]{}).getClass();
	
	public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
		OperateData p0 = list.get(0);
		Object obj = p0.getObject(parent);
		if(obj instanceof OperateDataVirClass ){
			OperateDataVirClass vClass = (OperateDataVirClass)obj;
			OperateData[] parameters = new OperateData[list.length - 1];
			for(int i=0;i< list.length -1;i++){
				parameters[i] =list.get(i+1);
			}
			return vClass.callSelfFunction(this.methodName, parameters);
		}
		
		if (obj == null) {
		    if(QLExpressRunStrategy.isAvoidNullPointer()){
		        return null;
            }
			// 对象为空，不能执行方法
			String msg = "对象为空，不能执行方法:";
			throw new QLException(msg + this.methodName);
		} else {
			Class<?>[] types = new Class[list.length - 1];
			Class<?>[] orgiTypes = new Class[list.length - 1];
			
			Object[] objs = new Object[list.length - 1];
			Object tmpObj;
			OperateData p;
			for (int i = 0; i < types.length; i++) {
				p = list.get(i+ 1); 
				tmpObj = p.getObject(parent);
				types[i] = p.getType(parent);
				orgiTypes[i] = p.getType(parent);
				objs[i] = tmpObj;
			}
			AppendingClassMethodManager appendingClassMethodManager = parent.getExpressRunner().getAppendingClassMethodManager();

			if(appendingClassMethodManager!=null) {
				AppendingClassMethodManager.AppendingMethod appendingClassMethod = appendingClassMethodManager.getAppendingClassMethod(obj, this.methodName);
				if (appendingClassMethod != null) {
					return appendingClassMethodManager.invoke(appendingClassMethod, parent, list, null);
				}
			}
			Method m = null;
			if (p0 instanceof OperateClass) {// 调用静态方法
				m = ExpressUtil.findMethodWithCache((Class<?>) obj, this.methodName,
						types, true, true);
			} else {
			    if(obj instanceof Class){
                    m = ExpressUtil.findMethodWithCache((Class<?>) obj, this.methodName,
                            types, true, true);
                }
                if(m==null) {
                    m = ExpressUtil.findMethodWithCache(obj.getClass(), this.methodName,
                            types, true, false);
                }
			}
			if(m == null){
				types = new Class[]{ArrayClass};
				if (p0 instanceof OperateClass) {// 调用静态方法
					m = ExpressUtil.findMethodWithCache((Class<?>) obj, methodName,
							types, true, true);
				} else {
					m = ExpressUtil.findMethodWithCache(obj.getClass(), methodName,
							types, true, false);
				}
				objs = new Object[]{objs};				
			}
			if (m == null) {
				StringBuilder  s = new StringBuilder();
				s.append("没有找到" + obj.getClass().getName() + "的方法："
						+ methodName + "(");
				for (int i = 0; i < orgiTypes.length; i++) {
					if (i > 0)
						s.append(",");
					if(orgiTypes[i] == null){
						s.append("null");
					}else{
					    s.append(orgiTypes[i].getName());
					}
				}
				s.append(")");
				throw new QLException(s.toString());
			}
			//阻止调用不安全的方法
			QLExpressRunStrategy.assertBlackMethod(m);

			if (p0 instanceof OperateClass) {// 调用静态方法
				boolean oldA = m.isAccessible();
				m.setAccessible(true);
				tmpObj = m.invoke(null,ExpressUtil.transferArray(objs,m.getParameterTypes()));
				m.setAccessible(oldA);
			} else {
				boolean oldA = m.isAccessible();
				m.setAccessible(true);
				tmpObj = m.invoke(obj, ExpressUtil.transferArray(objs,m.getParameterTypes()));
				m.setAccessible(oldA);
			}
			return OperateDataCacheManager.fetchOperateData(tmpObj, m.getReturnType());
		}
	}
    public String toString(){
    	return this.name + ":" + this.methodName;
    }
}
