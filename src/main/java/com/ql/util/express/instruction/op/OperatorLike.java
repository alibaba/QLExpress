package com.ql.util.express.instruction.op;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public OperatorLike(String name) {
        this.name = name;
    }

    public OperatorLike(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        String s1 = op1.toString();
        String s2 = op2.toString();
        if(!s2.contains("%")){
            return s1.equals(s2);
        }else {
            String regex = quotaMeta(s2);
            regex = regex.replace("_",".").replace("%",".*?");
            Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            return p.matcher(s1).matches();
        }
    }

    private String quotaMeta(String s){
        int len = s.length();
        if(len == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(len*2);
        for(int i = 0; i < len; i++){
            char c = s.charAt(i);
            if("[](){}.*+?$^|#\\".indexOf(c) != -1){
                stringBuilder.append("\\");
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
