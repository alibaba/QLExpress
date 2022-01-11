package com.ql.util.express.instruction.op;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

/**
 * 操作符号定义
 *
 * @author qhlhl2010@gmail.com
 */
public abstract class OperatorBase {
    protected String name;
    protected String aliasName;
    protected String errorInfo;

    /**
     * 是否需要高精度计算
     */
    protected boolean isPrecise = false;

    /**
     * 操作数描述
     */
    protected String[] operateDataDesc;

    /**
     * 操作数的其它定义
     */
    protected String[] operateDataAnnotation;

    public Object[] toObjectList(InstructionSetContext parent, ArraySwap list) throws Exception {
        if (list == null) {
            return new Object[0];
        }
        Object[] result = new Object[list.length];
        OperateData p;
        for (int i = 0; i < list.length; i++) {
            p = list.get(i);
            if (p instanceof OperateDataAttr) {
                result[i] = ((OperateDataAttr)p).getName() + ":" + p.getObject(parent);
            } else {
                result[i] = p.getObject(parent);
            }
        }
        return result;
    }

    public OperateData execute(InstructionSetContext context, ArraySwap list, List<String> errorList) throws Exception {
        OperateData result;
        result = this.executeInner(context, list);
        //输出错误信息
        if (errorList != null && this.errorInfo != null && result != null) {
            Object obj = result.getObject(context);
            if (obj instanceof Boolean && !(Boolean)obj) {
                String tmpStr = ExpressUtil.replaceString(this.errorInfo, toObjectList(context, list));
                if (!errorList.contains(tmpStr)) {
                    errorList.add(tmpStr);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.aliasName != null) {
            return this.aliasName;
        } else {
            return this.name;
        }
    }

    public abstract OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception;

    public String[] getOperateDataDesc() {
        return this.operateDataDesc;
    }

    public String[] getOperateDataAnnotation() {
        return this.operateDataAnnotation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getAliasName() {
        if (this.aliasName != null) {
            return this.aliasName;
        } else {
            return this.name;
        }
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public boolean isPrecise() {
        return isPrecise;
    }

    public void setPrecise(boolean isPrecise) {
        this.isPrecise = isPrecise;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}

class OperatorFunction extends OperatorBase {
    public OperatorFunction(String name) {
        this.name = name;
    }

    public OperatorFunction(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        throw new QLException("还没有实现");
    }
}

class OperatorReturn extends OperatorBase {
    public OperatorReturn(String name) {
        this.name = name;
    }

    public OperatorReturn(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        return executeInner(parent);
    }

    public OperateData executeInner(InstructionSetContext parent) throws Exception {
        throw new QLException("return 是通过特殊指令来实现的，不能支持此方法");
    }
}

class OperatorBreak extends OperatorBase {
    public OperatorBreak(String name) {
        this.name = name;
    }

    public OperatorBreak(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        throw new QLException("OperatorBreak 是通过特殊指令来实现的，不能支持此方法");
    }
}

class OperatorContinue extends OperatorBase {
    public OperatorContinue(String name) {
        this.name = name;
    }

    public OperatorContinue(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        throw new QLException("OperatorContinue 是通过特殊指令来实现的，不能支持此方法");
    }
}

