package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;
import com.alibaba.qlexpress4.runtime.QFunction;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:02
 */
public class StrategySandBox implements IStrategy {
    @Override
    public boolean check(IMethod iMethod) {
        return false;
    }
}
