package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.Operator;

/**
 * Created by tianqiao on 16/10/16.
 */
public class AppendingClassFieldManager {
    private final List<AppendingField> appendingFields = new ArrayList<>();

    public void addAppendingField(String name, Class<?> bindingClass, Class<?> returnType, Operator operator) {
        appendingFields.add(new AppendingField(name, bindingClass, returnType, operator));
    }

    public AppendingField getAppendingClassField(Object object, String fieldName) {
        for (AppendingField appendingField : appendingFields) {
            //object是定义类型的子类
            if (fieldName.equals(appendingField.name) && (object.getClass() == appendingField.bindingClass
                || appendingField.bindingClass.isAssignableFrom(object.getClass()))) {
                return appendingField;
            }
        }
        return null;
    }

    public Object invoke(AppendingField appendingField, Object fieldObject) throws Exception {
        Operator operator = appendingField.operator;
        return operator.executeInner(new Object[] {fieldObject});
    }

    public static class AppendingField {
        private final String name;

        private final Class<?> bindingClass;

        private final Operator operator;

        private final Class<?> returnType;

        public AppendingField(String name, Class<?> bindingClass, Class<?> returnType, Operator operator) {
            this.name = name;
            this.bindingClass = bindingClass;
            this.operator = operator;
            this.returnType = returnType;
        }

        public Class<?> getReturnType() {
            return returnType;
        }
    }
}
