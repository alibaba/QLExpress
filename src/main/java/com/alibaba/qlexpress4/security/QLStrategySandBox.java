package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.runtime.QFunction;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:09
 */
public class QLStrategySandBox extends StrategySandBox {

    public QLStrategySandBox(boolean isSandBoxMode) {
        super(isSandBoxMode);
    }

    @Override
    public boolean checkMethodInRules(IMethod iMethod, Class<?> clazz, String name, Map<String, QFunction> userDefineFunction) {
        if (this.isSandBoxMode()) {
            if (clazz == iMethod.getClazz() && iMethod.getName().equals(name) && !userDefineFunction.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkFieldInRules(IField iField, Class<?> clazz, String name, Map<String, QFunction> userDefineField) {
        if (this.isSandBoxMode()) {
            if (clazz == iField.getClazz() && iField.getName().equals(name) && !userDefineField.containsKey(name)) {
                return true;
            }
        }
        return false;
    }
}
