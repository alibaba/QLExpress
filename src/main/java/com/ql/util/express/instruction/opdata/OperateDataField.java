package com.ql.util.express.instruction.opdata;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.parse.AppendingClassFieldManager;

public class OperateDataField extends OperateDataAttr {
    private Object fieldObject;
    private String originalFieldName;

    public OperateDataField(Object fieldObject, String fieldName) {
        super(null, null);
        if (fieldObject == null) {
            this.name = "没有初始化的Field";
        } else {
            this.name = fieldObject.getClass().getName() + "." + fieldName;
        }
        this.fieldObject = fieldObject;
        this.originalFieldName = fieldName;
    }

    public void initialDataField(Object fieldObject, String fieldName) {
        super.initialDataAttr(null, null);
        if (fieldObject == null) {
            this.name = Void.class.getName() + "." + fieldName;
        } else {
            this.name = fieldObject.getClass().getName() + "." + fieldName;
        }
        this.fieldObject = fieldObject;
        this.originalFieldName = fieldName;
    }

    public void clearDataField() {
        super.clearDataAttr();
        this.name = null;
        this.fieldObject = null;
        this.originalFieldName = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        try {
            return name;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public Object transferFieldName(InstructionSetContext context, String oldName) {
        if (!context.isSupportDynamicFieldName()) {
            return oldName;
        } else {
            try {
                OperateDataAttr o = (OperateDataAttr)context.findAliasOrDefSymbol(oldName);
                if (o != null) {
                    return o.getObject(context);
                } else {
                    return oldName;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) throws Exception {
        AppendingClassFieldManager appendingClassFieldManager = context.getExpressRunner()
            .getAppendingClassFieldManager();

        if (appendingClassFieldManager != null) {
            AppendingClassFieldManager.AppendingField appendingField
                = appendingClassFieldManager.getAppendingClassField(this.fieldObject, this.originalFieldName);
            if (appendingField != null) {
                return appendingClassFieldManager.invoke(appendingField, this.fieldObject);
            }
        }
        //如果能找到aFieldName的定义,则再次运算
        if (this.fieldObject instanceof OperateDataVirClass) {
            return ((OperateDataVirClass)this.fieldObject).getValue(transferFieldName(context, this.originalFieldName));
        } else {
            return ExpressUtil.getProperty(this.fieldObject, transferFieldName(context, this.originalFieldName));
        }
    }

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        AppendingClassFieldManager appendingClassFieldManager = context.getExpressRunner()
            .getAppendingClassFieldManager();

        if (appendingClassFieldManager != null) {
            AppendingClassFieldManager.AppendingField appendingField
                = appendingClassFieldManager.getAppendingClassField(this.fieldObject, this.originalFieldName);
            if (appendingField != null) {
                return appendingField.getReturnType();
            }
        }
        if (this.fieldObject instanceof OperateDataVirClass) {
            return ((OperateDataVirClass)this.fieldObject).getValueType(
                transferFieldName(context, this.originalFieldName));
        } else {
            if (this.fieldObject == null && QLExpressRunStrategy.isAvoidNullPointer()) {
                return Void.class;
            }
            return ExpressUtil.getPropertyClass(this.fieldObject, transferFieldName(context, this.originalFieldName));
        }
    }

    @Override
    public void setObject(InstructionSetContext context, Object value) throws Exception {
        if (this.fieldObject instanceof OperateDataVirClass) {
            ((OperateDataVirClass)this.fieldObject).setValue(
                transferFieldName(context, this.originalFieldName).toString(), value);
        } else {
            ExpressUtil.setProperty(fieldObject, transferFieldName(context, this.originalFieldName), value);
        }
    }
}
