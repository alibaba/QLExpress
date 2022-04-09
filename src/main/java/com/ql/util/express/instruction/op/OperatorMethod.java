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
    private String methodName;

    private static final Class<?> ARRAY_CLASS = Object[].class;

    public OperatorMethod() {
        this.name = "MethodCall";
    }

    public OperatorMethod(String methodName) {
        this.name = "MethodCall";
        this.methodName = methodName;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        OperateData p0 = list.get(0);
        Object obj = p0.getObject(parent);
        if (obj instanceof OperateDataVirClass) {
            OperateDataVirClass vClass = (OperateDataVirClass)obj;
            OperateData[] parameters = new OperateData[list.length - 1];
            for (int i = 0; i < list.length - 1; i++) {
                parameters[i] = list.get(i + 1);
            }
            return vClass.callSelfFunction(this.methodName, parameters);
        }

        if (obj == null) {
            if (QLExpressRunStrategy.isAvoidNullPointer()) {
                return null;
            }
            // 对象为空，不能执行方法
            String msg = "对象为空，不能执行方法:";
            throw new QLException(msg + this.methodName);
        } else {
            Class<?>[] types = new Class[list.length - 1];
            Class<?>[] originalTypes = new Class[list.length - 1];

            Object[] objs = new Object[list.length - 1];
            Object tmpObj;
            OperateData p;
            for (int i = 0; i < types.length; i++) {
                p = list.get(i + 1);
                tmpObj = p.getObject(parent);
                types[i] = p.getType(parent);
                originalTypes[i] = p.getType(parent);
                objs[i] = tmpObj;
            }
            AppendingClassMethodManager appendingClassMethodManager = parent.getExpressRunner()
                .getAppendingClassMethodManager();

            if (appendingClassMethodManager != null) {
                AppendingClassMethodManager.AppendingMethod appendingClassMethod
                    = appendingClassMethodManager.getAppendingClassMethod(obj, this.methodName);
                if (appendingClassMethod != null) {
                    return appendingClassMethodManager.invoke(appendingClassMethod, parent, list, null);
                }
            }
            if (QLExpressRunStrategy.isSandboxMode()) {
                throw new QLException("没有找到方法:" + this.methodName);
            }
            Method method = null;
            // 调用静态方法
            if (p0 instanceof OperateClass) {
                method = ExpressUtil.findMethodWithCache((Class<?>)obj, this.methodName, types, true, true);
            } else {
                if (obj instanceof Class) {
                    method = ExpressUtil.findMethodWithCache((Class<?>)obj, this.methodName, types, true, true);
                }
                if (method == null) {
                    method = ExpressUtil.findMethodWithCache(obj.getClass(), this.methodName, types, true, false);
                }
            }
            if (method == null) {
                types = new Class[] {ARRAY_CLASS};
                // 调用静态方法
                if (p0 instanceof OperateClass) {
                    method = ExpressUtil.findMethodWithCache((Class<?>)obj, methodName, types, true, true);
                } else {
                    method = ExpressUtil.findMethodWithCache(obj.getClass(), methodName, types, true, false);
                }
                objs = new Object[] {objs};
            }
            if (method == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("没有找到").append(obj.getClass().getName()).append("的方法：").append(methodName).append(
                    "(");
                for (int i = 0; i < originalTypes.length; i++) {
                    if (i > 0) {
                        stringBuilder.append(",");
                    }
                    if (originalTypes[i] == null) {
                        stringBuilder.append("null");
                    } else {
                        stringBuilder.append(originalTypes[i].getName());
                    }
                }
                stringBuilder.append(")");
                throw new QLException(stringBuilder.toString());
            }

            //阻止调用不安全的方法
            QLExpressRunStrategy.assertSecurityRiskMethod(method);

            // 调用静态方法
            if (p0 instanceof OperateClass) {
                boolean oldA = method.isAccessible();
                method.setAccessible(true);
                tmpObj = method.invoke(null, ExpressUtil.transferArray(objs, method.getParameterTypes()));
                method.setAccessible(oldA);
            } else {
                boolean oldA = method.isAccessible();
                method.setAccessible(true);
                tmpObj = method.invoke(obj, ExpressUtil.transferArray(objs, method.getParameterTypes()));
                method.setAccessible(oldA);
            }
            return OperateDataCacheManager.fetchOperateData(tmpObj, method.getReturnType());
        }
    }

    @Override
    public String toString() {
        return this.name + ":" + this.methodName;
    }
}
