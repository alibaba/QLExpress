package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

/**
 * @Author TaoKan
 * @Date 2023/5/23 上午10:31
 */
public interface IStrategyBlackList extends IRules {
    boolean checkRulesPassed(IMethod iMethod);
}
