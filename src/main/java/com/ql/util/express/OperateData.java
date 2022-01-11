package com.ql.util.express;

import com.ql.util.express.exception.QLException;

/**
 * 数据类型定义
 *
 * @author qhlhl2010@gmail.com
 */
public class OperateData {

    protected Object dataObject;
    protected Class<?> type;

    public OperateData(Object obj, Class<?> type) {
        this.type = type;
        this.dataObject = obj;
    }

    /**
     * 给对象缓存接口使用
     *
     * @param obj
     * @param type
     */
    public void initial(Object obj, Class<?> type) {
        this.type = type;
        this.dataObject = obj;
    }

    public void clear() {
        this.dataObject = null;
        this.type = null;
    }

    public Class<?> getDefineType() {
        throw new RuntimeException(this.getClass().getName() + "必须实现方法:getDefineType");
    }

    public Class<?> getOriginalType() {
        return this.type;
    }

    public Class<?> getType(InstructionSetContext parent) throws Exception {
        if (type != null) {
            return type;
        }

        Object obj = this.getObject(parent);
        if (obj == null) {
            return null;
        } else {
            return obj.getClass();
        }
    }

    public final Object getObject(InstructionSetContext context) throws Exception {
        if (this.type != null && this.type.equals(void.class)) {
            throw new QLException("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数,或者分支不完整的if语句");
        }
        return getObjectInner(context);
    }

    public Object getObjectInner(InstructionSetContext context) throws Exception {
        return this.dataObject;
    }

    public void setObject(InstructionSetContext parent, Object object) throws Exception {
        throw new RuntimeException("必须在子类中实现此方法");
    }

    public String toJavaCode() {
        if (!this.getClass().equals(OperateData.class)) {
            throw new RuntimeException(this.getClass().getName() + "没有实现：toJavaCode()");
        }
        String result = "new " + OperateData.class.getName() + "(";
        if (String.class.equals(this.type)) {
            result = result + "\"" + this.dataObject + "\"";
        } else if (this.type.isPrimitive()) {
            result = result + this.dataObject.getClass().getName() + ".valueOf(\"" + this.dataObject + "\")";
        } else {
            result = result + "new " + this.dataObject.getClass().getName() + "(\"" + this.dataObject
                + "\")";
        }
        result = result + "," + type.getName() + ".class";
        result = result + ")";
        return result;
    }

    @Override
    public String toString() {
        if (this.dataObject == null) {
            return this.type + ":null";
        } else {
            if (this.dataObject instanceof Class) {
                return ExpressUtil.getClassName((Class<?>)this.dataObject);
            } else {
                return this.dataObject.toString();
            }
        }
    }

    public void toResource(StringBuilder builder, int level) {
        if (this.dataObject != null) {
            builder.append(this.dataObject);
        } else {
            builder.append("null");
        }
    }
}
