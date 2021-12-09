package com.ql.util.express.test.rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"serial", "rawtypes"})
public class SubjectMananger extends HashMap {
    @SuppressWarnings("unchecked")
    public Object get(Object userName) {
        UserSubject userSubject = (UserSubject)super.get(userName);
        if (userSubject == null) {
            userSubject = new UserSubject(userName);
            super.put(userName, userSubject);
        }
        return userSubject;
    }

    public Object put(String userName, Object userSubject) {
        throw new RuntimeException("不运行的方法");
    }

    public List<SubjectValue> getSubjectValues() {
        List<SubjectValue> result = new ArrayList<>();
        for (Object f : this.values()) {
            UserSubject item = (UserSubject)f;
            for (Object t : item.entrySet()) {
                Map.Entry me = (Map.Entry)t;
                SubjectValue value = new SubjectValue();
                value.userId = item.getUserId();
                value.subjectId = me.getKey();
                value.value = ((Number)me.getValue()).doubleValue();
                result.add(value);
            }
        }
        return result;
    }
}

@SuppressWarnings({"serial", "rawtypes"})
class UserSubject extends HashMap {
    Object userId;

    public UserSubject(Object aUserId) {
        super();
        this.userId = aUserId;
    }

    public Double get(String subjectId) {
        Double value = (Double)super.get(subjectId);
        if (value == null) {
            return 0d;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public Object put(String subjectId, Object value) {
        return super.put(subjectId, value);
    }

    public Object getUserId() {
        return userId;
    }
}



