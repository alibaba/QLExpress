package com.ql.util.express.instruction.opdata;

import java.util.List;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.InstructionSetRunner;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * 虚拟Class的内存对象
 *
 * @author xuannan
 */
public class OperateDataVirClass extends OperateDataAttr {
    /**
     * 虚拟Class的数据上下文
     */
    private InstructionSetContext context;

    /**
     * 虚拟类的指令集合
     */
    private InstructionSet virClassInstructionSet;

    private boolean isTrace;

    public OperateDataVirClass(String name) {
        super(name, null);
    }

    public void initialInstance(InstructionSetContext parent, OperateData[] parameters, List<String> errorList,
        boolean isTrace) throws Exception {
        this.isTrace = isTrace;
        this.context = OperateDataCacheManager.fetchInstructionSetContext(false, parent.getExpressRunner(), parent,
            parent.getExpressLoader(), parent.isSupportDynamicFieldName());
        Object functionSet = parent.getSymbol(this.name);
        if (!(functionSet instanceof InstructionSet)) {
            throw new QLException("没有找到自定义对象\"" + this.name + "\"");
        }
        this.virClassInstructionSet = (InstructionSet)functionSet;

        OperateDataLocalVar[] vars = virClassInstructionSet.getParameters();
        for (int i = 0; i < vars.length; i++) {
            //注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
            OperateDataLocalVar operateDataLocalVar = OperateDataCacheManager.fetchOperateDataLocalVar(
                vars[i].getName(), vars[i].getOriginalType());
            this.context.addSymbol(operateDataLocalVar.getName(), operateDataLocalVar);
            operateDataLocalVar.setObject(context, parameters[i].getObject(parent));
        }
        InstructionSetRunner.execute(virClassInstructionSet, context, errorList, isTrace, false, false);
    }

    public OperateData callSelfFunction(String functionName, OperateData[] parameters) throws Exception {
        Object function = this.context.getSymbol(functionName);
        if (!(function instanceof InstructionSet)) {
            throw new QLException("在VClass:" + this.name + "中没有定义函数\"" + functionName + "\"");
        }
        InstructionSet functionSet = (InstructionSet)function;

        InstructionSetContext tempContext = OperateDataCacheManager.fetchInstructionSetContext(true,
            this.context.getExpressRunner(), this.context, this.context.getExpressLoader(),
            this.context.isSupportDynamicFieldName());
        OperateDataLocalVar[] vars = functionSet.getParameters();
        for (int i = 0; i < vars.length; i++) {
            //注意此处必须new 一个新的对象，否则就会在多次调用的时候导致数据冲突
            OperateDataLocalVar operateDataLocalVar = OperateDataCacheManager.fetchOperateDataLocalVar(
                vars[i].getName(), vars[i].getOriginalType());
            tempContext.addSymbol(operateDataLocalVar.getName(), operateDataLocalVar);
            operateDataLocalVar.setObject(tempContext, parameters[i].getObject(this.context));
        }
        Object result = InstructionSetRunner.execute(functionSet, tempContext, null, this.isTrace, false, true);
        return OperateDataCacheManager.fetchOperateData(result, null);
    }

    public Object getValue(Object name) throws Exception {
        Object o = this.context.findAliasOrDefSymbol(name.toString());
        if (o == null) {
            return null;
        } else if (o instanceof OperateData) {
            //变量定义
            return ((OperateData)o).getObject(context);
        } else if (o instanceof InstructionSet) {
            //宏定义
            InstructionSetContext tempContext = OperateDataCacheManager.fetchInstructionSetContext(true,
                this.context.getExpressRunner(), this.context, this.context.getExpressLoader(),
                this.context.isSupportDynamicFieldName());
            Object result = InstructionSetRunner.execute(this.context.getExpressRunner(), (InstructionSet)o,
                this.context.getExpressLoader(), tempContext, null, this.isTrace, false, false,
                this.context.isSupportDynamicFieldName());
            if (result instanceof OperateData) {
                return ((OperateData)result).getObject(this.context);
            } else {
                return result;
            }
        } else {
            throw new QLException("不支持的数据类型:" + o.getClass().getName());
        }
    }

    public void setValue(String name, Object value) throws Exception {
        Object o = this.context.findAliasOrDefSymbol(name);
        if (o instanceof OperateData) {
            ((OperateData)o).setObject(context, value);
        } else {
            throw new QLException("不支持的数据类型:" + o.getClass().getName());
        }
    }

    public Class<?> getValueType(Object name) throws Exception {
        Object o = this.context.findAliasOrDefSymbol(name.toString());
        if (o instanceof OperateData) {
            return ((OperateData)o).getType(context);
        } else {
            throw new QLException("不支持的数据类型:" + o.getClass().getName());
        }
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        return this;
    }

    @Override
    public Class<?> getType(InstructionSetContext context) {
        return this.getClass();
    }

    @Override
    public void setObject(InstructionSetContext parent, Object object) {
        throw new RuntimeException("不支持的方法");
    }

    @Override
    public String toString() {
        return "VClass[" + this.name + "]";
    }
}
