package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.Operator;

/**
 * Created by tianqiao on 16/10/16.
 */
public class AppendingClassFieldManager {

    public class AppendingField {
        public String name;

        public Class<?> bindingClass;

        public Operator op;

        public Class<?> returnType;

        public AppendingField(String name, Class<?> bindingClass, Class<?> returnType, Operator op) {
            this.name = name;
            this.bindingClass = bindingClass;
            this.op = op;
            this.returnType = returnType;
        }
    }

    private final List<AppendingField> Fields = new ArrayList<>();

    public void addAppendingField(String name, Class<?> bindingClass, Class<?> returnType, Operator op) {
        Fields.add(new AppendingField(name, bindingClass, returnType, op));
    }

    public AppendingField getAppendingClassField(Object object, String FieldName) {
        for (AppendingField Field : Fields) {
            //object是定义类型的子类
            if (FieldName.equals(Field.name) && (object.getClass() == Field.bindingClass
                || Field.bindingClass.isAssignableFrom(object.getClass()))) {
                return Field;
            }
        }
        return null;
    }

    public Object invoke(AppendingField Field, InstructionSetContext context, Object aFieldObject,
        List<String> errorList) throws Exception {
        Operator op = Field.op;
        return op.executeInner(new Object[] {aFieldObject});
    }
}
