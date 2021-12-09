package com.ql.util.express.test.rating;

import java.util.Map;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.Operator;

/**
 * 科目操作符号
 *
 * @author xuannan
 */
class SujectOperator extends Operator {
    public SujectOperator(String aName) {
        this.name = aName;
    }

    public SujectOperator(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @SuppressWarnings("rawtypes")
    public OperateData executeInner(InstructionSetContext context,
        ArraySwap list) throws Exception {
        if (list.length != 2) {
            throw new Exception("科目操作的参数必须包括：科目主体ID和科目名称");
        }
        Object userId = list.get(0).getObject(context);
        Object subjectId = list.get(1).getObject(context);
        if (userId == null || subjectId == null) {
            throw new Exception("科目主体ID和科目名称不能为null");
        }
        OperateData result = new OperateDataSubject((Map)context.get("费用科目"), userId, subjectId);
        return result;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        new Exception("不需要实现的方法").printStackTrace();
        throw new Exception("不需要实现的方法");
    }
}

@SuppressWarnings("rawtypes")
class OperateDataSubject extends OperateData {
    Object userId;
    Object subjectId;
    Map container;

    public OperateDataSubject(Map aContainer, Object aUserId, Object aSubjectId) {
        super(null, Double.class);
        this.userId = aUserId;
        this.subjectId = aSubjectId;
        this.container = aContainer;
    }

    public Class<?> getDefineType() {
        return this.type;
    }

    public Object getObjectInner(InstructionSetContext context) {
        String key = this.userId + "-" + this.subjectId;
        SubjectValue subject = (SubjectValue)container.get(key);
        if (subject == null) {
            return 0d;
        } else {
            return subject.value;
        }
    }

    @SuppressWarnings("unchecked")
    public void setObject(InstructionSetContext parent, Object value) {
        String key = this.userId + "-" + this.subjectId;
        SubjectValue subject = (SubjectValue)container.get(key);
        if (subject == null) {
            subject = new SubjectValue();
            subject.subjectId = this.subjectId;
            subject.userId = this.userId;
            container.put(key, subject);
        }
        subject.value = ((Number)value).doubleValue();
    }
}
