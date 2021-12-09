package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;

/**
 * Created by tianqiao on 16/10/16.
 */
public class AppendingClassMethodManager {

    public class AppendingMethod {
        public String name;

        public Class<?> bindingClass;

        public OperatorBase op;

        public AppendingMethod(String name, Class<?> bindingClass, OperatorBase op) {
            this.name = name;
            this.bindingClass = bindingClass;
            this.op = op;
        }
    }

    private List<AppendingMethod> methods = new ArrayList<>();

    public void addAppendingMethod(String name, Class<?> bindingClass, OperatorBase op) {
        methods.add(new AppendingMethod(name, bindingClass, op));
    }

    public AppendingMethod getAppendingClassMethod(Object object, String methodName) {
        for (AppendingMethod method : methods) {
            //object是定义类型的子类
            if (methodName.equals(method.name) && (object.getClass() == method.bindingClass
                || method.bindingClass.isAssignableFrom(object.getClass()))) {
                return method;
            }
        }
        return null;
    }

    public OperateData invoke(AppendingMethod method, InstructionSetContext context, ArraySwap list,
        List<String> errorList) throws Exception {
        OperatorBase op = method.op;
        return op.execute(context, list, errorList);
    }

}
