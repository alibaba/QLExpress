package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.runtime.QFunction;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/5/23 上午10:31
 */
public interface IStrategySandBox extends IRules {
    boolean checkMethodRulesPassed(IMethod iMethod, Class<?> clazz, String name, Map<String, QFunction> userDefineFunction);

    boolean checkFieldRulesPassed(IField iField, Class<?> clazz, String name, Map<String, QFunction> userDefineField);
}
