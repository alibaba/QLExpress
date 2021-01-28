package com.ql.util.express;


import com.ql.util.express.exception.QLException;

/**
 * Data type definition
 * @author qhlhl2010@gmail.com
 *
 */

public class OperateData implements java.io.Serializable {
	private static final long serialVersionUID = 4749348640699065036L;	
	protected Object dataObject;
	protected Class<?> type;

	public OperateData(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}
	/**
	 * Use for object cache interface
	 * @param obj
	 * @param aType
	 */
	public void initial(Object obj, Class<?> aType) {
		this.type = aType;
		this.dataObject = obj;
	}
	public void clear(){
		this.dataObject = null;
		this.type = null;
	}
    public Class<?> getDefineType(){
    	throw new RuntimeException(this.getClass().getName() + "必须实现方法:getDefineType");
    }
    public Class<?> getOrgiType(){
    	return this.type;
    }
	public Class<?> getType(InstructionSetContext parent) throws Exception {
		if (type != null)
			return type;

		Object obj = this.getObject(parent);
		if (obj == null)
			return null;
		else
			return obj.getClass();
	}

	public final Object getObject(InstructionSetContext context) throws Exception {
    	if(this.type != null && this.type.equals(void.class)){
    		throw new QLException("Cannot participate in any operation, please check the use of a function that does not return a value in the expression, or if the branch is incomplete ");
    	}
		return getObjectInner(context);
	}
    public Object getObjectInner(InstructionSetContext context) throws Exception{
    	return this.dataObject;
    }
    public void setObject(InstructionSetContext parent, Object object) throws Exception {
		throw new RuntimeException("This method must be implemented in the subclass");
	}
	public String toJavaCode(){		
		if(this.getClass().equals(OperateData.class) == false){
			throw new RuntimeException(this.getClass().getName() + "Not realized：toJavaCode()");
		}
		String result ="new " + OperateData.class.getName() +"(";
		if(String.class.equals(this.type)){
			result = result + "\"" + this.dataObject + "\"";
		}else if(this.type.isPrimitive()){
			result = result + this.dataObject.getClass().getName() +".valueOf(\"" + this.dataObject + "\")";
		}else{
			result = result + "new " + this.dataObject.getClass().getName() + "(\"" + this.dataObject.toString() + "\")";
		}
		result = result + "," + type.getName() + ".class";
		result = result + ")";
		return result;
	}
	public String toString() {
		if( this.dataObject == null)
			return this.type + ":null";
		else{
			if(this.dataObject instanceof Class){
				return ExpressUtil.getClassName((Class<?>)this.dataObject);
			}else{
			    return this.dataObject.toString();
			}
		}
	}
	public void toResource(StringBuilder builder,int level){
		if(this.dataObject != null){
			builder.append(this.dataObject.toString());
		}else{
			builder.append("null");
		}
	}
}
