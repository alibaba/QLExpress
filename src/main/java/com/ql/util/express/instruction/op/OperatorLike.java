package com.ql.util.express.instruction.op;

import java.util.ArrayList;

import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public OperatorLike(String name) {
        this.name = name;
    }

    public OperatorLike(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        boolean result = true;
        String s1 = op1.toString();
        String s2 = op2.toString();
        if (s2.contains("%")) {
            String[] list = split(s2, "%");
            int index = 0;
            for (int i = 0; i < list.length; i++) {
                if (index >= s1.length()) {
                    result = false;
                    break;
                }
                index = s1.indexOf(list[i], index);
                if (index < 0) {
                    result = false;
                    break;
                }
                index = index + 1;
            }
        } else {
            result = s1.equals(s2);
        }

        return result;
    }

    public String[] split(String str, String s) {
        int start = 0;
        int end;
        String tmpStr;
        ArrayList<String> list = new ArrayList<>();
        do {
            end = str.indexOf(s, start);
            if (end < 0) {
                tmpStr = str.substring(start);
            } else {
                tmpStr = str.substring(start, end);
            }
            if (tmpStr.length() > 0) {
                list.add(tmpStr);
            }
            start = end + 1;
            if (start >= str.length()) {
                break;
            }
        } while (end >= 0);
        return list.toArray(new String[0]);
    }
}
