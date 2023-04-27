package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.runtime.QFunction;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:02
 */
public abstract class StrategySandBox implements IStrategy {
    private boolean isSandBoxMode;

    public StrategySandBox(boolean isSandBoxMode) {
        this.isSandBoxMode = isSandBoxMode;
    }

    public abstract boolean checkMethodInRules(IMethod iMethod, Class<?> clazz, String name, Map<String, QFunction> userDefineFunction);

    public abstract boolean checkFieldInRules(IField iField, Class<?> clazz, String name, Map<String, QFunction> userDefineField);

    protected boolean isSandBoxMode() {
        return isSandBoxMode;
    }

}
