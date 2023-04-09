package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:34
 */
public class QLStrategyBlackList extends StrategyBlackList {

    public QLStrategyBlackList(Map<Class,String> blackStrategyStructList) {
        super(blackStrategyStructList);
    }

    @Override
    public boolean checkInRules(IMethod iMethod) {
        if(this.getBlackStrategyStructList().containsKey(iMethod.getClazz())){
            String name = this.getBlackStrategyStructList().get(iMethod.getClazz());
            if(name == null || "".equals(name)){
                return true;
            }else {
                return iMethod.getName().equals(name);
            }
        }
        return false;
    }
}
