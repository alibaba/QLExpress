package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.runtime.QFunction;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:02
 */
public class StrategySandBox implements IStrategySandBox {
    @Override
    public boolean checkMethodRulesPassed(IMethod iMethod, Class<?> clazz, String name, Map<String, QFunction> userDefineFunction) {
        if (clazz == iMethod.getClazz() && iMethod.getName().equals(name) && !userDefineFunction.containsKey(name)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkFieldRulesPassed(IField iField, Class<?> clazz, String name, Map<String, QFunction> userDefineField) {
        if (clazz == iField.getClazz() && iField.getName().equals(name) && !userDefineField.containsKey(name)) {
            return false;
        }
        return true;
    }
}
